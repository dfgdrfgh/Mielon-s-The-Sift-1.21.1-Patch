package mielon.thesift.worldgen;

import mielon.thesift.block.ModBlocks;
import mielon.thesift.world.TheSiftDimension;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public final class SiftFlowerPatchFeature extends Feature<NoneFeatureConfiguration> {
   public SiftFlowerPatchFeature() {
      super(NoneFeatureConfiguration.CODEC);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
      WorldGenLevel level = context.level();
      RandomSource random = context.random();
      int centerX = (context.origin().getX() & -16) + 8;
      int centerZ = (context.origin().getZ() & -16) + 8;
      int target = 3 + random.nextInt(2);
      int placed = 0;
      int surfaceY = level.getHeight(Types.WORLD_SURFACE_WG, centerX, centerZ);
      boolean overgrown = TheSiftDimension.isOvergrownBiome(level.getBiome(new BlockPos(centerX, surfaceY, centerZ)));
      int selection = random.nextInt(overgrown ? 6 : 24);
      if (selection < 3) {
         return false;
      } else {
         Block flower = switch (selection % 3) {
            case 0 -> ModBlocks.OVERGROWN_LOTUS;
            case 1 -> ModBlocks.SUNBURST_PLANT;
            default -> ModBlocks.WHISPERBLOOM;
         };

         for (int attempt = 0; attempt < 24 && placed < target; attempt++) {
            int x = centerX + random.nextInt(7) - 3;
            int z = centerZ + random.nextInt(7) - 3;
            int y = level.getHeight(Types.WORLD_SURFACE_WG, x, z);
            BlockPos plantPos = new BlockPos(x, y, z);
            BlockState floor = level.getBlockState(plantPos.below());
            if (isFlowerSoil(floor) && level.isEmptyBlock(plantPos) && level.ensureCanWrite(plantPos)) {
               BlockState flowerState = flower.defaultBlockState();
               if (flowerState.canSurvive(level, plantPos)) {
                  level.setBlock(plantPos, flowerState, 2);
                  placed++;
               }
            }
         }

         return placed > 0;
      }
   }

   static boolean isFlowerSoil(BlockState floor) {
      return floor.is(ModBlocks.SIFTSLATE)
         || floor.is(ModBlocks.SIFTSLATE_GROWTH)
         || floor.is(ModBlocks.DRY_HEALTHY_SCULK)
         || floor.is(ModBlocks.HEALTHY_SCULK);
   }

   static boolean isLakeFlowerSoil(BlockState floor) {
      return isFlowerSoil(floor) || floor.is(ModBlocks.DRY_HEALTHY_SCULK_GROWTH);
   }
}
