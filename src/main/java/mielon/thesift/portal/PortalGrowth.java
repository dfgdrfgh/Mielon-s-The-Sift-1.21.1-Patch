package mielon.thesift.portal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mielon.thesift.block.ModBlocks;
import mielon.thesift.block.SiftPortalBlock;
import mielon.thesift.block.entity.SiftPortalBlockEntity;
import mielon.thesift.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;

public class PortalGrowth {
   public static void register() {
   }

   public static void start(ServerLevel level, List<BlockPos> interior, Axis axis) {
      cancelAnimationsFor(level, interior);
      List<BlockPos> queue = new ArrayList<>(interior);
      sortFromEdgesToCenter(queue, axis);
      if (!queue.isEmpty()) {
         BlockPos anchorPos = queue.get(0);
         if (PortalFrameScanner.isAirLikeInterior(level, anchorPos)) {
            level.setBlockAndUpdate(anchorPos, (BlockState)ModBlocks.SIFT_PORTAL.defaultBlockState().setValue(SiftPortalBlock.AXIS, axis));
            level.sendParticles(ParticleTypes.END_ROD, anchorPos.getX() + 0.5, anchorPos.getY() + 0.5, anchorPos.getZ() + 0.5, 5, 0.32, 0.32, 0.32, 0.025);
            level.playSound(null, anchorPos, ModSounds.SIFT_PORTAL_AMBIENT, SoundSource.BLOCKS, 0.3F, 1.15F);
         }

         if (level.getBlockEntity(anchorPos) instanceof SiftPortalBlockEntity controller) {
            level.playSound(null, anchorPos, ModSounds.THE_SIFT_PORTAL_OPEN, SoundSource.BLOCKS, 4.0F, 1.0F);
            controller.startGrowth(queue, 1);
         }
      }
   }

   public static void startClosing(ServerLevel level, List<BlockPos> interior, Axis axis) {
      List<BlockPos> queue = new ArrayList<>();

      for (BlockPos pos : interior) {
         if (level.getBlockState(pos).is(ModBlocks.SIFT_PORTAL)) {
            queue.add(pos);
         }
      }

      if (!queue.isEmpty()) {
         cancelAnimationsFor(level, queue);
         sortFromCenterToEdges(queue, axis);
         BlockPos anchorPos = queue.get(queue.size() - 1);
         if (level.getBlockEntity(anchorPos) instanceof SiftPortalBlockEntity controller) {
            level.playSound(null, anchorPos, ModSounds.THE_SIFT_PORTAL_CLOSE, SoundSource.BLOCKS, 4.0F, 1.0F);
            controller.startClosing(queue);
         }
      }
   }

   private static void sortFromEdgesToCenter(List<BlockPos> queue, Axis axis) {
      sortFromCenterToEdges(queue, axis);
      Collections.reverse(queue);
   }

   private static void sortFromCenterToEdges(List<BlockPos> queue, Axis axis) {
      if (!queue.isEmpty()) {
         boolean useX = axis == Axis.X;
         double minW = Double.POSITIVE_INFINITY;
         double maxW = Double.NEGATIVE_INFINITY;
         double minH = Double.POSITIVE_INFINITY;
         double maxH = Double.NEGATIVE_INFINITY;

         for (BlockPos pos : queue) {
            double w = useX ? pos.getX() : pos.getZ();
            double h = pos.getY();
            minW = Math.min(minW, w);
            maxW = Math.max(maxW, w);
            minH = Math.min(minH, h);
            maxH = Math.max(maxH, h);
         }

         double centerW = (minW + maxW) * 0.5;
         double centerH = (minH + maxH) * 0.5;
         queue.sort(Comparator.comparing(posx -> {
            int w = useX ? posx.getX() : posx.getZ();
            int h = posx.getY();
            int depth = useX ? posx.getZ() : posx.getX();
            return PortalSortKey.fromCenter(w, h, depth, centerW, centerH);
         }));
      }
   }

   private static void cancelAnimationsFor(ServerLevel level, List<BlockPos> positions) {
      if (!positions.isEmpty()) {
         Set<BlockPos> positionSet = new HashSet<>(positions);

         for (BlockPos pos : positions) {
            if (level.getBlockEntity(pos) instanceof SiftPortalBlockEntity portal && portal.isAnimatingPortal() && portal.animationOverlaps(positionSet)) {
               portal.stopPortalAnimation();
            }
         }
      }
   }
}
