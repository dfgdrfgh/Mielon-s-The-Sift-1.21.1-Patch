package mielon.thesift.client.mixin;

import mielon.thesift.client.compat.SiftShaderCompat;
import mielon.thesift.client.render.SiftShaders;
import mielon.thesift.world.TheSiftDimension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LevelRenderer.class})
public abstract class SiftCloudRendererMixin {
   @Inject(
      method = {"renderClouds"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void theSift$hideDryClouds(CallbackInfo ci) {
      ClientLevel level = Minecraft.getInstance().level;
      if (level != null && level.dimension().equals(TheSiftDimension.LEVEL_KEY) && level.getRainLevel(1.0F) <= 0.0F) {
         ci.cancel();
      }
   }

   @ModifyArg(
      method = {"renderClouds"},
      at = @At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/VertexBuffer;drawWithShader(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/client/renderer/ShaderInstance;)V"
      ),
      index = 2
   )
   private ShaderInstance theSift$cloudShader(ShaderInstance original) {
      ClientLevel level = Minecraft.getInstance().level;
      return !SiftShaderCompat.isShaderPackInUse() && level != null && level.dimension().equals(TheSiftDimension.LEVEL_KEY) ? SiftShaders.clouds() : original;
   }
}
