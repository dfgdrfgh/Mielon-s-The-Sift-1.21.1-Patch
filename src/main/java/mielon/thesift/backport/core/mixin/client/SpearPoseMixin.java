package mielon.thesift.backport.core.mixin.client;
import mielon.thesift.backport.common.level.items.spear.SpearItem;
import mielon.thesift.backport.common.level.items.spear.SpearAnimations;
import mielon.thesift.backport.common.api.extensions.entity.spear.SpearSwingTracker;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(HumanoidModel.class)
public abstract class SpearPoseMixin {
 @Inject(method="poseRightArm",at=@At("HEAD"),cancellable=true)
 private void theSift$right(LivingEntity entity,CallbackInfo ci){theSift$pose(entity,HumanoidArm.RIGHT,ci);}
 @Inject(method="poseLeftArm",at=@At("HEAD"),cancellable=true)
 private void theSift$left(LivingEntity entity,CallbackInfo ci){theSift$pose(entity,HumanoidArm.LEFT,ci);}
 private void theSift$pose(LivingEntity entity,HumanoidArm arm,CallbackInfo ci){
  var item=entity.getItemInHand(entity.getMainArm()==arm?InteractionHand.MAIN_HAND:InteractionHand.OFF_HAND);
  if(item.getItem() instanceof SpearItem){var model=(HumanoidModel<LivingEntity>)(Object)this;SpearAnimations.thirdPersonHandUse(model,arm==HumanoidArm.RIGHT?model.rightArm:model.leftArm,model.head,arm,item,entity);ci.cancel();}
 }
 @Inject(method="setupAttackAnimation",at=@At("HEAD"),cancellable=true)
 private void theSift$attack(LivingEntity entity,float age,CallbackInfo ci){
  var model=(HumanoidModel<LivingEntity>)(Object)this;
  if(model.attackTime>0 && entity.swingingArm!=null && (entity instanceof SpearSwingTracker tracker && tracker.theSift$isAttackSwing()) && entity.getItemInHand(entity.swingingArm).getItem() instanceof SpearItem){
   var arm=entity.swingingArm==InteractionHand.MAIN_HAND?entity.getMainArm():entity.getMainArm().getOpposite();
   // Modern vanilla performs the common torso/arm swing setup before the
   // spear-specific STAB pose. Reproduce that prelude before cancelling 1.21.1.
   model.body.yRot=Mth.sin(Mth.sqrt(model.attackTime)*Mth.TWO_PI)*0.2F;
   if(arm==HumanoidArm.LEFT)model.body.yRot*=-1.0F;
   model.rightArm.z=Mth.sin(model.body.yRot)*5.0F;
   model.rightArm.x=-Mth.cos(model.body.yRot)*5.0F;
   model.leftArm.z=-Mth.sin(model.body.yRot)*5.0F;
   model.leftArm.x=Mth.cos(model.body.yRot)*5.0F;
   model.rightArm.yRot+=model.body.yRot;
   model.leftArm.yRot+=model.body.yRot;
   model.leftArm.xRot+=model.body.yRot;
   SpearAnimations.thirdPersonAttackHand(model,model.attackTime,arm);ci.cancel();
  }
 }
}
