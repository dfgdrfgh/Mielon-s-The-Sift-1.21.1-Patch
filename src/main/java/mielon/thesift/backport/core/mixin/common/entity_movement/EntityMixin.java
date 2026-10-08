package mielon.thesift.backport.core.mixin.common.entity_movement;

import mielon.thesift.backport.common.api.extensions.entity.movement.MotionAwareEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Entity.class})
public abstract class EntityMixin implements MotionAwareEntity {
   @Unique
   private Vec3 theSift$lastKnownSpeed = Vec3.ZERO;
   @Unique
   
   private Vec3 theSift$lastKnownPosition;

   @Shadow
   public abstract Vec3 position();

   @Shadow
   
   public abstract LivingEntity getControllingPassenger();

   @Shadow
   public abstract boolean isAlive();

   @Shadow
   public abstract Vec3 getDeltaMovement();

   @Inject(
      method = {"reapplyPosition"},
      at = {@At("TAIL")}
   )
   private void theSift$reapplyPosition(CallbackInfo ci) {
      this.theSift$lastKnownPosition = null;
   }

   @Inject(
      method = {"baseTick"},
      at = {@At("TAIL")}
   )
   private void theSift$baseTick(CallbackInfo ci) {
      this.theSift$computeSpeed();
   }

   @Override
   public void theSift$computeSpeed() {
      if (this.theSift$lastKnownPosition == null) {
         this.theSift$lastKnownPosition = this.position();
      }

      this.theSift$lastKnownSpeed = this.position().subtract(this.theSift$lastKnownPosition);
      this.theSift$lastKnownPosition = this.position();
   }

   @Override
   public Vec3 theSift$getKnownSpeed() {
      return this.getControllingPassenger() instanceof Player player && this.isAlive() ? ((MotionAwareEntity)player).theSift$getKnownSpeed() : this.theSift$lastKnownSpeed;
   }
}
