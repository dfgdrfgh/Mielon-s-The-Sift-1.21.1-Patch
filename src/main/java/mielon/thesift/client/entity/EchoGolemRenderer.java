package mielon.thesift.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mielon.thesift.client.light.EchoGolemLightBridge;
import mielon.thesift.entity.EchoGolemEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class EchoGolemRenderer extends GeoEntityRenderer<EchoGolemEntity> {
   public EchoGolemRenderer(Context context) {
      super(context, new EchoGolemModel());
      this.shadowRadius = 0.72F;
      this.addRenderLayer(new EchoGolemSoulGlowLayer(this));
   }

   public void render(EchoGolemEntity entity, float yaw, float partial, PoseStack poses, MultiBufferSource buffers, int light) {
      EchoGolemLightBridge.update(entity, entity.getPosition(partial).add(0.0, 2.125, 0.0));
      super.render(entity, yaw, partial, poses, buffers, light);
   }

   public void renderCubesOfBone(PoseStack poses, GeoBone bone, VertexConsumer buffer, int light, int overlay, int color) {
      super.renderCubesOfBone(poses, bone, buffer, "soul_block".equals(bone.getName()) ? 15728880 : light, overlay, color);
   }
}
