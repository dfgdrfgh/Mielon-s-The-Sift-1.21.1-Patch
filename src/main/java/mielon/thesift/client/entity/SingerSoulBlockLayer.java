package mielon.thesift.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import mielon.thesift.block.ModBlocks;
import mielon.thesift.entity.SingerEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

public final class SingerSoulBlockLayer extends BlockAndItemGeoLayer<SingerEntity> {
   public SingerSoulBlockLayer(SingerRenderer renderer) {
      super(renderer);
   }

   protected BlockState getBlockForBone(GeoBone bone, SingerEntity singer) {
      return bone.getName().equals("held_soul") && singer.isHoldingSoulBlock() ? ModBlocks.SOUL_BLOCK.defaultBlockState() : null;
   }

   protected void renderBlockForBone(
      PoseStack poses, GeoBone bone, BlockState block, SingerEntity singer, MultiBufferSource buffers, float partialTick, int light, int overlay
   ) {
      poses.pushPose();
      poses.scale(1.75F, 1.75F, 1.75F);
      super.renderBlockForBone(poses, bone, block, singer, buffers, partialTick, 15728880, overlay);
      poses.popPose();
   }
}
