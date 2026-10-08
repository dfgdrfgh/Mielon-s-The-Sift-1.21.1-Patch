package mielon.thesift.client.mixin;

import mielon.thesift.block.ModBlocks;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.HangingSignEditScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({HangingSignEditScreen.class})
public abstract class WillowHangingSignEditScreenMixin {
   private static final ResourceLocation THE_SIFT$TEXTURE = ResourceLocation.fromNamespaceAndPath("the_sift", "textures/gui/hanging_signs/overgrown_willow.png");
   @Shadow
   @Final
   @Mutable
   private ResourceLocation texture;

   @Inject(
      method = {"renderSignBackground"},
      at = {@At("HEAD")}
   )
   private void theSift$useWillowTexture(GuiGraphics graphics, BlockState state, CallbackInfo ci) {
      if (state.is(ModBlocks.OVERGROWN_WILLOW_HANGING_SIGN) || state.is(ModBlocks.OVERGROWN_WILLOW_WALL_HANGING_SIGN)) {
         this.texture = THE_SIFT$TEXTURE;
      }
   }
}
