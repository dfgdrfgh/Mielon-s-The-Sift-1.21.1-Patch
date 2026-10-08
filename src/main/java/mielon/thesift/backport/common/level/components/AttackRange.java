package mielon.thesift.backport.common.level.components;

import mielon.thesift.backport.common.api.extensions.entity.movement.MotionAwareEntity;
import mielon.thesift.backport.common.registries.items.ModDataComponents;
import mielon.thesift.backport.core.util.AdditionalCodecs;
import mielon.thesift.backport.core.util.ProjectileUtils;
import mielon.thesift.backport.core.util.Utilities;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;


public record AttackRange(float minReach, float maxReach, float minCreativeReach, float maxCreativeReach, float hitboxMargin, float mobFactor) {
   public static final Codec<AttackRange> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            AdditionalCodecs.floatRange(0.0F, 64.0F).optionalFieldOf("min_reach", 0.0F).forGetter(AttackRange::minReach),
            AdditionalCodecs.floatRange(0.0F, 64.0F).optionalFieldOf("max_reach", 0.0F).forGetter(AttackRange::maxReach),
            AdditionalCodecs.floatRange(0.0F, 64.0F).optionalFieldOf("min_creative_reach", 0.0F).forGetter(AttackRange::minCreativeReach),
            AdditionalCodecs.floatRange(0.0F, 64.0F).optionalFieldOf("max_creative_reach", 0.0F).forGetter(AttackRange::maxCreativeReach),
            AdditionalCodecs.floatRange(0.0F, 1.0F).optionalFieldOf("hitbox_margin", 0.0F).forGetter(AttackRange::hitboxMargin),
            Codec.floatRange(0.0F, 2.0F).optionalFieldOf("mob_factor", 1.0F).forGetter(AttackRange::mobFactor)
         )
         .apply(instance, AttackRange::new)
   );
   public static final StreamCodec<ByteBuf, AttackRange> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.FLOAT,
      AttackRange::minReach,
      ByteBufCodecs.FLOAT,
      AttackRange::maxReach,
      ByteBufCodecs.FLOAT,
      AttackRange::minCreativeReach,
      ByteBufCodecs.FLOAT,
      AttackRange::maxCreativeReach,
      ByteBufCodecs.FLOAT,
      AttackRange::hitboxMargin,
      ByteBufCodecs.FLOAT,
      AttackRange::mobFactor,
      AttackRange::new
   );

   public static AttackRange defaultFor(LivingEntity entity) {
      return new AttackRange(
         0.0F,
         (float)entity.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE),
         0.0F,
         (float)entity.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE),
         0.0F,
         1.0F
      );
   }

   public HitResult getClosestHit(Entity attacker, float partial, Predicate<Entity> matching) {
      Either<BlockHitResult, Collection<EntityHitResult>> result = ProjectileUtils.getHitEntitiesAlong(attacker, this, matching, Block.OUTLINE);
      if (result.left().isPresent()) {
         return (HitResult)result.left().get();
      } else {
         EntityHitResult entity = null;
         Vec3 attackerPos = attacker.getEyePosition(partial);
         double closestDistance = Double.MAX_VALUE;

         for (EntityHitResult target : result.right().get()) {
            double distance = attackerPos.distanceToSqr(target.getLocation());
            if (distance < closestDistance) {
               closestDistance = distance;
               entity = target;
            }
         }

         if (entity != null) {
            return entity;
         } else {
            Vec3 eyeGaze = ((MotionAwareEntity)attacker).theSift$getHeadLookAngle();
            Vec3 missPosition = attacker.getEyePosition(partial).add(eyeGaze);
            return BlockHitResult.miss(missPosition, Utilities.DirectionUtils.getApproximateNearest(eyeGaze), BlockPos.containing(missPosition));
         }
      }
   }

   public float effectiveMinRange(Entity entity) {
      if (entity instanceof Player player) {
         return player.isCreative() ? this.minCreativeReach : this.minReach;
      } else {
         return this.minReach * this.mobFactor;
      }
   }

   public float effectiveMaxRange(Entity entity) {
      if (entity instanceof Player player) {
         return player.isCreative() ? this.maxCreativeReach : this.maxReach;
      } else {
         return this.maxReach * this.mobFactor;
      }
   }

   public boolean isInRange(LivingEntity attacker, Vec3 location) {
      return this.isInRange(attacker, location::distanceToSqr, 0.0);
   }

   public boolean isInRange(LivingEntity attacker, AABB boundingBox, double extraBuffer) {
      return this.isInRange(attacker, boundingBox::distanceToSqr, extraBuffer);
   }

   private boolean isInRange(LivingEntity attacker, ToDoubleFunction<Vec3> distanceFunction, double extraBuffer) {
      double distance = Math.sqrt(distanceFunction.applyAsDouble(attacker.getEyePosition()));
      double minReach = this.effectiveMinRange(attacker) - this.hitboxMargin - extraBuffer;
      double maxReach = this.effectiveMaxRange(attacker) + this.hitboxMargin + extraBuffer;
      return distance >= minReach && distance <= maxReach;
   }

   public static AttackRange get(ItemStack stack) {
      Object component = stack.get(ModDataComponents.ATTACK_RANGE.get());
      if (component == null) {
         return null;
      } else {
         return component instanceof AttackRange range ? range : parseForeign(component);
      }
   }

   
   private static AttackRange parseForeign(Object component) {
      try {
         Class<?> clazz = component.getClass();
         float minReach = (Float)clazz.getMethod("minReach").invoke(component);
         float maxReach = (Float)clazz.getMethod("maxReach").invoke(component);
         float minCreativeReach = (Float)clazz.getMethod("minCreativeReach").invoke(component);
         float maxCreativeReach = (Float)clazz.getMethod("maxCreativeReach").invoke(component);
         float hitboxMargin = (Float)clazz.getMethod("hitboxMargin").invoke(component);
         float mobFactor = (Float)clazz.getMethod("mobFactor").invoke(component);
         return new AttackRange(minReach, maxReach, minCreativeReach, maxCreativeReach, hitboxMargin, mobFactor);
      } catch (Exception var8) {
         return null;
      }
   }
}
