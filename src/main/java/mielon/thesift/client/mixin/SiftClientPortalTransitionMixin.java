package mielon.thesift.client.mixin;

import mielon.thesift.client.loading.SiftClientPortalTransition;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Minecraft.class})
public abstract class SiftClientPortalTransitionMixin {
   @Inject(
      method = {"tick"},
      at = {@At("TAIL")}
   )
   private void theSift$finishLoadingTransition(CallbackInfo ci) {
      SiftClientPortalTransition.tick();
   }
}
