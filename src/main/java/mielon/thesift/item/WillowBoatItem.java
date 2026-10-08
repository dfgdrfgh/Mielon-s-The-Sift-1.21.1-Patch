package mielon.thesift.item;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class WillowBoatItem extends Item {
   private static final Predicate<Entity> ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);
   private final EntityType<? extends Boat> entityType;

   public WillowBoatItem(EntityType<? extends Boat> type, Properties properties) {
      super(properties);
      this.entityType = type;
   }

   public InteractionResultHolder<ItemStack> use(Level p_40622_, Player p_40623_, InteractionHand p_40624_) {
      ItemStack itemstack = p_40623_.getItemInHand(p_40624_);
      HitResult hitresult = getPlayerPOVHitResult(p_40622_, p_40623_, Fluid.ANY);
      if (hitresult.getType() == Type.MISS) {
         return InteractionResultHolder.pass(itemstack);
      } else {
         Vec3 vec3 = p_40623_.getViewVector(1.0F);
         double d0 = 5.0;
         List<Entity> list = p_40622_.getEntities(p_40623_, p_40623_.getBoundingBox().expandTowards(vec3.scale(5.0)).inflate(1.0), ENTITY_PREDICATE);
         if (!list.isEmpty()) {
            Vec3 vec31 = p_40623_.getEyePosition();

            for (Entity entity : list) {
               AABB aabb = entity.getBoundingBox().inflate(entity.getPickRadius());
               if (aabb.contains(vec31)) {
                  return InteractionResultHolder.pass(itemstack);
               }
            }
         }

         if (hitresult.getType() == Type.BLOCK) {
            Boat boat = this.getBoat(p_40622_, hitresult, itemstack, p_40623_);
            boat.setYRot(p_40623_.getYRot());
            if (!p_40622_.noCollision(boat, boat.getBoundingBox())) {
               return InteractionResultHolder.fail(itemstack);
            } else {
               if (!p_40622_.isClientSide) {
                  p_40622_.addFreshEntity(boat);
                  p_40622_.gameEvent(p_40623_, GameEvent.ENTITY_PLACE, hitresult.getLocation());
                  itemstack.consume(1, p_40623_);
               }

               p_40623_.awardStat(Stats.ITEM_USED.get(this));
               return InteractionResultHolder.sidedSuccess(itemstack, p_40622_.isClientSide());
            }
         } else {
            return InteractionResultHolder.pass(itemstack);
         }
      }
   }

   private Boat getBoat(Level p_220017_, HitResult p_220018_, ItemStack p_305852_, Player p_306127_) {
      Vec3 vec3 = p_220018_.getLocation();
      Boat boat = (Boat)this.entityType.create(p_220017_);
      boat.setPos(vec3.x, vec3.y, vec3.z);
      if (p_220017_ instanceof ServerLevel serverlevel) {
         EntityType.createDefaultStackConfig(serverlevel, p_305852_, p_306127_).accept(boat);
      }

      return boat;
   }
}
