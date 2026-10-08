package mielon.thesift.world;

import java.util.Arrays;
import java.util.Optional;
import mielon.thesift.TheSiftMod;
import mielon.thesift.block.ModBlocks;
import mielon.thesift.worldgen.SiftFeaturePlacementGuard;
import mielon.thesift.worldgen.SiftSurfaceDecorator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public final class SiftPortalStructure {
   private static final ResourceLocation TEMPLATE_ID = ResourceLocation.fromNamespaceAndPath("the_sift", "main_portal");
   private static final int SIZE_X = 9;
   private static final int SIZE_Y = 19;
   private static final int SIZE_Z = 40;
   private static final BlockPos MARKER_RELATIVE = new BlockPos(3, 4, 19);
   private static final BlockPos PLAYER_RELATIVE = new BlockPos(3, 5, 19);
   private static final int STRUCTURE_GROUND_RELATIVE_Y = MARKER_RELATIVE.getY() - 4;
   private static final int[][] GROUND_PROFILE = new int[][]{
      {-1, -1, -1, -1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1},
      {-1, -1, -1, -1, 1, 7, 14, 1, 6, 4, 3, 3, 3, 2, -1, -1, 3, 2, 2, 3, 3, 2, 2, 3, 3, 2, 2, 3, 3, 4, -1, 1, 12, 16, 14, 2, -1, -1, -1, -1},
      {-1, -1, -1, -1, 0, 17, 17, 12, 18, 6, 5, 4, 3, 3, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 6, 7, 17, 17, 17, 14, 14, -1, -1, -1, -1},
      {-1, -1, -1, -1, 0, 17, 18, 18, 18, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 16, 17, 17, 15, 14, -1, -1, -1, -1},
      {-1, -1, -1, -1, 0, 17, 17, 18, 18, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 5, 5, 6, 16, 17, 17, 15, 15, -1, -1, -1, -1},
      {-1, -1, -1, -1, 0, 17, 17, 2, 6, 4, 3, 4, 3, 2, 2, 3, 3, 2, 2, 3, 3, 2, 2, 3, 3, 2, 2, 3, 3, 2, 4, 6, 17, 16, 15, 15, -1, -1, -1, -1},
      {-1, -1, -1, -1, 0, 0, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 1, 1, 1, 2, 1, 0, 2, 2, 2, 3, 3, 5, 5, 5, 4, -1, -1, -1, -1},
      {-1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, -1, 1, 2, 2, -1, -1, -1, -1, -1, -1, -1, -1},
      {
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            0,
            0,
            0,
            0,
            0,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1
      }
   };
   private static final int[][] CLIFF_PROFILE = new int[][]{
      {-1, -1, -1, -1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1},
      {
            -1,
            -1,
            -1,
            -1,
            15,
            17,
            17,
            17,
            17,
            17,
            17,
            17,
            17,
            16,
            16,
            16,
            17,
            16,
            16,
            17,
            17,
            17,
            17,
            17,
            17,
            17,
            16,
            16,
            17,
            17,
            17,
            17,
            17,
            16,
            14,
            14,
            -1,
            -1,
            -1,
            -1
      },
      {
            9,
            10,
            10,
            9,
            16,
            17,
            17,
            17,
            18,
            18,
            17,
            17,
            17,
            16,
            16,
            16,
            17,
            16,
            16,
            17,
            17,
            17,
            18,
            17,
            17,
            17,
            17,
            17,
            17,
            17,
            16,
            17,
            17,
            17,
            14,
            14,
            12,
            12,
            12,
            11
      },
      {
            10,
            10,
            10,
            10,
            17,
            17,
            18,
            18,
            18,
            18,
            18,
            17,
            17,
            16,
            17,
            16,
            17,
            17,
            16,
            17,
            17,
            18,
            18,
            18,
            18,
            18,
            17,
            17,
            17,
            16,
            16,
            16,
            17,
            17,
            15,
            14,
            12,
            12,
            12,
            12
      },
      {
            10,
            10,
            10,
            10,
            17,
            17,
            17,
            18,
            18,
            18,
            17,
            17,
            17,
            16,
            16,
            16,
            17,
            17,
            17,
            17,
            17,
            18,
            17,
            17,
            18,
            18,
            17,
            17,
            17,
            17,
            17,
            16,
            17,
            17,
            15,
            15,
            12,
            12,
            12,
            12
      },
      {
            -1,
            -1,
            -1,
            -1,
            17,
            17,
            17,
            17,
            17,
            17,
            17,
            17,
            16,
            16,
            16,
            17,
            17,
            16,
            16,
            17,
            17,
            17,
            17,
            17,
            17,
            17,
            17,
            17,
            17,
            17,
            17,
            17,
            17,
            16,
            15,
            15,
            3,
            -1,
            -1,
            -1
      },
      {-1, -1, -1, -1, 0, 0, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 1, 1, 1, 2, 1, 0, 2, 2, 2, 3, 3, 5, 5, 5, 4, -1, -1, -1, -1},
      {-1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, -1, 1, 2, 2, -1, -1, -1, -1, -1, -1, -1, -1},
      {
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            0,
            0,
            0,
            0,
            0,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1
      }
   };
   private static final SiftPortalStructure.ProfilePoint[] GROUND_POINTS = collectProfilePoints(GROUND_PROFILE);
   private static final SiftPortalStructure.ProfilePoint[] CLIFF_POINTS = collectProfilePoints(CLIFF_PROFILE);
   private static final int BLEND_RADIUS = 52;
   private static final int FOUNDATION_BLEND_RADIUS = 15;
   private static final int SEARCH_RADIUS = 96;
   private static final int COARSE_STEP = 16;
   private static final int REFINE_RADIUS = 16;
   private static final int REFINE_STEP = 4;
   private static final int OUTER_SAMPLE_MARGIN = 12;
   private static final int FEATURE_CLEAR_MARGIN = 15;
   private static final int PORTAL_BUILD_FLAGS = 48;

   private SiftPortalStructure() {
   }

   public static Optional<BlockPos> ensurePortal(MinecraftServer server, ServerLevel sift) {
      Optional<BlockPos> stored = SiftWorldStorage.getPortalAnchor(server);
      if (stored.isPresent()) {
         BlockPos storedOrigin = stored.get().offset(-PLAYER_RELATIVE.getX(), -PLAYER_RELATIVE.getY(), -PLAYER_RELATIVE.getZ());
         SiftFeaturePlacementGuard.reservePortal(storedOrigin, 9, 40);
         return stored;
      } else {
         Optional<StructureTemplate> templateOptional = server.getStructureManager().get(TEMPLATE_ID);
         if (templateOptional.isEmpty()) {
            TheSiftMod.LOGGER.error("Missing portal structure template: {}", TEMPLATE_ID);
            return Optional.empty();
         } else {
            TheSiftMod.LOGGER.info("Selecting a location for the main Sift portal.");
            StructureTemplate template = templateOptional.get();
            SiftPortalStructure.PlacementCandidate candidate = findBestCandidate(sift);
            int naturalGroundY = candidate.groundY();
            int originY = naturalGroundY - STRUCTURE_GROUND_RELATIVE_Y;
            originY = Math.max(0, Math.min(237, originY));
            BlockPos origin = new BlockPos(candidate.anchorX() - MARKER_RELATIVE.getX(), originY, candidate.anchorZ() - MARKER_RELATIVE.getZ());
            SiftFeaturePlacementGuard.reservePortal(origin, 9, 40);
            int markerY = originY + MARKER_RELATIVE.getY();
            TheSiftMod.LOGGER
               .debug(
                  "Main portal candidate: anchor=({}, {}, {}), markerY={}, origin={}, roughness={}",
                  new Object[]{candidate.anchorX(), naturalGroundY, candidate.anchorZ(), markerY, origin, candidate.roughness()}
               );
            clearLargeFeaturesAroundPortal(sift, origin);
            blendTerrainIntoPortal(sift, origin);
            sealPortalFoundation(sift, origin);
            StructurePlaceSettings settings = new StructurePlaceSettings()
               .setMirror(Mirror.NONE)
               .setRotation(Rotation.NONE)
               .setIgnoreEntities(true)
               .setKnownShape(true)
               .setRandom(RandomSource.create(sift.getSeed() ^ 1397311060L));
            boolean placed = template.placeInWorld(sift, origin, origin, settings, RandomSource.create(sift.getSeed() ^ 88301613891916L), 48);
            if (!placed) {
               TheSiftMod.LOGGER.error("Failed to place the main Sift portal at {}", origin);
               return Optional.empty();
            } else {
               BlockPos marker = origin.offset(MARKER_RELATIVE.getX(), MARKER_RELATIVE.getY(), MARKER_RELATIVE.getZ());
               setDuringPortalBuild(sift, marker, ModBlocks.REINFORCED_SIFTSLATE.defaultBlockState());
               normalizeCoveredGrowth(sift, origin);
               BlockPos playerAnchor = origin.offset(PLAYER_RELATIVE.getX(), PLAYER_RELATIVE.getY(), PLAYER_RELATIVE.getZ());
               decoratePortalAndBlend(sift, origin, playerAnchor);
               SiftWorldStorage.setPortalAnchor(server, playerAnchor);
               TheSiftMod.LOGGER.info("Main Sift portal ready at {}", playerAnchor);
               return Optional.of(playerAnchor);
            }
         }
      }
   }

   private static void normalizeCoveredGrowth(ServerLevel level, BlockPos origin) {
      MutableBlockPos cursor = new MutableBlockPos();
      MutableBlockPos above = new MutableBlockPos();

      for (int x = 0; x < 9; x++) {
         for (int z = 0; z < 40; z++) {
            for (int y = 0; y < 18; y++) {
               cursor.set(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
               if (level.getBlockState(cursor).is(ModBlocks.SIFTSLATE_GROWTH)) {
                  above.set(cursor.getX(), cursor.getY() + 1, cursor.getZ());
                  if (level.getBlockState(above).isCollisionShapeFullBlock(level, above)) {
                     setDuringPortalBuild(level, cursor, ModBlocks.SIFTSLATE.defaultBlockState());
                  }
               }
            }
         }
      }
   }

   private static void clearLargeFeaturesAroundPortal(ServerLevel level, BlockPos origin) {
      int minX = origin.getX() - 15;
      int maxX = origin.getX() + 9 - 1 + 15;
      int minZ = origin.getZ() - 15;
      int maxZ = origin.getZ() + 40 - 1 + 15;
      MutableBlockPos cursor = new MutableBlockPos();

      for (int x = minX; x <= maxX; x++) {
         for (int z = minZ; z <= maxZ; z++) {
            int generatedTop = generatedSurfaceBlockY(level, x, z);
            int actualTop = level.getHeight(Types.WORLD_SURFACE, x, z) - 1;

            for (int y = actualTop; y > generatedTop; y--) {
               cursor.set(x, y, z);
               BlockState state = level.getBlockState(cursor);
               if (!state.isAir()) {
                  if (!isSiftTerrain(state) && !isSiftPlant(state)) {
                     break;
                  }

                  setDuringPortalBuild(level, cursor, Blocks.AIR.defaultBlockState());
               }
            }
         }
      }
   }

   private static SiftPortalStructure.PlacementCandidate findBestCandidate(ServerLevel level) {
      SiftPortalStructure.PlacementCandidate coarse = searchArea(level, -96, 96, -96, 96, 16);
      if (coarse == null) {
         int height = generatedSurfaceBlockY(level, 0, 0);
         return new SiftPortalStructure.PlacementCandidate(0, 0, height, 999.0);
      } else {
         SiftPortalStructure.PlacementCandidate refined = searchArea(
            level, coarse.anchorX() - 16, coarse.anchorX() + 16, coarse.anchorZ() - 16, coarse.anchorZ() + 16, 4
         );
         return refined != null ? refined : coarse;
      }
   }

   private static SiftPortalStructure.PlacementCandidate searchArea(ServerLevel level, int minSearchX, int maxSearchX, int minSearchZ, int maxSearchZ, int step) {
      SiftPortalStructure.PlacementCandidate best = null;
      double bestScore = Double.POSITIVE_INFINITY;
      int x = minSearchX;

      while (x <= maxSearchX) {
         for (int z = minSearchZ; z <= maxSearchZ; z += step) {
            SiftPortalStructure.TerrainSample sample = sampleFootprintWithoutLoadingChunks(level, x, z);
            SiftPortalStructure.PlacementCandidate candidate = new SiftPortalStructure.PlacementCandidate(x, z, sample.medianHeight(), sample.roughness());
            double candidateScore = score(candidate, x, z) + sample.range() * 135.0 + sample.outerRange() * 75.0;
            if (sample.medianHeight() < 12 || sample.medianHeight() > 232) {
               candidateScore += 10000.0;
            }

            if (candidateScore < bestScore) {
               bestScore = candidateScore;
               best = candidate;
            }
         }

         x += step;
      }

      return best;
   }

   private static double score(SiftPortalStructure.PlacementCandidate candidate, int x, int z) {
      double distancePenalty = Math.sqrt((double)x * x + (double)z * z) * 0.8;
      return candidate.roughness() * 18.0 + distancePenalty;
   }

   private static SiftPortalStructure.TerrainSample sampleFootprintWithoutLoadingChunks(ServerLevel level, int anchorX, int anchorZ) {
      int minX = anchorX - MARKER_RELATIVE.getX();
      int minZ = anchorZ - MARKER_RELATIVE.getZ();
      int maxX = minX + 9 - 1;
      int maxZ = minZ + 40 - 1;
      int midX = (minX + maxX) / 2;
      int midZ = (minZ + maxZ) / 2;
      int[] core = new int[]{
         generatedSurfaceBlockY(level, minX, minZ),
         generatedSurfaceBlockY(level, midX, minZ),
         generatedSurfaceBlockY(level, maxX, minZ),
         generatedSurfaceBlockY(level, minX, midZ),
         generatedSurfaceBlockY(level, midX, midZ),
         generatedSurfaceBlockY(level, maxX, midZ),
         generatedSurfaceBlockY(level, minX, maxZ),
         generatedSurfaceBlockY(level, midX, maxZ),
         generatedSurfaceBlockY(level, maxX, maxZ)
      };
      int outerMinX = minX - 12;
      int outerMaxX = maxX + 12;
      int outerMinZ = minZ - 12;
      int outerMaxZ = maxZ + 12;
      int outerMidX = (outerMinX + outerMaxX) / 2;
      int outerMidZ = (outerMinZ + outerMaxZ) / 2;
      int[] outer = new int[]{
         generatedSurfaceBlockY(level, outerMinX, outerMinZ),
         generatedSurfaceBlockY(level, outerMidX, outerMinZ),
         generatedSurfaceBlockY(level, outerMaxX, outerMinZ),
         generatedSurfaceBlockY(level, outerMinX, outerMidZ),
         generatedSurfaceBlockY(level, outerMaxX, outerMidZ),
         generatedSurfaceBlockY(level, outerMinX, outerMaxZ),
         generatedSurfaceBlockY(level, outerMidX, outerMaxZ),
         generatedSurfaceBlockY(level, outerMaxX, outerMaxZ)
      };
      int[] sorted = (int[])core.clone();
      Arrays.sort(sorted);
      int median = sorted[sorted.length / 2];
      int coreMin = Arrays.stream(core).min().orElse(median);
      int coreMax = Arrays.stream(core).max().orElse(median);
      int outerMin = Arrays.stream(outer).min().orElse(median);
      int outerMax = Arrays.stream(outer).max().orElse(median);
      double roughness = 0.0;

      for (int height : core) {
         roughness += Math.abs(height - median);
      }

      for (int height : outer) {
         roughness += Math.abs(height - median) * 0.45;
      }

      roughness /= core.length + outer.length * 0.45;
      return new SiftPortalStructure.TerrainSample(median, coreMax - coreMin, Math.max(coreMax, outerMax) - Math.min(coreMin, outerMin), roughness);
   }

   private static void blendTerrainIntoPortal(ServerLevel level, BlockPos origin) {
      int structureMinX = origin.getX();
      int structureMaxX = origin.getX() + 9 - 1;
      int structureMinZ = origin.getZ();
      int structureMaxZ = origin.getZ() + 40 - 1;
      int minX = structureMinX - 52;
      int maxX = structureMaxX + 52;
      int minZ = structureMinZ - 52;
      int maxZ = structureMaxZ + 52;
      long seed = level.getSeed();
      buildNaturalPortalFoundation(level, origin, seed);

      for (int x = minX; x <= maxX; x++) {
         for (int z = minZ; z <= maxZ; z++) {
            int localX = x - structureMinX;
            int localZ = z - structureMinZ;
            boolean inside = localX >= 0 && localX < 9 && localZ >= 0 && localZ < 40;
            int naturalY = findTerrainSurfaceBlockY(level, x, z);
            if (naturalY > 1 && naturalY < 254) {
               BlockState originalTop = level.getBlockState(new BlockPos(x, naturalY, z));
               if (isSiftTerrain(originalTop)) {
                  if (inside) {
                     int exactRelativeY = GROUND_PROFILE[localX][localZ];
                     if (exactRelativeY >= 0) {
                        int desiredY = Math.max(2, Math.min(253, origin.getY() + exactRelativeY));
                        if (naturalY > desiredY) {
                           lowerTerrainColumn(level, x, z, naturalY, desiredY, originalTop, false);
                        }
                     }
                  } else {
                     double rectangleDistance = distanceOutsideRectangle(x, z, structureMinX, structureMaxX, structureMinZ, structureMaxZ);
                     if (!(rectangleDistance > 52.0)) {
                        double envelopeRelativeY = portalTerrainEnvelope(localX, localZ, seed);
                        double approach = portalApproachMask(localX, localZ);
                        if (approach > 0.0) {
                           double lowForecourt = 3.6 + terrainBlendNoise(seed ^ -3335678366873096957L, localX, localZ) * 0.55;
                           envelopeRelativeY = envelopeRelativeY * (1.0 - approach) + Math.min(envelopeRelativeY, lowForecourt) * approach;
                        }

                        if (!(envelopeRelativeY <= 0.0)) {
                           int desiredY = origin.getY() + (int)Math.floor(envelopeRelativeY);
                           desiredY = Math.max(2, Math.min(253, desiredY));
                           if (desiredY > naturalY) {
                              raiseTerrainColumn(level, x, z, naturalY, desiredY, originalTop, true);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static void sealPortalFoundation(ServerLevel level, BlockPos origin) {
      int foundationTopY = Math.max(2, origin.getY() - 1);

      for (int localX = 0; localX < 9; localX++) {
         for (int localZ = 0; localZ < 40; localZ++) {
            if (isPortalBaseColumn(localX, localZ)) {
               int x = origin.getX() + localX;
               int z = origin.getZ() + localZ;
               int terrainY = findTerrainSurfaceBlockY(level, x, z);
               if (terrainY < 1) {
                  terrainY = Math.max(1, generatedSurfaceBlockY(level, x, z));
               }

               BlockState surface = level.getBlockState(new BlockPos(x, terrainY, z));
               if (terrainY < foundationTopY) {
                  raiseTerrainColumn(level, x, z, terrainY, foundationTopY, surface, false);
               }

               MutableBlockPos cursor = new MutableBlockPos();

               for (int y = foundationTopY; y > terrainY; y--) {
                  cursor.set(x, y, z);
                  BlockState state = level.getBlockState(cursor);
                  if (state.isAir() || isSiftPlant(state)) {
                     setDuringPortalBuild(level, cursor, ModBlocks.SIFTSLATE.defaultBlockState());
                  }
               }
            }
         }
      }
   }

   private static void buildNaturalPortalFoundation(ServerLevel level, BlockPos origin, long seed) {
      int foundationTopY = Math.max(2, origin.getY() - 1);
      int minX = origin.getX() - 15;
      int maxX = origin.getX() + 9 - 1 + 15;
      int minZ = origin.getZ() - 15;
      int maxZ = origin.getZ() + 40 - 1 + 15;

      for (int x = minX; x <= maxX; x++) {
         for (int z = minZ; z <= maxZ; z++) {
            int localX = x - origin.getX();
            int localZ = z - origin.getZ();
            boolean core = isPortalBaseColumn(localX, localZ);
            double broadNoise = terrainBlendNoise(seed ^ 7640891576956012809L, localX * 3 + 11, localZ * 3 - 17);
            double fineNoise = terrainBlendNoise(seed ^ -4942790177534073029L, localX * 9 - 23, localZ * 9 + 31);
            double distance = core ? 0.0 : distanceToPortalBase(localX + broadNoise * 1.45, localZ - broadNoise * 1.15);
            if (!(distance > 15.0)) {
               double erosion = distance * (0.7 + fineNoise * 0.08) + distance * distance * 0.016;
               int targetY = core ? foundationTopY : foundationTopY - Math.max(1, (int)Math.floor(erosion));
               int naturalY = findTerrainSurfaceBlockY(level, x, z);
               if (naturalY > 1 && naturalY < targetY) {
                  BlockState originalTop = level.getBlockState(new BlockPos(x, naturalY, z));
                  if (isSiftTerrain(originalTop)) {
                     boolean preserveBiomeSurface = !core && distance > 2.25;
                     raiseTerrainColumn(level, x, z, naturalY, targetY, originalTop, preserveBiomeSurface);
                  }
               }
            }
         }
      }
   }

   private static boolean isPortalBaseColumn(int localX, int localZ) {
      return localX >= 0 && localX < 9 && localZ >= 0 && localZ < 40 && GROUND_PROFILE[localX][localZ] >= 0;
   }

   private static double distanceToPortalBase(double localX, double localZ) {
      double bestSquared = Double.POSITIVE_INFINITY;

      for (SiftPortalStructure.ProfilePoint point : GROUND_POINTS) {
         double dx = localX - point.x();
         double dz = localZ - point.z();
         bestSquared = Math.min(bestSquared, dx * dx + dz * dz);
      }

      return Math.sqrt(bestSquared);
   }

   private static double portalTerrainEnvelope(int localX, int localZ, long seed) {
      double broadWarp = terrainBlendNoise(seed ^ 1375183133L, localX * 3 + 17, localZ * 3 - 29);
      double fineWarp = terrainBlendNoise(seed ^ 2082071971L, localX * 7 - 41, localZ * 7 + 13);
      double warpedX = localX + broadWarp * 1.65 + fineWarp * 0.35;
      double warpedZ = localZ - broadWarp * 1.1 + fineWarp * 0.45;
      double columnNoise = terrainBlendNoise(seed ^ -6752110988234923001L, localX * 5 + 31, localZ * 5 - 47);
      double erosionPerBlock = 0.405 + columnNoise * 0.022;
      double best = Double.NEGATIVE_INFINITY;

      for (SiftPortalStructure.ProfilePoint point : CLIFF_POINTS) {
         double dx = (warpedX - point.x()) * 0.76;
         double dz = (warpedZ - point.z()) * 1.08;
         double distance = Math.sqrt(dx * dx + dz * dz);
         double candidate = point.height() - distance * erosionPerBlock;
         if (candidate > best) {
            best = candidate;
         }
      }

      best += columnNoise * 0.55;
      return !Double.isFinite(best) ? -1.0 : best;
   }

   private static double portalApproachMask(int localX, int localZ) {
      int distanceZ;
      if (localZ < 9) {
         distanceZ = 9 - localZ;
      } else if (localZ > 30) {
         distanceZ = localZ - 30;
      } else {
         distanceZ = 0;
      }

      int distanceX;
      if (localX < 0) {
         distanceX = -localX;
      } else {
         if (localX < 9) {
            return 0.0;
         }

         distanceX = localX - 8;
      }

      double zMask = 1.0 - smootherstep(distanceZ / 6.0);
      double xMask = 1.0 - smootherstep(Math.max(0.0, distanceX - 2.0) / 18.0);
      return clamp01(zMask * xMask);
   }

   private static void lowerTerrainColumn(ServerLevel level, int x, int z, int naturalY, int desiredY, BlockState originalTop, boolean preserveSurface) {
      MutableBlockPos cursor = new MutableBlockPos();

      for (int y = naturalY; y > desiredY; y--) {
         cursor.set(x, y, z);
         BlockState state = level.getBlockState(cursor);
         if (!isSiftTerrain(state)) {
            break;
         }

         setDuringPortalBuild(level, cursor, Blocks.AIR.defaultBlockState());
      }

      cursor.set(x, desiredY, z);
      BlockState exposed = level.getBlockState(cursor);
      if (!preserveSurface && isSiftTerrain(exposed)) {
         setDuringPortalBuild(level, cursor, ModBlocks.SIFTSLATE.defaultBlockState());
      } else if (exposed.is(ModBlocks.SIFTSLATE)) {
         if (originalTop.is(ModBlocks.SIFTSLATE_GROWTH)) {
            setDuringPortalBuild(level, cursor, ModBlocks.SIFTSLATE_GROWTH.defaultBlockState());
         } else if (originalTop.is(ModBlocks.HEALTHY_SCULK)) {
            setDuringPortalBuild(level, cursor, ModBlocks.HEALTHY_SCULK.defaultBlockState());
         }
      }
   }

   private static void raiseTerrainColumn(ServerLevel level, int x, int z, int naturalY, int desiredY, BlockState originalTop, boolean preserveSurface) {
      MutableBlockPos cursor = new MutableBlockPos();

      for (int y = naturalY + 1; y <= desiredY; y++) {
         cursor.set(x, y, z);
         setDuringPortalBuild(level, cursor, ModBlocks.SIFTSLATE.defaultBlockState());
      }

      cursor.set(x, desiredY, z);
      if (!preserveSurface) {
         setDuringPortalBuild(level, cursor, ModBlocks.SIFTSLATE.defaultBlockState());
      } else if (originalTop.is(ModBlocks.SIFTSLATE_GROWTH)) {
         setDuringPortalBuild(level, cursor, ModBlocks.SIFTSLATE_GROWTH.defaultBlockState());
      } else if (originalTop.is(ModBlocks.HEALTHY_SCULK)) {
         setDuringPortalBuild(level, cursor, ModBlocks.HEALTHY_SCULK.defaultBlockState());
      }
   }

   private static void decoratePortalAndBlend(ServerLevel level, BlockPos origin, BlockPos playerAnchor) {
      RandomSource random = RandomSource.create(level.getSeed() ^ 22601841507586892L);
      int structureMinX = origin.getX();
      int structureMaxX = origin.getX() + 9 - 1;
      int structureMinZ = origin.getZ();
      int structureMaxZ = origin.getZ() + 40 - 1;

      for (int x = structureMinX; x <= structureMaxX; x++) {
         for (int z = structureMinZ; z <= structureMaxZ; z++) {
            for (int y = origin.getY(); y < origin.getY() + 19; y++) {
               BlockPos floor = new BlockPos(x, y, z);
               if (!isPortalApproach(floor, playerAnchor)) {
                  SiftSurfaceDecorator.decorateStructureSurface(level, random, floor, 0.92F, 48);
               }
            }
         }
      }
   }

   private static boolean setDuringPortalBuild(ServerLevel level, BlockPos pos, BlockState state) {
      return level.getBlockState(pos) == state ? false : level.setBlock(pos, state, 48);
   }

   private static SiftPortalStructure.ProfilePoint[] collectProfilePoints(int[][] profile) {
      SiftPortalStructure.ProfilePoint[] temporary = new SiftPortalStructure.ProfilePoint[360];
      int count = 0;

      for (int x = 0; x < 9; x++) {
         for (int z = 0; z < 40; z++) {
            int height = profile[x][z];
            if (height >= 0) {
               temporary[count++] = new SiftPortalStructure.ProfilePoint(x, z, height);
            }
         }
      }

      return Arrays.copyOf(temporary, count);
   }

   private static boolean isPortalApproach(BlockPos pos, BlockPos playerAnchor) {
      int dx = Math.abs(pos.getX() - playerAnchor.getX());
      int dz = Math.abs(pos.getZ() - playerAnchor.getZ());
      return dx <= 4 && dz <= 5;
   }

   private static boolean isSiftTerrain(BlockState state) {
      return state.is(ModBlocks.SIFTSLATE)
         || state.is(ModBlocks.SIFTSLATE_GROWTH)
         || state.is(ModBlocks.HEALTHY_SCULK)
         || state.is(ModBlocks.DRY_HEALTHY_SCULK);
   }

   private static boolean isSiftPlant(BlockState state) {
      return state.is(ModBlocks.OVERGROWN_CHARD)
         || state.is(ModBlocks.OVERGROWN_STALKS)
         || state.is(ModBlocks.OVERGROWN_FRONDS)
         || state.is(ModBlocks.SIFTSLATE_STALKS)
         || state.is(ModBlocks.HEALTHY_SCULK_SPROUTS)
         || state.is(ModBlocks.DRY_HEALTHY_SCULK_SPROUTS)
         || state.is(ModBlocks.OVERGROWN_WILLOW_FOLIAGE)
         || state.is(ModBlocks.OVERGROWN_WILLOW_VINES)
         || state.is(ModBlocks.OVERGROWN_WILLOW_VINES_PLANT)
         || state.is(ModBlocks.OVERGROWN_WILLOW_LOG)
         || state.is(ModBlocks.OVERGROWN_WILLOW_WOOD);
   }

   private static int findTerrainSurfaceBlockY(ServerLevel level, int x, int z) {
      int top = level.getHeight(Types.WORLD_SURFACE, x, z) - 1;
      int minimum = Math.max(level.getMinBuildHeight() + 1, top - 48);
      MutableBlockPos cursor = new MutableBlockPos();

      for (int y = top; y >= minimum; y--) {
         cursor.set(x, y, z);
         if (isSiftTerrain(level.getBlockState(cursor))) {
            return y;
         }
      }

      return -1;
   }

   private static double distanceOutsideRectangle(int x, int z, int minX, int maxX, int minZ, int maxZ) {
      int dx = x < minX ? minX - x : Math.max(0, x - maxX);
      int dz = z < minZ ? minZ - z : Math.max(0, z - maxZ);
      return Math.sqrt((double)dx * dx + (double)dz * dz);
   }

   private static double terrainBlendNoise(long seed, int x, int z) {
      double phase = (seed & 65535L) * 1.73E-4;
      double a = Math.sin(x * 0.083 + z * 0.031 + phase);
      double b = Math.cos(x * 0.037 - z * 0.071 - phase * 0.73);
      double c = Math.sin((x + z) * 0.021 + phase * 1.91);
      return a * 0.5 + b * 0.33 + c * 0.17;
   }

   private static double smootherstep(double value) {
      double t = clamp01(value);
      return t * t * t * (t * (t * 6.0 - 15.0) + 10.0);
   }

   private static double clamp01(double value) {
      return Math.max(0.0, Math.min(1.0, value));
   }

   private static int generatedSurfaceBlockY(ServerLevel level, int x, int z) {
      ServerChunkCache chunkSource = level.getChunkSource();
      int firstFreeY = chunkSource.getGenerator().getBaseHeight(x, z, Types.WORLD_SURFACE_WG, level, chunkSource.randomState());
      return firstFreeY - 1;
   }

   private record PlacementCandidate(int anchorX, int anchorZ, int groundY, double roughness) {
   }

   private record ProfilePoint(int x, int z, int height) {
   }

   private record TerrainSample(int medianHeight, int range, int outerRange, double roughness) {
   }
}
