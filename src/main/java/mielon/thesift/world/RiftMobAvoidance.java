package mielon.thesift.world;

import java.util.ArrayList;
import java.util.List;
import mielon.thesift.entity.RiftEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;

public final class RiftMobAvoidance {
   private RiftMobAvoidance() {
   }

   public static List<AABB> dangerZones(Mob mob, float searchRange) {
      List<AABB> zones = new ArrayList<>();

      for (RiftEntity rift : RiftDirectory.loaded()) {
         if (!rift.isRemoved() && !rift.isClosing() && rift.level() == mob.level() && rift.getBoundingBox().inflate(searchRange).contains(mob.position())) {
            zones.add(dangerZone(rift, mob));
         }
      }

      return zones;
   }

   private static AABB dangerZone(RiftEntity rift, Mob mob) {
      AABB portal = rift.getBoundingBox();
      double margin = mob.getBbWidth() * 0.5 + 1.0;
      return new AABB(
         portal.minX - margin, portal.minY - mob.getBbHeight(), portal.minZ - margin, portal.maxX + margin, portal.maxY + 0.5, portal.maxZ + margin
      );
   }
}
