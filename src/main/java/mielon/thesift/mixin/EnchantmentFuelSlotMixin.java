package mielon.thesift.mixin;

import com.mojang.datafixers.util.Pair;
import mielon.thesift.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   targets = {"net.minecraft.world.inventory.EnchantmentMenu$3"}
)
public abstract class EnchantmentFuelSlotMixin {
   @Inject(
      method = {"mayPlace"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void theSift$allowCharoite(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
      if (stack.is(ModItems.CHAROITE)) {
         cir.setReturnValue(true);
      }
   }

   @Inject(
      method = {"getNoItemIcon"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void theSift$hideStaticLapisIcon(CallbackInfoReturnable<Pair<ResourceLocation, ResourceLocation>> cir) {
      cir.setReturnValue(null);
   }
}
