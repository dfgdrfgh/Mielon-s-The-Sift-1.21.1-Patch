package mielon.thesift.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import mielon.thesift.client.compat.SiftShaderCompat;
import mielon.thesift.client.render.RiftRenderTypes;
import mielon.thesift.entity.RiftEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public final class RiftRenderer extends EntityRenderer<RiftEntity> {
   private static final RiftMesh OPEN = new RiftMesh(RiftAssetModel.INSTANCE.boxes(1.0F));

   public RiftRenderer(Context context) {
      super(context);
      this.shadowRadius = 0.0F;
   }

   protected int getBlockLightLevel(RiftEntity entity, BlockPos pos) {
      return 15;
   }

   public ResourceLocation getTextureLocation(RiftEntity entity) {
      return RiftRenderTypes.SIFT_TEXTURE;
   }

   public boolean shouldRender(RiftEntity entity, Frustum frustum, double x, double y, double z) {
      return frustum.isVisible(entity.getPortalBounds().inflate(4.0));
   }

   public void render(RiftEntity entity, float yaw, float partial, PoseStack poses, MultiBufferSource buffers, int light) {
      float open = entity.getOpenScale(partial);
      if (!(open <= 0.0F)) {
         RiftMesh mesh = open >= 1.0F ? OPEN : new RiftMesh(RiftAssetModel.INSTANCE.boxes(open));
         if (SiftShaderCompat.isShaderPackInUse()) {
            RiftShaderpackRenderer.render(mesh, entity, entity.isLongAlongX(), entity.targetsSift(), partial, poses, buffers);
         } else {
            mesh.draw(buffers.getBuffer(entity.targetsSift() ? RiftRenderTypes.SIFT : RiftRenderTypes.OVERWORLD), poses.last(), entity.isLongAlongX(), false);
            mesh.draw(buffers.getBuffer(RiftRenderTypes.GLOW), poses.last(), entity.isLongAlongX(), true);
         }
      }
   }
}
