package mielon.thesift.block;

import java.util.Map;
import mielon.thesift.fluid.ModFluids;
import mielon.thesift.item.IchorBottleItem;
import mielon.thesift.item.ModItems;
import mielon.thesift.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.cauldron.CauldronInteraction.InteractionMap;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

final class IchorCauldronInteractions {
   private static boolean registered;

   private IchorCauldronInteractions() {
   }

   static void register() {
      if (!registered) {
         registered = true;
         InteractionMap ichor = IchorCauldronBlock.INTERACTIONS;
         CauldronInteraction.addDefaultInteractions(ichor.map());
         itemInteractions(CauldronInteraction.EMPTY)
            .put(
               ModFluids.ICHOR_BUCKET,
               (state, level, pos, player, hand, stack) -> CauldronInteraction.emptyBucket(
                  level,
                  pos,
                  player,
                  hand,
                  stack,
                  (BlockState)ModBlocks.ICHOR_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3),
                  ModSounds.ICHOR_BUCKET_EMPTY
               )
            );
         itemInteractions(CauldronInteraction.EMPTY).put(ModItems.ICHOR_BOTTLE, IchorCauldronInteractions::fillFromBottle);
         itemInteractions(ichor).put(ModItems.ICHOR_BOTTLE, IchorCauldronInteractions::fillFromBottle);
         itemInteractions(ichor)
            .put(
               Items.BUCKET,
               (state, level, pos, player, hand, stack) -> CauldronInteraction.fillBucket(
                  state,
                  level,
                  pos,
                  player,
                  hand,
                  stack,
                  new ItemStack(ModFluids.ICHOR_BUCKET),
                  candidate -> candidate.is(ModBlocks.ICHOR_CAULDRON) && (Integer)candidate.getValue(LayeredCauldronBlock.LEVEL) == 3,
                  ModSounds.ICHOR_BUCKET_FILL
               )
            );
         itemInteractions(ichor).put(Items.GLASS_BOTTLE, IchorBottleItem::fillFromCauldron);
      }
   }

   private static Map<Item, CauldronInteraction> itemInteractions(InteractionMap dispatcher) {
      return dispatcher.map();
   }

   private static ItemInteractionResult fillFromBottle(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
      int currentLevel = state.is(ModBlocks.ICHOR_CAULDRON) ? (Integer)state.getValue(LayeredCauldronBlock.LEVEL) : 0;
      if (currentLevel >= 3) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      } else {
         if (!level.isClientSide()) {
            ItemStack emptyBottle = new ItemStack(Items.GLASS_BOTTLE);
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, emptyBottle));
            player.awardStat(Stats.USE_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            BlockState next = (BlockState)ModBlocks.ICHOR_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, currentLevel + 1);
            level.setBlockAndUpdate(pos, next);
            level.playSound(null, pos, ModSounds.ICHOR_BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(player, GameEvent.FLUID_PLACE, pos);
         }

         return ItemInteractionResult.SUCCESS;
      }
   }
}
