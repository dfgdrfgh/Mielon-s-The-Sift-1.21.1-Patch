package mielon.thesift.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import java.util.EnumSet;
import java.util.Set;
import mielon.thesift.block.entity.SiftPortalBlockEntity;
import mielon.thesift.client.compat.SiftShaderCompat;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class SiftPortalRenderer extends TheEndPortalRenderer<SiftPortalBlockEntity> {
   private static final int FULL_BRIGHT = 15728880;
   private static final ResourceLocation SHADERPACK_TEXTURE = ResourceLocation.fromNamespaceAndPath("the_sift", "textures/misc/sift_portal_shaderpack.png");

   public SiftPortalRenderer(Context context) {
      super(context);
   }

   protected RenderType renderType() {
      return SiftPortalRenderTypes.SIFT_PORTAL;
   }

   protected float getOffsetDown() {
      return 0.0F;
   }

   protected float getOffsetUp() {
      return 1.0F;
   }

   public int getViewDistance() {
      return 512;
   }

   public void render(SiftPortalBlockEntity entity, float partial, PoseStack poses, MultiBufferSource buffers, int light, int overlay) {
      Set<Direction> faces = EnumSet.noneOf(Direction.class);

      for (Direction direction : Direction.values()) {
         if (entity.shouldRenderFace(direction)) {
            faces.add(direction);
         }
      }

      if (!faces.isEmpty()) {
         if (!SiftShaderCompat.isShaderPackInUse()) {
            drawVanillaPortal(faces, poses.last(), buffers.getBuffer(SiftPortalRenderTypes.SIFT_PORTAL));
         } else {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.level != null) {
               Camera camera = minecraft.gameRenderer.getMainCamera();
               Vec3 cameraPos = camera.getPosition();
               Quaternionf inverseCamera = new Quaternionf(camera.rotation()).conjugate();
               Matrix4f projectionView = new Matrix4f(RenderSystem.getProjectionMatrix()).mul(new Matrix4f().rotation(inverseCamera));
               float aspect = (float)minecraft.getWindow().getWidth() / Math.max(1, minecraft.getWindow().getHeight());
               float seconds = ((float)Math.floorMod(minecraft.level.getGameTime(), 24000L) + partial) / 20.0F;
               drawShaderpackPortal(
                  faces,
                  entity.getBlockPos(),
                  cameraPos,
                  inverseCamera,
                  projectionView,
                  aspect,
                  seconds,
                  poses.last(),
                  buffers.getBuffer(RenderType.beaconBeam(SHADERPACK_TEXTURE, false))
               );
            }
         }
      }
   }

   private static void drawVanillaPortal(Set<Direction> faces, Pose pose, VertexConsumer buffer) {
      for (Direction direction : faces) {
         switch (direction) {
            case DOWN:
               plainFace(pose, buffer, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F);
               break;
            case UP:
               plainFace(pose, buffer, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F);
               break;
            case NORTH:
               plainFace(pose, buffer, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F);
               break;
            case SOUTH:
               plainFace(pose, buffer, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F);
               break;
            case WEST:
               plainFace(pose, buffer, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 0.0F);
               break;
            case EAST:
               plainFace(pose, buffer, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F);
         }
      }
   }

   private static void plainFace(
      Pose pose, VertexConsumer buffer, float x0, float y0, float z0, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3
   ) {
      buffer.addVertex(pose.pose(), x0, y0, z0);
      buffer.addVertex(pose.pose(), x1, y1, z1);
      buffer.addVertex(pose.pose(), x2, y2, z2);
      buffer.addVertex(pose.pose(), x3, y3, z3);
   }

   private static void drawShaderpackPortal(
      Set<Direction> faces,
      BlockPos blockPos,
      Vec3 cameraPos,
      Quaternionf inverseCamera,
      Matrix4f projectionView,
      float aspect,
      float seconds,
      Pose pose,
      VertexConsumer buffer
   ) {
      for (Direction direction : faces) {
         switch (direction) {
            case DOWN:
               face(
                  blockPos,
                  cameraPos,
                  inverseCamera,
                  projectionView,
                  aspect,
                  seconds,
                  pose,
                  buffer,
                  direction,
                  0.0F,
                  0.0F,
                  1.0F,
                  0.0F,
                  0.0F,
                  0.0F,
                  1.0F,
                  0.0F,
                  0.0F,
                  1.0F,
                  0.0F,
                  1.0F
               );
               break;
            case UP:
               face(
                  blockPos,
                  cameraPos,
                  inverseCamera,
                  projectionView,
                  aspect,
                  seconds,
                  pose,
                  buffer,
                  direction,
                  0.0F,
                  1.0F,
                  0.0F,
                  0.0F,
                  1.0F,
                  1.0F,
                  1.0F,
                  1.0F,
                  1.0F,
                  1.0F,
                  1.0F,
                  0.0F
               );
               break;
            case NORTH:
               face(
                  blockPos,
                  cameraPos,
                  inverseCamera,
                  projectionView,
                  aspect,
                  seconds,
                  pose,
                  buffer,
                  direction,
                  1.0F,
                  0.0F,
                  0.0F,
                  0.0F,
                  0.0F,
                  0.0F,
                  0.0F,
                  1.0F,
                  0.0F,
                  1.0F,
                  1.0F,
                  0.0F
               );
               break;
            case SOUTH:
               face(
                  blockPos,
                  cameraPos,
                  inverseCamera,
                  projectionView,
                  aspect,
                  seconds,
                  pose,
                  buffer,
                  direction,
                  0.0F,
                  0.0F,
                  1.0F,
                  1.0F,
                  0.0F,
                  1.0F,
                  1.0F,
                  1.0F,
                  1.0F,
                  0.0F,
                  1.0F,
                  1.0F
               );
               break;
            case WEST:
               face(
                  blockPos,
                  cameraPos,
                  inverseCamera,
                  projectionView,
                  aspect,
                  seconds,
                  pose,
                  buffer,
                  direction,
                  0.0F,
                  0.0F,
                  0.0F,
                  0.0F,
                  0.0F,
                  1.0F,
                  0.0F,
                  1.0F,
                  1.0F,
                  0.0F,
                  1.0F,
                  0.0F
               );
               break;
            case EAST:
               face(
                  blockPos,
                  cameraPos,
                  inverseCamera,
                  projectionView,
                  aspect,
                  seconds,
                  pose,
                  buffer,
                  direction,
                  1.0F,
                  0.0F,
                  1.0F,
                  1.0F,
                  0.0F,
                  0.0F,
                  1.0F,
                  1.0F,
                  0.0F,
                  1.0F,
                  1.0F,
                  1.0F
               );
         }
      }
   }

   private static void face(
      BlockPos blockPos,
      Vec3 cameraPos,
      Quaternionf inverseCamera,
      Matrix4f projectionView,
      float aspect,
      float seconds,
      Pose pose,
      VertexConsumer buffer,
      Direction normal,
      float x0,
      float y0,
      float z0,
      float x1,
      float y1,
      float z1,
      float x2,
      float y2,
      float z2,
      float x3,
      float y3,
      float z3
   ) {
      vertex(blockPos, cameraPos, inverseCamera, projectionView, aspect, seconds, pose, buffer, normal, x0, y0, z0);
      vertex(blockPos, cameraPos, inverseCamera, projectionView, aspect, seconds, pose, buffer, normal, x1, y1, z1);
      vertex(blockPos, cameraPos, inverseCamera, projectionView, aspect, seconds, pose, buffer, normal, x2, y2, z2);
      vertex(blockPos, cameraPos, inverseCamera, projectionView, aspect, seconds, pose, buffer, normal, x3, y3, z3);
   }

   private static void vertex(
      BlockPos blockPos,
      Vec3 cameraPos,
      Quaternionf inverseCamera,
      Matrix4f projectionView,
      float aspect,
      float seconds,
      Pose pose,
      VertexConsumer buffer,
      Direction normal,
      float x,
      float y,
      float z
   ) {
      double worldX = blockPos.getX() + x;
      double worldY = blockPos.getY() + y;
      double worldZ = blockPos.getZ() + z;
      Vector3f cameraOffset = new Vector3f((float)(worldX - cameraPos.x), (float)(worldY - cameraPos.y), (float)(worldZ - cameraPos.z));
      Vector3f screen = projectionView.transformProject(new Vector3f(cameraOffset));
      float u = screen.x * 0.5F * aspect * 0.65F + 0.5F;
      float v = screen.y * 0.5F + 0.5F;
      Vector3f view = new Vector3f(cameraOffset).rotate(inverseCamera);
      float denominator = Math.max(Math.abs(view.z), 1.0F);
      u += view.x / denominator * 0.055F + seconds * 0.006F;
      v += view.y / denominator * 0.055F - seconds * 0.004F;
      buffer.addVertex(pose.pose(), x, y, z)
         .setColor(255, 255, 255, 255)
         .setUv(u, v)
         .setLight(15728880)
         .setNormal(normal.getStepX(), normal.getStepY(), normal.getStepZ());
   }
}
