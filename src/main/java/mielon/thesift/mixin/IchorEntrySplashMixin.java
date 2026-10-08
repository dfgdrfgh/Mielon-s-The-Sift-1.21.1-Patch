/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.vehicle.Boat
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package mielon.thesift.mixin;

import mielon.thesift.fluid.ModFluids;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Entity.class})
public abstract class IchorEntrySplashMixin {
    @Shadow
    protected boolean firstTick;
    @Unique
    private boolean theSift$touchingIchor;
    @Unique
    private boolean theSift$vanillaSplashThisUpdate;
    @Unique
    private boolean theSift$vanillaSwimSoundThisTick;
    @Unique
    private int theSift$lastFallbackSwimSoundTick = -1000000;

    @Shadow
    protected abstract void doWaterSplashEffect();

    @Shadow
    protected abstract void playSwimSound(float var1);

    @Inject(method={"updateInWaterStateAndDoFluidPushing"}, at={@At(value="HEAD")})
    private void theSift$beginFluidUpdate(CallbackInfoReturnable<Boolean> cir) {
        this.theSift$vanillaSplashThisUpdate = false;
    }

    @Inject(method={"doWaterSplashEffect"}, at={@At(value="HEAD")})
    private void theSift$rememberVanillaSplash(CallbackInfo ci) {
        this.theSift$vanillaSplashThisUpdate = true;
    }

    @Inject(method={"playSwimSound"}, at={@At(value="HEAD")})
    private void theSift$rememberVanillaSwimSound(float volume, CallbackInfo ci) {
        this.theSift$vanillaSwimSoundThisTick = true;
    }

    @Inject(method={"tick"}, at={@At(value="HEAD")})
    private void theSift$beginEntityTick(CallbackInfo ci) {
        this.theSift$vanillaSwimSoundThisTick = false;
    }

    @Inject(method={"updateInWaterStateAndDoFluidPushing"}, at={@At(value="RETURN")})
    private void theSift$restoreIchorWaterSounds(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity)(Object)this;
        boolean touching = mielon.thesift.fluid.IchorRegeneration.isTouchingIchor(entity);
        boolean inBoat = entity.getVehicle() instanceof Boat;
        if (!(!touching || this.theSift$touchingIchor || this.firstTick || inBoat || this.theSift$vanillaSplashThisUpdate)) {
            this.doWaterSplashEffect();
        }
        if (touching && !inBoat && !this.theSift$vanillaSwimSoundThisTick && entity.tickCount - this.theSift$lastFallbackSwimSoundTick >= 8 && entity.getDeltaMovement().lengthSqr() > 0.0025) {
            float speed = (float)Math.min(1.0, Math.sqrt(entity.getDeltaMovement().lengthSqr()) * 1.8);
            this.playSwimSound(0.18f + speed * 0.32f);
            this.theSift$lastFallbackSwimSoundTick = entity.tickCount;
        }
        this.theSift$touchingIchor = touching;
    }
}

