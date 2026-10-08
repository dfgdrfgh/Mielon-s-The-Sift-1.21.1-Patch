package mielon.thesift.client.mixin;

import mielon.thesift.portal.SonorousColors;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoteParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({NoteParticle.class})
public abstract class NoteParticleMixin {
   private static final double THE_SIFT_NOTE_MARKER = 100.0;

   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   private void theSift$applySonorousColor(ClientLevel level, double x, double y, double z, double color, CallbackInfo ci) {
      if (!(color < 100.0)) {
         int encoded = (int)Math.round(color - 100.0);
         if (encoded >= 0 && encoded <= 23) {
            int soundIndex = encoded / 3 + 1;
            int shadeIndex = encoded % 3;
            if (soundIndex >= 1 && soundIndex <= 8) {
               if (shadeIndex >= 0 && shadeIndex <= 2) {
                  int[] shades = SonorousColors.shades(soundIndex);
                  int rgb = shades[shadeIndex];
                  float red = (rgb >> 16 & 0xFF) / 255.0F;
                  float green = (rgb >> 8 & 0xFF) / 255.0F;
                  float blue = (rgb & 0xFF) / 255.0F;
                  ((NoteParticle)(Object)this).setColor(red, green, blue);
               }
            }
         }
      }
   }
}
