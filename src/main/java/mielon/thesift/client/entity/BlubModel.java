package mielon.thesift.client.entity;

import mielon.thesift.entity.BlubEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public final class BlubModel extends GeoModel<BlubEntity> {
   private static final ResourceLocation NORMAL_TEXTURE = ResourceLocation.fromNamespaceAndPath("the_sift", "textures/entity/blub.png");
   private static final ResourceLocation TAMED_TEXTURE = ResourceLocation.fromNamespaceAndPath("the_sift", "textures/entity/tamed_blub.png");
   private static final ResourceLocation MIELON_TEXTURE = ResourceLocation.fromNamespaceAndPath("the_sift", "textures/entity/blub_mielon.png");
   private static final ResourceLocation MIELON_TAMED_TEXTURE = ResourceLocation.fromNamespaceAndPath("the_sift", "textures/entity/blub_mielon_tamed.png");

   public ResourceLocation getModelResource(BlubEntity state) {
      return ResourceLocation.fromNamespaceAndPath("the_sift", "geo/blub.geo.json");
   }

   public ResourceLocation getTextureResource(BlubEntity state) {
      if (isMielon(state)) {
         return state.isTame() ? MIELON_TAMED_TEXTURE : MIELON_TEXTURE;
      } else {
         return state != null && state.isTame() ? TAMED_TEXTURE : NORMAL_TEXTURE;
      }
   }

   private static boolean isMielon(BlubEntity blub) {
      if (blub != null && blub.getCustomName() != null) {
         String name = blub.getCustomName().getString();
         return "mielon".equals(name) || "Mielon".equals(name) || "MIELON".equals(name);
      } else {
         return false;
      }
   }

   public ResourceLocation getAnimationResource(BlubEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("the_sift", "animations/blub.animation.json");
   }

   public void setCustomAnimations(BlubEntity blub, long instanceId, AnimationState<BlubEntity> animationState) {
      super.setCustomAnimations(blub, instanceId, animationState);
      if (blub.isBlubSitting()) {
         float degrees = (float) (Math.PI / 180.0);
         this.getBone("head").ifPresent(body -> {
            body.setPosY(body.getPosY() - 3.25F);
            body.setPosZ(body.getPosZ() + 0.65F);
            body.setRotX(body.getRotX() + 17.5F * degrees);
         });
         this.getBone("front_left_leg").ifPresent(leg -> {
            leg.setRotX(leg.getRotX() + 42.0F * degrees);
            leg.setPosZ(leg.getPosZ() - 0.55F);
         });
         this.getBone("front_right_leg").ifPresent(leg -> {
            leg.setRotX(leg.getRotX() + 42.0F * degrees);
            leg.setPosZ(leg.getPosZ() - 0.55F);
         });
         this.getBone("back_left_leg").ifPresent(leg -> {
            leg.setRotX(leg.getRotX() - 68.0F * degrees);
            leg.setPosY(leg.getPosY() + 0.55F);
            leg.setPosZ(leg.getPosZ() + 0.75F);
         });
         this.getBone("back_right_leg").ifPresent(leg -> {
            leg.setRotX(leg.getRotX() - 68.0F * degrees);
            leg.setPosY(leg.getPosY() + 0.55F);
            leg.setPosZ(leg.getPosZ() + 0.75F);
         });
      }

      float healthRatio = Mth.clamp(blub.getHealth() / blub.getMaxHealth(), 0.0F, 1.0F);
      float droop = (1.0F - healthRatio) * 72.0F * (float) (Math.PI / 180.0);
      this.getBone("left_ear").ifPresent(ear -> ear.setRotX(ear.getRotX() + droop));
      this.getBone("right_ear").ifPresent(ear -> ear.setRotX(ear.getRotX() + droop));
      this.getAnimationProcessor().getRegisteredBones().forEach(GeoBone::resetStateChanges);
   }
}
