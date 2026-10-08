package mielon.thesift.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BaseEntityBlock.class})
public abstract class WillowSignShapeMixin {
   @Inject(
      method = {"getRenderShape"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void theSift$blockSignModel(BlockState state, CallbackInfoReturnable<RenderShape> cir) {
      if (state.getBlock() instanceof SignBlock && BuiltInRegistries.BLOCK.getKey(state.getBlock()).getNamespace().equals("the_sift")) {
         cir.setReturnValue(RenderShape.MODEL);
      }
   }
}
