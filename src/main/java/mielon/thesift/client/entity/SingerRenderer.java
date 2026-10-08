package mielon.thesift.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mielon.thesift.client.light.SingerHeadLightBridge;
import mielon.thesift.client.light.TheSiftDynamicLightsInitializer;
import mielon.thesift.entity.SingerEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class SingerRenderer extends GeoEntityRenderer<SingerEntity> {
   public SingerRenderer(Context context) {
      super(context, new SingerModel());
      this.addRenderLayer(new SingerSoulBlockLayer(this));
      this.shadowRadius = 0.0F;
      this.addRenderLayer(new SingerGlowLayer(this));
   }

   public void renderRecursively(
      PoseStack poses,
      SingerEntity entity,
      GeoBone bone,
      RenderType renderType,
      MultiBufferSource buffers,
      VertexConsumer buffer,
      boolean reRender,
      float partialTick,
      int packedLight,
      int overlay,
      int color
   ) {
      boolean head = bone.getName().equals("head");
      if (head) {
         bone.setTrackingMatrices(true);
      }

      int singerLight = TheSiftDynamicLightsInitializer.light(entity.blockPosition().above(), packedLight);
      super.renderRecursively(poses, entity, bone, renderType, buffers, buffer, reRender, partialTick, singerLight, overlay, color);
      if (head && !reRender) {
         Vector4f point = bone.getWorldSpaceMatrix().transform(new Vector4f(0.0F, 0.8125F, -0.0625F, 1.0F));
         Vec3 position = new Vec3(point.x, point.y, point.z).add(entity.getPosition(partialTick).subtract(entity.position()));
         SingerHeadLightBridge.update(entity, position);
      }
   }
}
