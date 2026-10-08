package mielon.thesift.worldgen;

import mielon.thesift.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public final class SiftDrySpikeFeature extends Feature<NoneFeatureConfiguration> {
   public SiftDrySpikeFeature() {
      super(NoneFeatureConfiguration.CODEC);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
      WorldGenLevel level = context.level();
      RandomSource random = context.random();
      BlockPos placementOrigin = context.origin();
      int centerX = (placementOrigin.getX() & -16) + 8 + random.nextInt(5) - 2;
      int centerZ = (placementOrigin.getZ() & -16) + 8 + random.nextInt(5) - 2;
      int centerY = SiftMonolithFeature.findTerrainSurface(level, centerX, centerZ);
      if (centerY < 0) {
         return false;
      } else {
         BlockPos center = new BlockPos(centerX, centerY, centerZ);
         if (!SiftFeaturePlacementGuard.intersectsPortal(center, 18) && SiftMonolithFeature.isDrySupportedSurface(level, centerX, centerY, centerZ, 6)) {
            int mainRadius = 3 + random.nextInt(2);
            if (hasStableFootprint(level, center, mainRadius + 2)
               && SiftFeaturePlacementGuard.tryReserveSolid(centerX - 18, centerZ - 18, centerX + 18, centerZ + 18)) {
               int placed = placeSpike(level, random, center, mainRadius, 19 + random.nextInt(13));
               int satellites = 2 + random.nextInt(3);

               for (int i = 0; i < satellites; i++) {
                  double angle = random.nextDouble() * Math.PI * 2.0;
                  int distance = 4 + random.nextInt(6);
                  int x = centerX + (int)Math.round(Math.cos(angle) * distance);
                  int z = centerZ + (int)Math.round(Math.sin(angle) * distance);
                  int y = SiftMonolithFeature.findTerrainSurface(level, x, z);
                  if (y >= 0) {
                     placed += placeSpike(level, random, new BlockPos(x, y, z), 2 + random.nextInt(2), 10 + random.nextInt(13));
                  }
               }

               return placed > 0;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   private static int placeSpike(WorldGenLevel level, RandomSource random, BlockPos origin, int baseRadius, int requestedHeight) {
      if (!hasStableFootprint(level, origin, baseRadius + 2)) {
         return 0;
      } else {
         int height = Math.min(requestedHeight, level.getMaxBuildHeight() - origin.getY() - 2);
         if (height < 8) {
            return 0;
         } else {
            int leanX = random.nextInt(7) - 3;
            int leanZ = random.nextInt(7) - 3;
            long salt = random.nextLong();
            int placed = placeRootFlare(level, origin, baseRadius, salt);
            MutableBlockPos cursor = new MutableBlockPos();

            for (int dy = 0; dy <= height; dy++) {
               double progress = Math.max(0.0, (double)dy / height);
               int centerX = origin.getX() + (int)Math.round(leanX * progress * progress);
               int centerZ = origin.getZ() + (int)Math.round(leanZ * progress * progress);
               double radius = Math.max(0.32, baseRadius * Math.pow(1.0 - progress, 0.72));
               int limit = Math.max(1, (int)Math.ceil(radius));

               for (int dx = -limit; dx <= limit; dx++) {
                  for (int dz = -limit; dz <= limit; dz++) {
                     double edge = hash01(centerX + dx, origin.getY() + dy, centerZ + dz, salt) - 0.5;
                     double edgeRadius = radius + edge * 0.45;
                     if (!(dx * dx + dz * dz > edgeRadius * edgeRadius)) {
                        cursor.set(centerX + dx, origin.getY() + dy, centerZ + dz);
                        if (level.ensureCanWrite(cursor) && SiftMonolithFeature.canReplace(level.getBlockState(cursor))) {
                           level.setBlock(cursor, ModBlocks.DRY_HEALTHY_SCULK.defaultBlockState(), 2);
                           placed++;
                        }
                     }
                  }
               }
            }

            return placed;
         }
      }
   }

   private static int placeRootFlare(WorldGenLevel level, BlockPos origin, int baseRadius, long salt) {
      int rootRadius = baseRadius + 2;
      int placed = 0;
      MutableBlockPos cursor = new MutableBlockPos();

      for (int dx = -rootRadius; dx <= rootRadius; dx++) {
         for (int dz = -rootRadius; dz <= rootRadius; dz++) {
            double edge = (hash01(origin.getX() + dx, origin.getY(), origin.getZ() + dz, salt ^ -3372029247567499371L) - 0.5) * 0.8;
            double edgeRadius = rootRadius + edge;
            int distanceSquared = dx * dx + dz * dz;
            if (!(distanceSquared > edgeRadius * edgeRadius)) {
               int x = origin.getX() + dx;
               int z = origin.getZ() + dz;
               int localSurface = SiftMonolithFeature.findTerrainSurface(level, x, z);
               if (localSurface >= 0 && Math.abs(localSurface - origin.getY()) <= 6) {
                  double distance = Math.sqrt(distanceSquared);
                  double centerStrength = 1.0 - Math.min(1.0, distance / rootRadius);
                  int targetTop = origin.getY() + (int)Math.round(centerStrength * 1.5);
                  if (localSurface <= targetTop + 1) {
                     for (int y = localSurface - 2; y <= targetTop; y++) {
                        cursor.set(x, y, z);
                        if (level.ensureCanWrite(cursor) && SiftMonolithFeature.canReplace(level.getBlockState(cursor))) {
                           level.setBlock(cursor, ModBlocks.DRY_HEALTHY_SCULK.defaultBlockState(), 2);
                           placed++;
                        }
                     }
                  }
               }
            }
         }
      }

      return placed;
   }

   private static boolean hasStableFootprint(WorldGenLevel level, BlockPos origin, int radius) {
      int minY = Integer.MAX_VALUE;
      int maxY = Integer.MIN_VALUE;
      int samples = 0;
      int valid = 0;

      for (int dx = -radius; dx <= radius; dx++) {
         for (int dz = -radius; dz <= radius; dz++) {
            if (dx * dx + dz * dz <= radius * radius) {
               samples++;
               int y = SiftMonolithFeature.findTerrainSurface(level, origin.getX() + dx, origin.getZ() + dz);
               if (y >= 0 && SiftMonolithFeature.isDrySupportedSurface(level, origin.getX() + dx, y, origin.getZ() + dz, 6)) {
                  valid++;
                  minY = Math.min(minY, y);
                  maxY = Math.max(maxY, y);
               }
            }
         }
      }

      return valid >= Math.ceil(samples * 0.82) && maxY - minY <= 12;
   }

   private static double hash01(int x, int y, int z, long salt) {
      long value = salt ^ x * -7046029254386353131L;
      value ^= y * -4417276706812531889L;
      value ^= z * 1609587929392839161L;
      value ^= value >>> 30;
      value *= -4658895280553007687L;
      value ^= value >>> 27;
      value *= -7723592293110705685L;
      value ^= value >>> 31;
      return (value >>> 11) * 1.110223E-16F;
   }
}
