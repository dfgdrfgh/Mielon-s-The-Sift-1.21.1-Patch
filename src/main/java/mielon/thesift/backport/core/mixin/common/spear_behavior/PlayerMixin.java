package mielon.thesift.backport.core.mixin.common.spear_behavior;

import mielon.thesift.backport.common.api.extensions.entity.spear.MobSpearHandler;
import mielon.thesift.backport.common.api.extensions.entity.spear.PlayerSpearHandler;
import mielon.thesift.backport.common.registries.items.ModDataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
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

@Mixin({Player.class})
public abstract class PlayerMixin extends LivingEntity implements PlayerSpearHandler, MobSpearHandler {
   @Unique
   private int theSift$itemSwapTicker;

   @Shadow
   public abstract void playSound(SoundEvent soundEvent, float f, float g);

   @Shadow
   public abstract void awardStat(ResourceLocation resourceLocation, int i);

   @Shadow
   public abstract float getAttackStrengthScale(float f);

   @Shadow
   public abstract ItemStack getItemBySlot(EquipmentSlot equipmentSlot);

   @Shadow
   public abstract void causeFoodExhaustion(float f);

   @Shadow
   public abstract float getCurrentItemAttackStrengthDelay();

   @Shadow
   public abstract void magicCrit(Entity entity);

   @Shadow
   protected abstract float getEnchantedDamage(Entity entity, float f, DamageSource damageSource);

   protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
      super(entityType, level);
   }

   @Inject(
      method = {"attack"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void theSift$handleSpecialInteractions(Entity target, CallbackInfo ci) {
      Player player = (Player)(Object)this;
      if (player.getMainHandItem().getItem() instanceof mielon.thesift.backport.common.level.items.spear.SpearItem && target.isAttackable() && target.skipAttackInteraction(player)) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"attack"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/player/Player;resetAttackStrengthTicker()V"
      )}
   )
   private void theSift$onAttackBeforeReset(Entity target, CallbackInfo ci) {
      this.theSift$onAttack();
   }

   @Inject(
      method = {"attack"},
      at = {@At("TAIL")}
   )
   private void theSift$postPiercingAttackTail(Entity target, CallbackInfo ci) {
      this.theSift$postPiercingAttack();
   }

   @Override
   public boolean theSift$cannotAttackWithItem(ItemStack stack, int tolerance) {
      float requiredStrength = (Float)stack.getOrDefault(ModDataComponents.MINIMUM_ATTACK_CHARGE.get(), 0.0F);
      float optimisticStrength = (this.attackStrengthTicker + tolerance) / this.getCurrentItemAttackStrengthDelay();
      return requiredStrength > 0.0F && optimisticStrength < requiredStrength;
   }

   @Inject(
      method = {"tick"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/player/Player;getMainHandItem()Lnet/minecraft/world/item/ItemStack;",
         shift = Shift.BEFORE
      )}
   )
   private void theSift$onTick(CallbackInfo ci) {
      this.theSift$itemSwapTicker++;
   }

   @Override
   public float theSift$getItemSwapScale(float scale) {
      return Mth.clamp((this.theSift$itemSwapTicker + scale) / this.getCurrentItemAttackStrengthDelay(), 0.0F, 1.0F);
   }

   @Inject(
      method = {"resetAttackStrengthTicker"},
      at = {@At("TAIL")}
   )
   private void theSift$resetAttackStrengthTicker(CallbackInfo ci) {
      this.theSift$itemSwapTicker = 0;
   }

   @Override
   public void theSift$onAttack() {
      this.attackStrengthTicker = 0;
   }

   @Override
   public boolean theSift$stabAttack(EquipmentSlot weaponSlot, Entity target, float baseDamage, boolean dealsDamage, boolean dealsKnockback, boolean dismounts) {
      Player self = (Player)(Object)this;
      if (target.isAttackable() && !target.skipAttackInteraction(this)) {
         ItemStack weaponItem = this.getItemBySlot(weaponSlot);
         DamageSource damageSource = this.damageSources().playerAttack(self);
         float magicBoost = this.getEnchantedDamage(target, baseDamage, damageSource) - baseDamage;
         if (!this.isUsingItem() || (this.getUsedItemHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND) != weaponSlot) {
            magicBoost *= this.getAttackStrengthScale(0.5F);
            baseDamage *= this.theSift$baseDamageScaleFactor();
         }

         if (dealsKnockback && this.theSift$deflectProjectile(target)) {
            return true;
         } else {
            float totalDamage = dealsDamage ? baseDamage + magicBoost : 0.0F;
            float oldTargetHealth = target instanceof LivingEntity living ? living.getHealth() : 0.0F;
            Vec3 oldMovement = target.getDeltaMovement();
            boolean wasHurt = dealsDamage && target.hurt(damageSource, totalDamage);
            if (dealsKnockback) {
               this.theSift$causeExtraKnockback(target, 0.4F, oldMovement);
               this.theSift$causeExtraKnockback(target, this.getKnockback(target, damageSource), oldMovement);
            }

            boolean dismounted = false;
            if (dismounts && target.isPassenger()) {
               dismounted = true;
               target.stopRiding();
            }

            if (!wasHurt && !dealsKnockback && !dismounted) {
               return false;
            } else {
               this.theSift$attackVisualEffects(target, dealsDamage, magicBoost);
               this.setLastHurtMob(target);
               this.theSift$itemAttackInteraction(target, weaponItem, damageSource, wasHurt);
               this.theSift$damageStatsAndHearts(target, oldTargetHealth);
               this.causeFoodExhaustion(0.1F);
               return true;
            }
         }
      } else {
         return false;
      }
   }

   @Unique
   private void theSift$attackVisualEffects(Entity target, boolean dealsDamage, float magicBoost) {
      this.level()
         .playSound(
            null,
            this.getX(),
            this.getY(),
            this.getZ(),
            dealsDamage ? SoundEvents.PLAYER_ATTACK_STRONG : SoundEvents.PLAYER_ATTACK_WEAK,
            this.getSoundSource(),
            1.0F,
            1.0F
         );
      if (magicBoost > 0.0F) {
         this.magicCrit(target);
      }
   }

   @Unique
   private void theSift$damageStatsAndHearts(Entity target, float oldTargetHealth) {
      if (target instanceof LivingEntity living) {
         float actualDamage = oldTargetHealth - living.getHealth();
         this.awardStat(Stats.DAMAGE_DEALT, Math.round(actualDamage * 10.0F));
         if (this.level() instanceof ServerLevel level && actualDamage > 2.0F) {
            int count = (int)(actualDamage * 0.5);
            level.sendParticles(ParticleTypes.DAMAGE_INDICATOR, living.getX(), living.getY(0.5), living.getZ(), count, 0.1, 0.0, 0.1, 0.2);
         }
      }
   }

   @Override
   public void theSift$causeExtraKnockback(Entity target, float knockback, Vec3 oldMovement) {
      if (knockback > 0.0F) {
         if (target instanceof LivingEntity living) {
            living.knockback(knockback, Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)), -Mth.cos(this.getYRot() * (float) (Math.PI / 180.0)));
         } else {
            target.push(-Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)) * knockback, 0.1, Mth.cos(this.getYRot() * (float) (Math.PI / 180.0)) * knockback);
         }

         this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
         this.setSprinting(false);
      }

      if (target instanceof ServerPlayer player && player.hurtMarked) {
         player.connection.send(new ClientboundSetEntityMotionPacket(target));
         target.hurtMarked = false;
         target.setDeltaMovement(oldMovement);
      }
   }

   @Unique
   private void theSift$itemAttackInteraction(Entity target, ItemStack weapon, DamageSource damageSource, boolean wasHurt) {
      Player self = (Player)(Object)this;
      Entity hurtTarget = target;
      if (target instanceof EnderDragonPart part) {
         hurtTarget = part.parentMob;
      }

      boolean enemyHurt = false;
      if (this.level() instanceof ServerLevel level) {
         if (hurtTarget instanceof LivingEntity living) {
            enemyHurt = weapon.hurtEnemy(living, self);
         }

         if (wasHurt) {
            EnchantmentHelper.doPostAttackEffectsWithItemSource(level, hurtTarget, damageSource, weapon);
         }
      }

      if (!this.level().isClientSide() && !weapon.isEmpty() && hurtTarget instanceof LivingEntity living) {
         if (enemyHurt) {
            weapon.postHurtEnemy(living, self);
         }

         if (weapon.isEmpty()) {
            if (weapon == this.getMainHandItem()) {
               this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            } else {
               this.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            }
         }
      }
   }

   @Unique
   private boolean theSift$deflectProjectile(Entity entity) {
      if (entity.getType().is(EntityTypeTags.REDIRECTABLE_PROJECTILE)
         && entity instanceof Projectile projectile
         && projectile.deflect(ProjectileDeflection.AIM_DEFLECT, this, this, true)) {
         this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, this.getSoundSource());
         return true;
      } else {
         return false;
      }
   }

   @Unique
   private float theSift$baseDamageScaleFactor() {
      float attackStrengthScale = this.getAttackStrengthScale(0.5F);
      return 0.2F + attackStrengthScale * attackStrengthScale * 0.8F;
   }
}
