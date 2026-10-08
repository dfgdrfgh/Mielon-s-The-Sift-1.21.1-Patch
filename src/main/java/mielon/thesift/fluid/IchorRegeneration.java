/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.LayeredCauldronBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.phys.AABB
 */
package mielon.thesift.fluid;

import mielon.thesift.block.ModBlocks;
import mielon.thesift.fluid.IchorFluid;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;

public final class IchorRegeneration {
    private static final int DURATION = 101;
    private static final int REFRESH_THRESHOLD = 51;
    private MobEffectInstance contactEffect;
    private boolean wasTouching;

    public void tick(LivingEntity entity) {
        if (entity.level().isClientSide()) {
            return;
        }
        MobEffectInstance current = entity.getEffect(MobEffects.REGENERATION);
        boolean touching = IchorRegeneration.isTouchingIchor(entity);
        if (!IchorRegeneration.isContactEffect(current)) {
            this.contactEffect = null;
        }
        if (!touching) {
            if (this.wasTouching && current != null && current == this.contactEffect) {
                IchorRegeneration.refresh(entity);
            }
            this.wasTouching = false;
            this.contactEffect = null;
            return;
        }
        this.wasTouching = true;
        if (current == null) {
            IchorRegeneration.refresh(entity);
            this.contactEffect = entity.getEffect(MobEffects.REGENERATION);
        } else if (IchorRegeneration.isContactEffect(current)) {
            this.contactEffect = current;
            if (current.getDuration() <= 51) {
                IchorRegeneration.refresh(entity);
            }
        }
    }

    private static boolean isContactEffect(MobEffectInstance effect) {
        return effect != null && effect.getAmplifier() == 0 && effect.isAmbient() && !effect.isVisible() && effect.showIcon() && effect.getDuration() > 0 && effect.getDuration() <= 101;
    }

    private static void refresh(LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 101, 0, true, false, true));
    }

    public static boolean isTouchingIchor(net.minecraft.world.entity.Entity entity) {
        return touching(entity, true);
    }

    public static boolean isInIchorFluid(net.minecraft.world.entity.Entity entity) { return touching(entity, false); }

    private static boolean touching(net.minecraft.world.entity.Entity entity, boolean cauldrons) {
        Level level = entity.level();
        AABB bounds = entity.getBoundingBox().deflate(0.001);
        for (BlockPos pos : BlockPos.betweenClosed((BlockPos)BlockPos.containing((double)bounds.minX, (double)bounds.minY, (double)bounds.minZ), (BlockPos)BlockPos.containing((double)bounds.maxX, (double)bounds.maxY, (double)bounds.maxZ))) {
            if (!level.hasChunkAt(pos)) continue;
            BlockState state = level.getBlockState(pos);
            FluidState fluid = state.getFluidState();
            if (fluid.getType() instanceof IchorFluid && bounds.minY < (double)((float)pos.getY() + fluid.getHeight((BlockGetter)level, pos))) {
                return true;
            }
            if (!cauldrons || !state.is(ModBlocks.ICHOR_CAULDRON)) continue;
            double height = (6.0 + 3.0 * (double)((Integer)state.getValue((Property)LayeredCauldronBlock.LEVEL)).intValue()) / 16.0;
            if (!bounds.intersects((double)pos.getX() + 0.125, (double)pos.getY() + 0.25, (double)pos.getZ() + 0.125, (double)pos.getX() + 0.875, (double)pos.getY() + height, (double)pos.getZ() + 0.875)) continue;
            return true;
        }
        return false;
    }
}

