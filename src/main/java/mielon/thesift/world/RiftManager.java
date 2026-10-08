package mielon.thesift.world;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import mielon.thesift.TheSiftMod;
import mielon.thesift.advancement.ModAdvancements;
import mielon.thesift.entity.MiniRiftEntity;
import mielon.thesift.entity.ModEntities;
import mielon.thesift.entity.RiftEntity;
import mielon.thesift.entity.SiftiteReturnEntity;
import mielon.thesift.network.RiftLoadingPayload;
import mielon.thesift.platform.EntityCompat;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.HangingRootsBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.level.portal.DimensionTransition.PostDimensionTransition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class RiftManager {
   private static final int REENTRY_COOLDOWN_TICKS = 100;
   private static final Map<UUID, Long> NEXT_SPAWN = new HashMap<>();
   private static final Map<UUID, RiftManager.Request> PENDING = new LinkedHashMap<>();
   private static final Map<UUID, RiftManager.RiftArrival> RIFT_EXIT_LATCHES = new HashMap<>();

   public static void tick(MinecraftServer server) {
      RiftDirectory.tick(server);
      RiftLightCleanup.tick(server);
      List<RiftManager.Request> requests = List.copyOf(PENDING.values());
      PENDING.clear();

      for (RiftManager.Request request : requests) {
         finish(server, request);
      }

      long now = server.overworld().getGameTime();
      if (now % 600L == 0L) {
         PortalTransitGuard.prune(server);
      }

      releaseRiftExitLatches(server);

      for (ServerPlayer player : List.copyOf(server.getPlayerList().getPlayers())) {
         if (player.serverLevel().dimension().equals(TheSiftDimension.LEVEL_KEY) && player.isAlive()) {
            long due = NEXT_SPAWN.computeIfAbsent(player.getUUID(), idx -> now + 5400L + player.getRandom().nextInt(1201));
            if (now >= due) {
               Optional<RiftManager.Placement> found = findNatural(player.serverLevel(), player.blockPosition(), player.getRandom());
               if (found.isPresent()) {
                  RiftManager.Placement p = found.get();
                  spawn(player.serverLevel(), p, false, now + 6000L, UUID.randomUUID());
               }

               NEXT_SPAWN.put(player.getUUID(), now + (found.isPresent() ? 5400 + player.getRandom().nextInt(1201) : 200));
            }
         } else {
            NEXT_SPAWN.remove(player.getUUID());
         }
      }

      Set<UUID> visited = new HashSet<>();

      for (RiftEntity rift : RiftDirectory.loaded()) {
         if (!rift.isRemoved() && !rift.isClosing()) {
            for (Entity candidate : List.copyOf(rift.level().getEntities(rift, rift.getPortalBounds().inflate(8.0), RiftManager::traveler))) {
               Entity entity = candidate.getRootVehicle();
               UUID id = entity.getUUID();
               if (visited.add(id) && !entity.isRemoved() && !isRiftExitBlocked(entity) && !PortalTransitGuard.isBlocked(entity)) {
                  if (!touches(entity, rift)) {
                     visited.remove(id);
                  } else {
                     queue(entity, rift, false, true);
                  }
               }
            }
         }
      }
   }

   private static boolean isRiftExitBlocked(Entity entity) {
      return entity.getSelfAndPassengers().anyMatch(passenger -> RIFT_EXIT_LATCHES.containsKey(passenger.getUUID()));
   }

   private static void latchRiftExit(MinecraftServer server, Entity entity) {
      long cooldownUntil = server.overworld().getGameTime() + 100L;
      entity.getSelfAndPassengers()
         .forEach(
            passenger -> RIFT_EXIT_LATCHES.put(
               passenger.getUUID(), new RiftManager.RiftArrival(passenger.level().dimension(), passenger.position(), cooldownUntil)
            )
         );
   }

   private static void releaseRiftExitLatches(MinecraftServer server) {
      Iterator<Entry<UUID, RiftManager.RiftArrival>> iterator = RIFT_EXIT_LATCHES.entrySet().iterator();

      while (iterator.hasNext()) {
         Entry<UUID, RiftManager.RiftArrival> entry = iterator.next();
         RiftManager.RiftArrival arrival = entry.getValue();
         if (server.overworld().getGameTime() >= arrival.cooldownUntil()) {
            Entity entity = EntityCompat.find(server, entry.getKey());
            if (entity != null && !entity.isRemoved()) {
               if (!arrival.dimension.equals(entity.level().dimension())) {
                  iterator.remove();
               } else if (!isTouchingRift(entity) && (hasLoadedRiftAt(arrival) || arrival.pos.distanceToSqr(entity.position()) >= 36.0)) {
                  iterator.remove();
               }
            } else {
               iterator.remove();
            }
         }
      }
   }

   private static boolean hasLoadedRiftAt(RiftManager.RiftArrival arrival) {
      for (RiftEntity rift : RiftDirectory.loaded()) {
         if (!rift.isRemoved() && rift.level().dimension().equals(arrival.dimension) && rift.getPortalBounds().inflate(0.5).contains(arrival.pos)) {
            return true;
         }
      }

      return false;
   }

   private static boolean traveler(Entity e) {
      return e.isAlive() && !e.isSpectator() && !(e instanceof RiftEntity) && !(e instanceof MiniRiftEntity) && !(e instanceof SiftiteReturnEntity);
   }

   private static boolean touches(Entity entity, RiftEntity rift) {
      Vec3 delta = new Vec3(entity.xo, entity.yo, entity.zo).subtract(entity.position());
      if (delta.lengthSqr() > 256.0) {
         delta = Vec3.ZERO;
      }

      return entity.getBoundingBox().expandTowards(delta).intersects(rift.getPortalBounds());
   }

   public static boolean isTouchingRift(Entity entity) {
      for (RiftEntity rift : RiftDirectory.loaded()) {
         if (!rift.isRemoved() && rift.level() == entity.level() && rift.getPortalBounds().intersects(entity.getBoundingBox())) {
            return true;
         }
      }

      return false;
   }

   private static void screen(Entity entity, boolean start, boolean rift) {
      entity.getSelfAndPassengers().forEach(e -> {
         if (e instanceof ServerPlayer player) {
            RiftLoadingPayload.send(player, start, rift);
         }
      });
   }

   private static void queue(Entity entity, RiftEntity origin, boolean closed, boolean riftScreen) {
      if (!PENDING.containsKey(entity.getUUID())) {
         PortalTransitGuard.block(entity);
         SiftTeleportManager.suppressUntilExit(entity);
         screen(entity, true, riftScreen);
         PENDING.put(entity.getUUID(), new RiftManager.Request(entity, origin, closed, riftScreen));
      }
   }

   public static void returnFromPortal(Entity entity, boolean closed) {
      queue(entity.getRootVehicle(), null, closed, false);
   }

   private static void finish(MinecraftServer server, RiftManager.Request request) {
      Entity entity = request.entity;
      List<ServerPlayer> players = entity.getSelfAndPassengers().filter(e -> e instanceof ServerPlayer).map(e -> (ServerPlayer)e).toList();

      try {
         if (entity.isRemoved() || !entity.isAlive()) {
            return;
         }

         RiftEntity origin = request.origin;
         if (origin == null || entity.level() == origin.level()) {
            boolean intoSift = origin != null && origin.targetsSift();
            RiftManager.Destination destination = intoSift ? intoSift(server, entity, origin) : outOfSift(server, entity, origin);
            if (destination == null || !entity.canChangeDimensions(entity.level(), destination.level)) {
               return;
            }

            if (intoSift) {
               entity.getSelfAndPassengers().forEach(SiftWorldStorage::markRiftEntry);
            }

            Vec3 motion = entity instanceof ServerPlayer ? Vec3.ZERO : entity.getDeltaMovement();
            Entity moved = moveToDestination(entity, destination, motion, DimensionTransition.PLAY_PORTAL_SOUND.then(DimensionTransition.PLACE_PORTAL_TICKET));
            if (moved != null) {
               if (request.riftScreen) {
                  for (ServerPlayer player : players) {
                     ModAdvancements.award(player, "the_sift/rifter");
                  }
               }

               PortalTransitGuard.block(moved);
               SiftTeleportManager.suppressUntilExit(moved);
               latchRiftExit(server, moved);
               if (!intoSift) {
                  SiftWorldStorage.clearReturnPoint(server, entity.getUUID());
                  if (!moved.getUUID().equals(entity.getUUID())) {
                     SiftWorldStorage.clearReturnPoint(server, moved.getUUID());
                  }
               }

               for (ServerPlayer player : players) {
                  if (destination.fallback) {
                     player.sendSystemMessage(Component.translatable("message.the_sift.rift_no_exit").withStyle(ChatFormatting.RED));
                  }

                  if (request.closedPortal) {
                     player.sendSystemMessage(Component.translatable("message.the_sift.portal_closed"));
                  }
               }
            }

            return;
         }
      } catch (RuntimeException var16) {
         TheSiftMod.LOGGER.error("Rift transfer failed", var16);
         if (entity.level().dimension().equals(TheSiftDimension.LEVEL_KEY) && !entity.isRemoved()) {
            RiftManager.Destination fallback = fallback(server, entity);
            Entity moved = moveToDestination(entity, fallback, Vec3.ZERO, DimensionTransition.PLACE_PORTAL_TICKET);
            if (moved != null) {
               PortalTransitGuard.block(moved);
               latchRiftExit(server, moved);
            }

            for (ServerPlayer player : players) {
               player.sendSystemMessage(Component.translatable("message.the_sift.rift_no_exit").withStyle(ChatFormatting.RED));
            }
         }

         return;
      } finally {
         for (ServerPlayer player : players) {
            RiftLoadingPayload.send(player, false, request.riftScreen);
         }
      }
   }

   private static RiftManager.Destination intoSift(MinecraftServer server, Entity entity, RiftEntity origin) {
      ServerLevel sift = server.getLevel(TheSiftDimension.LEVEL_KEY);
      if (sift == null) {
         return null;
      } else {
         BlockPos pos = SiftWorldStorage.getRiftRoute(server, entity.getUUID(), origin.getUUID(), server.overworld().getGameTime())
            .orElse(origin.getLinkedPos());
         if (pos == null) {
            pos = SiftPortalStructure.ensurePortal(server, sift).orElse(null);
         }

         if (pos == null) {
            return null;
         } else {
            load(sift, pos);
            return new RiftManager.Destination(sift, new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5), false);
         }
      }
   }

   private static RiftManager.Destination outOfSift(MinecraftServer server, Entity entity, RiftEntity origin) {
      ServerLevel overworld = server.overworld();
      BlockPos anchor = overworldAnchor(server, entity);
      long expiry = origin == null ? overworld.getGameTime() + 6000L : Math.max(overworld.getGameTime() + 20L, origin.getExpiresAt());
      Optional<RiftDirectory.Exit> existing = RiftDirectory.near(anchor, overworld.getGameTime());
      RiftDirectory.Exit exit;
      if (existing.isPresent()) {
         exit = RiftDirectory.extend(existing.get(), expiry);
         load(overworld, exit.pos());
      } else {
         Optional<RiftManager.Placement> found = findNear(overworld, anchor);
         if (found.isEmpty()) {
            return fallback(server, entity);
         }

         RiftEntity rift = spawn(overworld, found.get(), true, expiry, UUID.randomUUID());
         if (origin != null) {
            rift.setLinkedPos(origin.getAnchorPos(), origin.getExpiresAt());
         }

         RiftDirectory.add(rift, anchor);
         exit = new RiftDirectory.Exit(rift.getUUID(), rift.getAnchorPos(), anchor, expiry);
      }

      if (origin != null) {
         RiftDirectory.Exit selected = exit;
         entity.getSelfAndPassengers()
            .forEach(e -> SiftWorldStorage.saveRiftRoute(server, e.getUUID(), selected.id(), origin.getAnchorPos(), origin.getExpiresAt()));
      }

      return new RiftManager.Destination(overworld, new Vec3(exit.pos().getX() + 0.5, exit.pos().getY(), exit.pos().getZ() + 0.5), false);
   }

   private static void load(ServerLevel level, BlockPos pos) {
      level.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4), 3, pos);
      level.getChunk(pos);
   }

   public static BlockPos overworldAnchor(MinecraftServer server, Entity entity) {
      return entity instanceof ServerPlayer player && player.getRespawnPosition() != null && player.getRespawnDimension().equals(Level.OVERWORLD)
         ? player.getRespawnPosition()
         : server.overworld().getSharedSpawnPos();
   }

   private static RiftManager.Destination fallback(MinecraftServer server, Entity entity) {
      ServerLevel level = server.overworld();
      BlockPos anchor = overworldAnchor(server, entity);
      load(level, anchor);
      BlockPos safe = entity.adjustSpawnLocation(level, anchor);
      return new RiftManager.Destination(level, new Vec3(safe.getX() + 0.5, safe.getY(), safe.getZ() + 0.5), true);
   }

   private static Entity moveToDestination(Entity entity, RiftManager.Destination destination, Vec3 motion, PostDimensionTransition postTransition) {
      if (destination.fallback && entity instanceof ItemEntity item) {
         if (item.getItem().isEmpty()) {
            return null;
         } else {
            ItemEntity returned = new ItemEntity(
               destination.level, destination.pos.x, destination.pos.y, destination.pos.z, item.getItem().copy(), 0.0, 0.0, 0.0
            );
            returned.setDefaultPickUpDelay();
            if (!destination.level.addFreshEntity(returned)) {
               return null;
            } else {
               item.discard();
               return returned;
            }
         }
      } else {
         return entity.changeDimension(new DimensionTransition(destination.level, destination.pos, motion, entity.getYRot(), entity.getXRot(), postTransition));
      }
   }

   public static boolean tryPlaceCreativeRift(ServerLevel level, BlockPos pos, boolean alongX) {
      if (!space(level, pos, alongX)) {
         return false;
      } else {
         RiftEntity rift = spawn(
            level,
            new RiftManager.Placement(pos, alongX),
            !level.dimension().equals(TheSiftDimension.LEVEL_KEY),
            level.getGameTime() + 6000L,
            UUID.randomUUID()
         );
         if (level.dimension().equals(Level.OVERWORLD)) {
            RiftDirectory.add(rift, pos);
         }

         return true;
      }
   }

   private static RiftEntity spawn(ServerLevel level, RiftManager.Placement p, boolean target, long expiry, UUID pair) {
      RiftEntity rift = new RiftEntity(ModEntities.RIFT, level);
      rift.configure(target, p.alongX, expiry, pair);
      rift.setPos(p.base.getX() + 0.5, p.base.getY() + 1, p.base.getZ() + 0.5);
      rift.setBoundingBox(rift.getPortalBounds());
      level.addFreshEntity(rift);
      RiftDirectory.track(rift);
      return rift;
   }

   private static boolean plant(BlockState state) {
      return state.getFluidState().isEmpty() && !(state.getBlock() instanceof LeavesBlock)
         ? state.getBlock() instanceof BushBlock || state.getBlock() instanceof VineBlock || state.getBlock() instanceof HangingRootsBlock
         : false;
   }

   private static boolean space(ServerLevel level, BlockPos base, boolean alongX) {
      if (base.getY() >= level.getMinBuildHeight() && base.getY() + 5 < level.getMaxBuildHeight()) {
         boolean supported = false;

         for (int gap = 1; gap <= 3; gap++) {
            BlockState under = level.getBlockState(base.below(gap));
            if (!under.isAir() && !plant(under)) {
               supported = true;
               break;
            }
         }

         if (!supported) {
            return false;
         } else {
            for (int a = -4; a <= 4; a++) {
               for (int y = 0; y < 5; y++) {
                  BlockPos p = alongX ? base.offset(a, y, 0) : base.offset(0, y, a);
                  if (!level.getWorldBorder().isWithinBounds(p)) {
                     return false;
                  }

                  BlockState state = level.getBlockState(p);
                  if (!state.isAir() && !plant(state)) {
                     return false;
                  }
               }
            }

            AABB volume = alongX
               ? new AABB(base.getX() - 4, base.getY(), base.getZ(), base.getX() + 5, base.getY() + 5, base.getZ() + 1)
               : new AABB(base.getX(), base.getY(), base.getZ() - 4, base.getX() + 1, base.getY() + 5, base.getZ() + 5);
            return level.getEntities((Entity)null, volume, e -> !e.isRemoved()).isEmpty();
         }
      } else {
         return false;
      }
   }

   private static Optional<RiftManager.Placement> column(ServerLevel level, int x, int z, boolean axis) {
      int surface = level.getHeight(Types.MOTION_BLOCKING, x, z);

      for (int rise = 0; rise <= 2; rise++) {
         BlockPos base = new BlockPos(x, surface + rise, z);
         if (space(level, base, axis)) {
            return Optional.of(new RiftManager.Placement(base, axis));
         }

         if (space(level, base, !axis)) {
            return Optional.of(new RiftManager.Placement(base, !axis));
         }
      }

      return Optional.empty();
   }

   private static Optional<RiftManager.Placement> findNear(ServerLevel level, BlockPos anchor) {
      Optional<RiftManager.Placement> first = column(level, anchor.getX(), anchor.getZ(), true);
      if (first.isPresent()) {
         return first;
      } else {
         for (int radius = 16; radius <= 256; radius += 16) {
            for (int i = 0; i < 16; i++) {
               double angle = (i + radius * 0.017) * Math.PI / 8.0;
               int x = anchor.getX() + (int)Math.round(Math.cos(angle) * radius);
               int z = anchor.getZ() + (int)Math.round(Math.sin(angle) * radius);
               Optional<RiftManager.Placement> found = column(level, x, z, (i & 1) == 0);
               if (found.isPresent()) {
                  return found;
               }
            }
         }

         return Optional.empty();
      }
   }

   private static Optional<RiftManager.Placement> findNatural(ServerLevel level, BlockPos center, RandomSource random) {
      for (int i = 0; i < 48; i++) {
         double angle = random.nextDouble() * Math.PI * 2.0;
         int distance = 20 + random.nextInt(45);
         Optional<RiftManager.Placement> found = column(
            level, center.getX() + (int)(Math.cos(angle) * distance), center.getZ() + (int)(Math.sin(angle) * distance), random.nextBoolean()
         );
         if (found.isPresent()) {
            return found;
         }
      }

      return Optional.empty();
   }

   public static boolean axisFromFacing(Direction facing) {
      return facing.getAxis() == Axis.Z;
   }

   public static void clearTransientState() {
      NEXT_SPAWN.clear();
      PENDING.clear();
      RIFT_EXIT_LATCHES.clear();
      RiftDirectory.clear();
      PortalTransitGuard.clear();
      RiftLightCleanup.clear();
   }

   private record Destination(ServerLevel level, Vec3 pos, boolean fallback) {
   }

   private record Placement(BlockPos base, boolean alongX) {
   }

   private record Request(Entity entity, RiftEntity origin, boolean closedPortal, boolean riftScreen) {
   }

   private record RiftArrival(ResourceKey<Level> dimension, Vec3 pos, long cooldownUntil) {
   }
}
