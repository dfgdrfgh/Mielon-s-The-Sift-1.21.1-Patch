package mielon.thesift.client.mixin;

import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.EnchantmentMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EnchantmentScreen.class})
public abstract class SiftEnchantmentScreenMixin extends AbstractContainerScreen<EnchantmentMenu> {
   @Unique
   private static final List<ResourceLocation> THE_SIFT$FUEL_ICONS = List.of(
      ResourceLocation.withDefaultNamespace("item/empty_slot_lapis_lazuli"), ResourceLocation.fromNamespaceAndPath("the_sift", "item/empty_slot_charoite")
   );
   @Unique
   private final CyclingSlotBackground theSift$fuelIcon = new CyclingSlotBackground(1);

   protected SiftEnchantmentScreenMixin(EnchantmentMenu menu, Inventory inventory, Component title) {
      super(menu, inventory, title);
   }

   @Inject(
      method = {"containerTick"},
      at = {@At("TAIL")}
   )
   private void theSift$tickFuelIcon(CallbackInfo ci) {
      this.theSift$fuelIcon.tick(THE_SIFT$FUEL_ICONS);
   }

   @Inject(
      method = {"renderBg"},
      at = {@At("TAIL")}
   )
   private void theSift$renderFuelIcon(GuiGraphics graphics, float partialTick, int mouseX, int mouseY, CallbackInfo ci) {
      this.theSift$fuelIcon.render(this.menu, graphics, partialTick, this.leftPos, this.topPos);
   }

   @Inject(
      method = {"render"},
      at = {@At("TAIL")}
   )
   private void theSift$renderFuelTooltip(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
      if (this.hoveredSlot != null && this.hoveredSlot.index == 1 && !this.hoveredSlot.hasItem()) {
         graphics.renderTooltip(this.font, Component.translatable("container.the_sift.enchanting_fuel"), mouseX, mouseY);
      }
   }
}
