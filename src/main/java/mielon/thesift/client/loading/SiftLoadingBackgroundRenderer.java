package mielon.thesift.client.loading;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import mielon.thesift.client.render.SiftShaders;
import mielon.thesift.world.TheSiftDimension;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public final class SiftLoadingBackgroundRenderer {
   public static void draw(GuiGraphics graphics, int width, int height) {
      graphics.flush();
      int clock = (int)(System.nanoTime() / 16666667L) & 16777215;
      String target = TheSiftDimension.LEVEL_KEY.equals(SiftLoadingScreenState.sourceDimension()) ? "overworld" : "sift";
      RenderSystem.setShader(() -> SiftShaders.loading(SiftLoadingScreenState.isRift()));
      RenderSystem.setShaderTexture(0, ResourceLocation.fromNamespaceAndPath("the_sift", "textures/entity/rift/" + target + ".png"));
      Matrix4f matrix = graphics.pose().last().pose();
      BufferBuilder builder = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
      int color = 0xFF000000 | clock;
      builder.addVertex(matrix, 0.0F, 0.0F, 0.0F).setUv(0.0F, 0.0F).setColor(color);
      builder.addVertex(matrix, 0.0F, height, 0.0F).setUv(0.0F, 1.0F).setColor(color);
      builder.addVertex(matrix, width, height, 0.0F).setUv(1.0F, 1.0F).setColor(color);
      builder.addVertex(matrix, width, 0.0F, 0.0F).setUv(1.0F, 0.0F).setColor(color);
      BufferUploader.drawWithShader(builder.buildOrThrow());
   }
}
