package mielon.thesift.client.light;

import mielon.thesift.entity.SingerEntity;
import net.minecraft.world.phys.Vec3;

public final class SingerHeadLightBridge {
   private static final SingerHeadLightBridge.Handler NO_OP = (singer, position) -> {};
   private static volatile SingerHeadLightBridge.Handler handler = NO_OP;

   private SingerHeadLightBridge() {
   }

   public static void install(SingerHeadLightBridge.Handler newHandler) {
      handler = newHandler != null ? newHandler : NO_OP;
   }

   public static void update(SingerEntity singer, Vec3 position) {
      handler.update(singer, position);
   }

   @FunctionalInterface
   public interface Handler {
      void update(SingerEntity var1, Vec3 var2);
   }
}
