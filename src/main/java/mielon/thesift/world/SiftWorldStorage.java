package mielon.thesift.world;

import java.util.Optional;
import java.util.UUID;
import mielon.thesift.block.ModBlocks;
import mielon.thesift.platform.NbtCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedData.Factory;

public final class SiftWorldStorage extends SavedData {
   private static final String DIMENSION_STATE = "dimension_state";
   private static final String RETURN_POINTS = "return_points";
   private static final String PORTAL_GUARDS = "portal_guards";
   private static final String RIFT_ROUTES = "rift_routes";
   private static final String FIRST_SIFT_ENTRIES = "first_sift_entries";
   private static final Factory<SiftWorldStorage> TYPE = new Factory<>(SiftWorldStorage::new, (tag, registries) -> new SiftWorldStorage(tag), null);
   private final CompoundTag root;

   private SiftWorldStorage() {
      this(new CompoundTag());
   }

   private SiftWorldStorage(CompoundTag root) {
      this.root = root.copy();
   }

   public CompoundTag save(CompoundTag tag, Provider registries) {
      return this.root.copy();
   }

   private static SiftWorldStorage data(MinecraftServer server) {
      return (SiftWorldStorage)server.overworld().getDataStorage().computeIfAbsent(TYPE, "the_sift_world_state");
   }

   public static Optional<BlockPos> getPortalAnchor(MinecraftServer server) {
      SiftWorldStorage data = data(server);
      CompoundTag tag = data.getOrMigrateDimensionState(server);
      return !NbtCompat.getBooleanOr(tag, "portal_generated", false)
         ? Optional.empty()
         : Optional.of(new BlockPos(NbtCompat.getIntOr(tag, "portal_x", 0), NbtCompat.getIntOr(tag, "portal_y", 64), NbtCompat.getIntOr(tag, "portal_z", 0)));
   }

   public static boolean claimFirstSiftEntry(MinecraftServer server, UUID playerId) {
      SiftWorldStorage data = data(server);
      CompoundTag entries = data.section("first_sift_entries");
      String key = playerId.toString();
      if (entries.contains(key)) {
         return false;
      } else {
         entries.putBoolean(key, true);
         data.root.put("first_sift_entries", entries);
         data.setDirty();
         return true;
      }
   }

   public static void setPortalAnchor(MinecraftServer server, BlockPos anchor) {
      CompoundTag tag = new CompoundTag();
      tag.putBoolean("portal_generated", true);
      tag.putInt("portal_x", anchor.getX());
      tag.putInt("portal_y", anchor.getY());
      tag.putInt("portal_z", anchor.getZ());
      SiftWorldStorage data = data(server);
      data.root.put("dimension_state", tag);
      data.setDirty();
   }

   public static void saveReturnPoint(Entity entity) {
      MinecraftServer server = entity.level().getServer();
      if (server != null) {
         CompoundTag tag = new CompoundTag();
         tag.putBoolean("valid", true);
         tag.putDouble("x", entity.getX());
         tag.putDouble("y", entity.getY());
         tag.putDouble("z", entity.getZ());
         tag.putFloat("yaw", entity.getYRot());
         tag.putFloat("pitch", entity.getXRot());
         BlockPos portal = BlockPos.containing(entity.position());
         BlockPos min = BlockPos.containing(entity.getBoundingBox().minX, entity.getBoundingBox().minY, entity.getBoundingBox().minZ);
         BlockPos max = BlockPos.containing(entity.getBoundingBox().maxX, entity.getBoundingBox().maxY, entity.getBoundingBox().maxZ);

         for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            if (entity.level().getBlockState(pos).is(ModBlocks.SIFT_PORTAL)) {
               portal = pos.immutable();
               break;
            }
         }

         tag.putInt("portal_x", portal.getX());
         tag.putInt("portal_y", portal.getY());
         tag.putInt("portal_z", portal.getZ());
         data(server).putEntry("return_points", entity.getUUID(), tag);
      }
   }

   public static Optional<SiftWorldStorage.ReturnPoint> getReturnPoint(MinecraftServer server, UUID entityId) {
      Optional<CompoundTag> optional = data(server).getOrMigrateEntry(server, "return_points", entityId, "return/" + entityId);
      if (optional.isEmpty()) {
         return Optional.empty();
      } else {
         CompoundTag tag = optional.get();
         if (!NbtCompat.getBooleanOr(tag, "valid", false)) {
            return Optional.empty();
         } else {
            double x = NbtCompat.getDoubleOr(tag, "x", 0.5);
            double y = NbtCompat.getDoubleOr(tag, "y", 80.0);
            double z = NbtCompat.getDoubleOr(tag, "z", 0.5);
            return Optional.of(
               new SiftWorldStorage.ReturnPoint(
                  x,
                  y,
                  z,
                  NbtCompat.getFloatOr(tag, "yaw", 0.0F),
                  NbtCompat.getFloatOr(tag, "pitch", 0.0F),
                  new BlockPos(
                     NbtCompat.getIntOr(tag, "portal_x", (int)Math.floor(x)),
                     NbtCompat.getIntOr(tag, "portal_y", (int)Math.floor(y)),
                     NbtCompat.getIntOr(tag, "portal_z", (int)Math.floor(z))
                  )
               )
            );
         }
      }
   }

   public static void clearReturnPoint(MinecraftServer server, UUID entityId) {
      data(server).removeEntry("return_points", entityId);
      clearLegacyIfPresent(server, "return/" + entityId);
   }

   public static void markPortalExitRequired(Entity entity) {
      MinecraftServer server = entity.level().getServer();
      if (server != null) {
         CompoundTag tag = new CompoundTag();
         tag.putBoolean("waiting_for_exit", true);
         data(server).putEntry("portal_guards", entity.getUUID(), tag);
      }
   }

   public static boolean requiresPortalExit(MinecraftServer server, UUID entityId) {
      return data(server)
         .getOrMigrateEntry(server, "portal_guards", entityId, "portal_guard/" + entityId)
         .map(tag -> NbtCompat.getBooleanOr(tag, "waiting_for_exit", false))
         .orElse(false);
   }

   public static void clearPortalExitRequired(MinecraftServer server, UUID entityId) {
      data(server).removeEntry("portal_guards", entityId);
      clearLegacyIfPresent(server, "portal_guard/" + entityId);
   }

   public static void markRiftEntry(Entity entity) {
      MinecraftServer server = entity.level().getServer();
      if (server != null) {
         clearReturnPoint(server, entity.getUUID());
      }
   }

   public static void saveRiftRoute(MinecraftServer server, UUID entityId, UUID exitId, BlockPos source, long expires) {
      CompoundTag tag = new CompoundTag();
      tag.putString("exit", exitId.toString());
      tag.putInt("x", source.getX());
      tag.putInt("y", source.getY());
      tag.putInt("z", source.getZ());
      tag.putLong("expires", expires);
      data(server).putEntry("rift_routes", entityId, tag);
   }

   public static Optional<BlockPos> getRiftRoute(MinecraftServer server, UUID entityId, UUID exitId, long now) {
      SiftWorldStorage data = data(server);
      Optional<CompoundTag> optional = data.getOrMigrateEntry(server, "rift_routes", entityId, "rift_route/" + entityId);
      if (optional.isEmpty()) {
         return Optional.empty();
      } else {
         CompoundTag tag = optional.get();
         if (NbtCompat.getStringOr(tag, "exit", "").equals(exitId.toString()) && NbtCompat.getLongOr(tag, "expires", 0L) > now) {
            return Optional.of(new BlockPos(NbtCompat.getIntOr(tag, "x", 0), NbtCompat.getIntOr(tag, "y", 80), NbtCompat.getIntOr(tag, "z", 0)));
         } else {
            data.removeEntry("rift_routes", entityId);
            clearLegacyIfPresent(server, "rift_route/" + entityId);
            return Optional.empty();
         }
      }
   }

   private CompoundTag getOrMigrateDimensionState(MinecraftServer server) {
      Optional<CompoundTag> current = optionalCompound(this.root, "dimension_state");
      if (current.isPresent()) {
         return current.get();
      } else {
         CompoundTag legacy = server.getCommandStorage().get(legacyId("dimension_state"));
         if (!legacy.isEmpty()) {
            CompoundTag migrated = legacy.copy();
            this.root.put("dimension_state", migrated);
            this.setDirty();
            clearLegacyIfPresent(server, "dimension_state");
            return migrated;
         } else {
            return new CompoundTag();
         }
      }
   }

   private Optional<CompoundTag> getOrMigrateEntry(MinecraftServer server, String sectionName, UUID entityId, String legacyPath) {
      String key = entityId.toString();
      Optional<CompoundTag> section = optionalCompound(this.root, sectionName);
      if (section.isPresent()) {
         Optional<CompoundTag> current = optionalCompound(section.get(), key);
         if (current.isPresent()) {
            return current;
         }
      }

      CompoundTag legacy = server.getCommandStorage().get(legacyId(legacyPath));
      if (legacy.isEmpty()) {
         return Optional.empty();
      } else {
         this.putEntry(sectionName, entityId, legacy);
         clearLegacyIfPresent(server, legacyPath);
         return Optional.of(legacy);
      }
   }

   private void putEntry(String sectionName, UUID entityId, CompoundTag value) {
      CompoundTag section = this.section(sectionName);
      section.put(entityId.toString(), value.copy());
      this.root.put(sectionName, section);
      this.setDirty();
   }

   private void removeEntry(String sectionName, UUID entityId) {
      Optional<CompoundTag> section = optionalCompound(this.root, sectionName);
      if (!section.isEmpty()) {
         CompoundTag entries = section.get();
         if (entries.contains(entityId.toString())) {
            entries.remove(entityId.toString());
            if (entries.isEmpty()) {
               this.root.remove(sectionName);
            } else {
               this.root.put(sectionName, entries);
            }

            this.setDirty();
         }
      }
   }

   private CompoundTag section(String name) {
      Optional<CompoundTag> current = optionalCompound(this.root, name);
      if (current.isPresent()) {
         return current.get();
      } else {
         CompoundTag created = new CompoundTag();
         this.root.put(name, created);
         return created;
      }
   }

   private static void clearLegacyIfPresent(MinecraftServer server, String path) {
      ResourceLocation id = legacyId(path);
      if (!server.getCommandStorage().get(id).isEmpty()) {
         server.getCommandStorage().set(id, new CompoundTag());
      }
   }

   private static Optional<CompoundTag> optionalCompound(CompoundTag tag, String key) {
      return tag.contains(key, 10) ? Optional.of(tag.getCompound(key)) : Optional.empty();
   }

   private static ResourceLocation legacyId(String path) {
      return ResourceLocation.fromNamespaceAndPath("the_sift", path);
   }

   public record ReturnPoint(double x, double y, double z, float yaw, float pitch, BlockPos portalBlock) {
   }
}
