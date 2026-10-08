package mielon.thesift.entity;

import mielon.thesift.item.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public final class WillowBoat extends Boat {
   public WillowBoat(EntityType<? extends Boat> type, Level level) {
      super(type, level);
   }

   public Item getDropItem() {
      return ModItems.OVERGROWN_WILLOW_BOAT;
   }
}
