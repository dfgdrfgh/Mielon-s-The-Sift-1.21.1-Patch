package mielon.thesift.client.mixin;

import mielon.thesift.client.render.SiftProceduralSkyRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LevelRenderer.class})
public abstract class SiftSkyRendererMixin {
   @Inject(
      method = {"close"},
      at = {@At("TAIL")}
   )
   private void theSift$closeSky(CallbackInfo ci) {
      SiftProceduralSkyRenderer.close();
   }
}
