package mielon.thesift.mixin;

import java.util.Comparator;
import mielon.thesift.entity.DarkSnifferEntity;
import mielon.thesift.entity.ModEntities;
import mielon.thesift.entity.SnifferInfectionAccess;
import mielon.thesift.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.animal.sniffer.Sniffer.State;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Sniffer.class})
public abstract class SnifferInfectionMixin implements SnifferInfectionAccess {
   @Unique
   private static final int THE_SIFT$CONVERSION_TICKS = 600;
   @Unique
   private static final EntityDataAccessor<Boolean> THE_SIFT$INFECTED = SynchedEntityData.defineId(Sniffer.class, EntityDataSerializers.BOOLEAN);
   @Unique
   private static final ResourceKey<LootTable> THE_SIFT$DARK_DIGGING_LOOT = ResourceKey.create(
      Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("the_sift", "gameplay/dark_sniffer_digging")
   );
   @Unique
   private static final ResourceKey<LootTable> THE_SIFT$INFECTED_DIGGING_LOOT = ResourceKey.create(
      Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("the_sift", "gameplay/infected_sniffer_digging")
   );
   @Unique
   private int theSift$infectionTicks;
   @Unique
   private static final double THE_SIFT$DARK_SNIFFER_FLEE_RANGE = 20.0;

   @Inject(
      method = {"defineSynchedData"},
      at = {@At("TAIL")}
   )
   private void theSift$defineInfectionFlag(Builder builder, CallbackInfo ci) {
      builder.define(THE_SIFT$INFECTED, false);
   }

   @Redirect(
      method = {"canDig(Lnet/minecraft/core/BlockPos;)Z"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z"
      )
   )
   private boolean theSift$restrictDarkSnifferDigging(BlockState state, TagKey<Block> vanillaDiggableTag) {
      return (Object)this instanceof DarkSnifferEntity ? state.is(Blocks.SCULK) : state.is(vanillaDiggableTag);
   }

   @ModifyArg(
      method = {"dropSeed"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/server/ReloadableServerRegistries$Holder;getLootTable(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/level/storage/loot/LootTable;"
      ),
      index = 0
   )
   private ResourceKey<LootTable> theSift$selectDiggingLoot(ResourceKey<LootTable> vanilla) {
      if ((Object)this instanceof DarkSnifferEntity) {
         return THE_SIFT$DARK_DIGGING_LOOT;
      } else {
         return this.theSift$isSculkInfected() ? THE_SIFT$INFECTED_DIGGING_LOOT : vanilla;
      }
   }

   @Inject(
      method = {"transitionTo"},
      at = {@At("HEAD")}
   )
   private void theSift$beginSculkInfection(State state, CallbackInfoReturnable<Sniffer> cir) {
      if (state == State.DIGGING && !((Object)this instanceof DarkSnifferEntity)) {
         Sniffer sniffer = (Sniffer)(Object)this;
         Vec3 head = sniffer.position().add(sniffer.getForward().scale(2.25));
         BlockPos sniffed = BlockPos.containing(head.x(), sniffer.getY() + 0.2, head.z()).below();
         if (sniffer.level().getBlockState(sniffed).is(Blocks.SCULK) && this.theSift$infectionTicks <= 0) {
            this.theSift$setSculkInfectionTicks(1);
         }
      }
   }

   @Inject(
      method = {"tick"},
      at = {@At("TAIL")}
   )
   private void theSift$tickSculkInfection(CallbackInfo ci) {
      Sniffer sniffer = (Sniffer)(Object)this;
      theSift$fleeFromDarkSniffer(sniffer);
      if (!((Object)this instanceof DarkSnifferEntity) && !sniffer.level().isClientSide() && this.theSift$infectionTicks > 0) {
         this.theSift$infectionTicks++;
         if (this.theSift$infectionTicks >= 600) {
            sniffer.playSound(ModSounds.DARK_SNIFFER_BLOOM, 1.0F, 0.72F);
            DarkSnifferEntity converted = (DarkSnifferEntity)sniffer.convertTo(ModEntities.DARK_SNIFFER, true);
            if (converted != null) {
               converted.setHealth(Math.min(converted.getMaxHealth(), sniffer.getHealth()));
               if (sniffer.isPersistenceRequired()) {
                  converted.setPersistenceRequired();
               }
            }
         }
      }
   }

   @Unique
   private static void theSift$fleeFromDarkSniffer(Sniffer sniffer) {
      if (!(sniffer instanceof DarkSnifferEntity) && sniffer.level() instanceof ServerLevel level) {
         DarkSnifferEntity threat = level.getEntitiesOfClass(DarkSnifferEntity.class, sniffer.getBoundingBox().inflate(20.0), entity -> entity.isAlive())
            .stream()
            .min(Comparator.comparingDouble(sniffer::distanceToSqr))
            .orElse(null);
         if (threat != null) {
            Vec3 away = DefaultRandomPos.getPosAway(sniffer, 16, 8, threat.position());
            if (away != null) {
               sniffer.getNavigation().moveTo(away.x(), away.y(), away.z(), 1.25);
            }
         }
      }
   }

   @Override
   public boolean theSift$isSculkInfected() {
      Sniffer sniffer = (Sniffer)(Object)this;
      return (Boolean)sniffer.getEntityData().get(THE_SIFT$INFECTED);
   }

   @Override
   public int theSift$getSculkInfectionTicks() {
      return this.theSift$infectionTicks;
   }

   @Override
   public void theSift$setSculkInfectionTicks(int ticks) {
      this.theSift$infectionTicks = Math.max(0, ticks);
      Sniffer sniffer = (Sniffer)(Object)this;
      sniffer.getEntityData().set(THE_SIFT$INFECTED, this.theSift$infectionTicks > 0);
   }
}
