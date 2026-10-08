package mielon.thesift.entity;

import java.util.UUID;
import mielon.thesift.advancement.ModAdvancements;
import mielon.thesift.platform.ValueInput;
import mielon.thesift.platform.ValueOutput;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class SiftiteReturnEntity extends Entity implements ItemSupplier {
   private static final EntityDataAccessor<ItemStack> DATA_ITEM = SynchedEntityData.defineId(SiftiteReturnEntity.class, EntityDataSerializers.ITEM_STACK);
   private UUID ownerId;
   private long deliveryOrder;
   private int lerpSteps;
   private double lerpX;
   private double lerpY;
   private double lerpZ;
   private float lerpYaw;
   private float lerpPitch;

   public void lerpTo(double x, double y, double z, float yaw, float pitch, int steps) {
      this.lerpX = x;
      this.lerpY = y;
      this.lerpZ = z;
      this.lerpYaw = yaw;
      this.lerpPitch = pitch;
      this.lerpSteps = 3;
   }

   private void interpolate() {
      if (this.lerpSteps > 0) {
         double delta = 1.0 / this.lerpSteps;
         this.setPos(
            this.getX() + (this.lerpX - this.getX()) * delta,
            this.getY() + (this.lerpY - this.getY()) * delta,
            this.getZ() + (this.lerpZ - this.getZ()) * delta
         );
         this.setYRot(this.getYRot() + (float)delta * Mth.wrapDegrees(this.lerpYaw - this.getYRot()));
         this.setXRot(this.getXRot() + (float)delta * (this.lerpPitch - this.getXRot()));
         this.lerpSteps--;
      }
   }

   public SiftiteReturnEntity(EntityType<? extends SiftiteReturnEntity> type, Level level) {
      super(type, level);
      this.noPhysics = true;
      this.setNoGravity(true);
      this.setInvulnerable(true);
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_ITEM, ItemStack.EMPTY);
   }

   public void configure(UUID ownerId, ItemStack stack, long deliveryOrder) {
      this.ownerId = ownerId;
      this.deliveryOrder = deliveryOrder;
      this.entityData.set(DATA_ITEM, stack.copy());
   }

   public ItemStack getItem() {
      return (ItemStack)this.entityData.get(DATA_ITEM);
   }

   public void tick() {
      super.tick();
      this.noPhysics = true;
      if (this.level() instanceof ServerLevel level) {
         ServerPlayer owner = this.ownerId == null ? null : level.getServer().getPlayerList().getPlayer(this.ownerId);
         if (owner != null && owner.isAlive() && owner.level() == level) {
            Vec3 center = owner.position().add(0.0, owner.getBbHeight() * 0.62, 0.0);
            boolean waitingForEarlierItem = this.hasEarlierDelivery(level);
            ItemStack remaining = this.getItem().copy();
            if (!waitingForEarlierItem && this.distanceToSqr(center) <= 0.42250000000000004) {
               int countBeforeDelivery = remaining.getCount();
               owner.getInventory().add(remaining);
               if (remaining.getCount() < countBeforeDelivery) {
                  ModAdvancements.award(owner, "the_sift/keep_inventory");
               }

               if (remaining.isEmpty()) {
                  this.discard();
                  return;
               }

               if (remaining.getCount() != this.getItem().getCount()) {
                  this.entityData.set(DATA_ITEM, remaining);
               }
            }

            boolean inventoryFull = waitingForEarlierItem || !canFitAny(owner, this.getItem());
            Vec3 destination;
            if (inventoryFull) {
               double angle = this.tickCount * 0.17 + (this.getId() & 15) * 0.39;
               destination = center.add(Math.cos(angle) * 1.65, 0.35 + Math.sin(angle * 1.7) * 0.35, Math.sin(angle) * 1.65);
            } else {
               destination = center;
            }

            Vec3 delta = destination.subtract(this.position());
            Vec3 desired = delta.scale(inventoryFull ? 0.3 : 0.38);
            Vec3 velocity = this.getDeltaMovement().scale(0.65).add(desired.scale(0.35));
            double maxSpeed = inventoryFull ? 0.45 : 1.25;
            if (velocity.lengthSqr() > maxSpeed * maxSpeed) {
               velocity = velocity.normalize().scale(maxSpeed);
            }

            this.setDeltaMovement(velocity);
            this.move(MoverType.SELF, velocity);
            if ((this.tickCount & 1) == 0) {
               level.sendParticles(ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(), 1, 0.03, 0.03, 0.03, 0.005);
            }
         } else {
            this.setDeltaMovement(Vec3.ZERO);
         }
      } else {
         this.interpolate();
      }
   }

   private boolean hasEarlierDelivery(ServerLevel level) {
      for (SiftiteReturnEntity other : level.getEntitiesOfClass(
         SiftiteReturnEntity.class,
         this.getBoundingBox().inflate(32.0),
         candidate -> candidate != this
            && !candidate.isRemoved()
            && candidate.ownerId != null
            && candidate.ownerId.equals(this.ownerId)
            && !candidate.getItem().isEmpty()
      )) {
         if (other.deliveryOrder < this.deliveryOrder) {
            return true;
         }
      }

      return false;
   }

   private static boolean canFitAny(ServerPlayer player, ItemStack incoming) {
      for (int slot = 0; slot < 36; slot++) {
         ItemStack existing = player.getInventory().getItem(slot);
         if (existing.isEmpty()) {
            return true;
         }

         if (ItemStack.isSameItemSameComponents(existing, incoming) && existing.getCount() < existing.getMaxStackSize()) {
            return true;
         }
      }

      return false;
   }

   public void readAdditionalSaveData(CompoundTag tag) {
      ValueInput input = new ValueInput(tag, this.registryAccess());
      this.ownerId = parseUuid(input.getStringOr("Owner", ""));
      this.deliveryOrder = input.getLongOr("DeliveryOrder", 0L);
      input.<ItemStack>read("Item", ItemStack.CODEC).ifPresent(stack -> this.entityData.set(DATA_ITEM, stack));
   }

   public void addAdditionalSaveData(CompoundTag tag) {
      ValueOutput output = new ValueOutput(tag, this.registryAccess());
      if (this.ownerId != null) {
         output.putString("Owner", this.ownerId.toString());
      }

      output.putLong("DeliveryOrder", this.deliveryOrder);
      if (!this.getItem().isEmpty()) {
         output.store("Item", ItemStack.CODEC, this.getItem());
      }
   }

   private static UUID parseUuid(String value) {
      try {
         return UUID.fromString(value);
      } catch (IllegalArgumentException var2) {
         return null;
      }
   }

   public boolean hurt(DamageSource source, float amount) {
      return this.level() instanceof ServerLevel level ? false : false;
   }

   public boolean isAttackable() {
      return false;
   }

   public boolean isPickable() {
      return false;
   }

   public boolean canBeHitByProjectile() {
      return false;
   }

   public boolean isPushable() {
      return false;
   }

   public boolean canCollideWith(Entity other) {
      return false;
   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public boolean skipAttackInteraction(Entity attacker) {
      return true;
   }
}
