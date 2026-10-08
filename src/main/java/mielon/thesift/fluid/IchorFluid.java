/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.LiquidBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.WaterFluid
 *  net.neoforged.neoforge.fluids.FluidType
 */
package mielon.thesift.fluid;

import java.util.Optional;
import mielon.thesift.fluid.IchorWaterlogging;
import mielon.thesift.fluid.ModFluids;
import mielon.thesift.particle.ModParticles;
import mielon.thesift.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.WaterFluid;


public abstract class IchorFluid
extends WaterFluid {
    public Fluid getFlowing() {
        return ModFluids.FLOWING_ICHOR;
    }

    public Fluid getSource() {
        return ModFluids.ICHOR;
    }

    public Item getBucket() {
        return ModFluids.ICHOR_BUCKET;
    }

    public Optional<SoundEvent> getPickupSound() {
        return Optional.of(ModSounds.ICHOR_BUCKET_FILL);
    }

    protected void spreadTo(LevelAccessor level, BlockPos pos, BlockState state, Direction direction, FluidState fluid) {
        if (IchorWaterlogging.canIchorlog(state)) {
            IchorWaterlogging.fill(level, pos, state);
            return;
        }
        super.spreadTo(level, pos, state, direction, fluid);
    }

    public BlockState createLegacyBlock(FluidState state) {
        return (BlockState)ModFluids.ICHOR_BLOCK.defaultBlockState().setValue((Property)LiquidBlock.LEVEL, (Comparable)Integer.valueOf(IchorFluid.getLegacyLevel((FluidState)state)));
    }

    public boolean isSame(Fluid fluid) {
        return fluid == ModFluids.ICHOR || fluid == ModFluids.FLOWING_ICHOR;
    }

    protected boolean canConvertToSource(Level level) {
        return false;
    }

    public int getSlopeFindDistance(LevelReader level) {
        return 6;
    }

    protected FluidState getNewLiquid(Level level, BlockPos pos, BlockState state) {
        FluidState next = super.getNewLiquid(level, pos, state);
        if (!next.isEmpty() && next.getType().isSame((Fluid)this) && !next.isSource() && !((Boolean)next.getValue((Property)FALLING)).booleanValue() && next.getAmount() > 6) {
            return this.getFlowing(6, false);
        }
        return next;
    }

    public int getDropOff(LevelReader level) {
        return 1;
    }

    public int getTickDelay(LevelReader level) {
        return 7;
    }

    public void animateTick(Level level, BlockPos pos, FluidState state, RandomSource random) {
        super.animateTick(level, pos, state, random);
        if (state.isSource() && level.getBlockState(pos.above()).isAir() && random.nextInt(8) == 0) {
            double surfaceY = (double)((float)pos.getY() + state.getHeight((BlockGetter)level, pos)) + 0.00625;
            level.addParticle((ParticleOptions)ModParticles.ICHOR_SURFACE_MIST, (double)pos.getX() + 0.1 + random.nextDouble() * 0.8, surfaceY, (double)pos.getZ() + 0.1 + random.nextDouble() * 0.8, 0.0, 0.0, 0.0);
        }
    }

    public static void applyMovementDrag(Entity entity) {
        entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.92, 0.94, 0.92));
    }



    public static final class Source
    extends IchorFluid {
        public int getAmount(FluidState state) {
            return 8;
        }

        public boolean isSource(FluidState state) {
            return true;
        }
    }

    public static final class Flowing
    extends IchorFluid {
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(new Property[]{LEVEL});
        }

        public int getAmount(FluidState state) {
            return (Integer)state.getValue((Property)LEVEL);
        }

        public boolean isSource(FluidState state) {
            return false;
        }
    }
}

