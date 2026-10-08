package mielon.thesift.client.mixin;

import mielon.thesift.block.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({SignEditScreen.class})
public abstract class WillowSignEditScreenMixin {
   @Inject(
      method = {"renderSignBackground"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void theSift$willowBoard(GuiGraphics graphics, BlockState state, CallbackInfo ci) {
      boolean standing = state.is(ModBlocks.OVERGROWN_WILLOW_SIGN);
      if (standing || state.is(ModBlocks.OVERGROWN_WILLOW_WALL_SIGN)) {
         graphics.pose().scale(93.75001F, -93.75001F, 93.75001F);
         graphics.pose().translate(-0.5, -(standing ? 13.33333 : 8.33333) / 16.0, -0.5);
         Minecraft.getInstance()
            .getBlockRenderer()
            .renderSingleBlock(state.getBlock().defaultBlockState(), graphics.pose(), graphics.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY);
         graphics.flush();
         ci.cancel();
      }
   }
}
