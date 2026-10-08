package mielon.thesift.client.light;

import mielon.thesift.entity.EchoGolemEntity;
import net.minecraft.world.phys.Vec3;

public final class EchoGolemLightBridge {
   private static final EchoGolemLightBridge.Handler NO_OP = (golem, position) -> {};
   private static volatile EchoGolemLightBridge.Handler handler = NO_OP;

   private EchoGolemLightBridge() {
   }

   public static void install(EchoGolemLightBridge.Handler newHandler) {
      handler = newHandler == null ? NO_OP : newHandler;
   }

   public static void update(EchoGolemEntity golem, Vec3 position) {
      handler.update(golem, position);
   }

   @FunctionalInterface
   public interface Handler {
      void update(EchoGolemEntity var1, Vec3 var2);
   }
}
