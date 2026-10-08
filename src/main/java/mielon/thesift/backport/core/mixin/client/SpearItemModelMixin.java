package mielon.thesift.backport.core.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import mielon.thesift.backport.BackportContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public abstract class SpearItemModelMixin {
    // Use vanilla's single render pass and GUI lighting for the generated icon.
    // Only the held contexts select the larger model and its hand transforms.
    @ModifyVariable(
        method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V",
        at = @At("HEAD"), argsOnly = true
    )
    private BakedModel theSift$heldModel(BakedModel original,
            @Local(argsOnly = true) ItemStack stack,
            @Local(argsOnly = true) ItemDisplayContext context) {
        if (!stack.is(BackportContent.SIFTITE_SPEAR)) return original;
        return switch (context) {
            case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND,
                 THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND ->
                Minecraft.getInstance().getModelManager().getModel(
                    BackportContent.id("item/siftite_spear_in_hand"));
            default -> original;
        };
    }
}
