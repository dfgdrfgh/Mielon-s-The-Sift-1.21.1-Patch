package mielon.thesift.block;

import mielon.thesift.particle.ModParticles;
import mielon.thesift.worldgen.SiftLandmarkTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public final class SoulBlock extends Block {
   public SoulBlock(Properties properties) {
      super(properties);
   }

   protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
      super.onPlace(state, level, pos, oldState, movedByPiston);
      if (!level.isClientSide() && !oldState.is(this)) {
         SiftLandmarkTracker.record(SiftLandmarkTracker.Kind.SOUL_CANYON_SOUL_BLOCK, pos);
      }
   }

   protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
      super.onRemove(state, level, pos, newState, movedByPiston);
      if (!state.is(newState.getBlock()) && !level.isClientSide()) {
         SiftLandmarkTracker.forget(SiftLandmarkTracker.Kind.SOUL_CANYON_SOUL_BLOCK, pos);
      }
   }

   protected void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean dropExperience) {
      super.spawnAfterBreak(state, level, pos, tool, dropExperience);
      RandomSource random = level.getRandom();
      level.sendParticles(
         ModParticles.CANYON_SOUL,
         pos.getX() - 1.75 + random.nextDouble() * 4.5,
         pos.getY() + 0.95,
         pos.getZ() - 1.75 + random.nextDouble() * 4.5,
         1,
         0.04,
         0.01,
         0.04,
         0.0
      );
   }
}
