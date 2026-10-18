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
        check(CreativeModeTabs.BUILDING_BLOCKS, Items.REINFORCED_DEEPSLATE,
            ModBlocks.REINFORCED_SIFTSLATE_ITEM, ModBlocks.SIFTSLATE_ITEM);
        check(CreativeModeTabs.BUILDING_BLOCKS, Items.MANGROVE_PLANKS,
            ModBlocks.OVERGROWN_WILLOW_LOG_ITEM, ModBlocks.OVERGROWN_WILLOW_WOOD_ITEM);
        check(CreativeModeTabs.NATURAL_BLOCKS, Items.SCULK,
            ModBlocks.SIFTSLATE_GROWTH_ITEM, ModBlocks.SIFT_RUBBLE_ITEM);
        check(CreativeModeTabs.FUNCTIONAL_BLOCKS, Items.CRAFTING_TABLE, BackportContent.SHELF_ITEM);
        check(CreativeModeTabs.COMBAT, Items.TRIDENT, BackportContent.SIFTITE_SPEAR);
        check(CreativeModeTabs.COMBAT, Items.NETHERITE_SWORD, ModItems.SIFTITE_SWORD);
        check(CreativeModeTabs.COMBAT, Items.NETHERITE_HELMET, ModItems.SIFTITE_HELMET);
        check(CreativeModeTabs.TOOLS_AND_UTILITIES, Items.NETHERITE_PICKAXE, ModItems.SIFTITE_PICKAXE);
        check(CreativeModeTabs.TOOLS_AND_UTILITIES, Items.MUSIC_DISC_OTHERSIDE,
            ModItems.MUSIC_DISC_RIFT, ModItems.MUSIC_DISC_WELLSPRING);
        check(CreativeModeTabs.INGREDIENTS, Items.NETHERITE_INGOT,
            ModItems.SIFTITE_INGOT, ModItems.SIFTITE_NUGGET);
        check(CreativeModeTabs.FOOD_AND_DRINKS, Items.COOKED_PORKCHOP,
            ModItems.RAW_SIFTER_MEAT, ModItems.COOKED_SIFTER_MEAT);
        check(CreativeModeTabs.SPAWN_EGGS, Items.SNIFFER_SPAWN_EGG,
            ModItems.SINGER_SPAWN_EGG, ModItems.ECHO_GOLEM_SPAWN_EGG,
            ModItems.DARK_SNIFFER_SPAWN_EGG, BlubItems.BLUB_SPAWN_EGG,
            ModItems.SIFTER_SPAWN_EGG);
        checkSonorousModes();
        System.out.println("SIFT_CREATIVE_TABS_PASS");
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
