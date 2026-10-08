package mielon.thesift.client.mixin;

import mielon.thesift.client.render.SiftEulerRotation;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.client.renderer.block.model.FaceBakery;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({FaceBakery.class})
public abstract class SiftFaceRotationMixin {
   @Inject(
      method = {"applyElementRotation"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void theSift$rotateEuler(Vector3f vertex, BlockElementRotation rotation, CallbackInfo ci) {
      if (rotation != null && ((SiftEulerRotation)(Object)rotation).theSift$getEuler() != null) {
         vertex.sub(rotation.origin()).rotate(((SiftEulerRotation)(Object)rotation).theSift$getEuler()).add(rotation.origin());
         ci.cancel();
      }
   }
}
