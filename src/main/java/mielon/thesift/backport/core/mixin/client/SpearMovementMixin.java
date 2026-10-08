package mielon.thesift.backport.core.mixin.client;
import net.minecraft.client.player.LocalPlayer;
import mielon.thesift.backport.common.level.items.spear.SpearItem;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
@Mixin(LocalPlayer.class)
public abstract class SpearMovementMixin {
 @ModifyExpressionValue(method="aiStep",at=@At(value="INVOKE",target="Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z",ordinal=0))
 private boolean theSift$slow(boolean original){return original&&!(((LocalPlayer)(Object)this).getUseItem().getItem() instanceof SpearItem);}
 @ModifyExpressionValue(method="canStartSprinting",at=@At(value="INVOKE",target="Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"))
 private boolean theSift$sprint(boolean original){return original&&!(((LocalPlayer)(Object)this).getUseItem().getItem() instanceof SpearItem);}
}
