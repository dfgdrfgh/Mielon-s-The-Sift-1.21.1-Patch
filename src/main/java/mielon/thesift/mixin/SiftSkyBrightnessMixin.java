package mielon.thesift.mixin;

import mielon.thesift.world.SiftDayNightCycle;
import mielon.thesift.world.TheSiftDimension;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Level.class})
public abstract class SiftSkyBrightnessMixin {
   @Shadow
   private int skyDarken;

   @Inject(
      method = {"updateSkyBrightness"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void theSift$skylightCycle(CallbackInfo ci) {
      Level level = (Level)(Object)this;
      if (level.dimension().equals(TheSiftDimension.LEVEL_KEY)) {
         double weather = (1.0 - level.getRainLevel(1.0F) * 5.0F / 16.0) * (1.0 - level.getThunderLevel(1.0F) * 5.0F / 16.0);
         double light = 1.0 - SiftDayNightCycle.nightBlend(level.getDayTime()) * 0.4;
         this.skyDarken = (int)((1.0 - light * weather) * 11.0);
         ci.cancel();
      }
   }
}
