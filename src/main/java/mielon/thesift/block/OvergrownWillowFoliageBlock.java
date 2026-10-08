package mielon.thesift.block;

import mielon.thesift.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public final class OvergrownWillowFoliageBlock extends LeavesBlock {
   private final float particleChance;

   public OvergrownWillowFoliageBlock(float particleChance, Properties properties) {
      super(properties);
      this.particleChance = particleChance;
   }

   public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
      super.animateTick(state, level, pos, random);
      if (random.nextFloat() < this.particleChance && !level.getBlockState(pos.below()).isCollisionShapeFullBlock(level, pos.below())) {
         level.addParticle(ModParticles.WILLOW_LEAF, pos.getX() + random.nextDouble(), pos.getY() - 0.05, pos.getZ() + random.nextDouble(), 0.0, 0.0, 0.0);
      }
   }
}
