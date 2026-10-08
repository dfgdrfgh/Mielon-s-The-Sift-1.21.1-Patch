package mielon.thesift.client.entity;

import mielon.thesift.entity.SingerEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public final class SingerGlowLayer extends AutoGlowingGeoLayer<SingerEntity> {
   private static final ResourceLocation GLOW_TEXTURE = ResourceLocation.fromNamespaceAndPath("the_sift", "textures/entity/singer_glowmask.png");

   public SingerGlowLayer(GeoRenderer<SingerEntity> renderer) {
      super(renderer);
   }

   protected RenderType getRenderType(SingerEntity entity) {
      return RenderType.entityCutoutNoCull(GLOW_TEXTURE);
   }
}
