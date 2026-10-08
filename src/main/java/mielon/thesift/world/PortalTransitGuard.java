package mielon.thesift.world;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import mielon.thesift.platform.EntityCompat;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class PortalTransitGuard {
   private static final Map<UUID, PortalTransitGuard.Arrival> blocked = new HashMap<>();

   public static void block(Entity e) {
      e.getSelfAndPassengers().forEach(p -> blocked.put(p.getUUID(), new PortalTransitGuard.Arrival(p.level().dimension(), p.position())));
   }

   public static boolean isBlocked(Entity e) {
      PortalTransitGuard.Arrival arrival = blocked.get(e.getUUID());
      if (arrival == null) {
         return false;
      } else if (arrival.dimension.equals(e.level().dimension()) && arrival.pos.distanceToSqr(e.position()) < 0.0625) {
         return true;
      } else {
         if (!SiftTeleportManager.isTouchingPortal(e) && !RiftManager.isTouchingRift(e)) {
            blocked.remove(e.getUUID());
         }

         return true;
      }
   }

   public static void clear() {
      blocked.clear();
   }

   public static void prune(MinecraftServer server) {
      blocked.keySet().removeIf(id -> EntityCompat.find(server.overworld().getServer(), id) == null);
   }

   private record Arrival(ResourceKey<Level> dimension, Vec3 pos) {
   }
}
