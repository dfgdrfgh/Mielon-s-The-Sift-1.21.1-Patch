package mielon.thesift.mixin;

import mielon.thesift.world.SiftDayNightCycle;
import mielon.thesift.world.TheSiftDimension;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelTimeAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LevelTimeAccess.class})
public interface SiftSunAngleMixin {
   @Inject(
      method = {"getTimeOfDay"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void theSift$fixedDayAndNightAngles(float partial, CallbackInfoReturnable<Float> cir) {
      if ((Object)this instanceof Level level && level.dimension().equals(TheSiftDimension.LEVEL_KEY)) {
         cir.setReturnValue(0.125F + 0.5F * SiftDayNightCycle.nightBlend(level.getDayTime()));
      }
   }
}
