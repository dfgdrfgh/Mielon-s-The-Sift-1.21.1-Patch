package mielon.thesift.client.loading;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class SiftTransitionScreen extends Screen {
   private final long createdAtNanos = System.nanoTime();

   public SiftTransitionScreen() {
      super(Component.translatable("multiplayer.downloadingTerrain"));
   }

   public boolean isPauseScreen() {
      return false;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      SiftLoadingBackgroundRenderer.draw(graphics, this.width, this.height);
      graphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 50, 16777215);
   }

   public void tick() {
      if (!SiftLoadingScreenState.isActive() || System.nanoTime() - this.createdAtNanos > 30000000000L) {
         SiftLoadingScreenState.clear();
         if (this.minecraft.screen == this) {
            this.minecraft.setScreen(null);
         }
      }
   }
}
