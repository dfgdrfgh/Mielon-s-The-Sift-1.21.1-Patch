package mielon.thesift.check;

import java.util.ArrayList;
import java.util.List;
import mielon.thesift.backport.BackportContent;
import mielon.thesift.block.ModBlocks;
import mielon.thesift.block.SonorousDeepslateBlock;
import mielon.thesift.item.BlubItems;
import mielon.thesift.item.ModItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.ItemLike;

/**
 * Run the actual registered Fabric creative-tab callbacks against mutable tab
 * contents. This catches omitted registrations, non-adjacent placements,
 * duplicate entries and items missing from the search index. The check mod
 * is intentionally excluded from the release JAR.
 */
public final class CreativeTabsCheck {
    private CreativeTabsCheck() {}

    public static void run() {
        check(ModItems.CREATIVE_TAB_KEY, ModItems.SIFTITE_SWORD, BackportContent.SIFTITE_SPEAR);
        check(ModItems.CREATIVE_TAB_KEY, ModBlocks.OVERGROWN_WILLOW_PLANKS_ITEM, BackportContent.SHELF_ITEM);
        check(CreativeModeTabs.BUILDING_BLOCKS, Items.REINFORCED_DEEPSLATE, ModBlocks.REINFORCED_SIFTSLATE_ITEM);
        check(CreativeModeTabs.BUILDING_BLOCKS, Items.DEEPSLATE, ModBlocks.SIFTSLATE_ITEM, ModBlocks.COBBLED_SIFTSLATE_ITEM);
        check(CreativeModeTabs.BUILDING_BLOCKS, Items.MANGROVE_LOG, ModBlocks.OVERGROWN_WILLOW_LOG_ITEM);
        check(CreativeModeTabs.BUILDING_BLOCKS, Items.MANGROVE_PLANKS, ModBlocks.OVERGROWN_WILLOW_PLANKS_ITEM);
        check(CreativeModeTabs.NATURAL_BLOCKS, Items.SCULK, ModBlocks.SIFTSLATE_GROWTH_ITEM, ModBlocks.SIFT_RUBBLE_ITEM);
        check(CreativeModeTabs.FUNCTIONAL_BLOCKS, Items.CHISELED_BOOKSHELF, BackportContent.SHELF_ITEM);
        check(CreativeModeTabs.FUNCTIONAL_BLOCKS, Items.MANGROVE_SIGN, ModBlocks.OVERGROWN_WILLOW_SIGN_ITEM);
        check(CreativeModeTabs.REDSTONE_BLOCKS, Items.MANGROVE_PRESSURE_PLATE, ModBlocks.OVERGROWN_WILLOW_PRESSURE_PLATE_ITEM);
        check(CreativeModeTabs.REDSTONE_BLOCKS, Items.MANGROVE_BUTTON, ModBlocks.OVERGROWN_WILLOW_BUTTON_ITEM);
        check(CreativeModeTabs.COMBAT, Items.TRIDENT, BackportContent.SIFTITE_SPEAR);
        check(CreativeModeTabs.COMBAT, Items.NETHERITE_SWORD, ModItems.SIFTITE_SWORD);
        check(CreativeModeTabs.TOOLS_AND_UTILITIES, Items.NETHERITE_PICKAXE, ModItems.SIFTITE_PICKAXE);
        check(CreativeModeTabs.TOOLS_AND_UTILITIES, Items.MUSIC_DISC_PRECIPICE,
            ModItems.MUSIC_DISC_RIFT, ModItems.MUSIC_DISC_WELLSPRING);
        check(CreativeModeTabs.INGREDIENTS, Items.NETHERITE_INGOT, ModItems.SIFTITE_INGOT);
        check(CreativeModeTabs.INGREDIENTS, Items.GOLD_NUGGET, ModItems.SIFTITE_NUGGET);
        check(CreativeModeTabs.FOOD_AND_DRINKS, Items.PORKCHOP, ModItems.RAW_SIFTER_MEAT);
        check(CreativeModeTabs.FOOD_AND_DRINKS, Items.COOKED_PORKCHOP, ModItems.COOKED_SIFTER_MEAT);
        check(CreativeModeTabs.SPAWN_EGGS, Items.BLAZE_SPAWN_EGG, BlubItems.BLUB_SPAWN_EGG);
        check(CreativeModeTabs.SPAWN_EGGS, Items.COW_SPAWN_EGG, ModItems.DARK_SNIFFER_SPAWN_EGG);
        check(CreativeModeTabs.SPAWN_EGGS, Items.DROWNED_SPAWN_EGG, ModItems.ECHO_GOLEM_SPAWN_EGG);
        check(CreativeModeTabs.SPAWN_EGGS, Items.SHEEP_SPAWN_EGG, ModItems.SIFTER_SPAWN_EGG);
        check(CreativeModeTabs.SPAWN_EGGS, Items.SILVERFISH_SPAWN_EGG, ModItems.SINGER_SPAWN_EGG);
        checkSonorousModes();
        auditActualTabs();
        System.out.println("SIFT_CREATIVE_TABS_PASS");
    }

    /**
     * Populate Minecraft's actual built-in tabs instead of seeding the anchor.
     * This catches cases where Fabric silently appends entries because their
     * expected anchor is absent. A small mock test alone cannot catch that.
     */
    private static void auditActualTabs() {
        var groups = List.of(
            CreativeModeTabs.BUILDING_BLOCKS, CreativeModeTabs.NATURAL_BLOCKS,
            CreativeModeTabs.FUNCTIONAL_BLOCKS, CreativeModeTabs.REDSTONE_BLOCKS,
            CreativeModeTabs.TOOLS_AND_UTILITIES, CreativeModeTabs.COMBAT,
            CreativeModeTabs.FOOD_AND_DRINKS, CreativeModeTabs.INGREDIENTS,
            CreativeModeTabs.SPAWN_EGGS, ModItems.CREATIVE_TAB_KEY);
        int actualGroupsChecked = 0;
        for (var key : groups) {
            try {
                CreativeModeTab tab = BuiltInRegistries.CREATIVE_MODE_TAB.get(key.location());
                tab.buildContents(new CreativeModeTab.ItemDisplayParameters(
                    FeatureFlags.VANILLA_SET, true, RegistryAccess.EMPTY));
                var stacks = List.copyOf(tab.getDisplayItems());
                long count = stacks.stream().filter(v -> BuiltInRegistries.ITEM.getKey(v.getItem())
                    .getNamespace().equals("the_sift")).count();
                System.out.println("SIFT_REAL_TAB " + key.location() + " size=" + stacks.size() + " sift=" + count);
                if (key.equals(CreativeModeTabs.BUILDING_BLOCKS)) {
                    realAfter(key, stacks, Items.REINFORCED_DEEPSLATE, ModBlocks.REINFORCED_SIFTSLATE_ITEM);
                    realAfter(key, stacks, Items.DEEPSLATE, ModBlocks.SIFTSLATE_ITEM, ModBlocks.COBBLED_SIFTSLATE_ITEM);
                    realAfter(key, stacks, Items.MANGROVE_LOG, ModBlocks.OVERGROWN_WILLOW_LOG_ITEM);
                    realAfter(key, stacks, Items.MANGROVE_PLANKS, ModBlocks.OVERGROWN_WILLOW_PLANKS_ITEM);
                    realAfter(key, stacks, Items.MANGROVE_DOOR, ModBlocks.OVERGROWN_WILLOW_DOOR_ITEM);
                    require(count >= 26, "Building blocks missing Sift items: " + count);
                } else if (key.equals(CreativeModeTabs.NATURAL_BLOCKS)) {
                    realAfter(key, stacks, Items.SCULK, ModBlocks.SIFTSLATE_GROWTH_ITEM, ModBlocks.SIFT_RUBBLE_ITEM);
                    realAfter(key, stacks, Items.MANGROVE_PROPAGULE, ModBlocks.OVERGROWN_WILLOW_SAPLING_ITEM);
                    realAfter(key, stacks, Items.VINE, ModBlocks.OVERGROWN_WILLOW_VINES_ITEM);
                    require(count >= 28, "Natural blocks missing Sift items: " + count);
                } else if (key.equals(CreativeModeTabs.REDSTONE_BLOCKS)) {
                    realAfter(key, stacks, Items.MANGROVE_PRESSURE_PLATE, ModBlocks.OVERGROWN_WILLOW_PRESSURE_PLATE_ITEM);
                    realAfter(key, stacks, Items.MANGROVE_BUTTON, ModBlocks.OVERGROWN_WILLOW_BUTTON_ITEM);
                    require(count >= 2, "Redstone tab missing Sift items");
                } else if (key.equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) {
                    realAfter(key, stacks, Items.NETHERITE_PICKAXE, ModItems.SIFTITE_PICKAXE);
                    realAfter(key, stacks, Items.MANGROVE_BOAT, ModItems.OVERGROWN_WILLOW_BOAT);
                    realAfter(key, stacks, Items.MUSIC_DISC_PRECIPICE,
                        ModItems.MUSIC_DISC_RIFT, ModItems.MUSIC_DISC_WELLSPRING);
                    require(count >= 10, "Tools tab missing Sift items: " + count);
                } else if (key.equals(CreativeModeTabs.COMBAT)) {
                    realAfter(key, stacks, Items.TRIDENT, BackportContent.SIFTITE_SPEAR);
                    realAfter(key, stacks, Items.NETHERITE_SWORD, ModItems.SIFTITE_SWORD);
                    require(count >= 7, "Combat tab missing Sift items: " + count);
                } else if (key.equals(CreativeModeTabs.FOOD_AND_DRINKS)) {
                    realAfter(key, stacks, Items.PORKCHOP, ModItems.RAW_SIFTER_MEAT);
                    realAfter(key, stacks, Items.COOKED_PORKCHOP, ModItems.COOKED_SIFTER_MEAT);
                    require(count >= 3, "Food tab missing Sift items: " + count);
                } else if (key.equals(CreativeModeTabs.INGREDIENTS)) {
                    realAfter(key, stacks, Items.NETHERITE_INGOT, ModItems.SIFTITE_INGOT);
                    realAfter(key, stacks, Items.GOLD_NUGGET, ModItems.SIFTITE_NUGGET);
                    require(count >= 5, "Ingredients tab missing Sift items: " + count);
                } else if (key.equals(CreativeModeTabs.SPAWN_EGGS)) {
                    realAfter(key, stacks, Items.BLAZE_SPAWN_EGG, BlubItems.BLUB_SPAWN_EGG);
                    realAfter(key, stacks, Items.COW_SPAWN_EGG, ModItems.DARK_SNIFFER_SPAWN_EGG);
                    realAfter(key, stacks, Items.DROWNED_SPAWN_EGG, ModItems.ECHO_GOLEM_SPAWN_EGG);
                    realAfter(key, stacks, Items.SHEEP_SPAWN_EGG, ModItems.SIFTER_SPAWN_EGG);
                    realAfter(key, stacks, Items.SILVERFISH_SPAWN_EGG, ModItems.SINGER_SPAWN_EGG);
                    require(count == 5, "Spawn egg tab should include exactly five Sift eggs");
                } else if (key.equals(ModItems.CREATIVE_TAB_KEY)) {
                    realAfter(key, stacks, ModItems.SIFTITE_SWORD, BackportContent.SIFTITE_SPEAR);
                    realAfter(key, stacks, ModBlocks.OVERGROWN_WILLOW_PLANKS_ITEM, BackportContent.SHELF_ITEM);
                    require(count == 91, "Sift custom tab must keep all 91 entries: " + count);
                    ordered(stacks, BlubItems.BLUB_SPAWN_EGG,
                        ModItems.DARK_SNIFFER_SPAWN_EGG, ModItems.ECHO_GOLEM_SPAWN_EGG,
                        ModItems.SIFTER_SPAWN_EGG, ModItems.SINGER_SPAWN_EGG);
                }
                actualGroupsChecked++;
            } catch (IllegalStateException e) {
                // At the title screen there is no synchronized banner-pattern
                // registry. Functional Blocks is also covered via event tests.
                if (key.equals(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                    && e.getMessage() != null && e.getMessage().contains("banner_pattern")) {
                    System.out.println("SIFT_REAL_TAB_SKIPPED functional_blocks needs dynamic banner patterns");
                } else {
                    throw e;
                }
            }
        }
        require(actualGroupsChecked == 9, "Expected 9 actual Minecraft creative tabs, got " + actualGroupsChecked);
        System.out.println("SIFT_REAL_CREATIVE_TABS_PASS groups=" + actualGroupsChecked);
    }

    private static void realAfter(ResourceKey<CreativeModeTab> key, List<ItemStack> stacks,
                                  ItemLike anchor, ItemLike... expected) {
        int before = indexOf(stacks, anchor.asItem());
        require(before >= 0, key.location() + ": real anchor absent: " + anchor.asItem());
        for (int n = 0; n < expected.length; n++) {
            Item item = expected[n].asItem();
            int at = before + n + 1;
            require(at < stacks.size() && stacks.get(at).is(item),
                key.location() + ": wrong real sort at " + at + " after " + anchor.asItem()
                + ", expected " + item);
            require(stacks.stream().filter(s -> s.is(item)).count() == 1,
                key.location() + ": duplicate real item " + item);
        }
    }

    private static void ordered(List<ItemStack> stacks, ItemLike... items) {
        int last = -1;
        for (ItemLike item : items) {
            int at = indexOf(stacks, item.asItem());
            require(at > last, "Custom tab mob eggs not sorted: " + item.asItem());
            last = at;
        }
    }

    private static FabricItemGroupEntries entries(ResourceKey<CreativeModeTab> key, ItemLike anchor,
                                                    List<ItemStack> display, List<ItemStack> search) {
        display.add(new ItemStack(anchor));
        search.add(new ItemStack(anchor));
        FabricItemGroupEntries entries = new FabricItemGroupEntries(
            new CreativeModeTab.ItemDisplayParameters(FeatureFlags.VANILLA_SET, true, RegistryAccess.EMPTY),
            display, search);
        ItemGroupEvents.modifyEntriesEvent(key).invoker().modifyEntries(entries);
        return entries;
    }

    private static int indexOf(List<ItemStack> stacks, Item item) {
        for (int i = 0; i < stacks.size(); i++) {
            if (stacks.get(i).is(item)) return i;
        }
        return -1;
    }

    private static void require(boolean good, String what) {
        if (!good) throw new IllegalStateException(what);
    }

    private static void check(ResourceKey<CreativeModeTab> tab, ItemLike anchor, ItemLike... expected) {
        var display = new ArrayList<ItemStack>();
        var search = new ArrayList<ItemStack>();
        FabricItemGroupEntries entries = entries(tab, anchor, display, search);
        int start = indexOf(entries.getDisplayStacks(), anchor.asItem());
        require(start >= 0, tab.location() + ": anchor not present");
        for (int i = 0; i < expected.length; i++) {
            Item item = expected[i].asItem();
            int position = start + i + 1;
            require(position < display.size() && display.get(position).is(item),
                tab.location() + ": " + item + " must immediately follow " + anchor.asItem()
                    + " (position " + position + ")");
            require(display.stream().filter(s -> s.is(item)).count() == 1,
                tab.location() + ": duplicate " + item);
            require(search.stream().filter(s -> s.is(item)).count() == 1,
                tab.location() + ": item missing from search or duplicated: " + item);
        }
    }

    private static void checkSonorousModes() {
        var display = new ArrayList<ItemStack>();
        var search = new ArrayList<ItemStack>();
        entries(CreativeModeTabs.FUNCTIONAL_BLOCKS, Items.NOTE_BLOCK, display, search);
        int base = indexOf(display, Items.NOTE_BLOCK);
        require(base >= 0 && base + 2 < display.size(), "Sonorous variants not present");
        String stateKey = SonorousDeepslateBlock.MODE.getName();
        for (int offset = 1; offset <= 2; offset++) {
            ItemStack stack = display.get(base + offset);
            require(stack.is(ModBlocks.SONOROUS_DEEPSLATE_ITEM),
                "Sonorous variant not sorted after the vanilla note block");
            BlockItemStateProperties props = stack.get(DataComponents.BLOCK_STATE);
            String expected = (offset == 1 ? SonorousDeepslateBlock.Mode.HORN :
                SonorousDeepslateBlock.Mode.NOTE).getSerializedName();
            require(props != null && expected.equals(props.properties().get(stateKey)),
                "Wrong Sonorous variant or mode order");
        }
        require(search.stream().filter(s -> s.is(ModBlocks.SONOROUS_DEEPSLATE_ITEM)).count() == 2,
            "Both Sonorous variants must appear in creative search");
    }
}
