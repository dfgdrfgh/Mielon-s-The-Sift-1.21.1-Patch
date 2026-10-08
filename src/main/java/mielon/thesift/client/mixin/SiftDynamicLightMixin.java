package mielon.thesift.client.mixin;

import mielon.thesift.client.light.TheSiftDynamicLightsInitializer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LevelRenderer.class})
public abstract class SiftDynamicLightMixin {
   @Inject(
      method = {"getLightColor(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)I"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private static void theSift$light(BlockAndTintGetter level, BlockState state, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
      cir.setReturnValue(TheSiftDynamicLightsInitializer.light(pos, cir.getReturnValueI()));
   }
}
