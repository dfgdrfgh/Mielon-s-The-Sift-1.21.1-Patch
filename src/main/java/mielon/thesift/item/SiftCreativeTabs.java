package mielon.thesift.item;

import java.util.List;
import java.util.Map;

import mielon.thesift.backport.BackportContent;
import mielon.thesift.block.ModBlocks;
import mielon.thesift.block.SonorousDeepslateBlock;
import mielon.thesift.fluid.ModFluids;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.ItemLike;

/**
 * Creative inventory registration for The Sift on Fabric 1.21.1.
 *
 * Vanilla tab placement is described as short, ordered sequences: a vanilla
 * anchor followed by its related Sift items. The same order is applied to
 * the display and search entries by Fabric API.
 *
 * Nothing here changes game behavior, mixins, recipes, or block registration.
 */
public final class SiftCreativeTabs {
    private SiftCreativeTabs() {
    }

    /** A contiguous group beginning with an existing creative-tab item. */
    private record Placement(ItemLike anchor, List<ItemLike> additions) {
    }

    private static Placement alongside(ItemLike anchor, ItemLike... additions) {
        return new Placement(anchor, List.of(additions));
    }

    private static void registerTab(ResourceKey<CreativeModeTab> tab, Placement... placements) {
        ItemGroupEvents.modifyEntriesEvent(tab).register(entries -> {
            for (Placement placement : placements) {
                ItemLike preceding = placement.anchor();
                for (ItemLike item : placement.additions()) {
                    entries.addAfter(preceding, item);
                    preceding = item;
                }
            }
        });
    }

    public static void register() {
        registerCustom();
        registerBuilding();
        registerNatural();
        registerFunctional();
        registerRedstone();
        registerTools();
        registerCombat();
        registerFood();
        registerIngredients();
        registerEggs();
    }

    private static void registerCustom() {
        // The custom tab already contains the original Sift items. Insert only
        // the two backported items, preserving the rest of its hand-authored order.
        registerTab(ModItems.CREATIVE_TAB_KEY,
                alongside(ModBlocks.OVERGROWN_WILLOW_PLANKS_ITEM, BackportContent.SHELF_ITEM),
                alongside(ModItems.SIFTITE_SWORD, BackportContent.SIFTITE_SPEAR));
    }

    private static void registerBuilding() {
        registerTab(CreativeModeTabs.BUILDING_BLOCKS,
                alongside(Items.REINFORCED_DEEPSLATE, ModBlocks.REINFORCED_SIFTSLATE_ITEM),
                alongside(Items.DEEPSLATE,
                        ModBlocks.SIFTSLATE_ITEM,
                        ModBlocks.COBBLED_SIFTSLATE_ITEM,
                        ModBlocks.COBBLED_SIFTSLATE_STAIRS_ITEM,
                        ModBlocks.COBBLED_SIFTSLATE_SLAB_ITEM,
                        ModBlocks.COBBLED_SIFTSLATE_WALL_ITEM,
                        ModBlocks.SIFTSLATE_BRICKS_ITEM,
                        ModBlocks.SIFTSLATE_BRICK_STAIRS_ITEM,
                        ModBlocks.SIFTSLATE_BRICK_SLAB_ITEM,
                        ModBlocks.SIFTSLATE_BRICK_WALL_ITEM,
                        ModBlocks.CHISELED_SIFTSLATE_ITEM),
                alongside(Items.MANGROVE_LOG, ModBlocks.OVERGROWN_WILLOW_LOG_ITEM),
                alongside(Items.MANGROVE_WOOD, ModBlocks.OVERGROWN_WILLOW_WOOD_ITEM),
                alongside(Items.STRIPPED_MANGROVE_LOG, ModBlocks.STRIPPED_OVERGROWN_WILLOW_LOG_ITEM),
                alongside(Items.STRIPPED_MANGROVE_WOOD, ModBlocks.STRIPPED_OVERGROWN_WILLOW_WOOD_ITEM),
                alongside(Items.MANGROVE_PLANKS, ModBlocks.OVERGROWN_WILLOW_PLANKS_ITEM),
                alongside(Items.MANGROVE_STAIRS, ModBlocks.OVERGROWN_WILLOW_STAIRS_ITEM),
                alongside(Items.MANGROVE_SLAB, ModBlocks.OVERGROWN_WILLOW_SLAB_ITEM),
                alongside(Items.MANGROVE_FENCE, ModBlocks.OVERGROWN_WILLOW_FENCE_ITEM),
                alongside(Items.MANGROVE_FENCE_GATE, ModBlocks.OVERGROWN_WILLOW_FENCE_GATE_ITEM),
                alongside(Items.MANGROVE_DOOR, ModBlocks.OVERGROWN_WILLOW_DOOR_ITEM),
                alongside(Items.MANGROVE_TRAPDOOR, ModBlocks.OVERGROWN_WILLOW_TRAPDOOR_ITEM),
                alongside(Items.MANGROVE_PRESSURE_PLATE, ModBlocks.OVERGROWN_WILLOW_PRESSURE_PLATE_ITEM),
                alongside(Items.MANGROVE_BUTTON, ModBlocks.OVERGROWN_WILLOW_BUTTON_ITEM),
                alongside(Items.TINTED_GLASS, ModBlocks.ICHOR_GLASS_ITEM, ModBlocks.ICHOR_GLASS_PANE_ITEM));
    }

    private static void registerNatural() {
        registerTab(CreativeModeTabs.NATURAL_BLOCKS,
                alongside(Items.DEEPSLATE_DIAMOND_ORE,
                        ModBlocks.SIFTSLATE_COAL_ORE_ITEM,
                        ModBlocks.SIFTSLATE_DIAMOND_ORE_ITEM,
                        ModBlocks.SIFTSLATE_EMERALD_ORE_ITEM,
                        ModBlocks.SIFTSLATE_CHAROITE_ORE_ITEM,
                        ModBlocks.SIFTSLATE_SIFTITE_ORE_ITEM),
                alongside(Items.SCULK,
                        ModBlocks.SIFTSLATE_GROWTH_ITEM,
                        ModBlocks.SIFT_RUBBLE_ITEM,
                        ModBlocks.HEALTHY_SCULK_ITEM,
                        ModBlocks.DRY_HEALTHY_SCULK_ITEM,
                        ModBlocks.DRY_HEALTHY_SCULK_GROWTH_ITEM,
                        ModBlocks.HEALTHY_SCULK_SPROUTS_ITEM,
                        ModBlocks.DRY_HEALTHY_SCULK_SPROUTS_ITEM),
                alongside(Items.SNOW_BLOCK, ModBlocks.ICHOR_SNOW_BLOCK_ITEM, ModBlocks.ICHOR_SNOW_ITEM),
                alongside(Items.MANGROVE_LEAVES, ModBlocks.OVERGROWN_WILLOW_FOLIAGE_ITEM),
                alongside(Items.MANGROVE_PROPAGULE, ModBlocks.OVERGROWN_WILLOW_SAPLING_ITEM),
                alongside(Items.VINE, ModBlocks.OVERGROWN_WILLOW_VINES_ITEM),
                alongside(Items.HANGING_ROOTS,
                        ModBlocks.OVERGROWN_FRONDS_ITEM,
                        ModBlocks.OVERGROWN_CHARD_ITEM,
                        ModBlocks.OVERGROWN_STALKS_ITEM,
                        ModBlocks.OVERGROWN_HANGING_ROOTS_ITEM,
                        ModBlocks.SIFTSLATE_STALKS_ITEM,
                        ModBlocks.SIFTSLATE_HANGING_ROOTS_ITEM),
                alongside(Items.SPORE_BLOSSOM,
                        ModBlocks.OVERGROWN_LOTUS_ITEM,
                        ModBlocks.SUNBURST_PLANT_ITEM,
                        ModBlocks.WHISPERBLOOM_ITEM,
                        ModBlocks.SCULKFLOWER_ITEM,
                        ModItems.SCULKFLOWER_SEEDS));
    }

    private static void registerFunctional() {
        registerTab(CreativeModeTabs.FUNCTIONAL_BLOCKS,
                alongside(Items.CHISELED_BOOKSHELF, BackportContent.SHELF_ITEM),
                alongside(Items.MANGROVE_SIGN, ModBlocks.OVERGROWN_WILLOW_SIGN_ITEM),
                alongside(Items.MANGROVE_HANGING_SIGN, ModBlocks.OVERGROWN_WILLOW_HANGING_SIGN_ITEM),
                alongside(Items.RESPAWN_ANCHOR, ModBlocks.SIFT_PORTAL_ITEM, ModBlocks.SOUL_BLOCK_ITEM));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> {
            ItemStack horn = sonorous(SonorousDeepslateBlock.Mode.HORN);
            ItemStack note = sonorous(SonorousDeepslateBlock.Mode.NOTE);
            entries.addAfter(Items.NOTE_BLOCK, horn);
            entries.addAfter(horn, note);
        });
    }

    private static ItemStack sonorous(SonorousDeepslateBlock.Mode mode) {
        ItemStack stack = new ItemStack(ModBlocks.SONOROUS_DEEPSLATE_ITEM);
        stack.set(DataComponents.BLOCK_STATE, new BlockItemStateProperties(
                Map.of(SonorousDeepslateBlock.MODE.getName(), mode.getSerializedName())));
        return stack;
    }

    private static void registerRedstone() {
        // 1.21.1 only includes the Oak representatives in the Redstone tab.
        registerTab(CreativeModeTabs.REDSTONE_BLOCKS,
                alongside(Items.OAK_PRESSURE_PLATE, ModBlocks.OVERGROWN_WILLOW_PRESSURE_PLATE_ITEM),
                alongside(Items.OAK_BUTTON, ModBlocks.OVERGROWN_WILLOW_BUTTON_ITEM));
    }

    private static void registerTools() {
        registerTab(CreativeModeTabs.TOOLS_AND_UTILITIES,
                alongside(Items.NETHERITE_PICKAXE, ModItems.SIFTITE_PICKAXE),
                alongside(Items.NETHERITE_AXE, ModItems.SIFTITE_AXE),
                alongside(Items.NETHERITE_SHOVEL, ModItems.SIFTITE_SHOVEL),
                alongside(Items.NETHERITE_HOE, ModItems.SIFTITE_HOE),
                alongside(Items.MANGROVE_BOAT, ModItems.OVERGROWN_WILLOW_BOAT),
                alongside(Items.MANGROVE_CHEST_BOAT, ModItems.OVERGROWN_WILLOW_CHEST_BOAT),
                alongside(Items.WATER_BUCKET, ModFluids.ICHOR_BUCKET),
                alongside(Items.MUSIC_DISC_PRECIPICE, ModItems.MUSIC_DISC_RIFT, ModItems.MUSIC_DISC_WELLSPRING),
                alongside(Items.ENDER_EYE, ModItems.SIFT_RIFT));
    }

    private static void registerCombat() {
        registerTab(CreativeModeTabs.COMBAT,
                alongside(Items.NETHERITE_SWORD, ModItems.SIFTITE_SWORD),
                alongside(Items.NETHERITE_HELMET, ModItems.SIFTITE_HELMET),
                alongside(Items.NETHERITE_CHESTPLATE, ModItems.SIFTITE_CHESTPLATE),
                alongside(Items.NETHERITE_LEGGINGS, ModItems.SIFTITE_LEGGINGS),
                alongside(Items.NETHERITE_BOOTS, ModItems.SIFTITE_BOOTS),
                alongside(Items.SNOWBALL, ModItems.ICHOR_SNOWBALL));

        // VanillaBackport may add the modern Netherite Spear to 1.21.1.
        // Base 1.21.1 has no spears, so place ours after the Trident there.
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(SiftCreativeTabs::insertSpear);
    }

    private static void insertSpear(FabricItemGroupEntries entries) {
        Item netherite = BuiltInRegistries.ITEM.get(
                ResourceLocation.withDefaultNamespace("netherite_spear"));
        boolean present = netherite != Items.AIR
                && entries.getDisplayStacks().stream().anyMatch(stack -> stack.is(netherite));
        entries.addAfter(present ? netherite : Items.TRIDENT, BackportContent.SIFTITE_SPEAR);
    }

    private static void registerFood() {
        registerTab(CreativeModeTabs.FOOD_AND_DRINKS,
                alongside(Items.PORKCHOP, ModItems.RAW_SIFTER_MEAT),
                alongside(Items.COOKED_PORKCHOP, ModItems.COOKED_SIFTER_MEAT),
                alongside(Items.HONEY_BOTTLE, ModItems.ICHOR_BOTTLE));
    }

    private static void registerIngredients() {
        registerTab(CreativeModeTabs.INGREDIENTS,
                alongside(Items.NETHERITE_INGOT, ModItems.SIFTITE_INGOT),
                alongside(Items.GOLD_NUGGET, ModItems.SIFTITE_NUGGET),
                alongside(Items.AMETHYST_SHARD, ModItems.ICHOR_SHARD, ModItems.CHAROITE),
                alongside(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, ModItems.SIFTITE_UPGRADE_SMITHING_TEMPLATE));
    }

    private static void registerEggs() {
        registerTab(CreativeModeTabs.SPAWN_EGGS,
                alongside(Items.BLAZE_SPAWN_EGG, BlubItems.BLUB_SPAWN_EGG),
                alongside(Items.COW_SPAWN_EGG, ModItems.DARK_SNIFFER_SPAWN_EGG),
                alongside(Items.DROWNED_SPAWN_EGG, ModItems.ECHO_GOLEM_SPAWN_EGG),
                alongside(Items.SHEEP_SPAWN_EGG, ModItems.SIFTER_SPAWN_EGG),
                alongside(Items.SILVERFISH_SPAWN_EGG, ModItems.SINGER_SPAWN_EGG));
    }
}
