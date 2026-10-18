package mielon.thesift.item;

import mielon.thesift.backport.BackportContent;
import mielon.thesift.block.ModBlocks;
import mielon.thesift.block.SonorousDeepslateBlock;
import mielon.thesift.fluid.ModFluids;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.ItemLike;

import java.util.Map;

/**
 * 1.21.1 Fabric: keep The Sift's complete custom tab while placing the same
 * content beside related vanilla items. Never register duplicate entries in
 * the same vanilla tab, and let the Fabric API propagate entries to Search.
 */
public final class SiftCreativeTabs {
    private SiftCreativeTabs() {}

    public static void register() {
        // Backported items were registered after ModItems; insertion is deferred
        // until the creative tab is populated, so both are available here.
        ItemGroupEvents.modifyEntriesEvent(ModItems.CREATIVE_TAB_KEY).register(entries -> {
            entries.addAfter(ModBlocks.OVERGROWN_WILLOW_PLANKS_ITEM, BackportContent.SHELF_ITEM);
            entries.addAfter(ModItems.SIFTITE_SWORD, BackportContent.SIFTITE_SPEAR);
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register(entries -> {
            after(entries, Items.REINFORCED_DEEPSLATE, ModBlocks.REINFORCED_SIFTSLATE_ITEM);
            after(entries, Items.DEEPSLATE,
                    ModBlocks.SIFTSLATE_ITEM,
                    ModBlocks.COBBLED_SIFTSLATE_ITEM,
                    ModBlocks.COBBLED_SIFTSLATE_STAIRS_ITEM,
                    ModBlocks.COBBLED_SIFTSLATE_SLAB_ITEM,
                    ModBlocks.COBBLED_SIFTSLATE_WALL_ITEM,
                    ModBlocks.SIFTSLATE_BRICKS_ITEM,
                    ModBlocks.SIFTSLATE_BRICK_STAIRS_ITEM,
                    ModBlocks.SIFTSLATE_BRICK_SLAB_ITEM,
                    ModBlocks.SIFTSLATE_BRICK_WALL_ITEM,
                    ModBlocks.CHISELED_SIFTSLATE_ITEM);
            // Follow vanilla's separate logs, planks, stairs, slabs, and
            // wood component families, rather than inserting everything
            // into one giant group after Mangrove Planks.
            after(entries, Items.MANGROVE_LOG, ModBlocks.OVERGROWN_WILLOW_LOG_ITEM);
            after(entries, Items.MANGROVE_WOOD, ModBlocks.OVERGROWN_WILLOW_WOOD_ITEM);
            after(entries, Items.STRIPPED_MANGROVE_LOG, ModBlocks.STRIPPED_OVERGROWN_WILLOW_LOG_ITEM);
            after(entries, Items.STRIPPED_MANGROVE_WOOD, ModBlocks.STRIPPED_OVERGROWN_WILLOW_WOOD_ITEM);
            after(entries, Items.MANGROVE_PLANKS, ModBlocks.OVERGROWN_WILLOW_PLANKS_ITEM);
            after(entries, Items.MANGROVE_STAIRS, ModBlocks.OVERGROWN_WILLOW_STAIRS_ITEM);
            after(entries, Items.MANGROVE_SLAB, ModBlocks.OVERGROWN_WILLOW_SLAB_ITEM);
            after(entries, Items.MANGROVE_FENCE, ModBlocks.OVERGROWN_WILLOW_FENCE_ITEM);
            after(entries, Items.MANGROVE_FENCE_GATE, ModBlocks.OVERGROWN_WILLOW_FENCE_GATE_ITEM);
            after(entries, Items.MANGROVE_DOOR, ModBlocks.OVERGROWN_WILLOW_DOOR_ITEM);
            after(entries, Items.MANGROVE_TRAPDOOR, ModBlocks.OVERGROWN_WILLOW_TRAPDOOR_ITEM);
            after(entries, Items.MANGROVE_PRESSURE_PLATE, ModBlocks.OVERGROWN_WILLOW_PRESSURE_PLATE_ITEM);
            after(entries, Items.MANGROVE_BUTTON, ModBlocks.OVERGROWN_WILLOW_BUTTON_ITEM);
            after(entries, Items.TINTED_GLASS, ModBlocks.ICHOR_GLASS_ITEM, ModBlocks.ICHOR_GLASS_PANE_ITEM);
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.NATURAL_BLOCKS).register(entries -> {
            after(entries, Items.DEEPSLATE_DIAMOND_ORE,
                    ModBlocks.SIFTSLATE_COAL_ORE_ITEM,
                    ModBlocks.SIFTSLATE_DIAMOND_ORE_ITEM,
                    ModBlocks.SIFTSLATE_EMERALD_ORE_ITEM,
                    ModBlocks.SIFTSLATE_CHAROITE_ORE_ITEM,
                    ModBlocks.SIFTSLATE_SIFTITE_ORE_ITEM);
            after(entries, Items.SCULK,
                    ModBlocks.SIFTSLATE_GROWTH_ITEM,
                    ModBlocks.SIFT_RUBBLE_ITEM,
                    ModBlocks.HEALTHY_SCULK_ITEM,
                    ModBlocks.DRY_HEALTHY_SCULK_ITEM,
                    ModBlocks.DRY_HEALTHY_SCULK_GROWTH_ITEM,
                    ModBlocks.HEALTHY_SCULK_SPROUTS_ITEM,
                    ModBlocks.DRY_HEALTHY_SCULK_SPROUTS_ITEM);
            after(entries, Items.SNOW_BLOCK, ModBlocks.ICHOR_SNOW_BLOCK_ITEM, ModBlocks.ICHOR_SNOW_ITEM);
            after(entries, Items.MANGROVE_LEAVES, ModBlocks.OVERGROWN_WILLOW_FOLIAGE_ITEM);
            after(entries, Items.MANGROVE_PROPAGULE, ModBlocks.OVERGROWN_WILLOW_SAPLING_ITEM);
            after(entries, Items.VINE, ModBlocks.OVERGROWN_WILLOW_VINES_ITEM);
            after(entries, Items.HANGING_ROOTS,
                    ModBlocks.OVERGROWN_FRONDS_ITEM,
                    ModBlocks.OVERGROWN_CHARD_ITEM,
                    ModBlocks.OVERGROWN_STALKS_ITEM,
                    ModBlocks.OVERGROWN_HANGING_ROOTS_ITEM,
                    ModBlocks.SIFTSLATE_STALKS_ITEM,
                    ModBlocks.SIFTSLATE_HANGING_ROOTS_ITEM);
            after(entries, Items.SPORE_BLOSSOM,
                    ModBlocks.OVERGROWN_LOTUS_ITEM,
                    ModBlocks.SUNBURST_PLANT_ITEM,
                    ModBlocks.WHISPERBLOOM_ITEM,
                    ModBlocks.SCULKFLOWER_ITEM,
                    ModItems.SCULKFLOWER_SEEDS);
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> {
            after(entries, Items.CHISELED_BOOKSHELF, BackportContent.SHELF_ITEM);
            after(entries, Items.MANGROVE_SIGN, ModBlocks.OVERGROWN_WILLOW_SIGN_ITEM);
            after(entries, Items.MANGROVE_HANGING_SIGN, ModBlocks.OVERGROWN_WILLOW_HANGING_SIGN_ITEM);
            ItemStack horn = sonorousStack(SonorousDeepslateBlock.Mode.HORN);
            ItemStack note = sonorousStack(SonorousDeepslateBlock.Mode.NOTE);
            entries.addAfter(Items.NOTE_BLOCK, horn);
            entries.addAfter(horn, note);
            after(entries, Items.RESPAWN_ANCHOR, ModBlocks.SIFT_PORTAL_ITEM, ModBlocks.SOUL_BLOCK_ITEM);
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(entries -> {
            // Vanilla's Redstone tab only contains the representative Oak
            // wood variants, not all wood types.
            after(entries, Items.OAK_PRESSURE_PLATE, ModBlocks.OVERGROWN_WILLOW_PRESSURE_PLATE_ITEM);
            after(entries, Items.OAK_BUTTON, ModBlocks.OVERGROWN_WILLOW_BUTTON_ITEM);
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
            after(entries, Items.NETHERITE_PICKAXE, ModItems.SIFTITE_PICKAXE);
            after(entries, Items.NETHERITE_AXE, ModItems.SIFTITE_AXE);
            after(entries, Items.NETHERITE_SHOVEL, ModItems.SIFTITE_SHOVEL);
            after(entries, Items.NETHERITE_HOE, ModItems.SIFTITE_HOE);
            after(entries, Items.MANGROVE_BOAT, ModItems.OVERGROWN_WILLOW_BOAT);
            after(entries, Items.MANGROVE_CHEST_BOAT, ModItems.OVERGROWN_WILLOW_CHEST_BOAT);
            after(entries, Items.WATER_BUCKET, ModFluids.ICHOR_BUCKET);
            after(entries, Items.MUSIC_DISC_PRECIPICE,
                    ModItems.MUSIC_DISC_RIFT, ModItems.MUSIC_DISC_WELLSPRING);
            after(entries, Items.ENDER_EYE, ModItems.SIFT_RIFT);
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(entries -> {
            after(entries, Items.NETHERITE_SWORD, ModItems.SIFTITE_SWORD);
            // VanillaBackport adds Minecraft's spear progression. Put Siftite
            // after Netherite when that spear is already in the tab; remain
            // compatible with base Fabric 1.21.1 where it doesn't exist.
            Item optionalNetheriteSpear = BuiltInRegistries.ITEM.get(
                    ResourceLocation.withDefaultNamespace("netherite_spear"));
            if (optionalNetheriteSpear != Items.AIR
                    && entries.getDisplayStacks().stream().anyMatch(s -> s.is(optionalNetheriteSpear))) {
                after(entries, optionalNetheriteSpear, BackportContent.SIFTITE_SPEAR);
            } else {
                after(entries, Items.TRIDENT, BackportContent.SIFTITE_SPEAR);
            }
            after(entries, Items.NETHERITE_HELMET, ModItems.SIFTITE_HELMET);
            after(entries, Items.NETHERITE_CHESTPLATE, ModItems.SIFTITE_CHESTPLATE);
            after(entries, Items.NETHERITE_LEGGINGS, ModItems.SIFTITE_LEGGINGS);
            after(entries, Items.NETHERITE_BOOTS, ModItems.SIFTITE_BOOTS);
            after(entries, Items.SNOWBALL, ModItems.ICHOR_SNOWBALL);
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(entries -> {
            after(entries, Items.PORKCHOP, ModItems.RAW_SIFTER_MEAT);
            after(entries, Items.COOKED_PORKCHOP, ModItems.COOKED_SIFTER_MEAT);
            after(entries, Items.HONEY_BOTTLE, ModItems.ICHOR_BOTTLE);
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(entries -> {
            after(entries, Items.NETHERITE_INGOT, ModItems.SIFTITE_INGOT);
            after(entries, Items.GOLD_NUGGET, ModItems.SIFTITE_NUGGET);
            after(entries, Items.AMETHYST_SHARD, ModItems.ICHOR_SHARD, ModItems.CHAROITE);
            after(entries, Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, ModItems.SIFTITE_UPGRADE_SMITHING_TEMPLATE);
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(entries -> {
            // Vanilla eggs are alphabetized. Do not add our five eggs in one
            // block after the Sniffer; that breaks the natural vanilla order.
            after(entries, Items.BLAZE_SPAWN_EGG, BlubItems.BLUB_SPAWN_EGG);
            after(entries, Items.COW_SPAWN_EGG, ModItems.DARK_SNIFFER_SPAWN_EGG);
            after(entries, Items.DROWNED_SPAWN_EGG, ModItems.ECHO_GOLEM_SPAWN_EGG);
            after(entries, Items.SHEEP_SPAWN_EGG, ModItems.SIFTER_SPAWN_EGG);
            after(entries, Items.SILVERFISH_SPAWN_EGG, ModItems.SINGER_SPAWN_EGG);
        });
    }

    private static ItemStack sonorousStack(SonorousDeepslateBlock.Mode mode) {
        ItemStack stack = new ItemStack(ModBlocks.SONOROUS_DEEPSLATE_ITEM);
        stack.set(DataComponents.BLOCK_STATE, new BlockItemStateProperties(
                Map.of(SonorousDeepslateBlock.MODE.getName(), mode.getSerializedName())));
        return stack;
    }

    private static void after(FabricItemGroupEntries entries, ItemLike anchor, ItemLike... items) {
        for (ItemLike item : items) {
            entries.addAfter(anchor, item);
            anchor = item;
        }
    }
}
