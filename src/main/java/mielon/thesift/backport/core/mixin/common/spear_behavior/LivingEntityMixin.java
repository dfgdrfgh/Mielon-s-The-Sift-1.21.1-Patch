package mielon.thesift.backport.core.mixin.common.spear_behavior;

import mielon.thesift.backport.common.api.extensions.entity.spear.MobSpearHandler;
import mielon.thesift.backport.common.api.extensions.entity.spear.SpearSwingTracker;
import mielon.thesift.backport.common.level.components.AttackRange;
import mielon.thesift.backport.common.level.components.KineticWeapon;
import mielon.thesift.backport.common.level.components.SwingAnimation;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LivingEntity.class})
public abstract class LivingEntityMixin extends Entity implements MobSpearHandler, SpearSwingTracker {
   @Shadow
   protected ItemStack useItem;
   @Shadow
   public InteractionHand swingingArm;
   @Unique
   
   protected Object2LongMap<Entity> theSift$recentKineticEnemies;
   @Unique
   private long theSift$lastKineticHitFeedbackTime = -2147483648L;
   @Unique
   private boolean theSift$isAttackSwing = true;

   @Shadow
   public abstract ItemStack getItemBySlot(EquipmentSlot equipmentSlot);

   @Shadow
   public abstract void setLastHurtMob(Entity entity);

   @Shadow
   public abstract ItemStack getItemInHand(InteractionHand interactionHand);

   @Shadow
   protected abstract float getKnockback(Entity entity, DamageSource damageSource);

   public LivingEntityMixin(EntityType<?> entityType, Level level) {
      super(entityType, level);
   }

   @Override
   public void theSift$setAttackSwing(boolean isAttack) {
      this.theSift$isAttackSwing = isAttack;
   }

   @Override
   public boolean theSift$isAttackSwing() {
      return this.theSift$isAttackSwing;
   }

   @Override
   public AttackRange theSift$getAttackRangeWith(ItemStack weapon) {
      AttackRange attackRange = AttackRange.get(weapon);
      return attackRange != null ? attackRange : AttackRange.defaultFor((LivingEntity)(Object)this);
   }

   @Inject(
      method = {"startUsingItem"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;gameEvent(Lnet/minecraft/core/Holder;)V",
         shift = Shift.AFTER
      )}
   )
   private void theSift$startUsingItem(InteractionHand hand, CallbackInfo ci) {
      if (KineticWeapon.get(this.useItem) != null) {
         this.theSift$recentKineticEnemies = new Object2LongOpenHashMap();
      }
   }

   @Inject(
      method = {"stopUsingItem"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;setLivingEntityFlag(IZ)V"
      )}
   )
   private void theSift$stopUsingItem(CallbackInfo ci) {
      this.theSift$recentKineticEnemies = null;
   }

   @ModifyExpressionValue(
      method = {"getCurrentSwingDuration"},
      at = {@At(
         value = "CONSTANT",
         args = {"intValue=6"}
      )}
   )
   private int theSift$getCurrentSwingDuration(int original) {
      if (!this.theSift$isAttackSwing()) {
         return original;
      } else {
         InteractionHand hand = this.swingingArm != null ? this.swingingArm : InteractionHand.MAIN_HAND;
         ItemStack heldItem = this.getItemInHand(hand);
         return SwingAnimation.get(heldItem).duration();
      }
   }

   @Override
   public boolean theSift$wasRecentlyStabbed(Entity target, int allowedTime) {
      if (this.theSift$recentKineticEnemies == null) {
         return false;
      } else {
         return this.theSift$recentKineticEnemies.containsKey(target) ? this.level().getGameTime() - this.theSift$recentKineticEnemies.getLong(target) < allowedTime : false;
      }
   }

   @Override
   public void theSift$rememberStabbedEntity(Entity target) {
      if (this.theSift$recentKineticEnemies != null) {
         this.theSift$recentKineticEnemies.put(target, this.level().getGameTime());
      }
   }

   @Override
   public int theSift$stabbedEntities(Predicate<Entity> filter) {
      return this.theSift$recentKineticEnemies == null ? 0 : (int)(Object)this.theSift$recentKineticEnemies.keySet().stream().filter(filter).count();
   }

   @Override
   public boolean theSift$stabAttack(EquipmentSlot weaponSlot, Entity target, float baseDamage, boolean dealsDamage, boolean dealsKnockback, boolean dismounts) {
      LivingEntity self = (LivingEntity)(Object)this;
      if (!(this.level() instanceof ServerLevel server)) {
         return false;
      } else {
         ItemStack var18 = this.getItemBySlot(weaponSlot);
         DamageSource damageSource = this.damageSources().mobAttack(self);
         float postEnchantmentDamage = EnchantmentHelper.modifyDamage(server, var18, target, damageSource, baseDamage);
         Vec3 oldMovement = target.getDeltaMovement();
         boolean dealtDamage = dealsDamage && target.hurt(damageSource, postEnchantmentDamage);
         boolean affected = dealsKnockback | dealtDamage;
         if (dealsKnockback) {
            this.theSift$causeExtraKnockback(target, 0.4F, oldMovement);
            this.theSift$causeExtraKnockback(target, this.getKnockback(target, damageSource), oldMovement);
         }

         if (dismounts && target.isPassenger()) {
            affected = true;
            target.stopRiding();
         }

         if (target instanceof LivingEntity living) {
            Item usedItem = var18.getItem();
            if (usedItem.hurtEnemy(var18, living, self) && self instanceof Player player) {
               player.awardStat(Stats.ITEM_USED.get(usedItem));
            }
         }

         if (dealtDamage) {
            EnchantmentHelper.doPostAttackEffects(server, target, damageSource);
         }

         if (!affected) {
            return false;
         } else {
            this.setLastHurtMob(target);
            return true;
         }
      }
   }

   @Override
   public void theSift$causeExtraKnockback(Entity target, float knockback, Vec3 oldMovement) {
      if (knockback > 0.0F && target instanceof LivingEntity living) {
         living.knockback(knockback, Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)), -Mth.cos(this.getYRot() * (float) (Math.PI / 180.0)));
         this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
      }
   }

   @Override
   public void theSift$postPiercingAttack() {
      if (this.level() instanceof ServerLevel level) {
         mielon.thesift.backport.SpearLunge.apply((LivingEntity)(Object)this);
      }
   }

   @Inject(
      method = {"handleEntityEvent"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void theSift$handleEntityEvent(byte id, CallbackInfo ci) {
      if (id == 2 && KineticWeapon.get(this.useItem) != null) {
         this.theSift$onKineticHit();
         ci.cancel();
      }
   }

   @Override
   public float theSift$getTicksSinceLastKineticHitFeedback(float partial) {
      return this.theSift$lastKineticHitFeedbackTime < 0L ? 0.0F : (float)(this.level().getGameTime() - this.theSift$lastKineticHitFeedbackTime) + partial;
   }

   @Unique
   private void theSift$onKineticHit() {
      if (this.level().getGameTime() - this.theSift$lastKineticHitFeedbackTime > 10L) {
         this.theSift$lastKineticHitFeedbackTime = this.level().getGameTime();
         KineticWeapon kineticWeapon = KineticWeapon.get(this.useItem);
         if (kineticWeapon != null) {
            kineticWeapon.makeLocalHitSound(this);
         }
      }
   }
}
