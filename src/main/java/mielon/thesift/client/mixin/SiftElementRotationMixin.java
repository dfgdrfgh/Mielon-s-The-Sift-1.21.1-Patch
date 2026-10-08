package mielon.thesift.client.mixin;

import mielon.thesift.client.render.SiftEulerRotation;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({BlockElementRotation.class})
public abstract class SiftElementRotationMixin implements SiftEulerRotation {
   @Unique
   private Quaternionf theSift$euler;

   @Override
   public Quaternionf theSift$getEuler() {
      return this.theSift$euler;
   }

   @Override
   public void theSift$setEuler(Quaternionf rotation) {
      this.theSift$euler = rotation;
   }
}
