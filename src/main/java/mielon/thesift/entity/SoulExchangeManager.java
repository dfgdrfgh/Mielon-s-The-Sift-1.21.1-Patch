package mielon.thesift.entity;

import java.util.Comparator;
import mielon.thesift.worldgen.SiftLandmarkTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;

public final class SoulExchangeManager {
   private SoulExchangeManager() {
   }

   public static SingerEntity requestSinger(ServerLevel level, EchoGolemEntity golem) {
      if (golem.hasSoulBlock() && golem.canInteractWithSinger()) {
         AABB search = golem.getBoundingBox().inflate(52.0, 32.0, 52.0);
         SingerEntity existing = level.getEntitiesOfClass(SingerEntity.class, search, SingerEntity::isSoulEvent)
            .stream()
            .min(Comparator.comparingDouble(golem::distanceToSqr))
            .orElse(null);
         if (existing != null) {
            if (!golem.tryAcquirePathBudget(level, 6)) {
               return null;
            }

            Path path = golem.getNavigation().createPath(existing.blockPosition(), 2);
            if (path == null || !path.canReach()) {
               existing = null;
            }
         }

         if (existing != null) {
            existing.offerSoulGolem(golem);
            return existing;
         } else {
            BlockPos spawn = findNaturalSpawn(level, golem.blockPosition());
            if (spawn == null) {
               return null;
            } else {
               SingerEntity singer = (SingerEntity)ModEntities.SINGER.create(level);
               if (singer == null) {
                  return null;
               } else {
                  singer.moveTo(spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5, level.getRandom().nextFloat() * 360.0F, 0.0F);
                  singer.beginSoulEvent(golem.getUUID());
                  singer.setPersistenceRequired();
                  if (!level.addFreshEntity(singer)) {
                     singer.discard();
                     return null;
                  } else {
                     return singer;
                  }
               }
            }
         }
      } else {
         return null;
      }
   }

   private static BlockPos findNaturalSpawn(ServerLevel level, BlockPos around) {
      BlockPos activeCanyon = SiftLandmarkTracker.nearestActiveSoulCanyon(level, around, 48.0).orElse(null);
      double phase = level.getRandom().nextDouble() * Math.PI * 2.0;

      for (int attempt = 0; attempt < 16; attempt++) {
         double angle = phase + attempt * (Math.PI / 8);
         int radius = 7 + level.getRandom().nextInt(6);
         int x = around.getX() + (int)Math.round(Math.cos(angle) * radius);
         int z = around.getZ() + (int)Math.round(Math.sin(angle) * radius);
         int y = level.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, x, z);
         BlockPos feet = new BlockPos(x, y, z);
         if ((activeCanyon == null || !(horizontalDistanceSqr(feet, activeCanyon) < 576.0))
            && level.isLoaded(feet)
            && level.getBlockState(feet.below()).isCollisionShapeFullBlock(level, feet.below())) {
            boolean clear = true;

            for (int dy = 0; dy <= 4; dy++) {
               if (!level.getBlockState(feet.above(dy)).isAir()) {
                  clear = false;
                  break;
               }
            }

            if (clear) {
               return feet;
            }
         }
      }

      return null;
   }

   private static double horizontalDistanceSqr(BlockPos first, BlockPos second) {
      double dx = first.getX() - second.getX();
      double dz = first.getZ() - second.getZ();
      return dx * dx + dz * dz;
   }
}
