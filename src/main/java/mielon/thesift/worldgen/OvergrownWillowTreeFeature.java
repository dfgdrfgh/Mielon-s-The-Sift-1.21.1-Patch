package mielon.thesift.worldgen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import mielon.thesift.block.ModBlocks;
import mielon.thesift.block.OvergrownWillowVinesBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public final class OvergrownWillowTreeFeature extends Feature<NoneFeatureConfiguration> {
   private static final OvergrownWillowTreeFeature.TemplateChoice SMALL = template("overgrown_willow/small_01", 3, 6);
   private static final OvergrownWillowTreeFeature.TemplateChoice BIG = template("overgrown_willow/big_01", 7, 6);
   private static final Direction[] ROOT_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

   public OvergrownWillowTreeFeature() {
      super(NoneFeatureConfiguration.CODEC);
   }

   public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
      WorldGenLevel level = context.level();
      RandomSource random = context.random();
      BlockPos placementOrigin = context.origin();
      SiftWorldgenBounds bounds = SiftWorldgenBounds.aroundWithNeighbourMargin(placementOrigin);
      int minX = placementOrigin.getX() & -16;
      int minZ = placementOrigin.getZ() & -16;
      int rootX = minX + 4 + random.nextInt(8);
      int rootZ = minZ + 4 + random.nextInt(8);
      int rootY = level.getHeight(Types.WORLD_SURFACE_WG, rootX, rootZ);
      BlockPos root = new BlockPos(rootX, rootY, rootZ);
      if (!SiftFeaturePlacementGuard.intersectsPortal(root, 12) && isTreeGround(level.getBlockState(root.below()))) {
         OvergrownWillowTreeFeature.TemplateChoice choice = random.nextFloat() < 0.38F ? BIG : SMALL;
         return placeAt(level, random, root, bounds, choice);
      } else {
         return false;
      }
   }

   public static boolean growFromSapling(ServerLevel level, RandomSource random, BlockPos root) {
      SiftWorldgenBounds bounds = SiftWorldgenBounds.aroundWithNeighbourMargin(root);
      boolean large = random.nextFloat() < 0.38F;
      return placeAt(level, random, root, bounds, large);
   }

   static boolean placeAt(WorldGenLevel level, RandomSource random, BlockPos root, SiftWorldgenBounds bounds, boolean large) {
      return !SiftFeaturePlacementGuard.intersectsPortal(root, 12) && isTreeGround(level.getBlockState(root.below()))
         ? placeAt(level, random, root, bounds, large ? BIG : SMALL)
         : false;
   }

   private static boolean placeAt(
      WorldGenLevel level, RandomSource random, BlockPos root, SiftWorldgenBounds bounds, OvergrownWillowTreeFeature.TemplateChoice choice
   ) {
      Optional<StructureTemplate> optional = level.getLevel().getStructureManager().get(choice.id());
      if (optional.isEmpty()) {
         return false;
      } else {
         StructureTemplate template = optional.get();
         Rotation rotation = Rotation.getRandom(random);
         Mirror mirror = random.nextBoolean() ? Mirror.NONE : Mirror.LEFT_RIGHT;
         BlockPos pivot = choice.root();
         BlockPos templateOrigin = root.offset(-pivot.getX(), 0, -pivot.getZ());
         StructurePlaceSettings settings = new StructurePlaceSettings()
            .setMirror(mirror)
            .setRotation(rotation)
            .setRotationPivot(pivot)
            .setIgnoreEntities(true)
            .setKnownShape(false)
            .setRandom(random);
         List<StructureBlockInfo> logs = template.filterBlocks(templateOrigin, settings, ModBlocks.OVERGROWN_WILLOW_LOG);
         List<StructureBlockInfo> wood = template.filterBlocks(templateOrigin, settings, ModBlocks.OVERGROWN_WILLOW_WOOD);
         List<StructureBlockInfo> foliage = template.filterBlocks(templateOrigin, settings, ModBlocks.OVERGROWN_WILLOW_FOLIAGE);
         List<StructureBlockInfo> vines = template.filterBlocks(templateOrigin, settings, ModBlocks.OVERGROWN_WILLOW_VINES);
         if (allInside(bounds, logs) && allInside(bounds, wood) && allInside(bounds, foliage) && allInside(bounds, vines)) {
            BlockPos actualBase = findActualBase(logs, root);
            if (actualBase != null
               && isTreeGround(level.getBlockState(actualBase.below()))
               && !hasNearbyTrunk(level, bounds, actualBase, 5)
               && trunkFits(level, bounds, logs)
               && trunkFits(level, bounds, wood)) {
               int placed = placeTemplateBlocks(level, bounds, logs);
               placed += placeTemplateBlocks(level, bounds, wood);
               placed += placeTemplateBlocks(level, bounds, foliage);
               placed += placeVineTemplateBlocksTopDown(level, bounds, vines);
               alignTemplateVinesToTree(level, bounds, vines);
               addSurfaceRoots(level, bounds, random, actualBase);
               varyHangingVines(level, bounds, random, vines);
               return placed > 0;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   private static BlockPos findActualBase(List<StructureBlockInfo> logs, BlockPos requestedRoot) {
      BlockPos best = null;
      int lowestY = Integer.MAX_VALUE;
      int bestDistance = Integer.MAX_VALUE;

      for (StructureBlockInfo info : logs) {
         BlockPos pos = info.pos();
         int y = pos.getY();
         int distance = horizontalDistanceSquared(pos, requestedRoot);
         if (y < lowestY || y == lowestY && distance < bestDistance) {
            lowestY = y;
            bestDistance = distance;
            best = pos;
         }
      }

      return best;
   }

   private static int horizontalDistanceSquared(BlockPos first, BlockPos second) {
      int dx = first.getX() - second.getX();
      int dz = first.getZ() - second.getZ();
      return dx * dx + dz * dz;
   }

   private static boolean trunkFits(WorldGenLevel level, SiftWorldgenBounds bounds, List<StructureBlockInfo> blocks) {
      for (StructureBlockInfo info : blocks) {
         if (!bounds.contains(info.pos()) || !level.ensureCanWrite(info.pos()) || !canReplaceTreeBlock(level.getBlockState(info.pos()))) {
            return false;
         }
      }

      return true;
   }

   private static int placeTemplateBlocks(WorldGenLevel level, SiftWorldgenBounds bounds, List<StructureBlockInfo> blocks) {
      int placed = 0;

      for (StructureBlockInfo info : blocks) {
         if (bounds.contains(info.pos()) && level.ensureCanWrite(info.pos()) && canReplaceTreeBlock(level.getBlockState(info.pos()))) {
            level.setBlock(info.pos(), info.state(), 2);
            placed++;
         }
      }

      return placed;
   }

   private static int placeVineTemplateBlocksTopDown(WorldGenLevel level, SiftWorldgenBounds bounds, List<StructureBlockInfo> vines) {
      List<StructureBlockInfo> topDown = new ArrayList<>(vines);
      topDown.sort((first, second) -> Integer.compare(second.pos().getY(), first.pos().getY()));
      int placed = 0;

      for (StructureBlockInfo info : topDown) {
         if (bounds.contains(info.pos()) && level.ensureCanWrite(info.pos()) && canReplaceTreeBlock(level.getBlockState(info.pos()))) {
            level.setBlock(info.pos(), info.state(), 2);
            placed++;
         }
      }

      return placed;
   }

   private static void addSurfaceRoots(WorldGenLevel level, SiftWorldgenBounds bounds, RandomSource random, BlockPos root) {
      BlockState verticalRoot = ModBlocks.OVERGROWN_WILLOW_LOG.defaultBlockState();
      BlockState baseState = level.getBlockState(root);
      if ((baseState.is(ModBlocks.OVERGROWN_WILLOW_LOG) || baseState.is(ModBlocks.OVERGROWN_WILLOW_WOOD)) && isTreeGround(level.getBlockState(root.below()))) {
         int firstDirection = random.nextInt(ROOT_DIRECTIONS.length);
         int rootCount = 2 + random.nextInt(3);

         for (int rootIndex = 0; rootIndex < rootCount; rootIndex++) {
            Direction direction = ROOT_DIRECTIONS[(firstDirection + rootIndex) % ROOT_DIRECTIONS.length];
            int length = 1 + random.nextInt(3);
            BlockState rootState = (BlockState)verticalRoot.setValue(RotatedPillarBlock.AXIS, direction.getAxis());

            for (int step = 1; step <= length; step++) {
               BlockPos pos = root.relative(direction, step);
               if (!bounds.contains(pos)
                  || !level.ensureCanWrite(pos)
                  || !canReplaceTreeBlock(level.getBlockState(pos))
                  || !isTreeGround(level.getBlockState(pos.below()))) {
                  break;
               }

               level.setBlock(pos, rootState, 2);
            }
         }
      }
   }

   private static void varyHangingVines(WorldGenLevel level, SiftWorldgenBounds bounds, RandomSource random, List<StructureBlockInfo> vines) {
      for (StructureBlockInfo info : vines) {
         BlockState tipState = level.getBlockState(info.pos());
         if (isWillowVine(tipState)) {
            for (Direction direction : ROOT_DIRECTIONS) {
               BooleanProperty face = VineBlock.getPropertyForFace(direction);
               BooleanProperty bottom = OvergrownWillowVinesBlock.getBottomProperty(direction);
               tipState = level.getBlockState(info.pos());
               if ((Boolean)tipState.getValue(face) && (Boolean)tipState.getValue(bottom) && !(random.nextFloat() >= 0.34F)) {
                  BlockPos current = info.pos();
                  int extension = 1 + random.nextInt(3);

                  for (int step = 0; step < extension; step++) {
                     BlockPos below = current.below();
                     if (!bounds.contains(below) || !level.ensureCanWrite(below)) {
                        break;
                     }

                     BlockState belowState = level.getBlockState(below);
                     if (belowState.isAir()) {
                        belowState = (BlockState)ModBlocks.OVERGROWN_WILLOW_VINES.defaultBlockState().setValue(face, true);
                     } else {
                        if (!isWillowVine(belowState)) {
                           break;
                        }

                        belowState = (BlockState)belowState.setValue(face, true);
                     }

                     belowState = OvergrownWillowVinesBlock.refreshBottomFaces(belowState, level.getBlockState(below.below()));
                     level.setBlock(below, belowState, 2);
                     refreshVineAt(level, bounds, current);
                     current = below;
                  }

                  refreshVineAt(level, bounds, current);
               }
            }
         }
      }
   }

   private static void alignTemplateVinesToTree(WorldGenLevel level, SiftWorldgenBounds bounds, List<StructureBlockInfo> vines) {
      List<StructureBlockInfo> topDown = new ArrayList<>(vines);
      topDown.sort((first, second) -> Integer.compare(second.pos().getY(), first.pos().getY()));

      for (StructureBlockInfo info : topDown) {
         BlockPos pos = info.pos();
         if (bounds.contains(pos)) {
            BlockState state = level.getBlockState(pos);
            if (isWillowVine(state)) {
               BlockState above = level.getBlockState(pos.above());
               boolean upSupport = VineBlock.isAcceptableNeighbour(level, pos.above(), Direction.UP);
               int directSupportMask = 0;

               for (int directionIndex = 0; directionIndex < ROOT_DIRECTIONS.length; directionIndex++) {
                  Direction direction = ROOT_DIRECTIONS[directionIndex];
                  if (VineBlock.isAcceptableNeighbour(level, pos.relative(direction), direction)) {
                     directSupportMask |= 1 << directionIndex;
                  }
               }

               boolean hasDirectSideSupport = directSupportMask != 0;
               boolean hasWallFace = false;

               for (int directionIndexx = 0; directionIndexx < ROOT_DIRECTIONS.length; directionIndexx++) {
                  Direction direction = ROOT_DIRECTIONS[directionIndexx];
                  BooleanProperty face = VineBlock.getPropertyForFace(direction);
                  boolean directSupport = (directSupportMask & 1 << directionIndexx) != 0;
                  boolean inheritedFromAbove = isWillowVine(above) && (Boolean)above.getValue(face);
                  boolean keepAuthoredFace = !hasDirectSideSupport && !isWillowVine(above) && upSupport && (Boolean)state.getValue(face);
                  boolean active = directSupport || inheritedFromAbove || keepAuthoredFace;
                  state = (BlockState)state.setValue(face, active);
                  hasWallFace |= active;
               }

               state = (BlockState)state.setValue(VineBlock.UP, upSupport);
               if (!hasWallFace && !upSupport) {
                  level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
               } else {
                  level.setBlock(pos, state, 2);
               }
            }
         }
      }

      for (StructureBlockInfo infox : topDown) {
         refreshVineAt(level, bounds, infox.pos());
      }
   }

   private static void refreshVineAt(WorldGenLevel level, SiftWorldgenBounds bounds, BlockPos pos) {
      if (bounds.contains(pos) && bounds.contains(pos.below())) {
         BlockState state = level.getBlockState(pos);
         if (isWillowVine(state)) {
            level.setBlock(pos, OvergrownWillowVinesBlock.refreshBottomFaces(state, level.getBlockState(pos.below())), 2);
         }
      }
   }

   private static boolean isWillowVine(BlockState state) {
      return state.is(ModBlocks.OVERGROWN_WILLOW_VINES) || state.is(ModBlocks.OVERGROWN_WILLOW_VINES_PLANT);
   }

   private static boolean hasNearbyTrunk(WorldGenLevel level, SiftWorldgenBounds bounds, BlockPos root, int radius) {
      MutableBlockPos cursor = new MutableBlockPos();
      OvergrownWillowTreeFeature.TrunkSectionCache sectionCache = new OvergrownWillowTreeFeature.TrunkSectionCache(level, bounds);

      for (int dx = -radius; dx <= radius; dx++) {
         for (int dz = -radius; dz <= radius; dz++) {
            for (int dy = -1; dy <= 10; dy++) {
               cursor.set(root.getX() + dx, root.getY() + dy, root.getZ() + dz);
               if (bounds.contains(cursor) && sectionCache.mayContainTrunk(cursor)) {
                  BlockState state = level.getBlockState(cursor);
                  if (isWillowTrunk(state)) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   private static boolean isWillowTrunk(BlockState state) {
      return state.is(ModBlocks.OVERGROWN_WILLOW_LOG) || state.is(ModBlocks.OVERGROWN_WILLOW_WOOD);
   }

   private static boolean allInside(SiftWorldgenBounds bounds, List<StructureBlockInfo> blocks) {
      for (StructureBlockInfo info : blocks) {
         if (!bounds.contains(info.pos())) {
            return false;
         }
      }

      return true;
   }

   private static boolean isTreeGround(BlockState state) {
      return state.is(BlockTags.DIRT)
         || state.is(ModBlocks.SIFTSLATE_GROWTH)
         || state.is(ModBlocks.HEALTHY_SCULK)
         || state.is(ModBlocks.DRY_HEALTHY_SCULK)
         || state.is(ModBlocks.DRY_HEALTHY_SCULK_GROWTH);
   }

   private static boolean canReplaceTreeBlock(BlockState state) {
      return state.isAir()
         || state.is(ModBlocks.OVERGROWN_WILLOW_FOLIAGE)
         || state.is(ModBlocks.OVERGROWN_WILLOW_VINES)
         || state.is(ModBlocks.OVERGROWN_WILLOW_VINES_PLANT)
         || state.is(ModBlocks.OVERGROWN_CHARD)
         || state.is(ModBlocks.OVERGROWN_STALKS)
         || state.is(ModBlocks.OVERGROWN_FRONDS)
         || state.is(ModBlocks.SIFTSLATE_STALKS)
         || state.is(ModBlocks.HEALTHY_SCULK_SPROUTS)
         || state.is(ModBlocks.DRY_HEALTHY_SCULK_SPROUTS);
   }

   private static OvergrownWillowTreeFeature.TemplateChoice template(String path, int rootX, int rootZ) {
      return new OvergrownWillowTreeFeature.TemplateChoice(ResourceLocation.fromNamespaceAndPath("the_sift", path), new BlockPos(rootX, 0, rootZ));
   }

   private record TemplateChoice(ResourceLocation id, BlockPos root) {
   }

   private static final class TrunkSectionCache {
      private final WorldGenLevel level;
      private final SiftWorldgenBounds bounds;
      private long[] keys = new long[8];
      private boolean[] values = new boolean[8];
      private int size;

      private TrunkSectionCache(WorldGenLevel level, SiftWorldgenBounds bounds) {
         this.level = level;
         this.bounds = bounds;
      }

      private boolean mayContainTrunk(BlockPos pos) {
         if (this.bounds.contains(pos) && !this.level.isOutsideBuildHeight(pos)) {
            long key = SectionPos.asLong(pos);

            for (int index = 0; index < this.size; index++) {
               if (this.keys[index] == key) {
                  return this.values[index];
               }
            }

            int sectionX = SectionPos.x(key);
            int sectionY = SectionPos.y(key);
            int sectionZ = SectionPos.z(key);
            ChunkAccess chunk = this.level.getChunk(sectionX, sectionZ);
            int sectionIndex = chunk.getSectionIndexFromSectionY(sectionY);
            boolean result = sectionIndex >= 0 && sectionIndex < chunk.getSections().length && sectionMayContainTrunk(chunk.getSection(sectionIndex));
            this.remember(key, result);
            return result;
         } else {
            return false;
         }
      }

      private static boolean sectionMayContainTrunk(LevelChunkSection section) {
         return !section.hasOnlyAir() && section.maybeHas(OvergrownWillowTreeFeature::isWillowTrunk);
      }

      private void remember(long key, boolean value) {
         if (this.size == this.keys.length) {
            this.keys = Arrays.copyOf(this.keys, this.size * 2);
            this.values = Arrays.copyOf(this.values, this.size * 2);
         }

         this.keys[this.size] = key;
         this.values[this.size] = value;
         this.size++;
      }
   }
}
