package mielon.thesift.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.resources.ResourceLocation;

public final class RiftRenderTypes extends RenderType {
   public static final ResourceLocation SIFT_TEXTURE = ResourceLocation.fromNamespaceAndPath("the_sift", "textures/entity/rift/sift.png");
   public static final RenderType SIFT = createType(SIFT_TEXTURE, false);
   public static final RenderType OVERWORLD = createType(ResourceLocation.fromNamespaceAndPath("the_sift", "textures/entity/rift/overworld.png"), false);
   public static final RenderType GLOW = createType(SIFT_TEXTURE, true);

   private RiftRenderTypes(String name, VertexFormat format, Mode mode, int size, boolean crumbling, boolean sort, Runnable setup, Runnable clear) {
      super(name, format, mode, size, crumbling, sort, setup, clear);
   }

   private static RenderType createType(ResourceLocation texture, boolean glow) {
      return create(
         "the_sift_rift_" + texture.getPath() + glow,
         DefaultVertexFormat.POSITION_TEX_COLOR,
         Mode.QUADS,
         4096,
         false,
         glow,
         CompositeState.builder()
            .setShaderState(new ShaderStateShard(SiftShaders::rift))
            .setTextureState(new TextureStateShard(texture, false, false))
            .setCullState(CULL)
            .setTransparencyState(glow ? TRANSLUCENT_TRANSPARENCY : NO_TRANSPARENCY)
            .setWriteMaskState(glow ? COLOR_WRITE : COLOR_DEPTH_WRITE)
            .createCompositeState(false)
      );
   }
}
