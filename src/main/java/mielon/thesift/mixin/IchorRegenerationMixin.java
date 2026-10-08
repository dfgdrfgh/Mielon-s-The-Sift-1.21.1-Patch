package mielon.thesift.mixin;

import mielon.thesift.fluid.IchorRegeneration;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LivingEntity.class})
public abstract class IchorRegenerationMixin {
   @Unique
   private final IchorRegeneration theSift$ichorRegeneration = new IchorRegeneration();

   @Inject(
      method = {"baseTick"},
      at = {@At("HEAD")}
   )
   private void theSift$updateIchorRegeneration(CallbackInfo ci) {
      this.theSift$ichorRegeneration.tick((LivingEntity)(Object)this);
   }
}
