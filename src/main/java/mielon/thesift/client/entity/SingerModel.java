package mielon.thesift.client.entity;

import mielon.thesift.entity.SingerEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SingerModel extends GeoModel<SingerEntity> {
   public ResourceLocation getModelResource(SingerEntity state) {
      return ResourceLocation.fromNamespaceAndPath("the_sift", "geo/singer.geo.json");
   }

   public ResourceLocation getTextureResource(SingerEntity state) {
      return ResourceLocation.fromNamespaceAndPath("the_sift", "textures/entity/singer.png");
   }

   public ResourceLocation getAnimationResource(SingerEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("the_sift", "animations/singer.animation.json");
   }
}
