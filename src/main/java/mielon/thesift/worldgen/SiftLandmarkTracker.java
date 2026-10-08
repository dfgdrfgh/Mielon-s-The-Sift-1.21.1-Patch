package mielon.thesift.worldgen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import mielon.thesift.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

public final class SiftLandmarkTracker {
   private static final int SAVE_INTERVAL_TICKS = 600;
   private static final ResourceLocation STORAGE_ID = ResourceLocation.fromNamespaceAndPath("the_sift", "generated_landmarks");
   private static final Map<SiftLandmarkTracker.Kind, Set<Long>> POSITIONS = new EnumMap<>(SiftLandmarkTracker.Kind.class);
   private static final Map<SiftLandmarkTracker.Kind, Map<Long, Set<Long>>> POSITIONS_BY_CHUNK = new EnumMap<>(SiftLandmarkTracker.Kind.class);
   private static final Map<Long, SiftLandmarkTracker.CanyonActivity> LEGACY_CANYON_ACTIVITY = new ConcurrentHashMap<>();
   private static volatile boolean dirty;
   private static boolean loaded;
   private static int saveCooldown;

   private SiftLandmarkTracker() {
   }

   public static void record(SiftLandmarkTracker.Kind kind, BlockPos pos) {
      long packed = pos.asLong();
      if (POSITIONS.get(kind).add(packed)) {
         index(kind, packed);
         dirty = true;
      }
   }

   public static void forget(SiftLandmarkTracker.Kind kind, BlockPos pos) {
      long packed = pos.asLong();
      if (POSITIONS.get(kind).remove(packed)) {
         unindex(kind, packed);
         dirty = true;
      }
   }

   private static void index(SiftLandmarkTracker.Kind kind, long packed) {
      BlockPos pos = BlockPos.of(packed);
      long chunk = chunkKey(pos.getX() >> 4, pos.getZ() >> 4);
      POSITIONS_BY_CHUNK.get(kind).computeIfAbsent(chunk, ignored -> ConcurrentHashMap.newKeySet()).add(packed);
   }

   private static void unindex(SiftLandmarkTracker.Kind kind, long packed) {
      BlockPos pos = BlockPos.of(packed);
      long chunk = chunkKey(pos.getX() >> 4, pos.getZ() >> 4);
      Set<Long> positions = POSITIONS_BY_CHUNK.get(kind).get(chunk);
      if (positions != null) {
         positions.remove(packed);
         if (positions.isEmpty()) {
            POSITIONS_BY_CHUNK.get(kind).remove(chunk, positions);
         }
      }
   }

   private static long chunkKey(int chunkX, int chunkZ) {
      return (long)chunkX << 32 ^ chunkZ & 4294967295L;
   }

   public static Optional<BlockPos> nearest(SiftLandmarkTracker.Kind kind, BlockPos origin) {
      BlockPos best = null;
      double bestDistance = Double.POSITIVE_INFINITY;

      for (long packed : POSITIONS.get(kind)) {
         BlockPos candidate = BlockPos.of(packed);
         double dx = candidate.getX() - origin.getX();
         double dz = candidate.getZ() - origin.getZ();
         double distance = dx * dx + dz * dz;
         if (distance < bestDistance) {
            bestDistance = distance;
            best = candidate;
         }
      }

      return Optional.ofNullable(best);
   }

   public static Optional<BlockPos> nearestOther(SiftLandmarkTracker.Kind kind, BlockPos origin, double maximumDistance, BlockPos excluded) {
      BlockPos best = null;
      double bestDistance = maximumDistance * maximumDistance;
      long excludedPacked = excluded == null ? Long.MIN_VALUE : excluded.asLong();
      int originChunkX = origin.getX() >> 4;
      int originChunkZ = origin.getZ() >> 4;
      int maximumRing = ceilChunks(maximumDistance);
      Map<Long, Set<Long>> index = POSITIONS_BY_CHUNK.get(kind);

      for (int ring = 0; ring <= maximumRing; ring++) {
         for (int chunkX = originChunkX - ring; chunkX <= originChunkX + ring; chunkX++) {
            for (int chunkZ = originChunkZ - ring; chunkZ <= originChunkZ + ring; chunkZ++) {
               if (ring <= 0 || Math.max(Math.abs(chunkX - originChunkX), Math.abs(chunkZ - originChunkZ)) == ring) {
                  Set<Long> positions = index.get(chunkKey(chunkX, chunkZ));
                  if (positions != null) {
                     for (long packed : positions) {
                        if (packed != excludedPacked) {
                           BlockPos candidate = BlockPos.of(packed);
                           double distance = horizontalDistanceSqr(candidate, origin);
                           if (distance < bestDistance) {
                              bestDistance = distance;
                              best = candidate;
                           }
                        }
                     }
                  }
               }
            }
         }

         if (best != null && outsideRingDistanceSqr(origin, originChunkX, originChunkZ, ring) >= bestDistance) {
            break;
         }
      }

      return Optional.ofNullable(best);
   }

   public static List<BlockPos> snapshot(SiftLandmarkTracker.Kind kind) {
      return POSITIONS.get(kind).stream().<BlockPos>map(BlockPos::of).toList();
   }

   public static Optional<BlockPos> nearestActiveSoulCanyon(ServerLevel level, BlockPos origin, double maximumDistance) {
      return nearestActiveSoulCanyon(level, origin, maximumDistance, Set.of());
   }

   public static Optional<BlockPos> nearestActiveSoulCanyon(ServerLevel level, BlockPos origin, double maximumDistance, Collection<Long> excluded) {
      BlockPos best = null;
      double bestDistance = maximumDistance * maximumDistance;
      int originChunkX = origin.getX() >> 4;
      int originChunkZ = origin.getZ() >> 4;
      int maximumRing = ceilChunks(maximumDistance);
      Map<Long, Set<Long>> index = POSITIONS_BY_CHUNK.get(SiftLandmarkTracker.Kind.SOUL_CANYON_CLUSTER);

      for (int ring = 0; ring <= maximumRing; ring++) {
         for (int chunkX = originChunkX - ring; chunkX <= originChunkX + ring; chunkX++) {
            for (int chunkZ = originChunkZ - ring; chunkZ <= originChunkZ + ring; chunkZ++) {
               if (ring <= 0 || Math.max(Math.abs(chunkX - originChunkX), Math.abs(chunkZ - originChunkZ)) == ring) {
                  Set<Long> positions = index.get(chunkKey(chunkX, chunkZ));
                  if (positions != null) {
                     for (long packed : positions) {
                        if (!excluded.contains(packed)) {
                           BlockPos candidate = BlockPos.of(packed);
                           double distance = horizontalDistanceSqr(candidate, origin);
                           if (distance < bestDistance && hasLoadedSoulBlockNear(level, candidate)) {
                              bestDistance = distance;
                              best = candidate;
                           }
                        }
                     }
                  }
               }
            }
         }

         if (best != null && outsideRingDistanceSqr(origin, originChunkX, originChunkZ, ring) >= bestDistance) {
            break;
         }
      }

      return Optional.ofNullable(best);
   }

   public static List<BlockPos> activeSoulBlocksNear(ServerLevel level, BlockPos center, int horizontal, int vertical) {
      double horizontalSqr = (double)horizontal * horizontal;
      int minimumChunkX = center.getX() - horizontal >> 4;
      int maximumChunkX = center.getX() + horizontal >> 4;
      int minimumChunkZ = center.getZ() - horizontal >> 4;
      int maximumChunkZ = center.getZ() + horizontal >> 4;
      List<BlockPos> result = new ArrayList<>();
      Map<Long, Set<Long>> index = POSITIONS_BY_CHUNK.get(SiftLandmarkTracker.Kind.SOUL_CANYON_SOUL_BLOCK);

      for (int chunkX = minimumChunkX; chunkX <= maximumChunkX; chunkX++) {
         for (int chunkZ = minimumChunkZ; chunkZ <= maximumChunkZ; chunkZ++) {
            Set<Long> positions = index.get(chunkKey(chunkX, chunkZ));
            if (positions != null) {
               for (long packed : positions) {
                  BlockPos candidate = BlockPos.of(packed);
                  if (horizontalDistanceSqr(candidate, center) <= horizontalSqr
                     && Math.abs(candidate.getY() - center.getY()) <= vertical
                     && level.isLoaded(candidate)
                     && level.getBlockState(candidate).is(ModBlocks.SOUL_BLOCK)) {
                     result.add(candidate);
                  }
               }
            }
         }
      }

      return result;
   }

   public static boolean hasIndexedSoulPositionsNear(BlockPos center, int horizontal, int vertical) {
      double horizontalSqr = (double)horizontal * horizontal;
      int minimumChunkX = center.getX() - horizontal >> 4;
      int maximumChunkX = center.getX() + horizontal >> 4;
      int minimumChunkZ = center.getZ() - horizontal >> 4;
      int maximumChunkZ = center.getZ() + horizontal >> 4;
      Map<Long, Set<Long>> index = POSITIONS_BY_CHUNK.get(SiftLandmarkTracker.Kind.SOUL_CANYON_SOUL_BLOCK);

      for (int chunkX = minimumChunkX; chunkX <= maximumChunkX; chunkX++) {
         for (int chunkZ = minimumChunkZ; chunkZ <= maximumChunkZ; chunkZ++) {
            Set<Long> positions = index.get(chunkKey(chunkX, chunkZ));
            if (positions != null) {
               for (long packed : positions) {
                  BlockPos candidate = BlockPos.of(packed);
                  if (horizontalDistanceSqr(candidate, center) <= horizontalSqr && Math.abs(candidate.getY() - center.getY()) <= vertical) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   private static boolean hasLoadedSoulBlockNear(ServerLevel level, BlockPos center) {
      List<BlockPos> recorded = activeSoulBlocksNear(level, center, 36, 28);
      if (!recorded.isEmpty()) {
         return true;
      } else if (hasIndexedSoulPositionsNear(center, 36, 28)) {
         return false;
      } else {
         long now = level.getGameTime();
         SiftLandmarkTracker.CanyonActivity cached = LEGACY_CANYON_ACTIVITY.get(center.asLong());
         if (cached != null && cached.validUntil() > now) {
            return cached.active();
         } else {
            MutableBlockPos cursor = new MutableBlockPos();

            for (int dy = -20; dy <= 20; dy++) {
               for (int dx = -24; dx <= 24; dx++) {
                  for (int dz = -24; dz <= 24; dz++) {
                     if (dx * dx + dz * dz <= 576) {
                        cursor.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                        if (level.isLoaded(cursor) && level.getBlockState(cursor).is(ModBlocks.SOUL_BLOCK)) {
                           LEGACY_CANYON_ACTIVITY.put(center.asLong(), new SiftLandmarkTracker.CanyonActivity(true, now + 100L));
                           return true;
                        }
                     }
                  }
               }
            }

            LEGACY_CANYON_ACTIVITY.put(center.asLong(), new SiftLandmarkTracker.CanyonActivity(false, now + 1200L));
            return false;
         }
      }
   }

   private static int ceilChunks(double distance) {
      return Math.max(0, (int)Math.ceil(distance / 16.0) + 1);
   }

   private static double outsideRingDistanceSqr(BlockPos origin, int originChunkX, int originChunkZ, int ring) {
      int minimumX = originChunkX - ring << 4;
      int maximumX = (originChunkX + ring << 4) + 15;
      int minimumZ = originChunkZ - ring << 4;
      int maximumZ = (originChunkZ + ring << 4) + 15;
      int distance = Math.min(
         Math.min(origin.getX() - (minimumX - 1), maximumX + 1 - origin.getX()), Math.min(origin.getZ() - (minimumZ - 1), maximumZ + 1 - origin.getZ())
      );
      return (double)distance * distance;
   }

   private static double horizontalDistanceSqr(BlockPos first, BlockPos second) {
      double dx = first.getX() - second.getX();
      double dz = first.getZ() - second.getZ();
      return dx * dx + dz * dz;
   }

   public static void tick(MinecraftServer server) {
      ensureLoaded(server);
      if (dirty && --saveCooldown <= 0) {
         saveCooldown = 600;
         writeStorage(server);
      }
   }

   public static void flush(MinecraftServer server) {
      ensureLoaded(server);
      if (dirty) {
         writeStorage(server);
      }
   }

   private static void ensureLoaded(MinecraftServer server) {
      if (!loaded) {
         loaded = true;
         CompoundTag stored = server.getCommandStorage().get(STORAGE_ID);

         for (SiftLandmarkTracker.Kind kind : SiftLandmarkTracker.Kind.values()) {
            long[] positions = stored.getLongArray(kind.storageKey());

            for (long position : positions) {
               if (POSITIONS.get(kind).add(position)) {
                  index(kind, position);
               }
            }
         }
      }
   }

   private static void writeStorage(MinecraftServer server) {
      dirty = false;
      CompoundTag stored = new CompoundTag();

      for (SiftLandmarkTracker.Kind kind : SiftLandmarkTracker.Kind.values()) {
         long[] positions = POSITIONS.get(kind).stream().mapToLong(Long::longValue).sorted().toArray();
         stored.putLongArray(kind.storageKey(), positions);
      }

      server.getCommandStorage().set(STORAGE_ID, stored);
   }

   public static void clearTransientState() {
      for (Set<Long> positions : POSITIONS.values()) {
         positions.clear();
      }

      for (Map<Long, Set<Long>> positions : POSITIONS_BY_CHUNK.values()) {
         positions.clear();
      }

      LEGACY_CANYON_ACTIVITY.clear();
      dirty = false;
      loaded = false;
      saveCooldown = 0;
   }

   static {
      for (SiftLandmarkTracker.Kind kind : SiftLandmarkTracker.Kind.values()) {
         POSITIONS.put(kind, ConcurrentHashMap.newKeySet());
         POSITIONS_BY_CHUNK.put(kind, new ConcurrentHashMap<>());
      }
   }

   private record CanyonActivity(boolean active, long validUntil) {
   }

   public static enum Kind {
      ABANDONED_MAIN_PORTAL("abandoned_main_portals"),
      SNIFFER_CAVE("sniffer_caves"),
      SOUL_CANYON_ENTRANCE("soul_canyon_entrances"),
      SOUL_CANYON_CLUSTER("soul_canyon_clusters"),
      SOUL_CANYON_MIDPOINT("soul_canyon_midpoints"),
      SOUL_CANYON_SOUL_BLOCK("soul_canyon_soul_blocks"),
      SOUL_CANYON_GOLEM_PENDING("soul_canyon_golems_pending");

      private final String storageKey;

      private Kind(String storageKey) {
         this.storageKey = storageKey;
      }

      private String storageKey() {
         return this.storageKey;
      }
   }
}
