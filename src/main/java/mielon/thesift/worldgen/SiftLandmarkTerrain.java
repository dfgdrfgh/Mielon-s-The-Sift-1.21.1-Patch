package mielon.thesift.worldgen;

import mielon.thesift.block.ModBlocks;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;

final class SiftLandmarkTerrain {
   private SiftLandmarkTerrain() {
   }

   static boolean isGround(BlockState state) {
      return state.is(ModBlocks.SIFTSLATE)
         || state.is(ModBlocks.COBBLED_SIFTSLATE)
         || state.is(ModBlocks.SIFT_RUBBLE)
         || state.is(ModBlocks.SIFTSLATE_GROWTH)
         || state.is(ModBlocks.HEALTHY_SCULK)
         || state.is(ModBlocks.DRY_HEALTHY_SCULK)
         || state.is(ModBlocks.DRY_HEALTHY_SCULK_GROWTH)
         || state.is(ModBlocks.SIFTSLATE_COAL_ORE)
         || state.is(ModBlocks.SIFTSLATE_DIAMOND_ORE)
         || state.is(ModBlocks.SIFTSLATE_EMERALD_ORE)
         || state.is(ModBlocks.SIFTSLATE_CHAROITE_ORE)
         || state.is(ModBlocks.SIFTSLATE_SIFTITE_ORE)
         || state.is(ModBlocks.ICHOR_SNOW_BLOCK)
         || state.is(Blocks.SCULK)
         || state.is(Blocks.BONE_BLOCK);
   }

   static boolean canReplace(BlockState state) {
      return state.isAir()
         || isGround(state)
         || state.getFluidState().isEmpty() && state.canBeReplaced()
         || state.is(ModBlocks.ICHOR_SNOW)
         || state.is(Blocks.SCULK_VEIN)
         || state.is(Blocks.SCULK_SENSOR)
         || state.is(Blocks.SCULK_SHRIEKER)
         || state.is(Blocks.SCULK_CATALYST)
         || state.is(ModBlocks.OVERGROWN_FRONDS)
         || state.is(ModBlocks.OVERGROWN_CHARD)
         || state.is(ModBlocks.OVERGROWN_STALKS)
         || state.is(ModBlocks.SIFTSLATE_STALKS)
         || state.is(ModBlocks.HEALTHY_SCULK_SPROUTS)
         || state.is(ModBlocks.DRY_HEALTHY_SCULK_SPROUTS)
         || state.is(ModBlocks.SIFTSLATE_HANGING_ROOTS)
         || state.is(ModBlocks.OVERGROWN_HANGING_ROOTS);
   }

   static int surfaceBlockY(WorldGenLevel level, int x, int z) {
      int top = level.getHeight(Types.WORLD_SURFACE_WG, x, z) - 1;
      int minimum = Math.max(level.getMinBuildHeight() + 1, top - 32);
      MutableBlockPos cursor = new MutableBlockPos(x, top, z);

      for (int y = top; y >= minimum; y--) {
         cursor.setY(y);
         BlockState state = level.getBlockState(cursor);
         if (isGround(state)) {
            return y;
         }

         if (!canReplace(state)) {
            return Integer.MIN_VALUE;
         }
      }

      return Integer.MIN_VALUE;
   }
}
