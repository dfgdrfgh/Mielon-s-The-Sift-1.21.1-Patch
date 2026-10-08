package mielon.thesift.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;

public final class SonorousEffects {
   private static final int BURST_PARTICLE_COUNT = 3;
   private static final double SPREAD_HORIZONTAL = 0.9;
   private static final double HEIGHT_BASE = 1.1;
   private static final double HEIGHT_RANDOM = 0.4;
   private static final double THE_SIFT_NOTE_MARKER = 100.0;

   private SonorousEffects() {
   }

   public static void spawnColorBurst(ServerLevel level, BlockPos notePos, int soundIndex1to8) {
      RandomSource random = level.getRandom();

      for (int shadeIndex = 0; shadeIndex < 3; shadeIndex++) {
         double ox = (random.nextDouble() - 0.5) * 0.9;
         double oy = 1.1 + random.nextDouble() * 0.4;
         double oz = (random.nextDouble() - 0.5) * 0.9;
         double encodedColor = 100.0 + (soundIndex1to8 - 1) * 3 + shadeIndex;
         level.sendParticles(ParticleTypes.NOTE, notePos.getX() + 0.5 + ox, notePos.getY() + oy, notePos.getZ() + 0.5 + oz, 0, encodedColor, 0.0, 0.0, 1.0);
      }
   }
}
