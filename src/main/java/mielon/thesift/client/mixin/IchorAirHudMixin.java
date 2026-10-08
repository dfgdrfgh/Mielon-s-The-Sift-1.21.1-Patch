package mielon.thesift.client.mixin;

import mielon.thesift.fluid.ModFluids;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin({Gui.class})
public abstract class IchorAirHudMixin {
   private static final ResourceLocation VANILLA_AIR = ResourceLocation.withDefaultNamespace("hud/air");
   private static final ResourceLocation VANILLA_BURSTING = ResourceLocation.withDefaultNamespace("hud/air_bursting");
   private static final ResourceLocation ICHOR_AIR = id("hud/ichor_air");
   private static final ResourceLocation ICHOR_BURSTING = id("hud/ichor_air_bursting");

   @ModifyArg(
      method = {"renderPlayerHealth"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"
      ),
      index = 0
   )
   private ResourceLocation theSift$useIchorAirSprite(ResourceLocation original) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player == null) {
         return original;
      } else {
         Fluid fluid = player.level().getFluidState(BlockPos.containing(player.getX(), player.getEyeY(), player.getZ())).getType();
         if (fluid != ModFluids.ICHOR && fluid != ModFluids.FLOWING_ICHOR) {
            return original;
         } else if (original.equals(VANILLA_AIR)) {
            return ICHOR_AIR;
         } else {
            return original.equals(VANILLA_BURSTING) ? ICHOR_BURSTING : original;
         }
      }
   }

   private static ResourceLocation id(String path) {
      return ResourceLocation.fromNamespaceAndPath("the_sift", path);
   }
}
