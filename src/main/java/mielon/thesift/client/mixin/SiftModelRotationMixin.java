package mielon.thesift.client.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mielon.thesift.client.render.SiftEulerRotation;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.GsonHelper;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   targets = {"net.minecraft.client.renderer.block.model.BlockElement$Deserializer"}
)
public abstract class SiftModelRotationMixin {
   @Inject(
      method = {"getRotation"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void theSift$modernElementRotation(JsonObject element, CallbackInfoReturnable<BlockElementRotation> cir) {
      if (element.has("rotation")) {
         JsonObject rotation = element.getAsJsonObject("rotation");
         if (GsonHelper.getAsBoolean(rotation, "the_sift_rotation", false) && !rotation.has("axis")) {
            JsonArray origin = rotation.getAsJsonArray("origin");
            Vector3f pivot = new Vector3f(origin.get(0).getAsFloat(), origin.get(1).getAsFloat(), origin.get(2).getAsFloat()).div(16.0F);
            BlockElementRotation result = new BlockElementRotation(pivot, Axis.Y, 0.0F, false);
            float radians = (float) (Math.PI / 180.0);
            ((SiftEulerRotation)(Object)result)
               .theSift$setEuler(
                  new Quaternionf()
                     .rotationZYX(
                        GsonHelper.getAsFloat(rotation, "z", 0.0F) * radians,
                        GsonHelper.getAsFloat(rotation, "y", 0.0F) * radians,
                        GsonHelper.getAsFloat(rotation, "x", 0.0F) * radians
                     )
               );
            cir.setReturnValue(result);
         }
      }
   }

   @Inject(
      method = {"getAngle"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void theSift$signAngles(JsonObject rotation, CallbackInfoReturnable<Float> cir) {
      if (GsonHelper.getAsBoolean(rotation, "the_sift_rotation", false)) {
         float angle = GsonHelper.getAsFloat(rotation, "angle");
         if (Float.isFinite(angle)) {
            cir.setReturnValue(angle);
         }
      }
   }
}
