package mielon.thesift.world;

import net.minecraft.resources.ResourceLocation;

public final class SiftDayNightCycle {
   public static final ResourceLocation CLOCK_ID = ResourceLocation.fromNamespaceAndPath("the_sift", "the_sift");
   public static final int DAY_TICKS = 12000;
   public static final int TRANSITION_TICKS = 60;
   public static final int NIGHT_TICKS = 12000;
   public static final int NIGHT_START = 12060;
   public static final int NIGHT_END = 24060;
   public static final int PERIOD_TICKS = 24120;

   private SiftDayNightCycle() {
   }

   public static float nightBlend(long totalTicks) {
      long tick = Math.floorMod(totalTicks, 24120L);
      if (tick < 12000L) {
         return 0.0F;
      } else if (tick < 12060L) {
         return smooth((float)(tick - 12000L) / 60.0F);
      } else {
         return tick < 24060L ? 1.0F : 1.0F - smooth((float)(tick - 24060L) / 60.0F);
      }
   }

   private static float smooth(float value) {
      float clamped = Math.max(0.0F, Math.min(1.0F, value));
      float t = clamped;

      for (int i = 0; i < 4; i++) {
         float x = t * (1.2F + t * (-0.6F + 0.4F * t));
         float derivative = 1.2F + t * (-1.2F + 1.2F * t);
         t -= (x - clamped) / derivative;
      }

      return t * t * (3.0F - 2.0F * t);
   }
}
