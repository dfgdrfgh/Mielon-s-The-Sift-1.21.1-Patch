package mielon.thesift.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public final class DryHealthySculkGrowthBlock extends Block {
   public DryHealthySculkGrowthBlock(Properties properties) {
      super(properties);
   }

   protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      BlockPos abovePos = pos.above();
      BlockState above = level.getBlockState(abovePos);
      if (!above.getFluidState().isEmpty() || above.isCollisionShapeFullBlock(level, abovePos) || level.getMaxLocalRawBrightness(abovePos) < 4) {
         level.setBlockAndUpdate(pos, ModBlocks.DRY_HEALTHY_SCULK.defaultBlockState());
      }
   }

   protected BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState, LevelAccessor level, BlockPos pos, BlockPos neighbourPos) {
      return direction != Direction.UP || !neighbourState.isCollisionShapeFullBlock(level, neighbourPos) && neighbourState.getFluidState().isEmpty()
         ? super.updateShape(state, direction, neighbourState, level, pos, neighbourPos)
         : ModBlocks.DRY_HEALTHY_SCULK.defaultBlockState();
   }
}
