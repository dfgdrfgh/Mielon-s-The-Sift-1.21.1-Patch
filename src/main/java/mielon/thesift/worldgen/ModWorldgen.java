package mielon.thesift.worldgen;

import mielon.thesift.platform.ModRegistration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;

public final class ModWorldgen {
   public static final Feature<NoneFeatureConfiguration> LUSH_MONOLITH = register("lush_monolith", new SiftMonolithFeature(true));
   public static final Feature<NoneFeatureConfiguration> BARE_MONOLITH = register("bare_monolith", new SiftMonolithFeature(false));
   public static final Feature<NoneFeatureConfiguration> CLIFF_ARCH = register("cliff_arch", new SiftArchFeature());
   public static final Feature<NoneFeatureConfiguration> DRY_SCULK_SPIKES = register("dry_sculk_spikes", new SiftDrySpikeFeature());
   public static final Feature<NoneFeatureConfiguration> OVERGROWN_WILLOW_TREE = register("overgrown_willow_tree", new OvergrownWillowTreeFeature());
   public static final Feature<NoneFeatureConfiguration> COVERED_GROWTH_CLEANUP = register("covered_growth_cleanup", new CoveredGrowthCleanupFeature());
   public static final Feature<NoneFeatureConfiguration> ABANDONED_MAIN_PORTAL = register("abandoned_main_portal", new AbandonedMainPortalFeature());
   public static final Feature<NoneFeatureConfiguration> SNIFFER_CAVE_OVERGROWN = register(
      "sniffer_cave_overgrown", new SnifferCaveFeature(16, 6002815919507592773L, 4)
   );
   public static final Feature<NoneFeatureConfiguration> SNIFFER_CAVE_WASTES = register(
      "sniffer_cave_wastes", new SnifferCaveFeature(28, 6002815919508111699L, 3)
   );
   public static final Feature<NoneFeatureConfiguration> SNIFFER_PLANT_PATCH = register("sniffer_plant_patch", new SnifferPlantPatchFeature());
   public static final Feature<NoneFeatureConfiguration> SIFT_FLOWER_PATCH = register("sift_flower_patch", new SiftFlowerPatchFeature());
   public static final Feature<NoneFeatureConfiguration> SIFTSLATE_PLANT_PATCH = register("siftslate_plant_patch", new SiftslatePlantPatchFeature());
   public static final Feature<NoneFeatureConfiguration> SOUL_CANYON = register("soul_canyon", new SoulCanyonFeature());
   public static final Feature<NoneFeatureConfiguration> ICHOR_LAKE = register("ichor_lake", new IchorLakeFeature());
   public static final Feature<NoneFeatureConfiguration> LAVA_FLOOR = register("lava_floor", new SiftLavaFloorFeature());
   public static final Feature<NoneFeatureConfiguration> SURFACE_SCULK_REGION = register("surface_sculk_region", new SiftSurfaceSculkRegionFeature());
   public static final Feature<NoneFeatureConfiguration> ICHOR_SNOW = register("ichor_snow", new SiftIchorSnowFeature());
   public static final Feature<SpringConfiguration> ICHOR_CAVE_SPRING = register("ichor_cave_spring", new SiftIchorCaveSpringFeature());

   private ModWorldgen() {
   }

   private static <C extends FeatureConfiguration> Feature<C> register(String name, Feature<C> feature) {
      return ModRegistration.register(BuiltInRegistries.FEATURE, ResourceLocation.fromNamespaceAndPath("the_sift", name), feature);
   }

   public static void initialize() {
   }
}
