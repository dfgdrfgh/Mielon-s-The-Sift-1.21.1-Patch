/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.tags.FluidTags
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.LiquidBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.LavaFluid
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package mielon.thesift.mixin;

import mielon.thesift.block.ModBlocks;
import mielon.thesift.fluid.IchorWaterlogging;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.LavaFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public final class LavaIchorMixins {
    private LavaIchorMixins() {
    }

    @Mixin(value={LavaFluid.class})
    public static abstract class Flow {
        @Inject(method={"spreadTo"}, at={@At(value="HEAD")}, cancellable=true)
        private void theSift$solidifyOnFlow(LevelAccessor level, BlockPos pos, BlockState state, Direction direction, FluidState fluid, CallbackInfo ci) {
            if (direction != Direction.DOWN || !(state.getBlock() instanceof LiquidBlock) || !IchorWaterlogging.isIchor(level.getFluidState(pos).getType())) {
                return;
            }
            level.setBlock(pos, ModBlocks.SIFTSLATE.defaultBlockState(), 3);
            level.levelEvent(1501, pos, 0);
            ci.cancel();
        }
    }

    @Mixin(value={LiquidBlock.class})
    public static abstract class DownwardContact {
        @Inject(method={"onPlace"}, at={@At(value="TAIL")})
        private void theSift$solidifyIchorBelow(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston, CallbackInfo ci) {
            BlockPos ichorPos;
            if (level.isClientSide()) {
                return;
            }
            if (state.is(Blocks.LAVA) && level.getBlockState(pos).is(Blocks.LAVA)) {
                ichorPos = pos.below();
            } else if (IchorWaterlogging.isIchor(state.getFluidState().getType()) && level.getFluidState(pos.above()).is(FluidTags.LAVA)) {
                ichorPos = pos;
            } else {
                return;
            }
            BlockState ichorState = level.getBlockState(ichorPos);
            if (!(ichorState.getBlock() instanceof LiquidBlock) || !IchorWaterlogging.isIchor(ichorState.getFluidState().getType())) {
                return;
            }
            level.setBlock(ichorPos, ModBlocks.SIFTSLATE.defaultBlockState(), 3);
            level.levelEvent(1501, ichorPos, 0);
        }
    }

    @Mixin(value={LiquidBlock.class})
    public static abstract class Contact {
        @Inject(method={"shouldSpreadLiquid"}, at={@At(value="HEAD")}, cancellable=true)
        private void theSift$solidifyLava(Level level, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
            if (!state.is(Blocks.LAVA)) {
                return;
            }
            boolean touchesIchor = false;
            boolean touchesWater = false;
            for (Direction direction : LiquidBlock.POSSIBLE_FLOW_DIRECTIONS) {
                FluidState neighbor = level.getFluidState(pos.relative(direction.getOpposite()));
                if (IchorWaterlogging.isIchor(neighbor.getType())) {
                    touchesIchor = true;
                    continue;
                }
                if (!neighbor.is(FluidTags.WATER)) continue;
                touchesWater = true;
            }
            if (touchesWater || !touchesIchor) {
                return;
            }
            BlockState result = level.getFluidState(pos).isSource() ? Blocks.OBSIDIAN.defaultBlockState() : ModBlocks.COBBLED_SIFTSLATE.defaultBlockState();
            level.setBlockAndUpdate(pos, result);
            level.levelEvent(1501, pos, 0);
            cir.setReturnValue(false);
        }
    }
}

