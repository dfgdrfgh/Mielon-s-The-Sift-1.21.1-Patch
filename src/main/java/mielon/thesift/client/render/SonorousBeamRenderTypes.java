package mielon.thesift.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.resources.ResourceLocation;

public final class SonorousBeamRenderTypes extends RenderType {
   private static final ResourceLocation BEAM_TEXTURE = ResourceLocation.fromNamespaceAndPath("the_sift", "textures/block/sonorous_deepslate_beam.png");
   public static final RenderType BEAM = create(
      "the_sift_sonorous_beam",
      DefaultVertexFormat.BLOCK,
      Mode.QUADS,
      256,
      false,
      true,
      CompositeState.builder()
         .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
         .setTextureState(new TextureStateShard(BEAM_TEXTURE, false, false))
         .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
         .setDepthTestState(LEQUAL_DEPTH_TEST)
         .setWriteMaskState(COLOR_DEPTH_WRITE)
         .setCullState(NO_CULL)
         .createCompositeState(false)
   );
   public static final RenderType WATER_OVERLAY = create(
      "the_sift_sonorous_beam_water_overlay",
      DefaultVertexFormat.BLOCK,
      Mode.QUADS,
      256,
      false,
      true,
      CompositeState.builder()
         .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
         .setTextureState(new TextureStateShard(BEAM_TEXTURE, false, false))
         .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
         .setDepthTestState(NO_DEPTH_TEST)
         .setWriteMaskState(COLOR_WRITE)
         .setCullState(NO_CULL)
         .createCompositeState(false)
   );

   private SonorousBeamRenderTypes() {
      super("the_sift_beam", DefaultVertexFormat.BLOCK, Mode.QUADS, 256, false, false, () -> {}, () -> {});
   }
}
