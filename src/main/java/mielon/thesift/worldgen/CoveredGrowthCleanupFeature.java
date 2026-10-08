package mielon.thesift.worldgen;

import mielon.thesift.block.ModBlocks;
import mielon.thesift.block.SiftslateGrowthBlock;
import mielon.thesift.fluid.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public final class CoveredGrowthCleanupFeature extends Feature<NoneFeatureConfiguration> {
   public CoveredGrowthCleanupFeature() {
      super(NoneFeatureConfiguration.CODEC);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
      WorldGenLevel level = context.level();
      int minX = context.origin().getX() & -16;
      int minZ = context.origin().getZ() & -16;
      int maxY = level.getMaxBuildHeight() - 1;
      MutableBlockPos cursor = new MutableBlockPos();
      MutableBlockPos above = new MutableBlockPos();
      boolean changed = false;

      for (int x = minX; x < minX + 16; x++) {
         for (int z = minZ; z < minZ + 16; z++) {
            int surface = level.getHeight(Types.WORLD_SURFACE_WG, x, z);
            int minY = Math.max(level.getMinBuildHeight(), surface - 56);
            int columnMaxY = Math.min(maxY, surface);

            for (int y = minY; y <= columnMaxY; y++) {
               cursor.set(x, y, z);
               BlockState state = level.getBlockState(cursor);
               if (theSift$isLakePlant(state) && theSift$hasUnsupportedIchorBelow(level, cursor)) {
                  level.setBlock(cursor, Blocks.AIR.defaultBlockState(), 2);
                  changed = true;
               } else if (state.is(ModBlocks.SIFTSLATE_GROWTH) || state.is(ModBlocks.DRY_HEALTHY_SCULK_GROWTH)) {
                  above.set(x, y + 1, z);
                  BlockState coveringState = level.getBlockState(above);
                  boolean dry = state.is(ModBlocks.DRY_HEALTHY_SCULK_GROWTH) || level.getBlockState(cursor.below()).is(ModBlocks.DRY_HEALTHY_SCULK);
                  boolean ichorSnowCover = SiftslateGrowthBlock.isIchorSnowCover(coveringState);
                  if (coveringState.isCollisionShapeFullBlock(level, above) && !ichorSnowCover) {
                     level.setBlock(cursor, (dry ? ModBlocks.DRY_HEALTHY_SCULK : ModBlocks.SIFTSLATE).defaultBlockState(), 2);
                     changed = true;
                  } else if (dry && state.is(ModBlocks.SIFTSLATE_GROWTH)) {
                     level.setBlock(cursor, ModBlocks.DRY_HEALTHY_SCULK_GROWTH.defaultBlockState(), 2);
                     changed = true;
                  } else if (state.is(ModBlocks.SIFTSLATE_GROWTH) && (Boolean)state.getValue(SiftslateGrowthBlock.SNOWY) != ichorSnowCover) {
                     level.setBlock(cursor, (BlockState)state.setValue(SiftslateGrowthBlock.SNOWY, ichorSnowCover), 2);
                     changed = true;
                  }
               }
            }
         }
      }

      return changed;
   }

   private static boolean theSift$isLakePlant(BlockState state) {
      return state.is(ModBlocks.OVERGROWN_CHARD)
         || state.is(ModBlocks.OVERGROWN_STALKS)
         || state.is(ModBlocks.OVERGROWN_FRONDS)
         || state.is(ModBlocks.OVERGROWN_LOTUS)
         || state.is(ModBlocks.SUNBURST_PLANT)
         || state.is(ModBlocks.WHISPERBLOOM)
         || state.is(ModBlocks.SIFTSLATE_STALKS)
         || state.is(ModBlocks.HEALTHY_SCULK_SPROUTS)
         || state.is(ModBlocks.DRY_HEALTHY_SCULK_SPROUTS)
         || state.is(Blocks.TORCHFLOWER)
         || state.is(Blocks.TORCHFLOWER_CROP)
         || state.is(Blocks.PITCHER_CROP)
         || state.is(Blocks.PITCHER_PLANT);
   }

   private static boolean theSift$hasUnsupportedIchorBelow(WorldGenLevel level, BlockPos plantPos) {
      MutableBlockPos below = new MutableBlockPos();

      for (int offset = 1; offset <= 6; offset++) {
         below.set(plantPos.getX(), plantPos.getY() - offset, plantPos.getZ());
         BlockState state = level.getBlockState(below);
         if (!state.getFluidState().isEmpty()) {
            return state.getFluidState().getType() == ModFluids.ICHOR || state.getFluidState().getType() == ModFluids.FLOWING_ICHOR;
         }

         if (!state.isAir()) {
            return false;
         }
      }

      return false;
   }
}
