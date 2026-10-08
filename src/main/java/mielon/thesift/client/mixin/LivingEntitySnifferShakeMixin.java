package mielon.thesift.client.mixin;

import mielon.thesift.entity.SnifferInfectionAccess;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LivingEntityRenderer.class})
public abstract class LivingEntitySnifferShakeMixin {
   @Inject(
      method = {"isShaking"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void theSift$shakeInfectedSniffer(LivingEntity state, CallbackInfoReturnable<Boolean> cir) {
      if (state instanceof SnifferInfectionAccess infectionState && infectionState.theSift$isSculkInfected()) {
         cir.setReturnValue(true);
      }
   }
}
