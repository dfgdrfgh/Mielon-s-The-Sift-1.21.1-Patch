package mielon.thesift.client.entity;

import mielon.thesift.entity.BlubEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class BlubRenderer extends GeoEntityRenderer<BlubEntity> {
   public BlubRenderer(Context context) {
      super(context, new BlubModel());
      this.shadowRadius = 0.38F;
   }
}
