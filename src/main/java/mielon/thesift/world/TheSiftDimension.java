package mielon.thesift.world;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public final class TheSiftDimension {
   public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("the_sift", "the_sift");
   public static final ResourceKey<Level> LEVEL_KEY = ResourceKey.create(Registries.DIMENSION, ID);
   public static final ResourceKey<Biome> SIFT_WASTES = ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("the_sift", "sift_wastes"));
   public static final ResourceKey<Biome> OVERGROWN_CLEARING = ResourceKey.create(
      Registries.BIOME, ResourceLocation.fromNamespaceAndPath("the_sift", "overgrown_clearing")
   );
   public static final ResourceKey<Biome> OVERGROWN_FOREST = ResourceKey.create(
      Registries.BIOME, ResourceLocation.fromNamespaceAndPath("the_sift", "overgrown_forest")
   );
   public static final ResourceKey<Biome> OVERGROWN_FOREST_SLOPES = ResourceKey.create(
      Registries.BIOME, ResourceLocation.fromNamespaceAndPath("the_sift", "overgrown_forest_slopes")
   );
   public static final ResourceKey<Biome> OVERGROWN_SLOPES = ResourceKey.create(
      Registries.BIOME, ResourceLocation.fromNamespaceAndPath("the_sift", "overgrown_slopes")
   );
   public static final ResourceKey<Biome> OVERGROWN_PEAKS = ResourceKey.create(
      Registries.BIOME, ResourceLocation.fromNamespaceAndPath("the_sift", "overgrown_peaks")
   );
   public static final ResourceKey<Biome> ICHOR_SNOWY_PEAKS = ResourceKey.create(
      Registries.BIOME, ResourceLocation.fromNamespaceAndPath("the_sift", "ichor_snowy_peaks")
   );

   public static boolean isOvergrownBiome(Holder<Biome> biome) {
      return biome.is(OVERGROWN_CLEARING)
         || biome.is(OVERGROWN_FOREST)
         || biome.is(OVERGROWN_FOREST_SLOPES)
         || biome.is(OVERGROWN_SLOPES)
         || biome.is(OVERGROWN_PEAKS);
   }

   private TheSiftDimension() {
   }
}
