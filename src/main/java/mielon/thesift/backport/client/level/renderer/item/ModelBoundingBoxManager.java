package mielon.thesift.backport.client.level.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class ModelBoundingBoxManager {
   public static final ModelBoundingBoxManager INSTANCE = new ModelBoundingBoxManager();
   private static final Direction[] CULL_FACES = new Direction[]{
      Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, null
   };
   private final Map<Item, AABB> cache = new HashMap<>();

   public AABB getModelBoundingBox(BakedModel model, ItemStack stack, ItemRenderer renderer, Level level, int seed) {
      return this.cache.computeIfAbsent(stack.getItem(), i -> compute(model, stack, renderer, level, seed));
   }

   private static AABB compute(BakedModel model, ItemStack stack, ItemRenderer renderer, Level level, int seed) {
      if (model.isCustomRenderer()) {
         return computeForCustomRenderer(stack, renderer, level, seed);
      } else {
         float minX = Float.POSITIVE_INFINITY;
         float minY = Float.POSITIVE_INFINITY;
         float minZ = Float.POSITIVE_INFINITY;
         float maxX = Float.NEGATIVE_INFINITY;
         float maxY = Float.NEGATIVE_INFINITY;
         float maxZ = Float.NEGATIVE_INFINITY;
         ItemTransform transform = model.getTransforms().getTransform(ItemDisplayContext.FIXED);
         PoseStack poseStack = new PoseStack();
         transform.apply(false, poseStack);
         poseStack.translate(-0.5F, -0.5F, -0.5F);
         Matrix4f pose = poseStack.last().pose();
         Vector4f pos = new Vector4f();

         for (Direction direction : CULL_FACES) {
            for (BakedQuad quad : model.getQuads(null, direction, level.getRandom())) {
               Direction quadDir = quad.getDirection();
               if (quadDir != Direction.SOUTH && quadDir != Direction.NORTH) {
                  int[] vertices = quad.getVertices();

                  for (int i = 0; i < 4; i++) {
                     int offset = i * 8;
                     float x = Float.intBitsToFloat(vertices[offset]);
                     float y = Float.intBitsToFloat(vertices[offset + 1]);
                     float z = Float.intBitsToFloat(vertices[offset + 2]);
                     pose.transform(x, y, z, 1.0F, pos);
                     minX = Math.min(minX, pos.x);
                     minY = Math.min(minY, pos.y);
                     minZ = Math.min(minZ, pos.z);
                     maxX = Math.max(maxX, pos.x);
                     maxY = Math.max(maxY, pos.y);
                     maxZ = Math.max(maxZ, pos.z);
                  }
               }
            }
         }

         return minX == Float.POSITIVE_INFINITY ? fallback() : new AABB(minX, minY, minZ, maxX, maxY, maxZ);
      }
   }

   private static AABB computeForCustomRenderer(ItemStack stack, ItemRenderer renderer, Level level, int seed) {
      PoseStack pose = new PoseStack();
      ModelBoundingBoxManager.CustomRendererBoundManager manager = new ModelBoundingBoxManager.CustomRendererBoundManager();
      renderer.renderStatic(stack, ItemDisplayContext.FIXED, 15728880, OverlayTexture.NO_OVERLAY, pose, renderType -> new BoundDelegate(manager), level, seed);
      if (!manager.hasData()) {
         return fallback();
      } else {
         AABB box = manager.toAABB();
         BakedModel model = renderer.getModel(stack, level, null, seed);
         ItemTransform transform = model.getTransforms().getTransform(ItemDisplayContext.FIXED);
         Vector3f scale = transform.scale;
         float scaleX = Math.abs(scale.x()) > 1.0E-4F ? scale.x() : 1.0F;
         float scaleY = Math.abs(scale.y()) > 1.0E-4F ? scale.y() : 1.0F;
         float scaleZ = Math.abs(scale.z()) > 1.0E-4F ? scale.z() : 1.0F;
         return new AABB(box.minX / scaleX, box.minY / scaleY, box.minZ / scaleZ, box.maxX / scaleX, box.maxY / scaleY, box.maxZ / scaleZ);
      }
   }

   private static AABB fallback() {
      return new AABB(-0.25, -0.25, -0.25, 0.25, 0.25, 0.25);
   }

   // Glint and base passes need distinct consumer identities, but share one bounds accumulator.
   private static final class BoundDelegate implements VertexConsumer {
      private final CustomRendererBoundManager bounds;
      BoundDelegate(CustomRendererBoundManager bounds) { this.bounds = bounds; }
      public VertexConsumer addVertex(float x,float y,float z) { bounds.addVertex(x,y,z); return this; }
      public VertexConsumer setColor(int r,int g,int b,int a) { return this; }
      public VertexConsumer setUv(float u,float v) { return this; }
      public VertexConsumer setUv1(int u,int v) { return this; }
      public VertexConsumer setUv2(int u,int v) { return this; }
      public VertexConsumer setNormal(float x,float y,float z) { return this; }
   }

   private static final class CustomRendererBoundManager implements VertexConsumer {
      private double minX = Double.POSITIVE_INFINITY;
      private double minY = Double.POSITIVE_INFINITY;
      private double minZ = Double.POSITIVE_INFINITY;
      private double maxX = Double.NEGATIVE_INFINITY;
      private double maxY = Double.NEGATIVE_INFINITY;
      private double maxZ = Double.NEGATIVE_INFINITY;
      private boolean hasData = false;

      public VertexConsumer addVertex(float x, float y, float z) {
         this.hasData = true;
         this.minX = Math.min(this.minX, (double)x);
         this.minY = Math.min(this.minY, (double)y);
         this.minZ = Math.min(this.minZ, (double)z);
         this.maxX = Math.max(this.maxX, (double)x);
         this.maxY = Math.max(this.maxY, (double)y);
         this.maxZ = Math.max(this.maxZ, (double)z);
         return this;
      }

      public VertexConsumer setColor(int r, int g, int b, int a) {
         return this;
      }

      public VertexConsumer setUv(float u, float v) {
         return this;
      }

      public VertexConsumer setUv1(int u, int v) {
         return this;
      }

      public VertexConsumer setUv2(int u, int v) {
         return this;
      }

      public VertexConsumer setNormal(float x, float y, float z) {
         return this;
      }

      boolean hasData() {
         return this.hasData;
      }

      AABB toAABB() {
         return new AABB(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
      }
   }
}
