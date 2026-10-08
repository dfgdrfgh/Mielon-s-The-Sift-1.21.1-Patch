package mielon.thesift.item;

import java.util.List;
import mielon.thesift.fluid.ModFluids;
import mielon.thesift.platform.NeoEvents;
import mielon.thesift.sound.ModSounds;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;

public final class IchorBottleItem extends Item {
   private static boolean interactionsRegistered;

   public IchorBottleItem(Properties properties) {
      super(properties);
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
      PotionContents contents = (PotionContents)stack.get(DataComponents.POTION_CONTENTS);
      if (contents != null) {
         contents.addPotionTooltip(lines::add, 1.0F, context.tickRate());
      }
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      return ItemUtils.startUsingInstantly(level, player, hand);
   }

   public int getUseDuration(ItemStack stack, LivingEntity entity) {
      return 32;
   }

   public UseAnim getUseAnimation(ItemStack stack) {
      return UseAnim.DRINK;
   }

   public SoundEvent getDrinkingSound() {
      return SoundEvents.GENERIC_DRINK;
   }

   public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
      if (entity instanceof ServerPlayer player) {
         CriteriaTriggers.CONSUME_ITEM.trigger(player, stack);
         player.awardStat(Stats.ITEM_USED.get(this));
      }

      if (!level.isClientSide()) {
         entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0));
      }

      stack.consume(1, entity);
      if (stack.isEmpty()) {
         return new ItemStack(Items.GLASS_BOTTLE);
      } else {
         if (entity instanceof Player player && !player.hasInfiniteMaterials()) {
            ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
            if (!player.getInventory().add(bottle)) {
               player.drop(bottle, false);
            }
         }

         return stack;
      }
   }

   public static void registerInteractions() {
      if (!interactionsRegistered) {
         interactionsRegistered = true;
         NeoEvents.UseItemCallback.EVENT.register(IchorBottleItem::fillFromSource);
      }
   }

   private static InteractionResult fillFromSource(Player player, Level level, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (stack.is(Items.GLASS_BOTTLE) && !player.isSpectator()) {
         BlockHitResult hit = getPlayerPOVHitResult(level, player, Fluid.SOURCE_ONLY);
         if (hit.getType() != Type.BLOCK) {
            return InteractionResult.PASS;
         } else {
            BlockPos pos = hit.getBlockPos();
            FluidState fluid = level.getFluidState(pos);
            if (!fluid.isSource() || !fluid.getType().isSame(ModFluids.ICHOR)) {
               return InteractionResult.PASS;
            } else if (level.mayInteract(player, pos) && player.mayUseItemAt(pos, hit.getDirection(), stack)) {
               if (!level.isClientSide()) {
                  ItemStack filled = new ItemStack(ModItems.ICHOR_BOTTLE);
                  player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, filled));
                  player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                  level.playSound(null, pos, ModSounds.ICHOR_BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                  level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
               }

               return InteractionResult.SUCCESS;
            } else {
               return InteractionResult.PASS;
            }
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   public static ItemInteractionResult fillFromCauldron(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
      int levelValue = (Integer)state.getValue(LayeredCauldronBlock.LEVEL);
      if (levelValue <= 0) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      } else {
         if (!level.isClientSide()) {
            ItemStack filled = new ItemStack(ModItems.ICHOR_BOTTLE);
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, filled));
            player.awardStat(Stats.USE_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            LayeredCauldronBlock.lowerFillLevel(state, level, pos);
            level.playSound(null, pos, ModSounds.ICHOR_BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
         }

         return ItemInteractionResult.SUCCESS;
      }
   }
}
