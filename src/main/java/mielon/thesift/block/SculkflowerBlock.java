package mielon.thesift.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class SculkflowerBlock extends BushBlock {
   public static final MapCodec<SculkflowerBlock> CODEC = simpleCodec(SculkflowerBlock::new);

   public SculkflowerBlock(Properties properties) {
      super(properties);
   }

   protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return Block.box(2.0, 0.0, 2.0, 14.0, 10.0, 14.0);
   }

   public MapCodec<? extends SculkflowerBlock> codec() {
      return CODEC;
   }

   protected boolean mayPlaceOn(BlockState floor, BlockGetter level, BlockPos floorPos) {
      return floor.is(Blocks.FARMLAND)
         || floor.is(Blocks.SCULK)
         || floor.is(ModBlocks.HEALTHY_SCULK)
         || floor.is(ModBlocks.DRY_HEALTHY_SCULK)
         || floor.isFaceSturdy(level, floorPos, Direction.UP);
   }
}
