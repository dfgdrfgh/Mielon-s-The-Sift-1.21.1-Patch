package mielon.thesift.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.CherryParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public final class WillowLeafParticle extends CherryParticle {
   private WillowLeafParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
      super(level, x, y, z, sprites);
      this.setColor(0.23921569F, 1.0F, 0.8980392F);
   }

   public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
      public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
         return new WillowLeafParticle(level, x, y, z, this.sprites);
      }
   }
}
