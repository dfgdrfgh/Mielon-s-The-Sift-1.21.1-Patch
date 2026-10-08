package mielon.thesift.client.mixin;

import mielon.thesift.entity.WardenPettingAccess;
import net.minecraft.client.model.WardenModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.warden.Warden;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({WardenModel.class})
public abstract class WardenModelPettingMixin {
   @Shadow
   @Final
   protected ModelPart rightArm;
   @Shadow
   @Final
   protected ModelPart head;

   @Inject(
      method = {"setupAnim(Lnet/minecraft/world/entity/monster/warden/Warden;FFFFF)V"},
      at = {@At("TAIL")}
   )
   private void theSift$animatePetting(Warden state, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch, CallbackInfo ci) {
      if (((WardenPettingAccess)state).theSift$isPettingDarkSniffer()) {
         float stroke = Mth.sin(ageInTicks * 0.32F) * 0.16F;
         this.rightArm.xRot = -0.66F + stroke;
         this.rightArm.yRot = -0.1F;
         this.rightArm.zRot = -0.08F;
         this.head.xRot = Math.max(this.head.xRot, 0.22F);
      }
   }
}
