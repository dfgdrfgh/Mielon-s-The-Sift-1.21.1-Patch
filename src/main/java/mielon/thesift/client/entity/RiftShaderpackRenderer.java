package mielon.thesift.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

final class RiftShaderpackRenderer {
   private static final ResourceLocation ENERGY = id("textures/misc/rift_energy_shaderpack.png");
   private static final ResourceLocation SIFT = id("textures/entity/rift/sift.png");
   private static final ResourceLocation OVERWORLD = id("textures/entity/rift/overworld.png");

   static void render(RiftMesh mesh, Entity entity, boolean alongX, boolean targetSift, float partial, PoseStack poses, MultiBufferSource buffers) {
      float seconds = ((float)entity.level().getGameTime() + partial) / 20.0F;
      double distance = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().distanceTo(entity.getPosition(partial));
      float t = Math.clamp((float)(distance - 3.0) / 12.0F, 0.0F, 1.0F);
      float destinationAlpha = (1.0F - t * t * (3.0F - 2.0F * t)) * 0.78F;
      VertexConsumer energy = buffers.getBuffer(RenderType.beaconBeam(ENERGY, false));
      mesh.drawShaderpack(energy, poses.last(), alongX, RiftMesh.ShaderpackPass.ENERGY, 1.0F, seconds);
      mesh.drawShaderpack(energy, poses.last(), alongX, RiftMesh.ShaderpackPass.FRAME, 1.0F, seconds);
      if (destinationAlpha > 0.001F) {
         mesh.drawShaderpack(
            buffers.getBuffer(RenderType.beaconBeam(targetSift ? SIFT : OVERWORLD, true)),
            poses.last(),
            alongX,
            RiftMesh.ShaderpackPass.DESTINATION,
            destinationAlpha,
            seconds
         );
      }

      mesh.drawShaderpack(buffers.getBuffer(RenderType.beaconBeam(ENERGY, true)), poses.last(), alongX, RiftMesh.ShaderpackPass.GLOW, 0.4F, seconds);
   }

   private static ResourceLocation id(String path) {
      return ResourceLocation.fromNamespaceAndPath("the_sift", path);
   }
}
