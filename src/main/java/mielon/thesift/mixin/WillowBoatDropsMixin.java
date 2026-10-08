package mielon.thesift.mixin;

import mielon.thesift.block.ModBlocks;
import mielon.thesift.entity.WillowBoat;
import mielon.thesift.entity.WillowChestBoat;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Boat.Type;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({Boat.class})
public abstract class WillowBoatDropsMixin {
   @Redirect(
      method = {"checkFallDamage"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/vehicle/Boat$Type;getPlanks()Lnet/minecraft/world/level/block/Block;"
      )
   )
   private Block theSift$willowPlanks(Type variant) {
      return !((Object)this instanceof WillowBoat) && !((Object)this instanceof WillowChestBoat) ? variant.getPlanks() : ModBlocks.OVERGROWN_WILLOW_PLANKS;
   }
}
