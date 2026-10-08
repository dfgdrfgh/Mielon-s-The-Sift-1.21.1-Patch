package mielon.thesift.backport.core.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mielon.thesift.backport.common.api.extensions.entity.spear.MobSpearHandler;
import mielon.thesift.backport.common.api.extensions.entity.spear.PlayerSpearHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mielon.thesift.backport.common.level.items.spear.SpearAnimations;
import mielon.thesift.backport.common.level.items.spear.SpearItem;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class SpearHandMixin {
    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void theSift$spear(
        AbstractClientPlayer player,
        float partialTicks,
        float pitch,
        InteractionHand hand,
        float swingProgress,
        ItemStack stack,
        float equippedProgress,
        PoseStack poseStack,
        MultiBufferSource buffer,
        int combinedLight,
        CallbackInfo ci
    ) {
        if (!(stack.getItem() instanceof SpearItem)) {
            return;
        }

        HumanoidArm arm = hand == InteractionHand.MAIN_HAND
            ? player.getMainArm()
            : player.getMainArm().getOpposite();
        int invert = arm == HumanoidArm.RIGHT ? 1 : -1;

        poseStack.pushPose();
        if (player.isUsingItem() && player.getUsedItemHand() == hand) {
            // Vanilla 26.3's SPEAR use animation has a custom arm transform.
            // It intentionally bypasses the normal equippedProgress offset.
            poseStack.translate((float) invert * 0.56F, -0.52F, -0.72F);
            float timeHeld = (float) stack.getUseDuration(player)
                - ((float) player.getUseItemRemainingTicks() - partialTicks + 1.0F);
            SpearAnimations.firstPersonUse(
                ((MobSpearHandler) player).theSift$getTicksSinceLastKineticHitFeedback(partialTicks),
                poseStack,
                timeHeld,
                arm,
                stack
            );
        } else {
            // When the spear is not actively being used, vanilla applies the
            // normal item/equip transform before the STAB swing animation.
            poseStack.translate(
                (float) invert * 0.56F,
                -0.52F + equippedProgress * -0.6F,
                -0.72F
            );
            SpearAnimations.firstPersonAttack(swingProgress, poseStack, invert);
        }

        ((ItemInHandRenderer) (Object) this).renderItem(
            player,
            stack,
            arm == HumanoidArm.RIGHT
                ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                : ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
            arm == HumanoidArm.LEFT,
            poseStack,
            buffer,
            combinedLight
        );
        poseStack.popPose();
        ci.cancel();
    }
    @WrapOperation(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"
        )
    )
    private float theSift$itemSwapScale(LocalPlayer player, float partialTicks, Operation<Float> original) {
        return ((PlayerSpearHandler) player).theSift$getItemSwapScale(partialTicks);
    }

}
