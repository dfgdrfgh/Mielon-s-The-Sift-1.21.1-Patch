package mielon.thesift.client.entity;

import mielon.thesift.entity.EchoGolemEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public final class EchoGolemModel extends GeoModel<EchoGolemEntity> {
   public ResourceLocation getModelResource(EchoGolemEntity state) {
      return ResourceLocation.fromNamespaceAndPath("the_sift", "geo/echo_golem.geo.json");
   }

   public ResourceLocation getTextureResource(EchoGolemEntity state) {
      return ResourceLocation.fromNamespaceAndPath("the_sift", "textures/entity/echo_golem.png");
   }

   public ResourceLocation getAnimationResource(EchoGolemEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("the_sift", "animations/echo_golem.animation.json");
   }

   public void setCustomAnimations(EchoGolemEntity golem, long instanceId, AnimationState<EchoGolemEntity> state) {
      super.setCustomAnimations(golem, instanceId, state);
      float partial = state.getPartialTick();
      float headYaw = Mth.rotLerp(partial, golem.yHeadRotO, golem.getYHeadRot());
      float bodyYaw = Mth.rotLerp(partial, golem.yBodyRotO, golem.yBodyRot);
      float yaw = Mth.clamp(Mth.wrapDegrees(headYaw - bodyYaw), -55.0F, 55.0F);
      this.getBone("head").ifPresent(head -> {
         head.setRotX(0.0F);
         head.setRotY(head.getRotY() + yaw * (float) (Math.PI / 180.0));
      });
      this.getBone("soul").ifPresent(bone -> bone.setHidden(!golem.hasSoulBlock()));
      this.getAnimationProcessor().getRegisteredBones().forEach(GeoBone::resetStateChanges);
   }
}
