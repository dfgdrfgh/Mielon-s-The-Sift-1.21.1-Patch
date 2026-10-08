package mielon.thesift.backport.core.mixin.common.entity_movement;

import mielon.thesift.backport.common.api.extensions.entity.movement.MotionAwareEntity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({AbstractMinecart.class})
public class AbstractMinecartMixin {
   @Inject(
      method = {"tick"},
      at = {@At("TAIL")}
   )
   private void theSift$handleMinecartTick(CallbackInfo ci) {
      ((MotionAwareEntity)(Object)this).theSift$computeSpeed();
   }
}
