package mielon.thesift.backport.common.level.components;


import mielon.thesift.backport.common.api.extensions.entity.movement.MotionAwareEntity;
import mielon.thesift.backport.common.api.extensions.entity.spear.MobSpearHandler;

import mielon.thesift.backport.common.registries.items.ModDataComponents;
import mielon.thesift.backport.core.util.AdditionalCodecs;
import mielon.thesift.backport.core.util.ProjectileUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public record KineticWeapon(
   int contactCooldownTicks,
   int delayTicks,
   Optional<KineticWeapon.Condition> dismountConditions,
   Optional<KineticWeapon.Condition> knockbackConditions,
   Optional<KineticWeapon.Condition> damageConditions,
   float forwardMovement,
   float damageMultiplier,
   Optional<Holder<SoundEvent>> sound,
   Optional<Holder<SoundEvent>> hitSound
) {
   public static final Codec<KineticWeapon> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("contact_cooldown_ticks", 10).forGetter(KineticWeapon::contactCooldownTicks),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("delay_ticks", 0).forGetter(KineticWeapon::delayTicks),
            KineticWeapon.Condition.CODEC.optionalFieldOf("dismount_conditions").forGetter(KineticWeapon::dismountConditions),
            KineticWeapon.Condition.CODEC.optionalFieldOf("knockback_conditions").forGetter(KineticWeapon::knockbackConditions),
            KineticWeapon.Condition.CODEC.optionalFieldOf("damage_conditions").forGetter(KineticWeapon::damageConditions),
            Codec.FLOAT.optionalFieldOf("forward_movement", 0.0F).forGetter(KineticWeapon::forwardMovement),
            Codec.FLOAT.optionalFieldOf("damage_multiplier", 1.0F).forGetter(KineticWeapon::damageMultiplier),
            SoundEvent.CODEC.optionalFieldOf("sound").forGetter(KineticWeapon::sound),
            SoundEvent.CODEC.optionalFieldOf("hit_sound").forGetter(KineticWeapon::hitSound)
         )
         .apply(instance, KineticWeapon::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, KineticWeapon> STREAM_CODEC = AdditionalCodecs.composite(
      ByteBufCodecs.VAR_INT,
      KineticWeapon::contactCooldownTicks,
      ByteBufCodecs.VAR_INT,
      KineticWeapon::delayTicks,
      KineticWeapon.Condition.STREAM_CODEC.apply(ByteBufCodecs::optional),
      KineticWeapon::dismountConditions,
      KineticWeapon.Condition.STREAM_CODEC.apply(ByteBufCodecs::optional),
      KineticWeapon::knockbackConditions,
      KineticWeapon.Condition.STREAM_CODEC.apply(ByteBufCodecs::optional),
      KineticWeapon::damageConditions,
      ByteBufCodecs.FLOAT,
      KineticWeapon::forwardMovement,
      ByteBufCodecs.FLOAT,
      KineticWeapon::damageMultiplier,
      SoundEvent.STREAM_CODEC.apply(ByteBufCodecs::optional),
      KineticWeapon::sound,
      SoundEvent.STREAM_CODEC.apply(ByteBufCodecs::optional),
      KineticWeapon::hitSound,
      KineticWeapon::new
   );

   public static Vec3 getMotion(Entity entity) {
      if (!(entity instanceof Player) && entity.isPassenger()) {
         entity = entity.getRootVehicle();
      }

      return ((MotionAwareEntity)entity).theSift$getKnownSpeed().scale(20.0);
   }

   public void makeSound(Entity causer) {
      this.sound
         .ifPresent(
            sound -> causer.level()
               .playSound(causer instanceof Player player ? player : null, causer.getX(), causer.getY(), causer.getZ(), (Holder<SoundEvent>)sound, causer.getSoundSource(), 1.0F, 1.0F)
         );
   }

   public void makeLocalHitSound(Entity causer) {
      this.hitSound.ifPresent(sound -> causer.level().playLocalSound(causer, (SoundEvent)sound.value(), causer.getSoundSource(), 1.0F, 1.0F));
   }

   public int computeDamageUseDuration() {
      return this.delayTicks + this.damageConditions.map(KineticWeapon.Condition::maxDurationTicks).orElse(0);
   }

   public void damageEntities(ItemStack stack, int ticksRemaining, LivingEntity attacker, EquipmentSlot equipmentSlot) {
      MobSpearHandler handler = (MobSpearHandler)attacker;
      int ticksUsed = stack.getUseDuration(attacker) - ticksRemaining;
      if (ticksUsed >= this.delayTicks) {
         ticksUsed -= this.delayTicks;
         Vec3 attackerLookVector = attacker.getLookAngle();
         double attackerSpeedProjection = attackerLookVector.dot(getMotion(attacker));
         float actionFactor = attacker instanceof Player ? 1.0F : 0.2F;
         AttackRange attackRange = handler.theSift$getAttackRangeWith(stack);
         double baseMobDamage = attacker.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
         boolean affected = false;

         for (EntityHitResult hitResult : ProjectileUtils.getHitEntitiesAlong(
               attacker, attackRange, e -> PiercingWeapon.canHitEntity(attacker, e), Block.COLLIDER
            )
            .<Collection<EntityHitResult>>map(a -> List.of(), e -> e)) {
            Entity otherEntity = hitResult.getEntity();
            if (otherEntity instanceof EnderDragonPart part) {
               otherEntity = (Entity)part.parentMob;
            }

            // Keep the verifier type explicit across the EnderDragonPart merge.
            // Some remap/ASM pipelines otherwise collapse this local to Object in the StackMapTable.
            otherEntity = (Entity)(Object)otherEntity;

            boolean wasStabbed = handler.theSift$wasRecentlyStabbed(otherEntity, this.contactCooldownTicks);
            if (!wasStabbed) {
               handler.theSift$rememberStabbedEntity(otherEntity);
               double targetSpeedProjection = attackerLookVector.dot(getMotion(otherEntity));
               double relativeSpeed = Math.max(0.0, attackerSpeedProjection - targetSpeedProjection);
               boolean dealsDismount = this.dismountConditions.isPresent()
                  && this.dismountConditions.get().test(ticksUsed, attackerSpeedProjection, relativeSpeed, actionFactor);
               boolean dealsKnockback = this.knockbackConditions.isPresent()
                  && this.knockbackConditions.get().test(ticksUsed, attackerSpeedProjection, relativeSpeed, actionFactor);
               boolean dealsDamage = this.damageConditions.isPresent()
                  && this.damageConditions.get().test(ticksUsed, attackerSpeedProjection, relativeSpeed, actionFactor);
               if (dealsDismount || dealsKnockback || dealsDamage) {
                  float damageDealt = (float)baseMobDamage + Mth.floor(relativeSpeed * this.damageMultiplier);
                  // Reassert Entity at the final branch merge so the 1.21.1 verifier does not widen this local to Object.
                  affected |= handler.theSift$stabAttack(
                     equipmentSlot, (Entity)(Object)otherEntity, damageDealt, dealsDamage, dealsKnockback, dealsDismount
                  );
               }
            }
         }

         if (affected) {
            attacker.level().broadcastEntityEvent(attacker, (byte)2);
            if (attacker instanceof ServerPlayer player) {
               
            }
         }
      }
   }

   public static KineticWeapon get(ItemStack stack) {
      Object component = stack.get(ModDataComponents.KINETIC_WEAPON.get());
      if (component == null) {
         return null;
      } else if (component instanceof KineticWeapon weapon) {
         return weapon;
      } else {
         try {
            Class<?> clazz = component.getClass();
            int contactCooldownTicks = (Integer)clazz.getMethod("contactCooldownTicks").invoke(component);
            int delayTicks = (Integer)clazz.getMethod("delayTicks").invoke(component);
            Optional<KineticWeapon.Condition> dismountConditions = convertConditionOpt(clazz.getMethod("dismountConditions").invoke(component));
            Optional<KineticWeapon.Condition> knockbackConditions = convertConditionOpt(clazz.getMethod("knockbackConditions").invoke(component));
            Optional<KineticWeapon.Condition> damageConditions = convertConditionOpt(clazz.getMethod("damageConditions").invoke(component));
            float forwardMovement = (Float)clazz.getMethod("forwardMovement").invoke(component);
            float damageMultiplier = (Float)clazz.getMethod("damageMultiplier").invoke(component);
            Optional<Holder<SoundEvent>> sound = (Optional<Holder<SoundEvent>>)clazz.getMethod("sound").invoke(component);
            Optional<Holder<SoundEvent>> hitSound = (Optional<Holder<SoundEvent>>)clazz.getMethod("hitSound").invoke(component);
            return new KineticWeapon(
               contactCooldownTicks, delayTicks, dismountConditions, knockbackConditions, damageConditions, forwardMovement, damageMultiplier, sound, hitSound
            );
         } catch (Exception var12) {
            return null;
         }
      }
   }

   private static Optional<KineticWeapon.Condition> convertConditionOpt(Object optRaw) {
      if (optRaw instanceof Optional<?> opt && !opt.isEmpty()) {
         Object rawCond = opt.get();
         if (rawCond instanceof KineticWeapon.Condition cond) {
            return Optional.of(cond);
         } else {
            try {
               Class<?> condClass = rawCond.getClass();
               int maxDurationTicks = (Integer)condClass.getMethod("maxDurationTicks").invoke(rawCond);
               float minSpeed = (Float)condClass.getMethod("minSpeed").invoke(rawCond);
               float minRelativeSpeed = (Float)condClass.getMethod("minRelativeSpeed").invoke(rawCond);
               return Optional.of(new KineticWeapon.Condition(maxDurationTicks, minSpeed, minRelativeSpeed));
            } catch (Exception var7) {
               return Optional.empty();
            }
         }
      } else {
         return Optional.empty();
      }
   }

   public record Condition(int maxDurationTicks, float minSpeed, float minRelativeSpeed) {
      public static final Codec<KineticWeapon.Condition> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(
               ExtraCodecs.NON_NEGATIVE_INT.fieldOf("max_duration_ticks").forGetter(KineticWeapon.Condition::maxDurationTicks),
               Codec.FLOAT.optionalFieldOf("min_speed", 0.0F).forGetter(KineticWeapon.Condition::minSpeed),
               Codec.FLOAT.optionalFieldOf("min_relative_speed", 0.0F).forGetter(KineticWeapon.Condition::minRelativeSpeed)
            )
            .apply(instance, KineticWeapon.Condition::new)
      );
      public static final StreamCodec<ByteBuf, KineticWeapon.Condition> STREAM_CODEC = StreamCodec.composite(
         ByteBufCodecs.VAR_INT,
         KineticWeapon.Condition::maxDurationTicks,
         ByteBufCodecs.FLOAT,
         KineticWeapon.Condition::minSpeed,
         ByteBufCodecs.FLOAT,
         KineticWeapon.Condition::minRelativeSpeed,
         KineticWeapon.Condition::new
      );

      public boolean test(int ticksUsed, double attackerSpeed, double relativeSpeed, double entityFactor) {
         return ticksUsed <= this.maxDurationTicks && attackerSpeed >= this.minSpeed * entityFactor && relativeSpeed >= this.minRelativeSpeed * entityFactor;
      }

      public static Optional<KineticWeapon.Condition> ofAttackerSpeed(int untilTicks, float minAttackerSpeed) {
         return Optional.of(new KineticWeapon.Condition(untilTicks, minAttackerSpeed, 0.0F));
      }

      public static Optional<KineticWeapon.Condition> ofRelativeSpeed(int untilTicks, float minRelativeSpeed) {
         return Optional.of(new KineticWeapon.Condition(untilTicks, 0.0F, minRelativeSpeed));
      }
   }
}
