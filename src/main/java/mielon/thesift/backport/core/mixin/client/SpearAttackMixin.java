package mielon.thesift.backport.core.mixin.client;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import mielon.thesift.backport.SpearAttackPayload;
import mielon.thesift.backport.common.level.items.spear.SpearItem;
import mielon.thesift.backport.common.level.components.PiercingWeapon;
import mielon.thesift.backport.common.api.extensions.entity.spear.PlayerSpearHandler;
import mielon.thesift.backport.common.api.extensions.entity.spear.MobSpearHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(Minecraft.class)
public abstract class SpearAttackMixin {
 @Inject(method="startAttack",at=@At("HEAD"),cancellable=true)
 private void theSift$jab(CallbackInfoReturnable<Boolean> ci){Minecraft mc=(Minecraft)(Object)this;if(mc.player==null||!(mc.player.getMainHandItem().getItem() instanceof SpearItem))return;
  if(!mc.player.isUsingItem()&&!((PlayerSpearHandler)mc.player).theSift$cannotAttackWithItem(mc.player.getMainHandItem(),0)){var weapon=PiercingWeapon.get(mc.player.getMainHandItem());ClientPlayNetworking.send(new SpearAttackPayload());mc.player.swing(InteractionHand.MAIN_HAND);((MobSpearHandler)mc.player).theSift$onAttack();if(weapon!=null)weapon.makeSound(mc.player);ci.setReturnValue(true);return;}
  ci.setReturnValue(false);
 }
}
