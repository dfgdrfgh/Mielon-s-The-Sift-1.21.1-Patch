package mielon.thesift.entity;

import java.util.Optional;
import java.util.UUID;
import mielon.thesift.advancement.ModAdvancements;
import mielon.thesift.block.ModBlocks;
import mielon.thesift.platform.ValueInput;
import mielon.thesift.platform.ValueOutput;
import mielon.thesift.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.MoveControl;

import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public final class BlubEntity extends TamableAnimal implements GeoEntity {
   private static final double PACK_ALERT_RANGE = 16.0;
   private static final int PACK_ANGER_TICKS = 600;
   private static final int FLAPPING_TICKS = 20;
   private static final String BODY_CONTROLLER = "body";
   private static final String HAPPY_TRIGGER = "happy";
   private static final EntityDataAccessor<Boolean> DATA_FLAPPING = SynchedEntityData.defineId(BlubEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_SITTING = SynchedEntityData.defineId(BlubEntity.class, EntityDataSerializers.BOOLEAN);
   private static final RawAnimation HOPPING = RawAnimation.begin().thenLoop("hopping");
   private static final RawAnimation HAPPY = RawAnimation.begin().thenPlay("happy");
   private static final RawAnimation FLAPPING = RawAnimation.begin().thenPlay("flapping");
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private int angerTicks;
   private UUID angerTargetId;
   private int flappingTicks;
   private int nextFlappingTick;
   private int jumpTicks;
   private int jumpDuration;
   private int jumpDelayTicks;
   private int nextIdleSoundTick;
   private boolean wasOnGround;

   public BlubEntity(EntityType<? extends TamableAnimal> type, Level level) {
      super(type, level);
      this.jumpControl = new BlubEntity.BlubJumpControl(this);
      this.moveControl = new BlubEntity.BlubMoveControl(this);
      this.setSpeedModifier(0.0);
      this.nextFlappingTick = 100 + this.random.nextInt(301);
      this.nextIdleSoundTick = 160 + this.random.nextInt(321);
   }

   public static Builder createAttributes() {
      return Mob.createMobAttributes()
         .add(Attributes.MAX_HEALTH, 20.0)
         .add(Attributes.MOVEMENT_SPEED, 0.3)
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(Attributes.FOLLOW_RANGE, 24.0)
         .add(Attributes.STEP_HEIGHT, 1.0);
   }

   public static boolean checkBlubSpawnRules(EntityType<BlubEntity> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
      return Mob.checkMobSpawnRules(type, level, reason, pos, random) && level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_FLAPPING, false);
      builder.define(DATA_SITTING, false);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, true));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.1, stack -> stack.getItem() == ModBlocks.SOUL_BLOCK_ITEM, false));
      this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.1, 10.0F, 2.0F));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 7.0F));
      this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
      this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
      this.targetSelector.addGoal(3, new HurtByTargetGoal(this, new Class[0]));
   }

   public void tick() {
      super.tick();
      if (this.level() instanceof ServerLevel) {
         if (this.flappingTicks > 0 && --this.flappingTicks == 0) {
            this.entityData.set(DATA_FLAPPING, false);
         }

         LivingEntity target = this.getTarget();
         boolean orderedSitting = this.isOrderedToSit();
         if ((Boolean)this.entityData.get(DATA_SITTING) != orderedSitting) {
            this.entityData.set(DATA_SITTING, orderedSitting);
         }

         if (this.isInSittingPose() != orderedSitting) {
            this.setInSittingPose(orderedSitting);
         }

         if (--this.nextIdleSoundTick <= 0) {
            if (target == null && this.isAlive()) {
               this.playAxolotlIdleSound(0.75F, 0.92F + this.random.nextFloat() * 0.22F);
            }

            this.nextIdleSoundTick = 180 + this.random.nextInt(361);
         }

         if (!this.isTame() && this.angerTicks > 0) {
            if (target == null && this.angerTargetId != null && this.level() instanceof ServerLevel level) {
               Player restoredTarget = level.getServer().getPlayerList().getPlayer(this.angerTargetId);
               if (restoredTarget != null
                  && restoredTarget.isAlive()
                  && restoredTarget.level() == this.level()
                  && !restoredTarget.isCreative()
                  && !restoredTarget.isSpectator()) {
                  this.setTarget(restoredTarget);
                  this.setAggressive(true);
                  target = restoredTarget;
               }
            }

            this.angerTicks--;
            if (this.angerTicks == 0 || target != null && !target.isAlive() || target instanceof Player player && (player.isCreative() || player.isSpectator())
               )
             {
               this.setTarget(null);
               this.setAggressive(false);
               this.angerTargetId = null;
            }
         }

         boolean canFlap = this.flappingTicks == 0 && this.getTarget() == null && this.getDeltaMovement().horizontalDistanceSqr() < 0.001;
         if (canFlap && --this.nextFlappingTick <= 0) {
            this.flappingTicks = 20;
            this.entityData.set(DATA_FLAPPING, true);
            this.nextFlappingTick = 140 + this.random.nextInt(401);
         } else if (!canFlap && this.flappingTicks == 0) {
            this.nextFlappingTick = Math.max(this.nextFlappingTick, 40);
         }
      }
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      ItemStack held = player.getItemInHand(hand);
      boolean soulBlock = held.getItem() == ModBlocks.SOUL_BLOCK_ITEM;
      boolean edible = held.get(DataComponents.FOOD) != null;
      if (this.isTame() && (soulBlock || edible) && this.getHealth() < this.getMaxHealth()) {
         if (this.level() instanceof ServerLevel) {
            float healing = soulBlock ? 4.0F : ((FoodProperties)held.get(DataComponents.FOOD)).nutrition();
            this.usePlayerItem(player, hand, held);
            this.heal(healing);
            return InteractionResult.CONSUME;
         } else {
            return InteractionResult.SUCCESS;
         }
      } else if (!this.isTame() && soulBlock && this.getTarget() == null && this.angerTicks <= 0) {
         if (this.level() instanceof ServerLevel level) {
            held.consume(1, player);
            if (this.random.nextInt(3) == 0) {
               this.tame(player);
               if (player instanceof ServerPlayer serverPlayer) {
                  ModAdvancements.award(serverPlayer, "the_sift/blub");
               }

               this.setPersistenceRequired();
               this.getNavigation().stop();
               this.setTarget(null);
               this.setAggressive(false);
               this.angerTicks = 0;
               this.angerTargetId = null;
               this.setBlubSitting(false);
               this.setJumping(false);
               this.facePoint(player.getX(), player.getZ());
               this.setYBodyRot(this.getYRot());
               this.setYHeadRot(this.getYRot());
               this.playAxolotlIdleSound(0.95F, 1.08F);
               this.triggerAnim("body", "happy");
               level.broadcastEntityEvent(this, (byte)7);
            } else {
               level.broadcastEntityEvent(this, (byte)6);
            }

            return InteractionResult.CONSUME;
         } else {
            return InteractionResult.SUCCESS;
         }
      } else if (!this.isTame() || !this.isOwnedBy(player)) {
         return super.mobInteract(player, hand);
      } else if (this.level() instanceof ServerLevel) {
         this.setBlubSitting(!this.isBlubSitting());
         this.setJumping(false);
         this.getNavigation().stop();
         this.setTarget(null);
         return InteractionResult.CONSUME;
      } else {
         return InteractionResult.SUCCESS;
      }
   }

   public boolean hurt(DamageSource source, float amount) {
      if (!(this.level() instanceof ServerLevel level)) {
         return false;
      } else {
         boolean var10 = super.hurt(source, amount);
         if (var10 && !this.isTame() && source.getEntity() instanceof Player player && !player.isCreative() && !player.isSpectator()) {
            AABB alertArea = this.getBoundingBox().inflate(16.0);

            for (BlubEntity blub : level.getEntitiesOfClass(BlubEntity.class, alertArea, candidate -> !candidate.isTame() && candidate.isAlive())) {
               blub.setBlubSitting(false);
               blub.setTarget(player);
               blub.setAggressive(true);
               blub.angerTicks = 600;
               blub.angerTargetId = player.getUUID();
               blub.getNavigation().moveTo(player, 1.2);
            }

            return true;
         } else {
            return var10;
         }
      }
   }

   public void addAdditionalSaveData(CompoundTag tag) {
      ValueOutput output = new ValueOutput(tag, this.registryAccess());
      super.addAdditionalSaveData(tag);
      if (!this.isTame() && this.angerTicks > 0) {
         output.putInt("TheSiftAngerTicks", this.angerTicks);
         if (this.angerTargetId != null) {
            output.putString("TheSiftAngryAt", this.angerTargetId.toString());
         }
      }
   }

   public void readAdditionalSaveData(CompoundTag tag) {
      ValueInput input = new ValueInput(tag, this.registryAccess());
      super.readAdditionalSaveData(tag);
      this.angerTicks = Math.max(0, input.getIntOr("TheSiftAngerTicks", 0));
      this.angerTargetId = input.getString("TheSiftAngryAt").flatMap(value -> {
         try {
            return Optional.of(UUID.fromString(value));
         } catch (IllegalArgumentException var2x) {
            return Optional.empty();
         }
      }).orElse(null);
      if (this.isTame()) {
         this.angerTicks = 0;
         this.angerTargetId = null;
      }
   }

   public boolean canAttack(LivingEntity target) {
      if (target instanceof BlubEntity) {
         return false;
      } else if (target instanceof OwnableEntity pet && pet.getOwner() != null) {
         return false;
      } else {
         return !(target instanceof Player player && (player.isCreative() || player.isSpectator()))
            ? !this.isOwnedBy(target) && super.canAttack(target)
            : false;
      }
   }

   public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
      if (target instanceof BlubEntity) {
         return false;
      } else {
         return target instanceof OwnableEntity pet && pet.getOwner() != null ? false : super.wantsToAttack(target, owner);
      }
   }

   private boolean isMovingForAnimation() {
      return this.isJumping() || !this.onGround() || this.getDeltaMovement().horizontalDistanceSqr() > 0.001;
   }

   public boolean isBlubSitting() {
      return (Boolean)this.entityData.get(DATA_SITTING);
   }

   private void setBlubSitting(boolean sitting) {
      this.setOrderedToSit(sitting);
      this.setInSittingPose(sitting);
      this.entityData.set(DATA_SITTING, sitting);
      if (sitting) {
         this.setJumping(false);
         this.getNavigation().stop();
         ((BlubEntity.BlubMoveControl)this.moveControl).setWait();
         this.setDeltaMovement(0.0, this.getDeltaMovement().y, 0.0);
      }
   }

   private double hoppingAnimationSpeed() {
      double horizontalSpeed = Math.sqrt(this.getDeltaMovement().horizontalDistanceSqr());
      return Mth.clamp(0.72 + horizontalSpeed * 7.0, 0.72, 2.35);
   }

   public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
      return null;
   }

   public boolean canMate(Animal other) {
      return false;
   }

   public boolean isFood(ItemStack stack) {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return this.isInWater() ? ModSounds.BLUB_IDLE_WATER : ModSounds.BLUB_IDLE_AIR;
   }

   private void playAxolotlIdleSound(float volume, float pitch) {
      if (!this.level().isClientSide()) {
         this.level()
            .playSound(null, this.blockPosition(), this.isInWater() ? ModSounds.BLUB_IDLE_WATER : ModSounds.BLUB_IDLE_AIR, this.getSoundSource(), volume, pitch);
      }
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return ModSounds.BLUB_HURT;
   }

   protected SoundEvent getDeathSound() {
      return ModSounds.BLUB_DEATH;
   }

   protected SoundEvent getSwimSound() {
      return ModSounds.BLUB_SWIM;
   }

   protected SoundEvent getSwimSplashSound() {
      return ModSounds.BLUB_SPLASH;
   }

   public boolean doHurtTarget(Entity target) {
      boolean hit = super.doHurtTarget(target);
      if (hit) {
         this.playSound(ModSounds.BLUB_ATTACK, 0.7F, 0.95F + this.random.nextFloat() * 0.15F);
      }

      return hit;
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
   }

   protected float getJumpPower() {
      float height = this.moveControl.getSpeedModifier() > 1.1 ? 0.36F : 0.3F;
      Path path = this.navigation.getPath();
      if (path != null && !path.isDone() && path.getNextEntityPos(this).y > this.getY() + 0.5) {
         height = 0.5F;
      }

      if (this.horizontalCollision || this.moveControl.getWantedY() > this.getY() + 0.5) {
         height = 0.5F;
      }

      return super.getJumpPower(height / 0.42F);
   }

   public void jumpFromGround() {
      super.jumpFromGround();
      if (this.moveControl.getSpeedModifier() > 0.0 && this.getDeltaMovement().horizontalDistanceSqr() < 0.01) {
         this.moveRelative(0.1F, new Vec3(0.0, 0.0, 1.0));
      }
   }

   public void setJumping(boolean jumping) {
      super.setJumping(jumping);
   }

   private void startJumping() {
      this.setJumping(true);
      this.jumpDuration = 15;
      this.jumpTicks = 0;
   }

   private void setSpeedModifier(double speed) {
      this.navigation.setSpeedModifier(speed);
      this.moveControl.setWantedPosition(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ(), speed);
   }

   private void facePoint(double x, double z) {
      this.setYRot((float)(Mth.atan2(z - this.getZ(), x - this.getX()) * (180.0 / Math.PI)) - 90.0F);
   }

   private void setLandingDelay() {
      double speed = this.moveControl.getSpeedModifier();
      this.jumpDelayTicks = speed >= 1.15 ? 4 : (speed >= 1.05 ? 6 : 8);
      ((BlubEntity.BlubJumpControl)this.jumpControl).setCanJump(false);
   }

   protected void customServerAiStep() {
      ServerLevel level = (ServerLevel)this.level();
      super.customServerAiStep();
      if (this.jumpDelayTicks > 0) {
         this.jumpDelayTicks--;
      }

      if (this.isBlubSitting()) {
         this.setJumping(false);
         ((BlubEntity.BlubMoveControl)this.moveControl).setWait();
         this.wasOnGround = this.onGround();
      } else {
         if (this.onGround()) {
            if (!this.wasOnGround) {
               this.setJumping(false);
               this.setLandingDelay();
            }

            BlubEntity.BlubJumpControl jumps = (BlubEntity.BlubJumpControl)this.jumpControl;
            if (!jumps.wantJump() && this.moveControl.hasWanted() && this.jumpDelayTicks == 0) {
               Vec3 destination = new Vec3(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ());
               Path path = this.navigation.getPath();
               if (path != null && !path.isDone()) {
                  destination = path.getNextEntityPos(this);
               }

               this.facePoint(destination.x, destination.z);
               this.startJumping();
            } else if (jumps.wantJump() && !jumps.canJump()) {
               jumps.setCanJump(true);
            }
         }

         this.wasOnGround = this.onGround();
      }
   }

   public void aiStep() {
      super.aiStep();
      if (this.jumpTicks != this.jumpDuration) {
         this.jumpTicks++;
      } else if (this.jumpDuration != 0) {
         this.jumpTicks = 0;
         this.jumpDuration = 0;
         this.setJumping(false);
      }
   }

   public boolean canSpawnSprintParticle() {
      return false;
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController(this, "body", 2, state -> {
         BlubEntity blub = (BlubEntity)state.getAnimatable();
         AnimationController<BlubEntity> controller = state.getController();
         if (controller.isPlayingTriggeredAnimation()) {
            controller.setAnimationSpeed(1.0);
            return PlayState.CONTINUE;
         } else if (blub.isBlubSitting()) {
            controller.setAnimationSpeed(1.0);
            controller.forceAnimationReset();
            return PlayState.STOP;
         } else if (blub.isMovingForAnimation()) {
            controller.setAnimationSpeed(blub.hoppingAnimationSpeed());
            controller.setAnimation(HOPPING);
            return PlayState.CONTINUE;
         } else {
            controller.setAnimationSpeed(1.0);
            controller.forceAnimationReset();
            return PlayState.STOP;
         }
      }).triggerableAnim("happy", HAPPY).receiveTriggeredAnimations());
      controllers.add(new AnimationController(this, "ears", 1, state -> {
         BlubEntity blub = (BlubEntity)state.getAnimatable();
         if ((Boolean)blub.entityData.get(DATA_FLAPPING)) {
            state.getController().setAnimation(FLAPPING);
            return PlayState.CONTINUE;
         } else {
            state.getController().forceAnimationReset();
            return PlayState.STOP;
         }
      }));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public boolean isJumping() {
      return this.jumping;
   }

   private static final class BlubJumpControl extends JumpControl {
      private final BlubEntity blub;
      private boolean canJump;

      private BlubJumpControl(BlubEntity blub) {
         super(blub);
         this.blub = blub;
      }

      private boolean wantJump() {
         return this.jump;
      }

      private boolean canJump() {
         return this.canJump;
      }

      private void setCanJump(boolean canJump) {
         this.canJump = canJump;
      }

      public void tick() {
         if (this.jump) {
            this.blub.startJumping();
            this.jump = false;
         }
      }
   }

   private static final class BlubMoveControl extends MoveControl {
      private final BlubEntity mob;
      private double nextJumpSpeed;

      public void setWait() {
         this.operation = Operation.WAIT;
      }

      private BlubMoveControl(BlubEntity blub) {
         super(blub);
         this.mob = blub;
      }

      public void tick() {
         BlubEntity.BlubJumpControl jumps = (BlubEntity.BlubJumpControl)this.mob.getJumpControl();
         if (this.mob.onGround() && !this.mob.isJumping() && !jumps.wantJump()) {
            this.mob.setSpeedModifier(0.0);
         } else if (this.hasWanted() || this.operation == Operation.JUMPING) {
            this.mob.setSpeedModifier(this.nextJumpSpeed);
         }

         super.tick();
      }

      public void setWantedPosition(double x, double y, double z, double speed) {
         if (this.mob.isInWater()) {
            speed = 1.5;
         }

         super.setWantedPosition(x, y, z, speed);
         if (speed > 0.0) {
            this.nextJumpSpeed = speed;
         }
      }
   }
}
