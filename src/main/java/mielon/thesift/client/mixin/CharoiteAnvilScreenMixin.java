package mielon.thesift.client.mixin;

import mielon.thesift.item.CharoiteEnchanting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({AnvilScreen.class})
public abstract class CharoiteAnvilScreenMixin extends AbstractContainerScreen<AnvilMenu> {
   @Unique
   private static final Component THE_SIFT$CHAROITE_USED = Component.translatable("container.the_sift.charoite_used");

   protected CharoiteAnvilScreenMixin(AnvilMenu menu, Inventory inventory, Component title) {
      super(menu, inventory, title);
   }

   @Inject(
      method = {"renderLabels"},
      at = {@At("TAIL")}
   )
   private void theSift$renderCharoiteUsed(GuiGraphics graphics, int mouseX, int mouseY, CallbackInfo ci) {
      if (CharoiteEnchanting.blocksAnvil(((AnvilMenu)(Object)this.menu).getSlot(0).getItem(), ((AnvilMenu)(Object)this.menu).getSlot(1).getItem())) {
         int x = this.imageWidth - 8 - this.font.width(THE_SIFT$CHAROITE_USED) - 2;
         graphics.fill(x - 2, 67, this.imageWidth - 8, 79, 1325400064);
         graphics.drawString(this.font, THE_SIFT$CHAROITE_USED, x, 69, -40864);
      }
   }
}
