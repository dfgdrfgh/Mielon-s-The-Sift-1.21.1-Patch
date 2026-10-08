package mielon.thesift.backport.common.api.extensions.entity.spear;

import mielon.thesift.backport.common.level.components.AttackRange;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public interface MobSpearHandler {
   AttackRange theSift$getAttackRangeWith(ItemStack itemStack);

   boolean theSift$wasRecentlyStabbed(Entity entity, int i);

   void theSift$rememberStabbedEntity(Entity entity);

   default int theSift$stabbedEntities(Predicate<Entity> filter) {
      return 0;
   }

   boolean theSift$stabAttack(EquipmentSlot equipmentSlot, Entity entity, float f, boolean bl, boolean bl2, boolean bl3);

   void theSift$causeExtraKnockback(Entity entity, float f, Vec3 vec3);

   default void theSift$onAttack() {
   }

   void theSift$postPiercingAttack();

   float theSift$getTicksSinceLastKineticHitFeedback(float f);
}
