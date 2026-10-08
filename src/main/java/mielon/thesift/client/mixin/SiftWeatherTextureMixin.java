package mielon.thesift.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import mielon.thesift.world.TheSiftDimension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome.Precipitation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({LevelRenderer.class})
public abstract class SiftWeatherTextureMixin {
   private static final ResourceLocation VANILLA_RAIN = ResourceLocation.withDefaultNamespace("textures/environment/rain.png");
   private static final ResourceLocation ICHOR_RAIN = ResourceLocation.fromNamespaceAndPath("the_sift", "textures/environment/ichor_rain.png");
   private static final ResourceLocation VANILLA_SNOW = ResourceLocation.withDefaultNamespace("textures/environment/snow.png");
   private static final ResourceLocation ICHOR_SNOW = ResourceLocation.fromNamespaceAndPath("the_sift", "textures/environment/ichor_snow.png");

   @ModifyVariable(
      method = {"renderSnowAndRain"},
      at = @At("STORE"),
      ordinal = 0
   )
   private Precipitation theSift$restrictSnowColumns(Precipitation original, @Local MutableBlockPos pos) {
      ClientLevel level = Minecraft.getInstance().level;
      if (level == null || !level.dimension().equals(TheSiftDimension.LEVEL_KEY)) {
         return original;
      } else if (level.getBiome(pos).is(TheSiftDimension.ICHOR_SNOWY_PEAKS)) {
         return Precipitation.SNOW;
      } else {
         return original == Precipitation.SNOW ? Precipitation.RAIN : original;
      }
   }

   @ModifyArg(
      method = {"renderSnowAndRain"},
      at = @At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V"
      ),
      index = 1
   )
   private ResourceLocation theSift$useIchorWeatherTexture(ResourceLocation original) {
      ClientLevel level = Minecraft.getInstance().level;
      if (level != null && level.dimension().equals(TheSiftDimension.LEVEL_KEY)) {
         if (original.equals(VANILLA_RAIN)) {
            return ICHOR_RAIN;
         }

         if (original.equals(VANILLA_SNOW)) {
            return ICHOR_SNOW;
         }
      }

      return original;
   }
}
