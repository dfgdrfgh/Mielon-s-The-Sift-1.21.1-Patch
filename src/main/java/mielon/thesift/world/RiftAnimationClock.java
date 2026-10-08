package mielon.thesift.world;

public final class RiftAnimationClock {
   public static final double DURATION_TICKS = 15.834;

   public static float progress(boolean closing, double elapsedTicks) {
      double t = Math.max(0.0, Math.min(1.0, elapsedTicks / 15.834));
      return (float)(closing ? 1.0 - t : t);
   }

   private RiftAnimationClock() {
   }
}
