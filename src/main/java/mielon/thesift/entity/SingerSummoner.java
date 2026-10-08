package mielon.thesift.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import mielon.thesift.block.ModBlocks;
import mielon.thesift.platform.NeoEvents;
import mielon.thesift.portal.PortalFrameScanner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class SingerSummoner {
   private static final int FRAME_RADIUS = 40;
   private static final int SONOROUS_RADIUS = 20;
   private static final double EXISTING_SINGER_RADIUS = 2.0;
   private static final int SUMMON_DELAY_TICKS = 60;
   private static final List<SingerSummoner.PendingSummon> PENDING = new ArrayList<>();

   private SingerSummoner() {
   }

   public static void register() {
      NeoEvents.ServerTickEvents.END_SERVER_TICK.register(server -> tick());
      NeoEvents.ServerLifecycleEvents.SERVER_STOPPED.register(server -> PENDING.clear());
   }

   public static void onGoatHornFinished(ServerLevel level, Player player) {
      PENDING.add(new SingerSummoner.PendingSummon(level, player.position()));
   }

   public static boolean isNearAncientCityCenter(ServerLevel level, Vec3 position) {
      return findNearestFrame(level, position).isPresent();
   }

   private static void tick() {
      Iterator<SingerSummoner.PendingSummon> iterator = PENDING.iterator();

      while (iterator.hasNext()) {
         SingerSummoner.PendingSummon pending = iterator.next();
         if (pending.tick()) {
            iterator.remove();
         }
      }
   }

   private static void trySummon(ServerLevel level, Vec3 origin) {
      Optional<PortalFrameScanner.Frame> frameOptional = findNearestFrame(level, origin);
      if (!frameOptional.isEmpty()) {
         PortalFrameScanner.Frame frame = frameOptional.get();
         Vec3 spawnPos = frame.bottomCenter();
         BlockPos portalCenter = BlockPos.containing(spawnPos);
         if (!hasSingerForPortal(level, portalCenter, spawnPos)) {
            BlockPos sonorous = findNearestSonorous(level, spawnPos);
            Direction facing = getCardinalDirection(spawnPos, sonorous);
            SingerEntity singer = new SingerEntity(ModEntities.SINGER, level);
            singer.setPortalCenter(portalCenter);
            singer.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
            float yaw = facing.toYRot();
            singer.setYRot(yaw);
            singer.setYBodyRot(yaw);
            singer.yBodyRotO = yaw;
            singer.setYHeadRot(yaw);
            singer.yHeadRotO = yaw;
            singer.beginSequence();
            level.addFreshEntity(singer);
         }
      }
   }

   private static boolean hasSingerForPortal(ServerLevel level, BlockPos portalCenter, Vec3 spawnPos) {
      double radius = 2.0;
      AABB box = new AABB(spawnPos.x - radius, spawnPos.y - radius, spawnPos.z - radius, spawnPos.x + radius, spawnPos.y + radius, spawnPos.z + radius);

      for (SingerEntity singer : level.getEntitiesOfClass(SingerEntity.class, box)) {
         BlockPos singerPortal = singer.getPortalCenter();
         if (portalCenter.equals(singerPortal)) {
            return true;
         }
      }

      return false;
   }

   private static Optional<PortalFrameScanner.Frame> findNearestFrame(ServerLevel level, Vec3 origin) {
      double bestDistance = Double.MAX_VALUE;
      PortalFrameScanner.Frame best = null;
      Set<Vec3> seenCenters = new HashSet<>();
      MutableBlockPos cursor = new MutableBlockPos();
      int originX = Mth.floor(origin.x);
      int originY = Mth.floor(origin.y);
      int originZ = Mth.floor(origin.z);

      for (int dx = -40; dx <= 40; dx++) {
         for (int dy = -40; dy <= 40; dy++) {
            for (int dz = -40; dz <= 40; dz++) {
               double distanceSquared = (double)dx * dx + (double)dy * dy + (double)dz * dz;
               if (!(distanceSquared > 1600.0)) {
                  cursor.set(originX + dx, originY + dy, originZ + dz);
                  if (level.getBlockState(cursor).is(Blocks.REINFORCED_DEEPSLATE)) {
                     Optional<PortalFrameScanner.Frame> candidate = PortalFrameScanner.scan(level, cursor);
                     if (!candidate.isEmpty()) {
                        PortalFrameScanner.Frame frame = candidate.get();
                        if (seenCenters.add(frame.bottomCenter())) {
                           double distance = frame.bottomCenter().distanceToSqr(origin);
                           if (distance <= 1600.0 && distance < bestDistance) {
                              bestDistance = distance;
                              best = frame;
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return Optional.ofNullable(best);
   }

   private static BlockPos findNearestSonorous(ServerLevel level, Vec3 origin) {
      MutableBlockPos cursor = new MutableBlockPos();
      BlockPos best = null;
      double bestDistance = Double.MAX_VALUE;
      int originX = Mth.floor(origin.x);
      int originY = Mth.floor(origin.y);
      int originZ = Mth.floor(origin.z);

      for (int dx = -20; dx <= 20; dx++) {
         for (int dy = -20; dy <= 20; dy++) {
            for (int dz = -20; dz <= 20; dz++) {
               double distanceSquared = (double)dx * dx + (double)dy * dy + (double)dz * dz;
               if (!(distanceSquared > 400.0)) {
                  cursor.set(originX + dx, originY + dy, originZ + dz);
                  if (level.getBlockState(cursor).is(ModBlocks.SONOROUS_DEEPSLATE)) {
                     Vec3 blockCenter = Vec3.atCenterOf(cursor);
                     double distance = blockCenter.distanceToSqr(origin);
                     if (distance < bestDistance) {
                        bestDistance = distance;
                        best = cursor.immutable();
                     }
                  }
               }
            }
         }
      }

      return best;
   }

   private static Direction getCardinalDirection(Vec3 origin, BlockPos target) {
      if (target == null) {
         return Direction.SOUTH;
      } else {
         double dx = target.getX() + 0.5 - origin.x;
         double dz = target.getZ() + 0.5 - origin.z;
         if (Math.abs(dx) >= Math.abs(dz)) {
            return dx >= 0.0 ? Direction.EAST : Direction.WEST;
         } else {
            return dz >= 0.0 ? Direction.SOUTH : Direction.NORTH;
         }
      }
   }

   private static final class PendingSummon {
      private final ServerLevel level;
      private final Vec3 origin;
      private int ticksLeft = 60;

      PendingSummon(ServerLevel level, Vec3 origin) {
         this.level = level;
         this.origin = origin;
      }

      boolean tick() {
         if (this.ticksLeft > 0) {
            this.ticksLeft--;
            return false;
         } else {
            SingerSummoner.trySummon(this.level, this.origin);
            return true;
         }
      }
   }
}
