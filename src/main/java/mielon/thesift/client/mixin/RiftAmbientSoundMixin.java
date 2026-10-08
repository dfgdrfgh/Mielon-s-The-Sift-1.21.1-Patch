package mielon.thesift.client.mixin;

import mielon.thesift.client.sound.RiftAmbientSound;
import mielon.thesift.entity.RiftEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Entity.class})
public abstract class RiftAmbientSoundMixin {
   @Unique
   private RiftAmbientSound theSift$ambientSound;

   @Inject(
      method = {"tick"},
      at = {@At("TAIL")}
   )
   private void theSift$tickAmbientSound(CallbackInfo ci) {
      if ((Object)this instanceof RiftEntity rift) {
         if (rift.level().isClientSide && !rift.isRemoved() && !rift.isSilent()) {
            Minecraft client = Minecraft.getInstance();
            Entity camera = client.getCameraEntity();
            if (camera != null && camera.level() == rift.level() && !(camera.distanceToSqr(rift) > 1024.0)) {
               if (this.theSift$ambientSound == null || rift.tickCount % 20 == 0) {
                  if (this.theSift$ambientSound == null
                     || this.theSift$ambientSound.isStopped()
                     || !client.getSoundManager().isActive(this.theSift$ambientSound)) {
                     this.theSift$ambientSound = new RiftAmbientSound(rift);
                     client.getSoundManager().play(this.theSift$ambientSound);
                  }
               }
            }
         }
      }
   }
}
