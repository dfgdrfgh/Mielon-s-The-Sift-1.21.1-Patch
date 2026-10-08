package mielon.thesift.backport.core.mixin.client;
import com.mojang.blaze3d.vertex.PoseStack;
import mielon.thesift.backport.common.level.items.spear.SpearItem;
import mielon.thesift.backport.common.level.items.spear.SpearAnimations;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(ItemInHandLayer.class)
public abstract class SpearThirdPersonItemMixin {
 @Inject(method="renderArmWithItem",at=@At(value="INVOKE",target="Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"))
 private void theSift$item(LivingEntity entity,ItemStack stack,ItemDisplayContext context,HumanoidArm arm,PoseStack poses,MultiBufferSource buffers,int light,CallbackInfo ci){
  if(stack.getItem() instanceof SpearItem && ((ItemInHandLayer<?,?>)(Object)this).getParentModel() instanceof HumanoidModel<?> parent){
   var model=(HumanoidModel<LivingEntity>)parent;
   if(entity.isUsingItem()&&entity.getUseItem()==stack)SpearAnimations.thirdPersonUseItem(model,entity,poses,entity.getTicksUsingItem(),arm,stack);
   else if(entity.swingingArm!=null && entity.getItemInHand(entity.swingingArm)==stack)SpearAnimations.thirdPersonAttackItem(model,entity,poses);
  }
 }
}
