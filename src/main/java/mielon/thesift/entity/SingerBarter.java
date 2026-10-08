package mielon.thesift.entity;

import mielon.thesift.advancement.ModAdvancements;
import mielon.thesift.block.ModBlocks;
import mielon.thesift.item.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public final class SingerBarter {
   private static final Item[] WOOL = new Item[]{
      Items.WHITE_WOOL,
      Items.ORANGE_WOOL,
      Items.MAGENTA_WOOL,
      Items.LIGHT_BLUE_WOOL,
      Items.YELLOW_WOOL,
      Items.LIME_WOOL,
      Items.PINK_WOOL,
      Items.GRAY_WOOL,
      Items.LIGHT_GRAY_WOOL,
      Items.CYAN_WOOL,
      Items.PURPLE_WOOL,
      Items.BLUE_WOOL,
      Items.BROWN_WOOL,
      Items.GREEN_WOOL,
      Items.RED_WOOL,
      Items.BLACK_WOOL
   };

   private SingerBarter() {
   }

   public static InteractionResult interact(SingerEntity singer, Player player, InteractionHand hand) {
      ItemStack held = player.getItemInHand(hand);
      if (!held.is(ModBlocks.SOUL_BLOCK.asItem())) {
         return InteractionResult.PASS;
      } else if (!singer.canBarter()) {
         return InteractionResult.SUCCESS;
      } else {
         if (singer.level() instanceof ServerLevel) {
            singer.beginBarter(player instanceof ServerPlayer serverPlayer ? serverPlayer : null, player.position());
            if (!player.isCreative()) {
               held.shrink(1);
            }
         }

         return InteractionResult.SUCCESS;
      }
   }

   public static void pickUp(SingerEntity singer, ItemEntity dropped) {
      if (singer.canBarter() && dropped.isAlive() && dropped.getItem().is(ModBlocks.SOUL_BLOCK.asItem())) {
         ServerPlayer owner = dropped.getOwner() instanceof ServerPlayer player ? player : null;
         singer.beginBarter(owner, owner != null ? owner.position() : dropped.position());
         singer.take(dropped, 1);
         dropped.getItem().shrink(1);
         if (dropped.getItem().isEmpty()) {
            dropped.discard();
         } else {
            dropped.setItem(dropped.getItem().copy());
         }
      }
   }

   public static void reward(SingerEntity singer, ServerLevel level, ServerPlayer player, Vec3 target) {
      int roll = singer.getRandom().nextInt(200);
      roll -= 30;
      Item item;
      int min;
      int max;
      if (roll < 0) {
         item = Items.IRON_BLOCK;
         min = 2;
         max = 4;
      } else {
         roll -= 20;
         if (roll < 0) {
            item = Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE;
            max = 1;
            min = 1;
         } else {
            roll -= 19;
            if (roll < 0) {
               item = Items.ANCIENT_DEBRIS;
               min = 4;
               max = 8;
            } else {
               roll -= 3;
               if (roll < 0) {
                  item = ModItems.MUSIC_DISC_WELLSPRING;
                  max = 1;
                  min = 1;
               } else {
                  roll -= 20;
                  if (roll < 0) {
                     item = Items.GOLD_BLOCK;
                     min = 1;
                     max = 2;
                  } else {
                     roll -= 11;
                     if (roll < 0) {
                        item = Items.WHEAT_SEEDS;
                        min = 6;
                        max = 16;
                     } else {
                        roll -= 9;
                        if (roll < 0) {
                           item = Items.BEETROOT_SEEDS;
                           min = 4;
                           max = 8;
                        } else {
                           roll -= 10;
                           if (roll < 0) {
                              item = Items.POTATO;
                              min = 6;
                              max = 16;
                           } else {
                              roll -= 10;
                              if (roll < 0) {
                                 item = Items.CARROT;
                                 min = 8;
                                 max = 20;
                              } else {
                                 roll -= 11;
                                 if (roll < 0) {
                                    item = Items.NETHERITE_INGOT;
                                    max = 1;
                                    min = 1;
                                 } else {
                                    roll -= 16;
                                    if (roll < 0) {
                                       item = Items.BOOKSHELF;
                                       min = 4;
                                       max = 12;
                                    } else {
                                       roll -= 8;
                                       if (roll < 0) {
                                          item = Items.END_STONE;
                                          min = 2;
                                          max = 12;
                                       } else {
                                          roll -= 6;
                                          if (roll < 0) {
                                             item = Items.CHORUS_FLOWER;
                                             max = 1;
                                             min = 1;
                                          } else {
                                             roll -= 5;
                                             if (roll < 0) {
                                                item = Items.ECHO_SHARD;
                                                min = 1;
                                                max = 3;
                                             } else {
                                                roll -= 5;
                                                if (roll < 0) {
                                                   item = Items.REDSTONE_BLOCK;
                                                   min = 4;
                                                   max = 16;
                                                } else {
                                                   roll -= 4;
                                                   if (roll < 0) {
                                                      item = Items.NAME_TAG;
                                                      min = 1;
                                                      max = 3;
                                                   } else {
                                                      item = WOOL[singer.getRandom().nextInt(WOOL.length)];
                                                      min = 9;
                                                      max = 24;
                                                   }
                                                }
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      ItemStack stack = new ItemStack(item, min + singer.getRandom().nextInt(max - min + 1));
      Vec3 direction = target.subtract(singer.position()).multiply(1.0, 0.0, 1.0);
      if (direction.lengthSqr() < 0.001) {
         direction = singer.getLookAngle().multiply(1.0, 0.0, 1.0);
      }

      direction = direction.normalize();
      ItemEntity reward = new ItemEntity(level, singer.getX() + direction.x * 0.6, singer.getY() + 1.2, singer.getZ() + direction.z * 0.6, stack);
      reward.setDeltaMovement(direction.scale(0.3).add(0.0, 0.2, 0.0));
      reward.setDefaultPickUpDelay();
      level.addFreshEntity(reward);
      if (player != null) {
         ModAdvancements.award(player, "the_sift/pitch_perfect");
      }
   }
}
