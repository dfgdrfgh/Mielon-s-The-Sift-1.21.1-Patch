package mielon.thesift.platform;

import net.minecraft.nbt.CompoundTag;

public final class NbtCompat {
   public static int getIntOr(CompoundTag tag, String key, int fallback) {
      return tag.contains(key, 99) ? tag.getInt(key) : fallback;
   }

   public static long getLongOr(CompoundTag tag, String key, long fallback) {
      return tag.contains(key, 99) ? tag.getLong(key) : fallback;
   }

   public static float getFloatOr(CompoundTag tag, String key, float fallback) {
      return tag.contains(key, 99) ? tag.getFloat(key) : fallback;
   }

   public static double getDoubleOr(CompoundTag tag, String key, double fallback) {
      return tag.contains(key, 99) ? tag.getDouble(key) : fallback;
   }

   public static boolean getBooleanOr(CompoundTag tag, String key, boolean fallback) {
      return tag.contains(key, 99) ? tag.getBoolean(key) : fallback;
   }

   public static String getStringOr(CompoundTag tag, String key, String fallback) {
      return tag.contains(key, 8) ? tag.getString(key) : fallback;
   }
}
