package mielon.thesift.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;

public final class SiftPortalRenderTypes extends RenderType {
   public static final RenderType SIFT_PORTAL = create(
      "the_sift_portal",
      DefaultVertexFormat.POSITION,
      Mode.QUADS,
      4096,
      false,
      false,
      CompositeState.builder()
         .setShaderState(new ShaderStateShard(SiftShaders::portal))
         .setDepthTestState(LEQUAL_DEPTH_TEST)
         .setCullState(CULL)
         .setTransparencyState(NO_TRANSPARENCY)
         .setWriteMaskState(COLOR_DEPTH_WRITE)
         .createCompositeState(false)
   );

   private SiftPortalRenderTypes(String name, VertexFormat format, Mode mode, int size, boolean crumbling, boolean sort, Runnable setup, Runnable clear) {
      super(name, format, mode, size, crumbling, sort, setup, clear);
   }
}
