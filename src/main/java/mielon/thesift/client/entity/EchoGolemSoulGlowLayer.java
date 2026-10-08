package mielon.thesift.client.entity;

import mielon.thesift.entity.EchoGolemEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public final class EchoGolemSoulGlowLayer extends AutoGlowingGeoLayer<EchoGolemEntity> {
   public EchoGolemSoulGlowLayer(GeoRenderer<EchoGolemEntity> renderer) {
      super(renderer);
   }

   protected RenderType getRenderType(EchoGolemEntity entity) {
      return entity.hasSoulBlock()
         ? RenderType.eyes(ResourceLocation.fromNamespaceAndPath("the_sift", "textures/entity/echo_golem/echo_golem_emissive.png"))
         : null;
   }
}
