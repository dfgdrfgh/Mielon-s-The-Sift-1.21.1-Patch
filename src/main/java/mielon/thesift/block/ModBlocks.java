package mielon.thesift.block;

import java.util.Collections;
import java.util.HashSet;
import java.util.function.Function;
import mielon.thesift.block.entity.SiftPortalBlockEntity;
import mielon.thesift.block.entity.SonorousDeepslateBlockEntity;
import mielon.thesift.mixin.BlockEntityTypeBlocksAccessor;
import mielon.thesift.platform.BlockProperties;
import mielon.thesift.platform.ModRegistration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.HangingRootsBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.storage.loot.LootTable;

public class ModBlocks {
   public static final ResourceKey<Block> SIFT_PORTAL_KEY = ResourceKey.create(
      BuiltInRegistries.BLOCK.key(), ResourceLocation.fromNamespaceAndPath("the_sift", "sift_portal")
   );
   public static final Block SIFT_PORTAL = ModRegistration.register(
      BuiltInRegistries.BLOCK,
      SIFT_PORTAL_KEY,
      new SiftPortalBlock(Properties.ofFullCopy(Blocks.NETHER_PORTAL).noCollission().noOcclusion().strength(-1.0F).lightLevel(state -> 12))
   );
   public static final ResourceKey<Item> SIFT_PORTAL_ITEM_KEY = ResourceKey.create(
      BuiltInRegistries.ITEM.key(), ResourceLocation.fromNamespaceAndPath("the_sift", "sift_portal")
   );
   public static final BlockItem SIFT_PORTAL_ITEM = ModRegistration.register(
      BuiltInRegistries.ITEM, SIFT_PORTAL_ITEM_KEY, new BlockItem(SIFT_PORTAL, new net.minecraft.world.item.Item.Properties())
   );
   public static final ResourceKey<BlockEntityType<?>> SIFT_PORTAL_BLOCK_ENTITY_KEY = ResourceKey.create(
      BuiltInRegistries.BLOCK_ENTITY_TYPE.key(), ResourceLocation.fromNamespaceAndPath("the_sift", "sift_portal")
   );
   public static final BlockEntityType<SiftPortalBlockEntity> SIFT_PORTAL_BLOCK_ENTITY = ModRegistration.register(
      BuiltInRegistries.BLOCK_ENTITY_TYPE, SIFT_PORTAL_BLOCK_ENTITY_KEY, Builder.of(SiftPortalBlockEntity::new, new Block[]{SIFT_PORTAL}).build(null)
   );
   public static final ResourceKey<Block> SONOROUS_DEEPSLATE_KEY = ResourceKey.create(
      BuiltInRegistries.BLOCK.key(), ResourceLocation.fromNamespaceAndPath("the_sift", "sonorous_deepslate")
   );
   public static final Block SONOROUS_DEEPSLATE = ModRegistration.register(
      BuiltInRegistries.BLOCK, SONOROUS_DEEPSLATE_KEY, new SonorousDeepslateBlock(Properties.ofFullCopy(Blocks.REINFORCED_DEEPSLATE))
   );
   public static final ResourceKey<Item> SONOROUS_DEEPSLATE_ITEM_KEY = ResourceKey.create(
      BuiltInRegistries.ITEM.key(), ResourceLocation.fromNamespaceAndPath("the_sift", "sonorous_deepslate")
   );
   public static final BlockItem SONOROUS_DEEPSLATE_ITEM = ModRegistration.register(
      BuiltInRegistries.ITEM, SONOROUS_DEEPSLATE_ITEM_KEY, new SonorousDeepslateBlockItem(SONOROUS_DEEPSLATE, new net.minecraft.world.item.Item.Properties())
   );
   public static final ResourceKey<BlockEntityType<?>> SONOROUS_DEEPSLATE_BLOCK_ENTITY_KEY = ResourceKey.create(
      BuiltInRegistries.BLOCK_ENTITY_TYPE.key(), ResourceLocation.fromNamespaceAndPath("the_sift", "sonorous_deepslate")
   );
   public static final BlockEntityType<SonorousDeepslateBlockEntity> SONOROUS_DEEPSLATE_BLOCK_ENTITY = ModRegistration.register(
      BuiltInRegistries.BLOCK_ENTITY_TYPE,
      SONOROUS_DEEPSLATE_BLOCK_ENTITY_KEY,
      Builder.of(SonorousDeepslateBlockEntity::new, new Block[]{SONOROUS_DEEPSLATE}).build(null)
   );
   public static final ResourceKey<Block> SIFTSLATE_KEY = ResourceKey.create(
      BuiltInRegistries.BLOCK.key(), ResourceLocation.fromNamespaceAndPath("the_sift", "siftslate")
   );
   public static final Block SIFTSLATE = ModRegistration.register(BuiltInRegistries.BLOCK, SIFTSLATE_KEY, new Block(Properties.ofFullCopy(Blocks.DEEPSLATE)));
   public static final ResourceKey<Item> SIFTSLATE_ITEM_KEY = ResourceKey.create(
      BuiltInRegistries.ITEM.key(), ResourceLocation.fromNamespaceAndPath("the_sift", "siftslate")
   );
   public static final BlockItem SIFTSLATE_ITEM = ModRegistration.register(
      BuiltInRegistries.ITEM, SIFTSLATE_ITEM_KEY, new BlockItem(SIFTSLATE, new net.minecraft.world.item.Item.Properties())
   );
   public static final ResourceKey<Block> SIFTSLATE_GROWTH_KEY = ResourceKey.create(
      BuiltInRegistries.BLOCK.key(), ResourceLocation.fromNamespaceAndPath("the_sift", "siftslate_growth")
   );
   public static final Block SIFTSLATE_GROWTH = ModRegistration.register(
      BuiltInRegistries.BLOCK, SIFTSLATE_GROWTH_KEY, new SiftslateGrowthBlock(Properties.ofFullCopy(Blocks.STONE).sound(SoundType.DEEPSLATE).randomTicks())
   );
   public static final ResourceKey<Item> SIFTSLATE_GROWTH_ITEM_KEY = ResourceKey.create(
      BuiltInRegistries.ITEM.key(), ResourceLocation.fromNamespaceAndPath("the_sift", "siftslate_growth")
   );
   public static final BlockItem SIFTSLATE_GROWTH_ITEM = ModRegistration.register(
      BuiltInRegistries.ITEM, SIFTSLATE_GROWTH_ITEM_KEY, new BlockItem(SIFTSLATE_GROWTH, new net.minecraft.world.item.Item.Properties())
   );
   public static final Block COBBLED_SIFTSLATE = registerBlock("cobbled_siftslate", Block::new, Properties.ofFullCopy(Blocks.COBBLED_DEEPSLATE));
   public static final BlockItem COBBLED_SIFTSLATE_ITEM = registerBlockItem("cobbled_siftslate", COBBLED_SIFTSLATE);
   public static final Block COBBLED_SIFTSLATE_STAIRS = registerBlock(
      "cobbled_siftslate_stairs",
      properties -> new StairBlock(COBBLED_SIFTSLATE.defaultBlockState(), properties),
      Properties.ofFullCopy(Blocks.COBBLED_DEEPSLATE_STAIRS)
   );
   public static final BlockItem COBBLED_SIFTSLATE_STAIRS_ITEM = registerBlockItem("cobbled_siftslate_stairs", COBBLED_SIFTSLATE_STAIRS);
   public static final Block COBBLED_SIFTSLATE_SLAB = registerBlock(
      "cobbled_siftslate_slab", SlabBlock::new, Properties.ofFullCopy(Blocks.COBBLED_DEEPSLATE_SLAB)
   );
   public static final BlockItem COBBLED_SIFTSLATE_SLAB_ITEM = registerBlockItem("cobbled_siftslate_slab", COBBLED_SIFTSLATE_SLAB);
   public static final Block COBBLED_SIFTSLATE_WALL = registerBlock(
      "cobbled_siftslate_wall", WallBlock::new, Properties.ofFullCopy(Blocks.COBBLED_DEEPSLATE_WALL)
   );
   public static final BlockItem COBBLED_SIFTSLATE_WALL_ITEM = registerBlockItem("cobbled_siftslate_wall", COBBLED_SIFTSLATE_WALL);
   public static final Block SIFTSLATE_BRICKS = registerBlock("siftslate_bricks", Block::new, Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS));
   public static final BlockItem SIFTSLATE_BRICKS_ITEM = registerBlockItem("siftslate_bricks", SIFTSLATE_BRICKS);
   public static final Block SIFTSLATE_BRICK_STAIRS = registerBlock(
      "siftslate_brick_stairs",
      properties -> new StairBlock(SIFTSLATE_BRICKS.defaultBlockState(), properties),
      Properties.ofFullCopy(Blocks.DEEPSLATE_BRICK_STAIRS)
   );
   public static final BlockItem SIFTSLATE_BRICK_STAIRS_ITEM = registerBlockItem("siftslate_brick_stairs", SIFTSLATE_BRICK_STAIRS);
   public static final Block SIFTSLATE_BRICK_SLAB = registerBlock("siftslate_brick_slab", SlabBlock::new, Properties.ofFullCopy(Blocks.DEEPSLATE_BRICK_SLAB));
   public static final BlockItem SIFTSLATE_BRICK_SLAB_ITEM = registerBlockItem("siftslate_brick_slab", SIFTSLATE_BRICK_SLAB);
   public static final Block SIFTSLATE_BRICK_WALL = registerBlock("siftslate_brick_wall", WallBlock::new, Properties.ofFullCopy(Blocks.DEEPSLATE_BRICK_WALL));
   public static final BlockItem SIFTSLATE_BRICK_WALL_ITEM = registerBlockItem("siftslate_brick_wall", SIFTSLATE_BRICK_WALL);
   public static final Block CHISELED_SIFTSLATE = registerBlock("chiseled_siftslate", Block::new, Properties.ofFullCopy(Blocks.CHISELED_DEEPSLATE));
   public static final BlockItem CHISELED_SIFTSLATE_ITEM = registerBlockItem("chiseled_siftslate", CHISELED_SIFTSLATE);
   public static final Block SIFT_RUBBLE = registerBlock(
      "sift_rubble", SiftRubbleBlock::new, Properties.ofFullCopy(Blocks.GRAVEL).sound(SoundType.SUSPICIOUS_GRAVEL)
   );
   public static final BlockItem SIFT_RUBBLE_ITEM = registerBlockItem("sift_rubble", SIFT_RUBBLE);
   public static final Block ICHOR_GLASS = registerBlock("ichor_glass", SiftGlassBlock::new, Properties.ofFullCopy(Blocks.GLASS));
   public static final BlockItem ICHOR_GLASS_ITEM = registerBlockItem("ichor_glass", ICHOR_GLASS);
   public static final Block ICHOR_GLASS_PANE = registerBlock("ichor_glass_pane", IronBarsBlock::new, Properties.ofFullCopy(Blocks.GLASS_PANE));
   public static final BlockItem ICHOR_GLASS_PANE_ITEM = registerBlockItem("ichor_glass_pane", ICHOR_GLASS_PANE);
   public static final Block ICHOR_SNOW_BLOCK = registerBlock("ichor_snow_block", Block::new, Properties.ofFullCopy(Blocks.SNOW_BLOCK));
   public static final BlockItem ICHOR_SNOW_BLOCK_ITEM = registerBlockItem("ichor_snow_block", ICHOR_SNOW_BLOCK);
   public static final SnowLayerBlock ICHOR_SNOW = registerBlock("ichor_snow", SnowLayerBlock::new, Properties.ofFullCopy(Blocks.SNOW));
   public static final BlockItem ICHOR_SNOW_ITEM = registerBlockItem("ichor_snow", ICHOR_SNOW);
   public static final SculkflowerCropBlock SCULKFLOWER_CROP = registerBlock(
      "sculkflower_crop", SculkflowerCropBlock::new, Properties.of().noCollission().randomTicks().instabreak().sound(SoundType.CROP)
   );
   public static final SculkflowerBlock SCULKFLOWER = registerBlock("sculkflower", SculkflowerBlock::new, Properties.ofFullCopy(Blocks.TORCHFLOWER));
   public static final BlockItem SCULKFLOWER_ITEM = registerBlockItem("sculkflower", SCULKFLOWER);
   public static final ResourceKey<Block> HEALTHY_SCULK_KEY = ResourceKey.create(
      BuiltInRegistries.BLOCK.key(), ResourceLocation.fromNamespaceAndPath("the_sift", "healthy_sculk")
   );
   public static final Block HEALTHY_SCULK = ModRegistration.register(
      BuiltInRegistries.BLOCK, HEALTHY_SCULK_KEY, new HealthySculkSoilBlock(Properties.ofFullCopy(Blocks.SCULK))
   );
   public static final ResourceKey<Item> HEALTHY_SCULK_ITEM_KEY = ResourceKey.create(
      BuiltInRegistries.ITEM.key(), ResourceLocation.fromNamespaceAndPath("the_sift", "healthy_sculk")
   );
   public static final BlockItem HEALTHY_SCULK_ITEM = ModRegistration.register(
      BuiltInRegistries.ITEM, HEALTHY_SCULK_ITEM_KEY, new BlockItem(HEALTHY_SCULK, new net.minecraft.world.item.Item.Properties())
   );
   public static final ResourceKey<Block> DRY_HEALTHY_SCULK_KEY = ResourceKey.create(
      BuiltInRegistries.BLOCK.key(), ResourceLocation.fromNamespaceAndPath("the_sift", "dry_healthy_sculk")
   );
   public static final Block DRY_HEALTHY_SCULK = ModRegistration.register(
      BuiltInRegistries.BLOCK, DRY_HEALTHY_SCULK_KEY, new HealthySculkSoilBlock(Properties.ofFullCopy(Blocks.SCULK))
   );
   public static final ResourceKey<Item> DRY_HEALTHY_SCULK_ITEM_KEY = ResourceKey.create(
      BuiltInRegistries.ITEM.key(), ResourceLocation.fromNamespaceAndPath("the_sift", "dry_healthy_sculk")
   );
   public static final BlockItem DRY_HEALTHY_SCULK_ITEM = ModRegistration.register(
      BuiltInRegistries.ITEM, DRY_HEALTHY_SCULK_ITEM_KEY, new BlockItem(DRY_HEALTHY_SCULK, new net.minecraft.world.item.Item.Properties())
   );
   public static final ResourceKey<Block> DRY_HEALTHY_SCULK_GROWTH_KEY = blockKey("dry_healthy_sculk_growth");
   public static final Block DRY_HEALTHY_SCULK_GROWTH = ModRegistration.register(
      BuiltInRegistries.BLOCK, DRY_HEALTHY_SCULK_GROWTH_KEY, new DryHealthySculkGrowthBlock(Properties.ofFullCopy(Blocks.SCULK).randomTicks())
   );
   public static final ResourceKey<Item> DRY_HEALTHY_SCULK_GROWTH_ITEM_KEY = itemKey("dry_healthy_sculk_growth");
   public static final BlockItem DRY_HEALTHY_SCULK_GROWTH_ITEM = ModRegistration.register(
      BuiltInRegistries.ITEM, DRY_HEALTHY_SCULK_GROWTH_ITEM_KEY, new BlockItem(DRY_HEALTHY_SCULK_GROWTH, new net.minecraft.world.item.Item.Properties())
   );
   public static final ResourceKey<Block> ICHOR_CAULDRON_KEY = blockKey("ichor_cauldron");
   public static final Block ICHOR_CAULDRON = ModRegistration.register(
      BuiltInRegistries.BLOCK,
      ICHOR_CAULDRON_KEY,
      new IchorCauldronBlock(Properties.ofFullCopy(Blocks.WATER_CAULDRON).lightLevel(state -> (Integer)state.getValue(LayeredCauldronBlock.LEVEL) * 5))
   );
   public static final ResourceKey<Block> SOUL_BLOCK_KEY = blockKey("soul_block");
   public static final Block SOUL_BLOCK = ModRegistration.register(
      BuiltInRegistries.BLOCK,
      SOUL_BLOCK_KEY,
      new SoulBlock(Properties.ofFullCopy(Blocks.SOUL_SOIL).strength(0.35F, 0.35F).sound(SoundType.SOUL_SOIL).lightLevel(state -> 14).noLootTable())
   );
   public static final ResourceKey<Item> SOUL_BLOCK_ITEM_KEY = itemKey("soul_block");
   public static final BlockItem SOUL_BLOCK_ITEM = ModRegistration.register(
      BuiltInRegistries.ITEM, SOUL_BLOCK_ITEM_KEY, new BlockItem(SOUL_BLOCK, new net.minecraft.world.item.Item.Properties())
   );
   public static final ResourceKey<Block> OVERGROWN_CHARD_KEY = blockKey("overgrown_chard");
   public static final Block OVERGROWN_CHARD = ModRegistration.register(
      BuiltInRegistries.BLOCK, OVERGROWN_CHARD_KEY, new SiftPlantBlock(Properties.ofFullCopy(Blocks.BEETROOTS).replaceable())
   );
   public static final ResourceKey<Item> OVERGROWN_CHARD_ITEM_KEY = itemKey("overgrown_chard");
   public static final BlockItem OVERGROWN_CHARD_ITEM = ModRegistration.register(
      BuiltInRegistries.ITEM, OVERGROWN_CHARD_ITEM_KEY, new BlockItem(OVERGROWN_CHARD, new net.minecraft.world.item.Item.Properties())
   );
   public static final ResourceKey<Block> OVERGROWN_STALKS_KEY = blockKey("overgrown_stalks");
   public static final Block OVERGROWN_STALKS = ModRegistration.register(
      BuiltInRegistries.BLOCK, OVERGROWN_STALKS_KEY, new SiftPlantBlock(Properties.ofFullCopy(Blocks.NETHER_SPROUTS))
   );
   public static final ResourceKey<Item> OVERGROWN_STALKS_ITEM_KEY = itemKey("overgrown_stalks");
   public static final BlockItem OVERGROWN_STALKS_ITEM = ModRegistration.register(
      BuiltInRegistries.ITEM, OVERGROWN_STALKS_ITEM_KEY, new BlockItem(OVERGROWN_STALKS, new net.minecraft.world.item.Item.Properties())
   );
   public static final ResourceKey<Block> OVERGROWN_FRONDS_KEY = blockKey("overgrown_fronds");
   public static final Block OVERGROWN_FRONDS = ModRegistration.register(
      BuiltInRegistries.BLOCK, OVERGROWN_FRONDS_KEY, new SiftPlantBlock(Properties.ofFullCopy(Blocks.NETHER_SPROUTS))
   );
   public static final ResourceKey<Item> OVERGROWN_FRONDS_ITEM_KEY = itemKey("overgrown_fronds");
   public static final BlockItem OVERGROWN_FRONDS_ITEM = ModRegistration.register(
      BuiltInRegistries.ITEM, OVERGROWN_FRONDS_ITEM_KEY, new BlockItem(OVERGROWN_FRONDS, new net.minecraft.world.item.Item.Properties())
   );
   public static final ResourceKey<Block> OVERGROWN_LOTUS_KEY = blockKey("overgrown_lotus");
   public static final Block OVERGROWN_LOTUS = ModRegistration.register(
      BuiltInRegistries.BLOCK, OVERGROWN_LOTUS_KEY, new OvergrownLotusBlock(Properties.ofFullCopy(Blocks.NETHER_SPROUTS))
   );
   public static final ResourceKey<Item> OVERGROWN_LOTUS_ITEM_KEY = itemKey("overgrown_lotus");
   public static final BlockItem OVERGROWN_LOTUS_ITEM = ModRegistration.register(
      BuiltInRegistries.ITEM, OVERGROWN_LOTUS_ITEM_KEY, new BlockItem(OVERGROWN_LOTUS, new net.minecraft.world.item.Item.Properties())
   );
   public static final ResourceKey<Block> SUNBURST_PLANT_KEY = blockKey("sunburst_plant");
   public static final Block SUNBURST_PLANT = ModRegistration.register(
      BuiltInRegistries.BLOCK, SUNBURST_PLANT_KEY, new SiftPlantBlock(Properties.ofFullCopy(Blocks.NETHER_SPROUTS), true)
   );
   public static final ResourceKey<Item> SUNBURST_PLANT_ITEM_KEY = itemKey("sunburst_plant");
   public static final BlockItem SUNBURST_PLANT_ITEM = ModRegistration.register(
      BuiltInRegistries.ITEM, SUNBURST_PLANT_ITEM_KEY, new BlockItem(SUNBURST_PLANT, new net.minecraft.world.item.Item.Properties())
   );
   public static final ResourceKey<Block> WHISPERBLOOM_KEY = blockKey("whisperbloom");
   public static final Block WHISPERBLOOM = ModRegistration.register(
      BuiltInRegistries.BLOCK, WHISPERBLOOM_KEY, new SiftPlantBlock(Properties.ofFullCopy(Blocks.NETHER_SPROUTS), true)
   );
   public static final ResourceKey<Item> WHISPERBLOOM_ITEM_KEY = itemKey("whisperbloom");
   public static final BlockItem WHISPERBLOOM_ITEM = ModRegistration.register(
      BuiltInRegistries.ITEM, WHISPERBLOOM_ITEM_KEY, new BlockItem(WHISPERBLOOM, new net.minecraft.world.item.Item.Properties())
   );
   public static final ResourceKey<Block> SIFTSLATE_STALKS_KEY = blockKey("siftslate_stalks");
   public static final Block SIFTSLATE_STALKS = ModRegistration.register(
      BuiltInRegistries.BLOCK, SIFTSLATE_STALKS_KEY, new SiftPlantBlock(Properties.ofFullCopy(Blocks.NETHER_SPROUTS))
   );
   public static final ResourceKey<Item> SIFTSLATE_STALKS_ITEM_KEY = itemKey("siftslate_stalks");
   public static final BlockItem SIFTSLATE_STALKS_ITEM = ModRegistration.register(
      BuiltInRegistries.ITEM, SIFTSLATE_STALKS_ITEM_KEY, new BlockItem(SIFTSLATE_STALKS, new net.minecraft.world.item.Item.Properties())
   );
   public static final ResourceKey<Block> HEALTHY_SCULK_SPROUTS_KEY = blockKey("healthy_sculk_sprouts");
   public static final Block HEALTHY_SCULK_SPROUTS = ModRegistration.register(
      BuiltInRegistries.BLOCK, HEALTHY_SCULK_SPROUTS_KEY, new SiftPlantBlock(Properties.ofFullCopy(Blocks.NETHER_SPROUTS))
   );
   public static final ResourceKey<Item> HEALTHY_SCULK_SPROUTS_ITEM_KEY = itemKey("healthy_sculk_sprouts");
   public static final BlockItem HEALTHY_SCULK_SPROUTS_ITEM = ModRegistration.register(
      BuiltInRegistries.ITEM, HEALTHY_SCULK_SPROUTS_ITEM_KEY, new BlockItem(HEALTHY_SCULK_SPROUTS, new net.minecraft.world.item.Item.Properties())
   );
   public static final ResourceKey<Block> DRY_HEALTHY_SCULK_SPROUTS_KEY = blockKey("dry_healthy_sculk_sprouts");
   public static final Block DRY_HEALTHY_SCULK_SPROUTS = ModRegistration.register(
      BuiltInRegistries.BLOCK, DRY_HEALTHY_SCULK_SPROUTS_KEY, new SiftPlantBlock(Properties.ofFullCopy(Blocks.NETHER_SPROUTS))
   );
   public static final ResourceKey<Item> DRY_HEALTHY_SCULK_SPROUTS_ITEM_KEY = itemKey("dry_healthy_sculk_sprouts");
   public static final BlockItem DRY_HEALTHY_SCULK_SPROUTS_ITEM = ModRegistration.register(
      BuiltInRegistries.ITEM, DRY_HEALTHY_SCULK_SPROUTS_ITEM_KEY, new BlockItem(DRY_HEALTHY_SCULK_SPROUTS, new net.minecraft.world.item.Item.Properties())
   );
   public static final Block SIFTSLATE_HANGING_ROOTS = registerBlock(
      "siftslate_hanging_roots", HangingRootsBlock::new, Properties.ofFullCopy(Blocks.HANGING_ROOTS).sound(SoundType.NETHER_SPROUTS)
   );
   public static final BlockItem SIFTSLATE_HANGING_ROOTS_ITEM = registerBlockItem("siftslate_hanging_roots", SIFTSLATE_HANGING_ROOTS);
   public static final Block OVERGROWN_HANGING_ROOTS = registerBlock(
      "overgrown_hanging_roots", HangingRootsBlock::new, Properties.ofFullCopy(Blocks.HANGING_ROOTS).sound(SoundType.NETHER_SPROUTS)
   );
   public static final BlockItem OVERGROWN_HANGING_ROOTS_ITEM = registerBlockItem("overgrown_hanging_roots", OVERGROWN_HANGING_ROOTS);
   public static final Block OVERGROWN_WILLOW_PLANKS = registerBlock("overgrown_willow_planks", Block::new, Properties.ofFullCopy(Blocks.OAK_PLANKS));
   public static final BlockItem OVERGROWN_WILLOW_PLANKS_ITEM = registerBlockItem("overgrown_willow_planks", OVERGROWN_WILLOW_PLANKS);
   public static final Block OVERGROWN_WILLOW_LOG = registerBlock("overgrown_willow_log", RotatedPillarBlock::new, Properties.ofFullCopy(Blocks.OAK_LOG));
   public static final BlockItem OVERGROWN_WILLOW_LOG_ITEM = registerBlockItem("overgrown_willow_log", OVERGROWN_WILLOW_LOG);
   public static final Block OVERGROWN_WILLOW_WOOD = registerBlock("overgrown_willow_wood", RotatedPillarBlock::new, Properties.ofFullCopy(Blocks.OAK_WOOD));
   public static final BlockItem OVERGROWN_WILLOW_WOOD_ITEM = registerBlockItem("overgrown_willow_wood", OVERGROWN_WILLOW_WOOD);
   public static final Block STRIPPED_OVERGROWN_WILLOW_LOG = registerBlock(
      "stripped_overgrown_willow_log", RotatedPillarBlock::new, Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG)
   );
   public static final BlockItem STRIPPED_OVERGROWN_WILLOW_LOG_ITEM = registerBlockItem("stripped_overgrown_willow_log", STRIPPED_OVERGROWN_WILLOW_LOG);
   public static final Block STRIPPED_OVERGROWN_WILLOW_WOOD = registerBlock(
      "stripped_overgrown_willow_wood", RotatedPillarBlock::new, Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD)
   );
   public static final BlockItem STRIPPED_OVERGROWN_WILLOW_WOOD_ITEM = registerBlockItem("stripped_overgrown_willow_wood", STRIPPED_OVERGROWN_WILLOW_WOOD);
   public static final Block OVERGROWN_WILLOW_STAIRS = registerBlock(
      "overgrown_willow_stairs",
      properties -> new StairBlock(OVERGROWN_WILLOW_PLANKS.defaultBlockState(), properties),
      Properties.ofFullCopy(Blocks.OAK_STAIRS)
   );
   public static final BlockItem OVERGROWN_WILLOW_STAIRS_ITEM = registerBlockItem("overgrown_willow_stairs", OVERGROWN_WILLOW_STAIRS);
   public static final Block OVERGROWN_WILLOW_SLAB = registerBlock("overgrown_willow_slab", SlabBlock::new, Properties.ofFullCopy(Blocks.OAK_SLAB));
   public static final BlockItem OVERGROWN_WILLOW_SLAB_ITEM = registerBlockItem("overgrown_willow_slab", OVERGROWN_WILLOW_SLAB);
   public static final Block OVERGROWN_WILLOW_FENCE = registerBlock("overgrown_willow_fence", FenceBlock::new, Properties.ofFullCopy(Blocks.OAK_FENCE));
   public static final BlockItem OVERGROWN_WILLOW_FENCE_ITEM = registerBlockItem("overgrown_willow_fence", OVERGROWN_WILLOW_FENCE);
   public static final Block OVERGROWN_WILLOW_FENCE_GATE = registerBlock(
      "overgrown_willow_fence_gate", properties -> new FenceGateBlock(WoodType.OAK, properties), Properties.ofFullCopy(Blocks.OAK_FENCE_GATE)
   );
   public static final BlockItem OVERGROWN_WILLOW_FENCE_GATE_ITEM = registerBlockItem("overgrown_willow_fence_gate", OVERGROWN_WILLOW_FENCE_GATE);
   public static final Block OVERGROWN_WILLOW_DOOR = registerBlock(
      "overgrown_willow_door", properties -> new DoorBlock(BlockSetType.OAK, properties), Properties.ofFullCopy(Blocks.OAK_DOOR)
   );
   public static final BlockItem OVERGROWN_WILLOW_DOOR_ITEM = registerBlockItem("overgrown_willow_door", OVERGROWN_WILLOW_DOOR);
   public static final Block OVERGROWN_WILLOW_TRAPDOOR = registerBlock(
      "overgrown_willow_trapdoor", properties -> new TrapDoorBlock(BlockSetType.OAK, properties), Properties.ofFullCopy(Blocks.OAK_TRAPDOOR)
   );
   public static final BlockItem OVERGROWN_WILLOW_TRAPDOOR_ITEM = registerBlockItem("overgrown_willow_trapdoor", OVERGROWN_WILLOW_TRAPDOOR);
   public static final Block OVERGROWN_WILLOW_PRESSURE_PLATE = registerBlock(
      "overgrown_willow_pressure_plate", properties -> new PressurePlateBlock(BlockSetType.OAK, properties), Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE)
   );
   public static final BlockItem OVERGROWN_WILLOW_PRESSURE_PLATE_ITEM = registerBlockItem("overgrown_willow_pressure_plate", OVERGROWN_WILLOW_PRESSURE_PLATE);
   public static final Block OVERGROWN_WILLOW_BUTTON = registerBlock(
      "overgrown_willow_button", properties -> new ButtonBlock(BlockSetType.OAK, 30, properties), Properties.ofFullCopy(Blocks.OAK_BUTTON)
   );
   public static final BlockItem OVERGROWN_WILLOW_BUTTON_ITEM = registerBlockItem("overgrown_willow_button", OVERGROWN_WILLOW_BUTTON);
   public static final Block OVERGROWN_WILLOW_SIGN = registerBlock(
      "overgrown_willow_sign", properties -> new StandingSignBlock(WoodType.OAK, properties), Properties.ofFullCopy(Blocks.OAK_SIGN)
   );
   public static final Block OVERGROWN_WILLOW_WALL_SIGN = registerBlock(
      "overgrown_willow_wall_sign",
      properties -> new WallSignBlock(WoodType.OAK, properties),
      dropsLike(Properties.ofFullCopy(Blocks.OAK_WALL_SIGN), "overgrown_willow_sign")
   );
   public static final Item OVERGROWN_WILLOW_SIGN_ITEM = registerItem(
      "overgrown_willow_sign", properties -> new SignItem(properties.stacksTo(16), OVERGROWN_WILLOW_SIGN, OVERGROWN_WILLOW_WALL_SIGN)
   );
   public static final Block OVERGROWN_WILLOW_HANGING_SIGN = registerBlock(
      "overgrown_willow_hanging_sign", properties -> new CeilingHangingSignBlock(WoodType.OAK, properties), Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN)
   );
   public static final Block OVERGROWN_WILLOW_WALL_HANGING_SIGN = registerBlock(
      "overgrown_willow_wall_hanging_sign",
      properties -> new WallHangingSignBlock(WoodType.OAK, properties),
      dropsLike(Properties.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN), "overgrown_willow_hanging_sign")
   );
   public static final Item OVERGROWN_WILLOW_HANGING_SIGN_ITEM = registerItem(
      "overgrown_willow_hanging_sign",
      properties -> new HangingSignItem(OVERGROWN_WILLOW_HANGING_SIGN, OVERGROWN_WILLOW_WALL_HANGING_SIGN, properties.stacksTo(16))
   );
   public static final Block OVERGROWN_WILLOW_FOLIAGE = registerBlock(
      "overgrown_willow_foliage",
      properties -> new OvergrownWillowFoliageBlock(0.01F, properties),
      Properties.ofFullCopy(Blocks.OAK_LEAVES).destroyTime(0.7F).sound(SoundType.NETHER_WART)
   );
   public static final BlockItem OVERGROWN_WILLOW_FOLIAGE_ITEM = registerBlockItem("overgrown_willow_foliage", OVERGROWN_WILLOW_FOLIAGE);
   public static final Block OVERGROWN_WILLOW_SAPLING = registerBlock(
      "overgrown_willow_sapling", OvergrownWillowSaplingBlock::new, Properties.ofFullCopy(Blocks.OAK_SAPLING).randomTicks().noCollission()
   );
   public static final BlockItem OVERGROWN_WILLOW_SAPLING_ITEM = registerBlockItem("overgrown_willow_sapling", OVERGROWN_WILLOW_SAPLING);
   public static final Block OVERGROWN_WILLOW_VINES = registerBlock(
      "overgrown_willow_vines", OvergrownWillowVinesBlock::new, Properties.ofFullCopy(Blocks.VINE)
   );
   public static final BlockItem OVERGROWN_WILLOW_VINES_ITEM = registerBlockItem("overgrown_willow_vines", OVERGROWN_WILLOW_VINES);
   public static final Block OVERGROWN_WILLOW_VINES_PLANT = registerBlock(
      "overgrown_willow_vines_plant", OvergrownWillowVinesPlantBlock::new, Properties.ofFullCopy(Blocks.VINE)
   );
   public static final DropExperienceBlock SIFTSLATE_COAL_ORE = registerBlock(
      "siftslate_coal_ore", properties -> new DropExperienceBlock(UniformInt.of(0, 2), properties), Properties.ofFullCopy(Blocks.DEEPSLATE_COAL_ORE)
   );
   public static final BlockItem SIFTSLATE_COAL_ORE_ITEM = registerBlockItem("siftslate_coal_ore", SIFTSLATE_COAL_ORE);
   public static final DropExperienceBlock SIFTSLATE_DIAMOND_ORE = registerBlock(
      "siftslate_diamond_ore", properties -> new DropExperienceBlock(UniformInt.of(3, 7), properties), Properties.ofFullCopy(Blocks.DEEPSLATE_DIAMOND_ORE)
   );
   public static final BlockItem SIFTSLATE_DIAMOND_ORE_ITEM = registerBlockItem("siftslate_diamond_ore", SIFTSLATE_DIAMOND_ORE);
   public static final DropExperienceBlock SIFTSLATE_EMERALD_ORE = registerBlock(
      "siftslate_emerald_ore", properties -> new DropExperienceBlock(UniformInt.of(3, 7), properties), Properties.ofFullCopy(Blocks.DEEPSLATE_EMERALD_ORE)
   );
   public static final BlockItem SIFTSLATE_EMERALD_ORE_ITEM = registerBlockItem("siftslate_emerald_ore", SIFTSLATE_EMERALD_ORE);
   public static final DropExperienceBlock SIFTSLATE_CHAROITE_ORE = registerBlock(
      "siftslate_charoite_ore", properties -> new DropExperienceBlock(UniformInt.of(2, 5), properties), Properties.ofFullCopy(Blocks.DEEPSLATE_LAPIS_ORE)
   );
   public static final BlockItem SIFTSLATE_CHAROITE_ORE_ITEM = registerBlockItem("siftslate_charoite_ore", SIFTSLATE_CHAROITE_ORE);
   public static final DropExperienceBlock SIFTSLATE_SIFTITE_ORE = registerBlock(
      "siftslate_siftite_ore", properties -> new DropExperienceBlock(UniformInt.of(3, 7), properties), Properties.ofFullCopy(Blocks.DEEPSLATE_DIAMOND_ORE)
   );
   public static final BlockItem SIFTSLATE_SIFTITE_ORE_ITEM = registerBlockItem("siftslate_siftite_ore", SIFTSLATE_SIFTITE_ORE);
   public static final ResourceKey<Block> REINFORCED_SIFTSLATE_KEY = ResourceKey.create(
      BuiltInRegistries.BLOCK.key(), ResourceLocation.fromNamespaceAndPath("the_sift", "reinforced_siftslate")
   );
   public static final Block REINFORCED_SIFTSLATE = ModRegistration.register(
      BuiltInRegistries.BLOCK, REINFORCED_SIFTSLATE_KEY, new Block(Properties.ofFullCopy(Blocks.REINFORCED_DEEPSLATE).strength(-1.0F, 3600000.0F))
   );
   public static final ResourceKey<Item> REINFORCED_SIFTSLATE_ITEM_KEY = ResourceKey.create(
      BuiltInRegistries.ITEM.key(), ResourceLocation.fromNamespaceAndPath("the_sift", "reinforced_siftslate")
   );
   public static final BlockItem REINFORCED_SIFTSLATE_ITEM = ModRegistration.register(
      BuiltInRegistries.ITEM, REINFORCED_SIFTSLATE_ITEM_KEY, new BlockItem(REINFORCED_SIFTSLATE, new net.minecraft.world.item.Item.Properties())
   );

   private static ResourceKey<Block> blockKey(String name) {
      return ResourceKey.create(BuiltInRegistries.BLOCK.key(), ResourceLocation.fromNamespaceAndPath("the_sift", name));
   }

   private static ResourceKey<Item> itemKey(String name) {
      return ResourceKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.fromNamespaceAndPath("the_sift", name));
   }

   private static <T extends Block> T registerBlock(String name, Function<Properties, T> factory, Properties properties) {
      ResourceKey<Block> key = blockKey(name);
      return ModRegistration.register(BuiltInRegistries.BLOCK, key, factory.apply(properties));
   }

   private static BlockItem registerBlockItem(String name, Block block) {
      ResourceKey<Item> key = itemKey(name);
      return ModRegistration.register(BuiltInRegistries.ITEM, key, new BlockItem(block, new net.minecraft.world.item.Item.Properties()));
   }

   private static <T extends Item> T registerItem(String name, Function<net.minecraft.world.item.Item.Properties, T> factory) {
      ResourceKey<Item> key = itemKey(name);
      return ModRegistration.register(BuiltInRegistries.ITEM, key, factory.apply(new net.minecraft.world.item.Item.Properties()));
   }

   private static Properties dropsLike(Properties properties, String lootTableName) {
      ResourceKey<LootTable> lootTable = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("the_sift", "blocks/" + lootTableName));
      ((mielon.thesift.mixin.BlockPropertiesAccessor)(Object)properties).theSift$setDrops(lootTable);
      return properties;
   }

   public static void initialize() {
      IchorCauldronInteractions.register();
      ComposterBlock.COMPOSTABLES.put(OVERGROWN_CHARD_ITEM, 0.3F);
      ComposterBlock.COMPOSTABLES.put(OVERGROWN_STALKS_ITEM, 0.3F);
      ComposterBlock.COMPOSTABLES.put(OVERGROWN_FRONDS_ITEM, 0.3F);
      ComposterBlock.COMPOSTABLES.put(OVERGROWN_LOTUS_ITEM, 0.65F);
      ComposterBlock.COMPOSTABLES.put(SUNBURST_PLANT_ITEM, 0.65F);
      ComposterBlock.COMPOSTABLES.put(WHISPERBLOOM_ITEM, 0.65F);
      ComposterBlock.COMPOSTABLES.put(SIFTSLATE_STALKS_ITEM, 0.3F);
      ComposterBlock.COMPOSTABLES.put(HEALTHY_SCULK_SPROUTS_ITEM, 0.3F);
      ComposterBlock.COMPOSTABLES.put(DRY_HEALTHY_SCULK_SPROUTS_ITEM, 0.3F);
      ComposterBlock.COMPOSTABLES.put(SIFTSLATE_HANGING_ROOTS_ITEM, 0.3F);
      ComposterBlock.COMPOSTABLES.put(OVERGROWN_HANGING_ROOTS_ITEM, 0.3F);
      BlockProperties.registerStrippable(OVERGROWN_WILLOW_LOG, STRIPPED_OVERGROWN_WILLOW_LOG);
      BlockProperties.registerStrippable(OVERGROWN_WILLOW_WOOD, STRIPPED_OVERGROWN_WILLOW_WOOD);
      FireBlock flammable = (FireBlock)Blocks.FIRE;
      flammable.setFlammable(OVERGROWN_WILLOW_LOG, 5, 5);
      flammable.setFlammable(OVERGROWN_WILLOW_WOOD, 5, 5);
      flammable.setFlammable(STRIPPED_OVERGROWN_WILLOW_LOG, 5, 5);
      flammable.setFlammable(STRIPPED_OVERGROWN_WILLOW_WOOD, 5, 5);
      flammable.setFlammable(OVERGROWN_WILLOW_PLANKS, 5, 20);
      flammable.setFlammable(OVERGROWN_WILLOW_STAIRS, 5, 20);
      flammable.setFlammable(OVERGROWN_WILLOW_SLAB, 5, 20);
      flammable.setFlammable(OVERGROWN_WILLOW_FENCE, 5, 20);
      flammable.setFlammable(OVERGROWN_WILLOW_FENCE_GATE, 5, 20);
      flammable.setFlammable(OVERGROWN_WILLOW_DOOR, 5, 20);
      flammable.setFlammable(OVERGROWN_WILLOW_TRAPDOOR, 5, 20);
      flammable.setFlammable(OVERGROWN_WILLOW_PRESSURE_PLATE, 5, 20);
      flammable.setFlammable(OVERGROWN_WILLOW_BUTTON, 5, 20);
      flammable.setFlammable(OVERGROWN_WILLOW_SIGN, 5, 20);
      flammable.setFlammable(OVERGROWN_WILLOW_WALL_SIGN, 5, 20);
      flammable.setFlammable(OVERGROWN_WILLOW_HANGING_SIGN, 5, 20);
      flammable.setFlammable(OVERGROWN_WILLOW_WALL_HANGING_SIGN, 5, 20);
      flammable.setFlammable(OVERGROWN_WILLOW_FOLIAGE, 30, 60);
      flammable.setFlammable(OVERGROWN_WILLOW_SAPLING, 60, 100);
      ComposterBlock.COMPOSTABLES.put(OVERGROWN_WILLOW_FOLIAGE_ITEM, 0.3F);
      ComposterBlock.COMPOSTABLES.put(OVERGROWN_WILLOW_SAPLING_ITEM, 0.3F);
      ComposterBlock.COMPOSTABLES.put(OVERGROWN_WILLOW_VINES_ITEM, 0.5F);
   }

   public static void bootstrap() {
   }

   public static void addSignBlocks() {
      addValidBlocks(BlockEntityType.SIGN, OVERGROWN_WILLOW_SIGN, OVERGROWN_WILLOW_WALL_SIGN);
      addValidBlocks(BlockEntityType.HANGING_SIGN, OVERGROWN_WILLOW_HANGING_SIGN, OVERGROWN_WILLOW_WALL_HANGING_SIGN);
   }

   private static void addValidBlocks(BlockEntityType<?> type, Block... blocks) {
      BlockEntityTypeBlocksAccessor access = (BlockEntityTypeBlocksAccessor)type;
      HashSet<Block> valid = new HashSet<>(access.theSift$getValidBlocks());
      Collections.addAll(valid, blocks);
      access.theSift$setValidBlocks(valid);
   }
}
