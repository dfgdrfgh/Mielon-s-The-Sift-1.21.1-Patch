package mielon.thesift.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import mielon.thesift.client.compat.SiftShaderCompat;
import mielon.thesift.client.render.RiftRenderTypes;
import mielon.thesift.entity.MiniRiftEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public final class MiniRiftRenderer extends EntityRenderer<MiniRiftEntity> {
   private static final RiftMesh CUBE = new RiftMesh(List.of(new RiftAssetModel.Box(-0.4F, -0.4F, -0.4F, 0.4F, 0.4F, 0.4F)));

   public MiniRiftRenderer(Context context) {
      super(context);
      this.shadowRadius = 0.0F;
   }

   protected int getBlockLightLevel(MiniRiftEntity entity, BlockPos pos) {
      return 15;
   }

   public ResourceLocation getTextureLocation(MiniRiftEntity entity) {
      return RiftRenderTypes.SIFT_TEXTURE;
   }

   public void render(MiniRiftEntity entity, float yaw, float partial, PoseStack poses, MultiBufferSource buffers, int light) {
      float open = entity.getOpenScale(partial);
      if (!(open <= 0.0F)) {
         poses.pushPose();
         float size = open * open * (3.0F - 2.0F * open);
         poses.scale(size, size, size);
         if (SiftShaderCompat.isShaderPackInUse()) {
            RiftShaderpackRenderer.render(CUBE, entity, true, true, partial, poses, buffers);
            poses.popPose();
         } else {
            CUBE.draw(buffers.getBuffer(RiftRenderTypes.SIFT), poses.last(), true, false);
            CUBE.draw(buffers.getBuffer(RiftRenderTypes.GLOW), poses.last(), true, true);
            poses.popPose();
         }
      }
   }
}
