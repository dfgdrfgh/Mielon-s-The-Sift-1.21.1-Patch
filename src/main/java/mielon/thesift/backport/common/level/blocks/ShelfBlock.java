package mielon.thesift.backport.common.level.blocks;

import mielon.thesift.backport.client.registries.ModSoundEvents;
import mielon.thesift.backport.common.level.block_entities.ShelfBlockEntity;


import mielon.thesift.backport.core.util.BlockShaper;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;


public class ShelfBlock extends BaseEntityBlock implements SelectableSlotContainer, SideChainPartBlock, SimpleWaterloggedBlock {
   public static final MapCodec<ShelfBlock> CODEC = simpleCodec(ShelfBlock::new);
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final EnumProperty<SideChainPart> SIDE_CHAIN_PART = EnumProperty.create("side_chain",SideChainPart.class);
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   private static final Map<Direction, VoxelShape> SHAPES = BlockShaper.rotateHorizontal(
      Shapes.or(
         Block.box(0.0, 12.0, 11.0, 16.0, 16.0, 13.0),
         new VoxelShape[]{Block.box(0.0, 0.0, 13.0, 16.0, 16.0, 16.0), Block.box(0.0, 0.0, 11.0, 16.0, 4.0, 13.0)}
      )
   );

   protected MapCodec<ShelfBlock> codec() {
      return CODEC;
   }

   public ShelfBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)(Object)this.stateDefinition.any()).setValue(FACING, Direction.NORTH))
                  .setValue(POWERED, false))
               .setValue(SIDE_CHAIN_PART, SideChainPart.UNCONNECTED))
            .setValue(WATERLOGGED, false)
      );
   }

   protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPES.get(state.getValue(FACING));
   }

   protected boolean useShapeForLightOcclusion(BlockState state) {
      return true;
   }

   protected boolean isPathfindable(BlockState state, PathComputationType type) {
      return type == PathComputationType.WATER && state.getFluidState().is(FluidTags.WATER);
   }

   
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new ShelfBlockEntity(pos, state);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, POWERED, SIDE_CHAIN_PART, WATERLOGGED});
   }

   protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
      Containers.dropContentsOnDestroy(state, newState, level, pos);
      if (!state.is(newState.getBlock())) {
         super.onRemove(state, level, pos, newState, movedByPiston);
         level.updateNeighbourForOutputSignal(pos, state.getBlock());
         this.updateNeighborsAfterPoweringDown(level, pos, state);
      }
   }

   protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
      if (!level.isClientSide()) {
         boolean signal = level.hasNeighborSignal(pos);
         if ((Boolean)state.getValue(POWERED) != signal) {
            BlockState newState = (BlockState)state.setValue(POWERED, signal);
            if (!signal) {
               newState = (BlockState)newState.setValue(SIDE_CHAIN_PART, SideChainPart.UNCONNECTED);
            }

            level.setBlock(pos, newState, 3);
            this.playSound(level, pos, signal ? ModSoundEvents.SHELF_ACTIVATE.get() : ModSoundEvents.SHELF_DEACTIVATE.get());
            level.gameEvent(signal ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, pos, Context.of(newState));
         }
      }
   }

   
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      FluidState replacedFluidState = context.getLevel().getFluidState(context.getClickedPos());
      return (BlockState)((BlockState)((BlockState)(Object)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()))
            .setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos())))
         .setValue(WATERLOGGED, replacedFluidState.is(Fluids.WATER));
   }

   protected BlockState rotate(BlockState state, Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   protected BlockState mirror(BlockState state, Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   protected RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   @Override
   public int getRows() {
      return 1;
   }

   @Override
   public int getColumns() {
      return 3;
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      if (level.getBlockEntity(pos) instanceof ShelfBlockEntity shelf && !hand.equals(InteractionHand.OFF_HAND)) {
         OptionalInt hitSlot = this.getHitSlot(hitResult, (Direction)state.getValue(FACING));
         if (hitSlot.isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
         } else {
            Inventory inventory = player.getInventory();
            if (level.isClientSide()) {
               return inventory.getSelected().isEmpty() ? ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION : ItemInteractionResult.SUCCESS;
            } else if (!(Boolean)state.getValue(POWERED)) {
               boolean itemRemoved = this.swapSingleItem(stack, player, shelf, hitSlot.getAsInt(), inventory);
               if (itemRemoved) {
                  this.playSound(level, pos, stack.isEmpty() ? ModSoundEvents.SHELF_TAKE_ITEM.get() : ModSoundEvents.SHELF_SINGLE_SWAP.get());
               } else {
                  if (stack.isEmpty()) {
                     return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                  }

                  this.playSound(level, pos, ModSoundEvents.SHELF_PLACE_ITEM.get());
               }

               return ItemInteractionResult.SUCCESS;
            } else {
               boolean anySwapped = this.swapHotbar(level, pos, inventory);
               if (!anySwapped) {
                  return ItemInteractionResult.CONSUME;
               } else {
                  this.playSound(level, pos, ModSoundEvents.SHELF_MULTI_SWAP.get());
                  return ItemInteractionResult.SUCCESS;
               }
            }
         }
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   private boolean swapSingleItem(ItemStack stack, Player player, ShelfBlockEntity shelf, int hitSlot, Inventory inventory) {
      ItemStack removedItem = shelf.swapItemNoUpdate(hitSlot, stack);
      ItemStack newInventoryItem = player.hasInfiniteMaterials() && removedItem.isEmpty() ? stack.copy() : removedItem;
      inventory.setItem(inventory.selected, newInventoryItem);
      inventory.setChanged();
      shelf.setChanged(GameEvent.ITEM_INTERACT_FINISH);
      return !removedItem.isEmpty();
   }

   private boolean swapHotbar(Level level, BlockPos pos, Inventory inventory) {
      List<BlockPos> connectedBlocks = this.getAllBlocksConnectedTo(level, pos);
      if (connectedBlocks.isEmpty()) {
         return false;
      } else {
         boolean anySwapped = false;

         for (int shelfPartIndex = 0; shelfPartIndex < connectedBlocks.size(); shelfPartIndex++) {
            ShelfBlockEntity shelfPart = (ShelfBlockEntity)level.getBlockEntity(connectedBlocks.get(shelfPartIndex));
            if (shelfPart != null) {
               for (int slot = 0; slot < shelfPart.getContainerSize(); slot++) {
                  int inventorySlot = 9 - (connectedBlocks.size() - shelfPartIndex) * shelfPart.getContainerSize() + slot;
                  if (inventorySlot >= 0 && inventorySlot <= inventory.getContainerSize()) {
                     ItemStack placedInventoryItem = inventory.removeItemNoUpdate(inventorySlot);
                     ItemStack removedShelfItem = shelfPart.swapItemNoUpdate(slot, placedInventoryItem);
                     if (!placedInventoryItem.isEmpty() || !removedShelfItem.isEmpty()) {
                        inventory.setItem(inventorySlot, removedShelfItem);
                        anySwapped = true;
                     }
                  }
               }

               inventory.setChanged();
               shelfPart.setChanged(GameEvent.ENTITY_INTERACT);
            }
         }

         return anySwapped;
      }
   }

   @Override
   public SideChainPart getSideChainPart(BlockState state) {
      return (SideChainPart)state.getValue(SIDE_CHAIN_PART);
   }

   @Override
   public BlockState setSideChainPart(BlockState state, SideChainPart newPart) {
      return (BlockState)state.setValue(SIDE_CHAIN_PART, newPart);
   }

   @Override
   public Direction getFacing(BlockState state) {
      return (Direction)state.getValue(FACING);
   }

   @Override
   public boolean isConnectable(BlockState state) {
      return state.getBlock() instanceof ShelfBlock && state.hasProperty(POWERED) && (Boolean)state.getValue(POWERED);
   }

   @Override
   public int getMaxChainLength() {
      return 3;
   }

   protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
      if ((Boolean)state.getValue(POWERED)) {
         this.updateSelfAndNeighborsOnPoweringUp(level, pos, state, oldState);
      } else {
         this.updateNeighborsAfterPoweringDown(level, pos, state);
      }
   }

   private void playSound(LevelAccessor level, BlockPos pos, SoundEvent sound) {
      level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
   }

   protected FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
   }

   protected boolean hasAnalogOutputSignal(BlockState state) {
      return true;
   }

   protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
      if (level.isClientSide()) {
         return 0;
      } else if (level.getBlockEntity(pos) instanceof ShelfBlockEntity shelf) {
         int item1Bit = shelf.getItem(0).isEmpty() ? 0 : 1;
         int item2Bit = shelf.getItem(1).isEmpty() ? 0 : 1;
         int item3Bit = shelf.getItem(2).isEmpty() ? 0 : 1;
         return item1Bit | item2Bit << 1 | item3Bit << 2;
      } else {
         return 0;
      }
   }
}
