package mielon.thesift.client.entity;

import mielon.thesift.entity.SifterEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public final class SifterModel extends GeoModel<SifterEntity> {
   public ResourceLocation getModelResource(SifterEntity state) {
      return ResourceLocation.fromNamespaceAndPath("the_sift", "geo/sifter.geo.json");
   }

   public ResourceLocation getTextureResource(SifterEntity state) {
      return ResourceLocation.fromNamespaceAndPath("the_sift", "textures/entity/sifter.png");
   }

   public ResourceLocation getAnimationResource(SifterEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("the_sift", "animations/sifter.animation.json");
   }
}
