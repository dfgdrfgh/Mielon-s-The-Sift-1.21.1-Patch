package mielon.thesift.item;

import mielon.thesift.backport.BackportContent;
import mielon.thesift.fluid.ModFluids;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;

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
        // All-tabs callbacks run AFTER the specific tab's additions. This
        // means VanillaBackport's spear row has already been inserted before
        // the Trident when we place the Siftite spear at its end.
        ItemGroupEvents.MODIFY_ENTRIES_ALL.register((tab, entries) -> {
            if (tab == BuiltInRegistries.CREATIVE_MODE_TAB.get(CreativeModeTabs.COMBAT.location())) {
                entries.addBefore(Items.TRIDENT, BackportContent.SIFTITE_SPEAR);
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


    }
}
