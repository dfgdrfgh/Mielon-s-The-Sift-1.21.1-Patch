package mielon.thesift.portal;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import mielon.thesift.block.ModBlocks;
import mielon.thesift.block.SonorousDeepslateBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class SonorousConsoles {
   public static final int GROUP_SEARCH_RADIUS = 16;
   public static final int[] TARGET_SEQUENCE = new int[]{1, 3, 7, 6, 5, 2, 4, 8};
   public static final int[] CLOSING_SEQUENCE = reverse(TARGET_SEQUENCE);
   private static final Map<SonorousConsoles.ConsoleKey, Deque<SonorousConsoles.PlayedNote>> BUFFERS = new HashMap<>();

   private SonorousConsoles() {
   }

   public static void clearTransientBuffers() {
      BUFFERS.clear();
   }

   public static Optional<SonorousConsoles.MatchedSequence> onSoundPlayed(ServerLevel level, BlockPos noteBlockPos, int soundIndex1to8) {
      List<BlockPos> group = findGroup(level, noteBlockPos);
      if (group.isEmpty()) {
         return Optional.empty();
      } else {
         SonorousConsoles.ConsoleKey key = new SonorousConsoles.ConsoleKey(level.dimension(), anchor(group));
         Deque<SonorousConsoles.PlayedNote> buffer = BUFFERS.computeIfAbsent(key, k -> new ArrayDeque<>(TARGET_SEQUENCE.length + 1));
         buffer.addLast(new SonorousConsoles.PlayedNote(noteBlockPos.immutable(), soundIndex1to8));

         while (buffer.size() > TARGET_SEQUENCE.length) {
            buffer.removeFirst();
         }

         if (matches(buffer, TARGET_SEQUENCE)) {
            List<SonorousConsoles.PlayedNote> winning = List.copyOf(buffer);
            BUFFERS.remove(key);
            return Optional.of(new SonorousConsoles.MatchedSequence(winning, SonorousConsoles.SequenceType.OPENING));
         } else if (matches(buffer, CLOSING_SEQUENCE)) {
            List<SonorousConsoles.PlayedNote> winning = List.copyOf(buffer);
            BUFFERS.remove(key);
            return Optional.of(new SonorousConsoles.MatchedSequence(winning, SonorousConsoles.SequenceType.CLOSING));
         } else {
            return Optional.empty();
         }
      }
   }

   public static void reset(ServerLevel level, BlockPos anyPosNearGroup) {
      BlockPos anchorPos = findAnchor(level, anyPosNearGroup);
      if (anchorPos != null) {
         BUFFERS.remove(new SonorousConsoles.ConsoleKey(level.dimension(), anchorPos));
      }
   }

   public static BlockPos findAnchor(ServerLevel level, BlockPos anyPosNearGroup) {
      List<BlockPos> group = findGroup(level, anyPosNearGroup);
      return group.isEmpty() ? null : anchor(group);
   }

   private static boolean matches(Deque<SonorousConsoles.PlayedNote> buffer, int[] target) {
      if (buffer.size() != target.length) {
         return false;
      } else {
         int i = 0;

         for (SonorousConsoles.PlayedNote played : buffer) {
            if (played.soundIndex1to8() != target[i]) {
               return false;
            }

            i++;
         }

         return true;
      }
   }

   private static int[] reverse(int[] source) {
      int[] result = new int[source.length];

      for (int i = 0; i < source.length; i++) {
         result[i] = source[source.length - 1 - i];
      }

      return result;
   }

   public static List<BlockPos> findGroup(ServerLevel level, BlockPos origin) {
      BlockPos seed = resolveSeed(level, origin);
      if (seed == null) {
         return List.of();
      } else {
         Set<BlockPos> found = new HashSet<>();
         Deque<BlockPos> queue = new ArrayDeque<>();
         found.add(seed);
         queue.add(seed);
         MutableBlockPos cursor = new MutableBlockPos();

         while (!queue.isEmpty()) {
            BlockPos current = queue.poll();

            for (int dx = -16; dx <= 16; dx++) {
               for (int dy = -16; dy <= 16; dy++) {
                  for (int dz = -16; dz <= 16; dz++) {
                     cursor.set(current.getX() + dx, current.getY() + dy, current.getZ() + dz);
                     if (isNoteState(level, cursor)) {
                        BlockPos immutable = cursor.immutable();
                        if (found.add(immutable)) {
                           queue.add(immutable);
                        }
                     }
                  }
               }
            }
         }

         return new ArrayList<>(found);
      }
   }

   private static BlockPos resolveSeed(ServerLevel level, BlockPos origin) {
      BlockPos originImmutable = origin.immutable();
      if (isNoteState(level, originImmutable)) {
         return originImmutable;
      } else {
         BlockPos below = originImmutable.below();
         return isNoteState(level, below) ? below : null;
      }
   }

   private static boolean isNoteState(ServerLevel level, BlockPos pos) {
      BlockState state = level.getBlockState(pos);
      return !state.is(ModBlocks.SONOROUS_DEEPSLATE) ? false : state.getValue(SonorousDeepslateBlock.MODE) == SonorousDeepslateBlock.Mode.NOTE;
   }

   private static BlockPos anchor(List<BlockPos> group) {
      return group.stream().min(Comparator.<BlockPos>comparingInt(p -> p.getY()).thenComparingInt(Vec3i::getX).thenComparingInt(Vec3i::getZ)).orElseThrow();
   }

   private record ConsoleKey(ResourceKey<Level> dimension, BlockPos anchor) {
   }

   public record MatchedSequence(List<SonorousConsoles.PlayedNote> notes, SonorousConsoles.SequenceType type) {
   }

   public record PlayedNote(BlockPos notePos, int soundIndex1to8) {
   }

   public static enum SequenceType {
      OPENING,
      CLOSING;
   }
}
