package mielon.thesift.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public abstract class SiftQuadParticle extends TextureSheetParticle {
   protected SiftQuadParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, TextureAtlasSprite sprite) {
      super(level, x, y, z, xd, yd, zd);
      this.setSprite(sprite);
   }

   protected SiftQuadParticle(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite) {
      super(level, x, y, z);
      this.setSprite(sprite);
   }

   protected abstract SiftQuadParticle.Layer getLayer();

   public ParticleRenderType getRenderType() {
      return this.getLayer() == SiftQuadParticle.Layer.OPAQUE ? ParticleRenderType.PARTICLE_SHEET_OPAQUE : ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   protected static enum Layer {
      TRANSLUCENT,
      OPAQUE;

      public static SiftQuadParticle.Layer bySprite(TextureAtlasSprite sprite) {
         return TRANSLUCENT;
      }
   }
}
