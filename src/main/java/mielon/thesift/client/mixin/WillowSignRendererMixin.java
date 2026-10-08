package mielon.thesift.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({SignRenderer.class})
public abstract class WillowSignRendererMixin {
   @Unique
   private boolean theSift$blockModel;

   @Inject(
      method = {"renderSignWithText"},
      at = {@At("HEAD")}
   )
   private void theSift$selectModel(
      SignBlockEntity sign,
      PoseStack pose,
      MultiBufferSource buffer,
      int light,
      int overlay,
      BlockState state,
      SignBlock block,
      WoodType wood,
      Model model,
      CallbackInfo ci
   ) {
      this.theSift$blockModel = BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals("the_sift");
   }

   @Inject(
      method = {"renderSign"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void theSift$keepCustomBoard(CallbackInfo ci) {
      if (this.theSift$blockModel) {
         ci.cancel();
      }
   }
}
