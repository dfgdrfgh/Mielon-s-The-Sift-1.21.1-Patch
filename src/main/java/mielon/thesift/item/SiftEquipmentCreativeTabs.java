package mielon.thesift.item;

import mielon.thesift.backport.BackportContent;
import mielon.thesift.fluid.ModFluids;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import java.util.List;

/**
 * Equipment placement, independent of the general building/nature tabs.
 * Follows the material-tier ordering of Minecraft's vanilla creative tabs.
 * No mixins or item registration changes are needed.
 */
final class SiftEquipmentCreativeTabs {
    private SiftEquipmentCreativeTabs() {}

    static void register() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register(SiftEquipmentCreativeTabs::populateTools);
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT)
                .register(SiftEquipmentCreativeTabs::populateCombat);
        // Add the spear in the specific Combat callback first, so it cannot
        // disappear if another mod's tab processing changes event order.
        // A final pass moves it after any VanillaBackport spears.
        ItemGroupEvents.MODIFY_ENTRIES_ALL.register((tab, entries) -> {
            if (CreativeModeTabs.COMBAT.location().equals(
                    BuiltInRegistries.CREATIVE_MODE_TAB.getKey(tab))) {
                positionSiftiteSpear(entries);
            }
        });
    }

    private static void populateTools(FabricItemGroupEntries entries) {
        // Keep all four tools together, in vanilla material-tier order.
        entries.addAfter(Items.NETHERITE_HOE, ModItems.SIFTITE_SHOVEL);
        entries.addAfter(ModItems.SIFTITE_SHOVEL, ModItems.SIFTITE_PICKAXE);
        entries.addAfter(ModItems.SIFTITE_PICKAXE, ModItems.SIFTITE_AXE);
        entries.addAfter(ModItems.SIFTITE_AXE, ModItems.SIFTITE_HOE);

        entries.addAfter(Items.MANGROVE_BOAT, ModItems.OVERGROWN_WILLOW_BOAT);
        entries.addAfter(Items.MANGROVE_CHEST_BOAT, ModItems.OVERGROWN_WILLOW_CHEST_BOAT);
        entries.addAfter(Items.WATER_BUCKET, ModFluids.ICHOR_BUCKET);
        entries.addAfter(Items.MUSIC_DISC_PRECIPICE, ModItems.MUSIC_DISC_RIFT);
        entries.addAfter(ModItems.MUSIC_DISC_RIFT, ModItems.MUSIC_DISC_WELLSPRING);
        entries.addAfter(Items.ENDER_EYE, ModItems.SIFT_RIFT);
    }

    private static void populateCombat(FabricItemGroupEntries entries) {
        entries.addAfter(Items.NETHERITE_SWORD, ModItems.SIFTITE_SWORD);
        entries.addAfter(Items.NETHERITE_AXE, ModItems.SIFTITE_AXE);

        // Add a complete armor tier instead of alternating with Netherite.
        entries.addAfter(Items.NETHERITE_BOOTS, ModItems.SIFTITE_HELMET);
        entries.addAfter(ModItems.SIFTITE_HELMET, ModItems.SIFTITE_CHESTPLATE);
        entries.addAfter(ModItems.SIFTITE_CHESTPLATE, ModItems.SIFTITE_LEGGINGS);
        entries.addAfter(ModItems.SIFTITE_LEGGINGS, ModItems.SIFTITE_BOOTS);

        entries.addAfter(Items.SNOWBALL, ModItems.ICHOR_SNOWBALL);
        // VanillaBackport inserts all seven spears before the first axe.
        // Put Siftite at the same boundary, not near the Trident.
        entries.addBefore(Items.WOODEN_AXE, BackportContent.SIFTITE_SPEAR);
    }

    /**
     * VanillaBackport inserts its seven spears before Wooden Axe, ahead of
     * the vanilla axe group. The late pass moves Siftite to exactly that
     * boundary, even if other tab listeners registered their spears later.
     * Rebuilding both lists keeps the operation idempotent.
     */
    private static void positionSiftiteSpear(FabricItemGroupEntries entries) {
        List<ItemStack> display = entries.getDisplayStacks();
        List<ItemStack> search = entries.getSearchTabStacks();
        display.removeIf(stack -> stack.is(BackportContent.SIFTITE_SPEAR));
        search.removeIf(stack -> stack.is(BackportContent.SIFTITE_SPEAR));

        int axeIndex = -1;
        for (int i = 0; i < display.size(); i++) {
            if (display.get(i).is(Items.WOODEN_AXE)) {
                axeIndex = i;
                break;
            }
        }
        // Keep the spear discoverable if another mod hides the vanilla axe.
        if (axeIndex < 0) {
            axeIndex = display.size();
        }
        display.add(axeIndex, new ItemStack(BackportContent.SIFTITE_SPEAR));
        search.add(new ItemStack(BackportContent.SIFTITE_SPEAR));
    }
}
