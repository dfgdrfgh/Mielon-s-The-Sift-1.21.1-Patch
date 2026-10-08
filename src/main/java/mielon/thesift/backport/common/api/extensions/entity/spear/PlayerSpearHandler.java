package mielon.thesift.backport.common.api.extensions.entity.spear;

import net.minecraft.world.item.ItemStack;

public interface PlayerSpearHandler {
   boolean theSift$cannotAttackWithItem(ItemStack itemStack, int i);

   float theSift$getItemSwapScale(float f);
}
