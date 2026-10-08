package mielon.thesift.client.mixin;
import com.llamalad7.mixinextras.sugar.Local;
import mielon.thesift.client.render.SiftDimensionEffects;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(LightTexture.class)
public abstract class SiftLightTextureMixin {
 @Inject(method="updateLightTexture",at=@At(value="INVOKE",target="Lnet/minecraft/client/renderer/LightTexture;clampColor(Lorg/joml/Vector3f;)V",ordinal=1))
 private void theSift$lightColors(float partial,CallbackInfo ci,@Local ClientLevel level,@Local(ordinal=0) int sky,@Local(ordinal=1) int block,@Local(ordinal=1) Vector3f color) {
  if(level.effects() instanceof SiftDimensionEffects sift)sift.adjustLightmapColors(level,partial,0,0,0,block,sky,color);
 }
}
