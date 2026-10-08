package mielon.thesift.client.entity;

import mielon.thesift.entity.SifterEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class SifterRenderer extends GeoEntityRenderer<SifterEntity> {
   public SifterRenderer(Context context) {
      super(context, new SifterModel());
      this.shadowRadius = 0.42F;
   }
}
