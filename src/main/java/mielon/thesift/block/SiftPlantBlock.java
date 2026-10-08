package mielon.thesift.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class SiftPlantBlock extends BushBlock {
   protected MapCodec<? extends BushBlock> codec() {
      return simpleCodec(SiftPlantBlock::new);
   }

   public SiftPlantBlock(Properties properties) {
      super(properties);
   }

   protected SiftPlantBlock(Properties properties, boolean ignoredAllowVanillaVegetationSoil) {
      super(properties);
   }

   protected boolean mayPlaceOn(BlockState floor, BlockGetter level, BlockPos pos) {
      return floor.is(ModBlocks.SIFTSLATE)
         || floor.is(ModBlocks.SIFTSLATE_GROWTH)
         || floor.is(ModBlocks.HEALTHY_SCULK)
         || floor.is(ModBlocks.DRY_HEALTHY_SCULK)
         || floor.is(ModBlocks.DRY_HEALTHY_SCULK_GROWTH)
         || floor.is(ModBlocks.OVERGROWN_WILLOW_FOLIAGE)
         || floor.is(BlockTags.DIRT);
   }
}
