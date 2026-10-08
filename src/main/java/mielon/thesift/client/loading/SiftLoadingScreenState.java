package mielon.thesift.client.loading;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public final class SiftLoadingScreenState {
   private static boolean active;
   private static boolean rift;
   private static long startedAtNanos;
   private static ResourceKey<Level> sourceDimension;

   private SiftLoadingScreenState() {
   }

   public static boolean isActive() {
      return active;
   }

   public static boolean isRift() {
      return rift;
   }

   public static void begin(ResourceKey<Level> source, boolean fromRift) {
      if (!active) {
         active = true;
         sourceDimension = source;
         startedAtNanos = System.nanoTime();
      }

      rift |= fromRift;
   }

   public static void markVanillaTransition(ResourceKey<Level> oldDimension) {
      begin(oldDimension, false);
      sourceDimension = oldDimension;
   }

   public static ResourceKey<Level> sourceDimension() {
      return sourceDimension;
   }

   public static boolean isOlderThanSeconds(double seconds) {
      return active && System.nanoTime() - startedAtNanos > seconds * 1.0E9;
   }

   public static void clear() {
      active = false;
      rift = false;
      startedAtNanos = 0L;
      sourceDimension = null;
   }
}
