package mielon.thesift.item;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import mielon.thesift.block.ModBlocks;
import mielon.thesift.block.SonorousDeepslateBlock;
import mielon.thesift.entity.ModEntities;
import mielon.thesift.fluid.ModFluids;
import mielon.thesift.platform.ModRegistration;
import mielon.thesift.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.item.SnowballItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.ComposterBlock;
import mielon.thesift.platform.SimpleTier;

public final class ModItems {
   public static final ResourceKey<Item> SINGER_SPAWN_EGG_KEY = ResourceKey.create(
      BuiltInRegistries.ITEM.key(), ResourceLocation.fromNamespaceAndPath("the_sift", "singer_spawn_egg")
   );
   public static final Item SINGER_SPAWN_EGG = ModRegistration.register(
      BuiltInRegistries.ITEM, SINGER_SPAWN_EGG_KEY, new SpawnEggItem(ModEntities.SINGER, 11069651, 15595223, new Properties())
   );
   public static final ResourceKey<Item> ECHO_GOLEM_SPAWN_EGG_KEY = ResourceKey.create(
      BuiltInRegistries.ITEM.key(), ResourceLocation.fromNamespaceAndPath("the_sift", "echo_golem_spawn_egg")
   );
   public static final Item ECHO_GOLEM_SPAWN_EGG = ModRegistration.register(
      BuiltInRegistries.ITEM, ECHO_GOLEM_SPAWN_EGG_KEY, new SpawnEggItem(ModEntities.ECHO_GOLEM, 3563346, 11202268, new Properties())
   );
   public static final ResourceKey<Item> DARK_SNIFFER_SPAWN_EGG_KEY = ResourceKey.create(
      BuiltInRegistries.ITEM.key(), ResourceLocation.fromNamespaceAndPath("the_sift", "dark_sniffer_spawn_egg")
   );
   public static final Item DARK_SNIFFER_SPAWN_EGG = ModRegistration.register(
      BuiltInRegistries.ITEM, DARK_SNIFFER_SPAWN_EGG_KEY, new SpawnEggItem(ModEntities.DARK_SNIFFER, 548963, 15594198, new Properties())
   );
   public static final ResourceKey<Item> SIFTER_SPAWN_EGG_KEY = ResourceKey.create(
      BuiltInRegistries.ITEM.key(), ResourceLocation.fromNamespaceAndPath("the_sift", "sifter_spawn_egg")
   );
   public static final Item SIFTER_SPAWN_EGG = ModRegistration.register(
      BuiltInRegistries.ITEM, SIFTER_SPAWN_EGG_KEY, new SpawnEggItem(ModEntities.SIFTER, 5813119, 15059560, new Properties())
   );
   public static final ResourceKey<Item> OVERGROWN_WILLOW_BOAT_KEY = ResourceKey.create(
      BuiltInRegistries.ITEM.key(), ResourceLocation.fromNamespaceAndPath("the_sift", "overgrown_willow_boat")
   );
   public static final Item OVERGROWN_WILLOW_BOAT = ModRegistration.register(
      BuiltInRegistries.ITEM, OVERGROWN_WILLOW_BOAT_KEY, new WillowBoatItem(ModEntities.OVERGROWN_WILLOW_BOAT, new Properties().stacksTo(1))
   );
   public static final ResourceKey<Item> OVERGROWN_WILLOW_CHEST_BOAT_KEY = ResourceKey.create(
      BuiltInRegistries.ITEM.key(), ResourceLocation.fromNamespaceAndPath("the_sift", "overgrown_willow_chest_boat")
   );
   public static final Item OVERGROWN_WILLOW_CHEST_BOAT = ModRegistration.register(
      BuiltInRegistries.ITEM, OVERGROWN_WILLOW_CHEST_BOAT_KEY, new WillowBoatItem(ModEntities.OVERGROWN_WILLOW_CHEST_BOAT, new Properties().stacksTo(1))
   );
   public static final TagKey<Item> SIFTITE_REPAIR_MATERIALS = TagKey.create(
      Registries.ITEM, ResourceLocation.fromNamespaceAndPath("the_sift", "siftite_repair_materials")
   );
   public static final TagKey<Item> SIFTITE_ITEMS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("the_sift", "siftite_items"));
   public static final Tier SIFTITE_TOOL_MATERIAL = new SimpleTier(
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      Tiers.NETHERITE.getUses() + 470,
      Tiers.NETHERITE.getSpeed() * 1.2F,
      Tiers.NETHERITE.getAttackDamageBonus() + 1.0F,
      Tiers.NETHERITE.getEnchantmentValue(),
      () -> Ingredient.of(SIFTITE_REPAIR_MATERIALS)
   );
   public static final Holder<ArmorMaterial> SIFTITE_ARMOR_MATERIAL = Holder.direct(
      ModRegistration.register(
         BuiltInRegistries.ARMOR_MATERIAL,
         ResourceLocation.fromNamespaceAndPath("the_sift", "siftite"),
         new ArmorMaterial(
            Map.of(Type.HELMET, 3, Type.CHESTPLATE, 8, Type.LEGGINGS, 6, Type.BOOTS, 3, Type.BODY, 19),
            ((ArmorMaterial)ArmorMaterials.NETHERITE.value()).enchantmentValue(),
            Holder.direct(ModSounds.SIFTITE_ARMOR_EQUIP),
            () -> Ingredient.of(SIFTITE_REPAIR_MATERIALS),
            List.of(new Layer(ResourceLocation.fromNamespaceAndPath("the_sift", "siftite"))),
            3.0F,
            0.15F
         )
      )
   );
   public static final Item ICHOR_SHARD = registerItem("ichor_shard", new Properties());
   public static final Item CHAROITE = registerItem("charoite", new Properties());
   public static final Item SIFTITE_NUGGET = registerItem("siftite_nugget", new Properties());
   public static final Item SIFTITE_INGOT = registerItem("siftite_ingot", new Properties());
   public static final Item ICHOR_SNOWBALL = registerItem("ichor_snowball", SnowballItem::new, new Properties().stacksTo(16));
   public static final Item ICHOR_BOTTLE = registerItem(
      "ichor_bottle",
      IchorBottleItem::new,
      new Properties()
         .stacksTo(1)
         .component(
            DataComponents.POTION_CONTENTS,
            new PotionContents(Optional.empty(), Optional.of(5909123), List.of(new MobEffectInstance(MobEffects.REGENERATION, 60)))
         )
   );
   public static final Item RAW_SIFTER_MEAT = registerItem("raw_sifter_meat", new Properties().food(Foods.PORKCHOP));
   public static final Item COOKED_SIFTER_MEAT = registerItem("cooked_sifter_meat", new Properties().food(Foods.COOKED_PORKCHOP));
   public static final Item SIFT_RIFT = registerItem("sift_rift", SiftRiftItem::new, new Properties().stacksTo(1).rarity(Rarity.EPIC));
   public static final ResourceKey<JukeboxSong> RIFT_JUKEBOX_SONG_KEY = ResourceKey.create(
      Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath("the_sift", "rift")
   );
   public static final Item MUSIC_DISC_RIFT = registerItem(
      "music_disc_rift", new Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(RIFT_JUKEBOX_SONG_KEY)
   );
   public static final ResourceKey<JukeboxSong> WELLSPRING_JUKEBOX_SONG_KEY = ResourceKey.create(
      Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath("the_sift", "wellspring")
   );
   public static final Item MUSIC_DISC_WELLSPRING = registerItem(
      "music_disc_wellspring", new Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(WELLSPRING_JUKEBOX_SONG_KEY)
   );
   public static final ResourceKey<Item> SCULKFLOWER_SEEDS_KEY = ResourceKey.create(
      BuiltInRegistries.ITEM.key(), ResourceLocation.fromNamespaceAndPath("the_sift", "sculkflower_seeds")
   );
   public static final Item SCULKFLOWER_SEEDS = ModRegistration.register(
      BuiltInRegistries.ITEM, SCULKFLOWER_SEEDS_KEY, new BlockItem(ModBlocks.SCULKFLOWER_CROP, new Properties())
   );
   public static final Item SIFTITE_SWORD = registerItem(
      "siftite_sword",
      properties -> new SwordItem(SIFTITE_TOOL_MATERIAL, properties.attributes(SwordItem.createAttributes(SIFTITE_TOOL_MATERIAL, 3, -2.4F))),
      new Properties().fireResistant()
   );
   public static final Item SIFTITE_SHOVEL = registerItem(
      "siftite_shovel",
      properties -> new ShovelItem(SIFTITE_TOOL_MATERIAL, properties.attributes(DiggerItem.createAttributes(SIFTITE_TOOL_MATERIAL, 1.5F, -3.0F))),
      new Properties().fireResistant()
   );
   public static final Item SIFTITE_PICKAXE = registerItem(
      "siftite_pickaxe",
      properties -> new PickaxeItem(SIFTITE_TOOL_MATERIAL, properties.attributes(DiggerItem.createAttributes(SIFTITE_TOOL_MATERIAL, 1.0F, -2.8F))),
      new Properties().fireResistant()
   );
   public static final Item SIFTITE_AXE = registerItem(
      "siftite_axe",
      properties -> new AxeItem(SIFTITE_TOOL_MATERIAL, properties.attributes(DiggerItem.createAttributes(SIFTITE_TOOL_MATERIAL, 5.0F, -3.0F))),
      new Properties().fireResistant()
   );
   public static final Item SIFTITE_HOE = registerItem(
      "siftite_hoe",
      properties -> new HoeItem(SIFTITE_TOOL_MATERIAL, properties.attributes(DiggerItem.createAttributes(SIFTITE_TOOL_MATERIAL, -4.0F, 0.0F))),
      new Properties().fireResistant()
   );
   public static final Item SIFTITE_HELMET = registerItem(
      "siftite_helmet",
      properties -> new ArmorItem(SIFTITE_ARMOR_MATERIAL, Type.HELMET, properties),
      new Properties().durability(Type.HELMET.getDurability(41)).fireResistant()
   );
   public static final Item SIFTITE_CHESTPLATE = registerItem(
      "siftite_chestplate",
      properties -> new ArmorItem(SIFTITE_ARMOR_MATERIAL, Type.CHESTPLATE, properties),
      new Properties().durability(Type.CHESTPLATE.getDurability(41)).fireResistant()
   );
   public static final Item SIFTITE_LEGGINGS = registerItem(
      "siftite_leggings",
      properties -> new ArmorItem(SIFTITE_ARMOR_MATERIAL, Type.LEGGINGS, properties),
      new Properties().durability(Type.LEGGINGS.getDurability(41)).fireResistant()
   );
   public static final Item SIFTITE_BOOTS = registerItem(
      "siftite_boots",
      properties -> new ArmorItem(SIFTITE_ARMOR_MATERIAL, Type.BOOTS, properties),
      new Properties().durability(Type.BOOTS.getDurability(41)).fireResistant()
   );
   public static final Item SIFTITE_UPGRADE_SMITHING_TEMPLATE = registerItem(
      "siftite_upgrade_smithing_template",
      properties -> new SmithingTemplateItem(
         Component.translatable("item.the_sift.smithing_template.siftite_upgrade.applies_to").withStyle(ChatFormatting.BLUE),
         Component.translatable("item.the_sift.smithing_template.siftite_upgrade.ingredients").withStyle(ChatFormatting.BLUE),
         Component.translatable("item.the_sift.siftite_upgrade_smithing_template.new").withStyle(ChatFormatting.GRAY),
         Component.translatable("item.the_sift.smithing_template.siftite_upgrade.base_slot_description"),
         Component.translatable("item.the_sift.smithing_template.siftite_upgrade.additions_slot_description"),
         List.of(
            ResourceLocation.withDefaultNamespace("item/empty_armor_slot_helmet"),
            ResourceLocation.withDefaultNamespace("item/empty_armor_slot_chestplate"),
            ResourceLocation.withDefaultNamespace("item/empty_armor_slot_leggings"),
            ResourceLocation.withDefaultNamespace("item/empty_armor_slot_boots"),
            ResourceLocation.withDefaultNamespace("item/empty_slot_sword"),
            ResourceLocation.withDefaultNamespace("item/empty_slot_pickaxe"),
            ResourceLocation.withDefaultNamespace("item/empty_slot_axe"),
            ResourceLocation.withDefaultNamespace("item/empty_slot_shovel"),
            ResourceLocation.withDefaultNamespace("item/empty_slot_hoe")
         ),
         List.of(ResourceLocation.withDefaultNamespace("item/empty_slot_ingot")),
         new FeatureFlag[0]
      ),
      new Properties().rarity(Rarity.UNCOMMON)
   );
   public static final ResourceKey<CreativeModeTab> CREATIVE_TAB_KEY = ResourceKey.create(
      BuiltInRegistries.CREATIVE_MODE_TAB.key(), ResourceLocation.fromNamespaceAndPath("the_sift", "the_sift")
   );
   public static final CreativeModeTab CREATIVE_TAB = net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup.builder()
      .title(Component.translatable("itemGroup.the_sift"))
      .icon(() -> new ItemStack(ModBlocks.DRY_HEALTHY_SCULK_GROWTH_ITEM))
      .displayItems(
         (params, output) -> {
            ItemStack horn = new ItemStack(ModBlocks.SONOROUS_DEEPSLATE_ITEM);
            horn.set(
               DataComponents.BLOCK_STATE,
               new BlockItemStateProperties(Map.of(SonorousDeepslateBlock.MODE.getName(), SonorousDeepslateBlock.Mode.HORN.getSerializedName()))
            );
            output.accept(horn);
            ItemStack note = new ItemStack(ModBlocks.SONOROUS_DEEPSLATE_ITEM);
            note.set(
               DataComponents.BLOCK_STATE,
               new BlockItemStateProperties(Map.of(SonorousDeepslateBlock.MODE.getName(), SonorousDeepslateBlock.Mode.NOTE.getSerializedName()))
            );
            output.accept(note);
            output.accept(ModBlocks.REINFORCED_SIFTSLATE_ITEM);
            output.accept(ModBlocks.SIFTSLATE_ITEM);
            output.accept(ModBlocks.SIFTSLATE_COAL_ORE_ITEM);
            output.accept(ModBlocks.SIFTSLATE_DIAMOND_ORE_ITEM);
            output.accept(ModBlocks.SIFTSLATE_EMERALD_ORE_ITEM);
            output.accept(ModBlocks.SIFTSLATE_CHAROITE_ORE_ITEM);
            output.accept(ModBlocks.SIFTSLATE_SIFTITE_ORE_ITEM);
            output.accept(ModBlocks.COBBLED_SIFTSLATE_ITEM);
            output.accept(ModBlocks.COBBLED_SIFTSLATE_STAIRS_ITEM);
            output.accept(ModBlocks.COBBLED_SIFTSLATE_SLAB_ITEM);
            output.accept(ModBlocks.COBBLED_SIFTSLATE_WALL_ITEM);
            output.accept(ModBlocks.SIFTSLATE_BRICKS_ITEM);
            output.accept(ModBlocks.SIFTSLATE_BRICK_STAIRS_ITEM);
            output.accept(ModBlocks.SIFTSLATE_BRICK_SLAB_ITEM);
            output.accept(ModBlocks.SIFTSLATE_BRICK_WALL_ITEM);
            output.accept(ModBlocks.CHISELED_SIFTSLATE_ITEM);
            output.accept(ModBlocks.SIFTSLATE_GROWTH_ITEM);
            output.accept(ModBlocks.SIFT_RUBBLE_ITEM);
            output.accept(ModBlocks.HEALTHY_SCULK_ITEM);
            output.accept(ModBlocks.DRY_HEALTHY_SCULK_ITEM);
            output.accept(ModBlocks.DRY_HEALTHY_SCULK_GROWTH_ITEM);
            output.accept(ModBlocks.ICHOR_SNOW_BLOCK_ITEM);
            output.accept(ModBlocks.ICHOR_SNOW_ITEM);
            output.accept(ICHOR_SNOWBALL);
            output.accept(ModBlocks.OVERGROWN_WILLOW_LOG_ITEM);
            output.accept(ModBlocks.OVERGROWN_WILLOW_WOOD_ITEM);
            output.accept(ModBlocks.STRIPPED_OVERGROWN_WILLOW_LOG_ITEM);
            output.accept(ModBlocks.STRIPPED_OVERGROWN_WILLOW_WOOD_ITEM);
            output.accept(ModBlocks.OVERGROWN_WILLOW_PLANKS_ITEM);
            output.accept(ModBlocks.OVERGROWN_WILLOW_STAIRS_ITEM);
            output.accept(ModBlocks.OVERGROWN_WILLOW_SLAB_ITEM);
            output.accept(ModBlocks.OVERGROWN_WILLOW_FENCE_ITEM);
            output.accept(ModBlocks.OVERGROWN_WILLOW_FENCE_GATE_ITEM);
            output.accept(ModBlocks.OVERGROWN_WILLOW_DOOR_ITEM);
            output.accept(ModBlocks.OVERGROWN_WILLOW_TRAPDOOR_ITEM);
            output.accept(ModBlocks.OVERGROWN_WILLOW_PRESSURE_PLATE_ITEM);
            output.accept(ModBlocks.OVERGROWN_WILLOW_BUTTON_ITEM);
            output.accept(ModBlocks.OVERGROWN_WILLOW_SIGN_ITEM);
            output.accept(ModBlocks.OVERGROWN_WILLOW_HANGING_SIGN_ITEM);
            output.accept(OVERGROWN_WILLOW_BOAT);
            output.accept(OVERGROWN_WILLOW_CHEST_BOAT);
            output.accept(ModBlocks.SOUL_BLOCK_ITEM);
            output.accept(ModBlocks.OVERGROWN_FRONDS_ITEM);
            output.accept(ModBlocks.OVERGROWN_CHARD_ITEM);
            output.accept(ModBlocks.OVERGROWN_STALKS_ITEM);
            output.accept(ModBlocks.OVERGROWN_HANGING_ROOTS_ITEM);
            output.accept(ModBlocks.SIFTSLATE_STALKS_ITEM);
            output.accept(ModBlocks.SIFTSLATE_HANGING_ROOTS_ITEM);
            output.accept(ModBlocks.HEALTHY_SCULK_SPROUTS_ITEM);
            output.accept(ModBlocks.DRY_HEALTHY_SCULK_SPROUTS_ITEM);
            output.accept(ModBlocks.OVERGROWN_LOTUS_ITEM);
            output.accept(ModBlocks.SUNBURST_PLANT_ITEM);
            output.accept(ModBlocks.WHISPERBLOOM_ITEM);
            output.accept(SCULKFLOWER_SEEDS);
            output.accept(ModBlocks.SCULKFLOWER_ITEM);
            output.accept(ModBlocks.OVERGROWN_WILLOW_FOLIAGE_ITEM);
            output.accept(ModBlocks.OVERGROWN_WILLOW_SAPLING_ITEM);
            output.accept(ModBlocks.OVERGROWN_WILLOW_VINES_ITEM);
            output.accept(RAW_SIFTER_MEAT);
            output.accept(COOKED_SIFTER_MEAT);
            output.accept(ModFluids.ICHOR_BUCKET);
            output.accept(ICHOR_BOTTLE);
            output.accept(ModBlocks.ICHOR_GLASS_ITEM);
            output.accept(ModBlocks.ICHOR_GLASS_PANE_ITEM);
            output.accept(ICHOR_SHARD);
            output.accept(CHAROITE);
            output.accept(SIFTITE_INGOT);
            output.accept(SIFTITE_NUGGET);
            output.accept(SIFTITE_SWORD);
            output.accept(SIFTITE_PICKAXE);
            output.accept(SIFTITE_AXE);
            output.accept(SIFTITE_SHOVEL);
            output.accept(SIFTITE_HOE);
            output.accept(SIFTITE_HELMET);
            output.accept(SIFTITE_CHESTPLATE);
            output.accept(SIFTITE_LEGGINGS);
            output.accept(SIFTITE_BOOTS);
            output.accept(SIFTITE_UPGRADE_SMITHING_TEMPLATE);
            output.accept(MUSIC_DISC_RIFT);
            output.accept(MUSIC_DISC_WELLSPRING);
            output.accept(ModBlocks.SIFT_PORTAL_ITEM);
            output.accept(SIFT_RIFT);
            output.accept(SINGER_SPAWN_EGG);
            output.accept(ECHO_GOLEM_SPAWN_EGG);
            output.accept(DARK_SNIFFER_SPAWN_EGG);
            output.accept(BlubItems.BLUB_SPAWN_EGG);
            output.accept(SIFTER_SPAWN_EGG);
         }
      )
      .build();

   private ModItems() {
   }

   public static void initialize() {
      IchorBottleItem.registerInteractions();
      ComposterBlock.COMPOSTABLES.put(SCULKFLOWER_SEEDS, 0.3F);
      ComposterBlock.COMPOSTABLES.put(ModBlocks.SCULKFLOWER_ITEM, 0.65F);
      ModRegistration.register(BuiltInRegistries.CREATIVE_MODE_TAB, CREATIVE_TAB_KEY, CREATIVE_TAB);
   }

   private static Item registerItem(String name, Properties properties) {
      ResourceKey<Item> key = ResourceKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.fromNamespaceAndPath("the_sift", name));
      return ModRegistration.register(BuiltInRegistries.ITEM, key, new Item(properties));
   }

   private static <T extends Item> T registerItem(String name, Function<Properties, T> factory, Properties properties) {
      ResourceKey<Item> key = ResourceKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.fromNamespaceAndPath("the_sift", name));
      return ModRegistration.register(BuiltInRegistries.ITEM, key, factory.apply(properties));
   }
}
