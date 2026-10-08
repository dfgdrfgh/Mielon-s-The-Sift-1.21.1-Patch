package mielon.thesift.backport.core.util;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;


public class Utilities {
   public static class BufferUtils {
      public static <L, R> void writeEither(
         FriendlyByteBuf buf, Either<L, R> value, StreamEncoder<? super FriendlyByteBuf, L> leftWriter, StreamEncoder<? super FriendlyByteBuf, R> rightWriter
      ) {
         value.ifLeft(l -> {
            buf.writeBoolean(true);
            leftWriter.encode(buf, l);
         }).ifRight(r -> {
            buf.writeBoolean(false);
            rightWriter.encode(buf, r);
         });
      }

      public static <L, R> Either<L, R> readEither(
         FriendlyByteBuf buf, StreamDecoder<? super FriendlyByteBuf, L> leftReader, StreamDecoder<? super FriendlyByteBuf, R> rightReader
      ) {
         return buf.readBoolean() ? Either.left(leftReader.decode(buf)) : Either.right(rightReader.decode(buf));
      }
   }

   public static class CollisionUtils {
      public static Vec3 getMinPosition(AABB aabb) {
         return new Vec3(aabb.minX, aabb.minY, aabb.minZ);
      }

      public static Vec3 getMaxPosition(AABB aabb) {
         return new Vec3(aabb.maxX, aabb.maxY, aabb.maxZ);
      }

      public static AABB nextDeflated(AABB box) {
         return new AABB(
            Math.nextUp(box.minX), Math.nextUp(box.minY), Math.nextUp(box.minZ), Math.nextDown(box.maxX), Math.nextDown(box.maxY), Math.nextDown(box.maxZ)
         );
      }

      public static boolean intersects(AABB box, BlockPos pos) {
         return box.intersects(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
      }

      public static boolean collidedWithFluid(Entity entity, FluidState state, BlockPos pos, Vec3 origin, Vec3 target) {
         AABB box = getFluidAABB(state, entity.level(), pos);
         return box != null && collidedWithShapeMovingFrom(entity, origin, target, List.of(box));
      }

      public static boolean collidedWithShapeMovingFrom(Entity entity, Vec3 origin, Vec3 target, List<AABB> boxes) {
         AABB box = entity.getDimensions(entity.getPose()).makeBoundingBox(origin);
         Vec3 distance = target.subtract(origin);
         return collidedAlongVector(box, distance, boxes);
      }

      
      public static AABB getFluidAABB(FluidState state, BlockGetter level, BlockPos pos) {
         if (state.isEmpty()) {
            return null;
         } else {
            float fluidHeight = state.getHeight(level, pos);
            return new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0, pos.getY() + fluidHeight, pos.getZ() + 1.0);
         }
      }

      public static boolean collidedAlongVector(AABB box, Vec3 origin, List<AABB> obstacles) {
         Vec3 center = box.getCenter();
         Vec3 distance = center.add(origin);

         for (AABB obstacle : obstacles) {
            AABB inflated = obstacle.inflate(box.getXsize() * 0.5 - 1.0E-7, box.getYsize() * 0.5 - 1.0E-7, box.getZsize() * 0.5 - 1.0E-7);
            if (inflated.contains(distance) || inflated.contains(center)) {
               return true;
            }

            if (inflated.clip(center, distance).isPresent()) {
               return true;
            }
         }

         return false;
      }

      public static CollisionContext positionContext(double y) {
         return new Utilities.CollisionUtils.PositionCollisionContext(y);
      }

      private static class PositionCollisionContext extends EntityCollisionContext {
         private final double y;

         private PositionCollisionContext(double y) {
            super(false, -Double.MAX_VALUE, ItemStack.EMPTY, fluidState -> false, null);
            this.y = y;
         }

         public boolean isDescending() {
            return false;
         }

         public boolean isAbove(VoxelShape shape, BlockPos pos, boolean canAscend) {
            return this.y > pos.getY() + shape.max(Axis.Y) - 1.0E-5F;
         }

         public boolean isHoldingItem(Item item) {
            return false;
         }

         public boolean canStandOnFluid(FluidState fluid1, FluidState fluid2) {
            return false;
         }
      }
   }

   public static class ColorUtils {
      public static int srgbLerp(float alpha, int p0, int p1) {
         int a = Mth.lerpInt(alpha, ARGB32.alpha(p0), ARGB32.alpha(p1));
         int red = Mth.lerpInt(alpha, ARGB32.red(p0), ARGB32.red(p1));
         int green = Mth.lerpInt(alpha, ARGB32.green(p0), ARGB32.green(p1));
         int blue = Mth.lerpInt(alpha, ARGB32.blue(p0), ARGB32.blue(p1));
         return ARGB32.color(a, red, green, blue);
      }

      public static int scaleRGB(int color, float red, float green, float blue) {
         return ARGB32.color(
            ARGB32.alpha(color),
            Mth.clamp((int)(ARGB32.red(color) * red), 0, 255),
            Mth.clamp((int)(ARGB32.green(color) * green), 0, 255),
            Mth.clamp((int)(ARGB32.blue(color) * blue), 0, 255)
         );
      }

      public static int colorFromFloat(float alpha, float red, float green, float blue) {
         return ARGB32.color((int)(alpha * 255.0F), (int)(red * 255.0F), (int)(green * 255.0F), (int)(blue * 255.0F));
      }

      public static int setBrightness(int color, float brightness) {
         int red = ARGB32.red(color);
         int green = ARGB32.green(color);
         int blue = ARGB32.blue(color);
         int alpha = ARGB32.alpha(color);
         int rgbMax = Math.max(Math.max(red, green), blue);
         int rgbMin = Math.min(Math.min(red, green), blue);
         float rgbConstantRange = rgbMax - rgbMin;
         float saturation;
         if (rgbMax != 0) {
            saturation = rgbConstantRange / rgbMax;
         } else {
            saturation = 0.0F;
         }

         float hue;
         if (saturation == 0.0F) {
            hue = 0.0F;
         } else {
            float constantRed = (rgbMax - red) / rgbConstantRange;
            float constantGreen = (rgbMax - green) / rgbConstantRange;
            float constantBlue = (rgbMax - blue) / rgbConstantRange;
            if (red == rgbMax) {
               hue = constantBlue - constantGreen;
            } else if (green == rgbMax) {
               hue = 2.0F + constantRed - constantBlue;
            } else {
               hue = 4.0F + constantGreen - constantRed;
            }

            hue /= 6.0F;
            if (hue < 0.0F) {
               hue++;
            }
         }

         if (saturation == 0.0F) {
            red = green = blue = Math.round(brightness * 255.0F);
         } else {
            float colorWheelSegment = (hue - (float)Math.floor(hue)) * 6.0F;
            float colorWheelOffset = colorWheelSegment - (float)Math.floor(colorWheelSegment);
            float primaryColor = brightness * (1.0F - saturation);
            float secondaryColor = brightness * (1.0F - saturation * colorWheelOffset);
            float tertiaryColor = brightness * (1.0F - saturation * (1.0F - colorWheelOffset));
            switch ((int)colorWheelSegment) {
               case 0:
                  red = Math.round(brightness * 255.0F);
                  green = Math.round(tertiaryColor * 255.0F);
                  blue = Math.round(primaryColor * 255.0F);
                  break;
               case 1:
                  red = Math.round(secondaryColor * 255.0F);
                  green = Math.round(brightness * 255.0F);
                  blue = Math.round(primaryColor * 255.0F);
                  break;
               case 2:
                  red = Math.round(primaryColor * 255.0F);
                  green = Math.round(brightness * 255.0F);
                  blue = Math.round(tertiaryColor * 255.0F);
                  break;
               case 3:
                  red = Math.round(primaryColor * 255.0F);
                  green = Math.round(secondaryColor * 255.0F);
                  blue = Math.round(brightness * 255.0F);
                  break;
               case 4:
                  red = Math.round(tertiaryColor * 255.0F);
                  green = Math.round(primaryColor * 255.0F);
                  blue = Math.round(brightness * 255.0F);
                  break;
               case 5:
                  red = Math.round(brightness * 255.0F);
                  green = Math.round(primaryColor * 255.0F);
                  blue = Math.round(secondaryColor * 255.0F);
            }
         }

         return ARGB32.color(alpha, red, green, blue);
      }

      public static DyeColor getMixedColor(ServerLevel level, DyeColor colorA, DyeColor colorB) {
         CraftingInput container = makeCraftColorInput(colorA, colorB);
         return level.getRecipeManager()
            .getRecipeFor(RecipeType.CRAFTING, container, level)
            .map(recipe -> ((CraftingRecipe)recipe.value()).assemble(container, level.registryAccess()))
            .<Item>map(ItemStack::getItem)
            .filter(DyeItem.class::isInstance)
            .map(DyeItem.class::cast)
            .<DyeColor>map(DyeItem::getDyeColor)
            .orElseGet(() -> level.random.nextBoolean() ? colorA : colorB);
      }

      private static CraftingInput makeCraftColorInput(DyeColor colorA, DyeColor colorB) {
         return CraftingInput.of(2, 1, List.of(new ItemStack(DyeItem.byColor(colorA)), new ItemStack(DyeItem.byColor(colorB))));
      }
   }

   public static class DirectionUtils {
      private static final ImmutableList<Axis> YXZ_AXIS_ORDER = ImmutableList.of(Axis.Y, Axis.X, Axis.Z);
      private static final ImmutableList<Axis> YZX_AXIS_ORDER = ImmutableList.of(Axis.Y, Axis.Z, Axis.X);

      public static ImmutableList<Axis> axisStepOrder(Vec3 movement) {
         return Math.abs(movement.x) < Math.abs(movement.z) ? YZX_AXIS_ORDER : YXZ_AXIS_ORDER;
      }

      public static Direction getPositive(Axis axis) {
         return switch (axis) {
            case X -> Direction.EAST;
            case Y -> Direction.UP;
            case Z -> Direction.SOUTH;
            default -> throw new MatchException(null, null);
         };
      }

      public static Direction getNegative(Axis axis) {
         return switch (axis) {
            case X -> Direction.WEST;
            case Y -> Direction.DOWN;
            case Z -> Direction.NORTH;
            default -> throw new MatchException(null, null);
         };
      }

      public static Direction[] getDirections(Axis axis) {
         return new Direction[]{getPositive(axis), getNegative(axis)};
      }

      public static Direction getApproximateNearest(double dx, double dy, double dz) {
         return getApproximateNearest((float)dx, (float)dy, (float)dz);
      }

      public static Direction getApproximateNearest(float dx, float dy, float dz) {
         Direction result = Direction.NORTH;
         float highestDot = Float.MIN_VALUE;

         for (Direction direction : Direction.values()) {
            float dot = dx * direction.getNormal().getX() + dy * direction.getNormal().getY() + dz * direction.getNormal().getZ();
            if (dot > highestDot) {
               highestDot = dot;
               result = direction;
            }
         }

         return result;
      }

      public static Direction getApproximateNearest(Vec3 vec) {
         return getApproximateNearest(vec.x, vec.y, vec.z);
      }
   }

   public static class MthUtils {
      public static final double EPSILON = 1.0E-7;

      public static float wrapDegrees90(float angle) {
         float normalizedAngle = angle % 90.0F;
         if (normalizedAngle >= 45.0F) {
            normalizedAngle -= 90.0F;
         }

         if (normalizedAngle < -45.0F) {
            normalizedAngle += 90.0F;
         }

         return normalizedAngle;
      }

      public static byte packDegrees(float angle) {
         return (byte)Mth.floor(angle * 256.0F / 360.0F);
      }

      public static float unpackDegrees(byte rot) {
         return rot * 360 / 256.0F;
      }

      public static Vec3 lerp(double alpha, Vec3 p1, Vec3 p2) {
         return new Vec3(Mth.lerp(alpha, p1.x, p2.x), Mth.lerp(alpha, p1.y, p2.y), Mth.lerp(alpha, p1.z, p2.z));
      }

      public static float cube(float x) {
         return x * x * x;
      }
   }

   public static class PositionUtils {
      public static Iterable<BlockPos> betweenClosed(AABB box) {
         BlockPos min = BlockPos.containing(box.minX, box.minY, box.minZ);
         BlockPos max = BlockPos.containing(box.maxX, box.maxY, box.maxZ);
         return BlockPos.betweenClosed(min, max);
      }

      public static boolean forEachBlockIntersectedBetween(Vec3 from, Vec3 to, AABB aabbAtTarget, Utilities.PositionUtils.BlockStepVisitor visitor) {
         Vec3 travel = to.subtract(from);
         if (travel.lengthSqr() < Mth.square(1.0E-5F)) {
            for (BlockPos pos : betweenClosed(aabbAtTarget)) {
               if (!visitor.visit(pos, 0)) {
                  return false;
               }
            }

            return true;
         } else {
            LongSet visitedBlocks = new LongOpenHashSet();

            for (BlockPos posx : betweenCornersInDirection(aabbAtTarget.move(travel.scale(-1.0)), travel)) {
               if (!visitor.visit(posx, 0)) {
                  return false;
               }

               visitedBlocks.add(posx.asLong());
            }

            int iterations = addCollisionsAlongTravel(visitedBlocks, travel, aabbAtTarget, visitor);
            if (iterations < 0) {
               return false;
            } else {
               for (BlockPos posx : betweenCornersInDirection(aabbAtTarget, travel)) {
                  if (!visitedBlocks.contains(posx.asLong()) && !visitor.visit(posx, iterations + 1)) {
                     return false;
                  }
               }

               return true;
            }
         }
      }

      public static Iterable<BlockPos> betweenCornersInDirection(AABB aabb, Vec3 direction) {
         Vec3 minCorner = Utilities.CollisionUtils.getMinPosition(aabb);
         int firstCornerX = Mth.floor(minCorner.x());
         int firstCornerY = Mth.floor(minCorner.y());
         int firstCornerZ = Mth.floor(minCorner.z());
         Vec3 maxCorner = Utilities.CollisionUtils.getMaxPosition(aabb);
         int secondCornerX = Mth.floor(maxCorner.x());
         int secondCornerY = Mth.floor(maxCorner.y());
         int secondCornerZ = Mth.floor(maxCorner.z());
         return betweenCornersInDirection(firstCornerX, firstCornerY, firstCornerZ, secondCornerX, secondCornerY, secondCornerZ, direction);
      }

      public static Iterable<BlockPos> betweenCornersInDirection(
         int firstCornerX, int firstCornerY, int firstCornerZ, int secondCornerX, int secondCornerY, int secondCornerZ, Vec3 direction
      ) {
         int minCornerX = Math.min(firstCornerX, secondCornerX);
         int minCornerY = Math.min(firstCornerY, secondCornerY);
         int minCornerZ = Math.min(firstCornerZ, secondCornerZ);
         int maxCornerX = Math.max(firstCornerX, secondCornerX);
         int maxCornerY = Math.max(firstCornerY, secondCornerY);
         int maxCornerZ = Math.max(firstCornerZ, secondCornerZ);
         int diffX = maxCornerX - minCornerX;
         int diffY = maxCornerY - minCornerY;
         int diffZ = maxCornerZ - minCornerZ;
         int startCornerX = direction.x >= 0.0 ? minCornerX : maxCornerX;
         int startCornerY = direction.y >= 0.0 ? minCornerY : maxCornerY;
         int startCornerZ = direction.z >= 0.0 ? minCornerZ : maxCornerZ;
         List<Axis> axes = Utilities.DirectionUtils.axisStepOrder(direction);
         Axis firstVisitAxis = axes.get(0);
         Axis secondVisitAxis = axes.get(1);
         Axis thirdVisitAxis = axes.get(2);
         Direction firstVisitDir = direction.get(firstVisitAxis) >= 0.0
            ? Utilities.DirectionUtils.getPositive(firstVisitAxis)
            : Utilities.DirectionUtils.getNegative(firstVisitAxis);
         Direction secondVisitDir = direction.get(secondVisitAxis) >= 0.0
            ? Utilities.DirectionUtils.getPositive(secondVisitAxis)
            : Utilities.DirectionUtils.getNegative(secondVisitAxis);
         Direction thirdVisitDir = direction.get(thirdVisitAxis) >= 0.0
            ? Utilities.DirectionUtils.getPositive(thirdVisitAxis)
            : Utilities.DirectionUtils.getNegative(thirdVisitAxis);
         int firstMax = firstVisitAxis.choose(diffX, diffY, diffZ);
         int secondMax = secondVisitAxis.choose(diffX, diffY, diffZ);
         int thirdMax = thirdVisitAxis.choose(diffX, diffY, diffZ);
         return () -> new AbstractIterator<BlockPos>() {
            private final MutableBlockPos cursor = new MutableBlockPos();
            private int firstIndex;
            private int secondIndex;
            private int thirdIndex;
            private boolean end;
            private final int firstDirX = firstVisitDir.getStepX();
            private final int firstDirY = firstVisitDir.getStepY();
            private final int firstDirZ = firstVisitDir.getStepZ();
            private final int secondDirX = secondVisitDir.getStepX();
            private final int secondDirY = secondVisitDir.getStepY();
            private final int secondDirZ = secondVisitDir.getStepZ();
            private final int thirdDirX = thirdVisitDir.getStepX();
            private final int thirdDirY = thirdVisitDir.getStepY();
            private final int thirdDirZ = thirdVisitDir.getStepZ();

            protected BlockPos computeNext() {
               if (this.end) {
                  return (BlockPos)(Object)this.endOfData();
               } else {
                  this.cursor
                     .set(
                        startCornerX + this.firstDirX * this.firstIndex + this.secondDirX * this.secondIndex + this.thirdDirX * this.thirdIndex,
                        startCornerY + this.firstDirY * this.firstIndex + this.secondDirY * this.secondIndex + this.thirdDirY * this.thirdIndex,
                        startCornerZ + this.firstDirZ * this.firstIndex + this.secondDirZ * this.secondIndex + this.thirdDirZ * this.thirdIndex
                     );
                  if (this.thirdIndex < thirdMax) {
                     this.thirdIndex++;
                  } else if (this.secondIndex < secondMax) {
                     this.secondIndex++;
                     this.thirdIndex = 0;
                  } else if (this.firstIndex < firstMax) {
                     this.firstIndex++;
                     this.thirdIndex = 0;
                     this.secondIndex = 0;
                  } else {
                     this.end = true;
                  }

                  return this.cursor;
               }
            }
         };
      }

      private static int addCollisionsAlongTravel(LongSet visitedBlocks, Vec3 deltaMove, AABB box, Utilities.PositionUtils.BlockStepVisitor visitor) {
         double boxSizeX = box.getXsize();
         double boxSizeY = box.getYsize();
         double boxSizeZ = box.getZsize();
         Vec3i cornerDir = getFurthestCorner(deltaMove);
         Vec3 toCenter = box.getCenter();
         Vec3 toCorner = new Vec3(
            toCenter.x() + boxSizeX * 0.5 * cornerDir.getX(),
            toCenter.y() + boxSizeY * 0.5 * cornerDir.getY(),
            toCenter.z() + boxSizeZ * 0.5 * cornerDir.getZ()
         );
         Vec3 fromCorner = toCorner.subtract(deltaMove);
         int cornerVisitedBlockX = Mth.floor(fromCorner.x);
         int cornerVisitedBlockY = Mth.floor(fromCorner.y);
         int cornerVisitedBlockZ = Mth.floor(fromCorner.z);
         int signX = Mth.sign(deltaMove.x);
         int signY = Mth.sign(deltaMove.y);
         int signZ = Mth.sign(deltaMove.z);
         double tDeltaX = signX == 0 ? Double.MAX_VALUE : signX / deltaMove.x;
         double tDeltaY = signY == 0 ? Double.MAX_VALUE : signY / deltaMove.y;
         double tDeltaZ = signZ == 0 ? Double.MAX_VALUE : signZ / deltaMove.z;
         double tX = tDeltaX * (signX > 0 ? 1.0 - Mth.frac(fromCorner.x) : Mth.frac(fromCorner.x));
         double tY = tDeltaY * (signY > 0 ? 1.0 - Mth.frac(fromCorner.y) : Mth.frac(fromCorner.y));
         double tZ = tDeltaZ * (signZ > 0 ? 1.0 - Mth.frac(fromCorner.z) : Mth.frac(fromCorner.z));
         int iterations = 0;

         while (tX <= 1.0 || tY <= 1.0 || tZ <= 1.0) {
            if (tX < tY) {
               if (tX < tZ) {
                  cornerVisitedBlockX += signX;
                  tX += tDeltaX;
               } else {
                  cornerVisitedBlockZ += signZ;
                  tZ += tDeltaZ;
               }
            } else if (tY < tZ) {
               cornerVisitedBlockY += signY;
               tY += tDeltaY;
            } else {
               cornerVisitedBlockZ += signZ;
               tZ += tDeltaZ;
            }

            Optional<Vec3> hitPointOpt = clip(
               cornerVisitedBlockX,
               cornerVisitedBlockY,
               cornerVisitedBlockZ,
               cornerVisitedBlockX + 1,
               cornerVisitedBlockY + 1,
               cornerVisitedBlockZ + 1,
               fromCorner,
               toCorner
            );
            if (hitPointOpt.isPresent()) {
               iterations++;
               Vec3 hitPoint = hitPointOpt.get();
               double cornerHitX = Mth.clamp(hitPoint.x, cornerVisitedBlockX + 1.0E-5F, cornerVisitedBlockX + 1.0 - 1.0E-5F);
               double cornerHitY = Mth.clamp(hitPoint.y, cornerVisitedBlockY + 1.0E-5F, cornerVisitedBlockY + 1.0 - 1.0E-5F);
               double cornerHitZ = Mth.clamp(hitPoint.z, cornerVisitedBlockZ + 1.0E-5F, cornerVisitedBlockZ + 1.0 - 1.0E-5F);
               int oppositeCornerX = Mth.floor(cornerHitX - boxSizeX * cornerDir.getX());
               int oppositeCornerY = Mth.floor(cornerHitY - boxSizeY * cornerDir.getY());
               int oppositeCornerZ = Mth.floor(cornerHitZ - boxSizeZ * cornerDir.getZ());
               int currentIteration = iterations;

               for (BlockPos pos : betweenCornersInDirection(
                  cornerVisitedBlockX, cornerVisitedBlockY, cornerVisitedBlockZ, oppositeCornerX, oppositeCornerY, oppositeCornerZ, deltaMove
               )) {
                  if (visitedBlocks.add(pos.asLong()) && !visitor.visit(pos, currentIteration)) {
                     return -1;
                  }
               }
            }
         }

         return iterations;
      }

      public static Optional<Vec3> clip(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Vec3 start, Vec3 end) {
         double[] distances = new double[]{1.0};
         double dirX = end.x - start.x;
         double dirY = end.y - start.y;
         double dirZ = end.z - start.z;
         Direction direction = getDirection(minX, minY, minZ, maxX, maxY, maxZ, start, distances, null, dirX, dirY, dirZ);
         if (direction == null) {
            return Optional.empty();
         } else {
            double distance = distances[0];
            return Optional.of(start.add(distance * dirX, distance * dirY, distance * dirZ));
         }
      }

      
      private static Direction getDirection(
         double minX,
         double minY,
         double minZ,
         double maxX,
         double maxY,
         double maxZ,
         Vec3 start,
         double[] minDistance,
          Direction prevDirection,
         double dirX,
         double dirY,
         double dirZ
      ) {
         if (dirX > 1.0E-7) {
            prevDirection = clipPoint(minDistance, prevDirection, dirX, dirY, dirZ, minX, minY, maxY, minZ, maxZ, Direction.WEST, start.x, start.y, start.z);
         } else if (dirX < -1.0E-7) {
            prevDirection = clipPoint(minDistance, prevDirection, dirX, dirY, dirZ, maxX, minY, maxY, minZ, maxZ, Direction.EAST, start.x, start.y, start.z);
         }

         if (dirY > 1.0E-7) {
            prevDirection = clipPoint(minDistance, prevDirection, dirY, dirZ, dirX, minY, minZ, maxZ, minX, maxX, Direction.DOWN, start.y, start.z, start.x);
         } else if (dirY < -1.0E-7) {
            prevDirection = clipPoint(minDistance, prevDirection, dirY, dirZ, dirX, maxY, minZ, maxZ, minX, maxX, Direction.UP, start.y, start.z, start.x);
         }

         if (dirZ > 1.0E-7) {
            prevDirection = clipPoint(minDistance, prevDirection, dirZ, dirX, dirY, minZ, minX, maxX, minY, maxY, Direction.NORTH, start.z, start.x, start.y);
         } else if (dirZ < -1.0E-7) {
            prevDirection = clipPoint(minDistance, prevDirection, dirZ, dirX, dirY, maxZ, minX, maxX, minY, maxY, Direction.SOUTH, start.z, start.x, start.y);
         }

         return prevDirection;
      }

      
      private static Direction clipPoint(
         double[] minDistance,
          Direction prevDirection,
         double mainAxisDir,
         double secondAxisDir,
         double thirdAxisDir,
         double facePosition,
         double secondAxisMin,
         double secondAxisMax,
         double thirdAxisMin,
         double thirdAxisMax,
         Direction hitDirection,
         double startMain,
         double startSecond,
         double startThird
      ) {
         double intersectionTime = (facePosition - startMain) / mainAxisDir;
         double secondAxisPos = startSecond + intersectionTime * secondAxisDir;
         double thirdAxisPos = startThird + intersectionTime * thirdAxisDir;
         if (0.0 < intersectionTime
            && intersectionTime < minDistance[0]
            && secondAxisMin - 1.0E-7 < secondAxisPos
            && secondAxisPos < secondAxisMax + 1.0E-7
            && thirdAxisMin - 1.0E-7 < thirdAxisPos
            && thirdAxisPos < thirdAxisMax + 1.0E-7) {
            minDistance[0] = intersectionTime;
            return hitDirection;
         } else {
            return prevDirection;
         }
      }

      private static Vec3i getFurthestCorner(Vec3 direction) {
         double xDot = Math.abs(Utilities.VectorUtils.X_AXIS.dot(direction));
         double yDot = Math.abs(Utilities.VectorUtils.Y_AXIS.dot(direction));
         double zDot = Math.abs(Utilities.VectorUtils.Z_AXIS.dot(direction));
         int xSign = direction.x >= 0.0 ? 1 : -1;
         int ySign = direction.y >= 0.0 ? 1 : -1;
         int zSign = direction.z >= 0.0 ? 1 : -1;
         if (xDot <= yDot && xDot <= zDot) {
            return new Vec3i(-xSign, -zSign, ySign);
         } else {
            return yDot <= zDot ? new Vec3i(zSign, -ySign, -xSign) : new Vec3i(-ySign, xSign, -zSign);
         }
      }

      @FunctionalInterface
      public interface BlockStepVisitor {
         boolean visit(BlockPos blockPos, int i);
      }
   }

   public static class VectorUtils {
      public static final Vec3 X_AXIS = new Vec3(1.0, 0.0, 0.0);
      public static final Vec3 Y_AXIS = new Vec3(0.0, 1.0, 0.0);
      public static final Vec3 Z_AXIS = new Vec3(0.0, 0.0, 1.0);

      public static Vec3 create(Vec3i vec) {
         return new Vec3(vec.getX(), vec.getY(), vec.getZ());
      }

      public static Vec3 horizontal(Vec3 source) {
         return new Vec3(source.x, 0.0, source.z);
      }

      public static Vec2 rotate(Vec2 source, float angleRadians) {
         float cosine = Mth.cos(angleRadians);
         float sine = Mth.sin(angleRadians);
         return new Vec2(source.x * cosine - source.y * sine, source.y * cosine + source.x * sine);
      }

      public static Vec3 applyLocalCoordinatesToRotation(Vec2 rotation, Vec3 direction) {
         float yCos = Mth.cos((rotation.y + 90.0F) * (float) (Math.PI / 180.0));
         float ySin = Mth.sin((rotation.y + 90.0F) * (float) (Math.PI / 180.0));
         float xCos = Mth.cos(-rotation.x * (float) (Math.PI / 180.0));
         float xSin = Mth.sin(-rotation.x * (float) (Math.PI / 180.0));
         float xCosUp = Mth.cos((-rotation.x + 90.0F) * (float) (Math.PI / 180.0));
         float xSinUp = Mth.sin((-rotation.x + 90.0F) * (float) (Math.PI / 180.0));
         Vec3 forwards = new Vec3(yCos * xCos, xSin, ySin * xCos);
         Vec3 up = new Vec3(yCos * xCosUp, xSinUp, ySin * xCosUp);
         Vec3 left = forwards.cross(up).scale(-1.0);
         double xa = forwards.x * direction.z + up.x * direction.y + left.x * direction.x;
         double ya = forwards.y * direction.z + up.y * direction.y + left.y * direction.x;
         double za = forwards.z * direction.z + up.z * direction.y + left.z * direction.x;
         return new Vec3(xa, ya, za);
      }

      public static Vec3 addLocalCoordinates(Vec3 source, Vec3 direction) {
         return applyLocalCoordinatesToRotation(rotation(source), direction);
      }

      public static Vec2 rotation(Vec3 source) {
         float yaw = (float)Math.atan2(-source.x, source.z) * (180.0F / (float)Math.PI);
         float pitch = (float)Math.asin(-source.y / Math.sqrt(source.x * source.x + source.y * source.y + source.z * source.z)) * (180.0F / (float)Math.PI);
         return new Vec2(pitch, yaw);
      }

      public static Vec3 rotateClockwise90(Vec3 source) {
         return new Vec3(-source.z, source.y, source.x);
      }
   }
}
