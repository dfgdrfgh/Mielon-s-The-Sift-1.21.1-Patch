package mielon.thesift.particle;

import mielon.thesift.platform.ModRegistration;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public final class ModParticles {
   public static final SimpleParticleType WILLOW_LEAF = net.fabricmc.fabric.api.particle.v1.FabricParticleTypes.simple();
   public static final SimpleParticleType SIFT_PARALLAX = net.fabricmc.fabric.api.particle.v1.FabricParticleTypes.simple();
   public static final SimpleParticleType SIFT_NOTE = net.fabricmc.fabric.api.particle.v1.FabricParticleTypes.simple();
   public static final SimpleParticleType SOUND_WAVE = net.fabricmc.fabric.api.particle.v1.FabricParticleTypes.simple();
   public static final SimpleParticleType SINGER_SOUND_WAVE = net.fabricmc.fabric.api.particle.v1.FabricParticleTypes.simple();
   public static final SimpleParticleType CANYON_SOUL = net.fabricmc.fabric.api.particle.v1.FabricParticleTypes.simple();
   public static final SimpleParticleType SOUL_FRAGMENT = net.fabricmc.fabric.api.particle.v1.FabricParticleTypes.simple();
   public static final SimpleParticleType ICHOR_SURFACE_MIST = net.fabricmc.fabric.api.particle.v1.FabricParticleTypes.simple();
   public static final SimpleParticleType ICHOR_BUBBLE = net.fabricmc.fabric.api.particle.v1.FabricParticleTypes.simple();
   public static final SimpleParticleType ICHOR_SPLASH = net.fabricmc.fabric.api.particle.v1.FabricParticleTypes.simple();
   public static final SimpleParticleType ICHOR_RAIN_SPLASH = net.fabricmc.fabric.api.particle.v1.FabricParticleTypes.simple();

   private ModParticles() {
   }

   public static void initialize() {
   }

   private static void register(String name, SimpleParticleType type) {
      ModRegistration.register(BuiltInRegistries.PARTICLE_TYPE, ResourceLocation.fromNamespaceAndPath("the_sift", name), type);
   }

   static {
      register("willow_leaf", WILLOW_LEAF);
      register("sift_parallax", SIFT_PARALLAX);
      register("sift_note", SIFT_NOTE);
      register("sound_wave", SOUND_WAVE);
      register("singer_sound_wave", SINGER_SOUND_WAVE);
      register("canyon_soul", CANYON_SOUL);
      register("soul_fragment", SOUL_FRAGMENT);
      register("ichor_surface_mist", ICHOR_SURFACE_MIST);
      register("ichor_bubble", ICHOR_BUBBLE);
      register("ichor_splash", ICHOR_SPLASH);
      register("ichor_rain_splash", ICHOR_RAIN_SPLASH);
   }
}
