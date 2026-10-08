package mielon.thesift.mixin;

import java.util.List;
import java.util.Set;
import mielon.thesift.world.RiftMobAvoidance;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PathFinder.class})
public abstract class RiftPathfindingMixin {
   @Unique
   private List<AABB> theSift$riftDangerZones = List.of();
   @Unique
   private double theSift$nodeCenterOffset;

   @Inject(
      method = {"findPath(Lnet/minecraft/world/level/PathNavigationRegion;Lnet/minecraft/world/entity/Mob;Ljava/util/Set;FIF)Lnet/minecraft/world/level/pathfinder/Path;"},
      at = {@At("HEAD")}
   )
   private void theSift$prepareRiftAvoidance(
      PathNavigationRegion region, Mob mob, Set<BlockPos> targets, float range, int accuracy, float nodeLimit, CallbackInfoReturnable<Path> cir
   ) {
      this.theSift$riftDangerZones = RiftMobAvoidance.dangerZones(mob, range);
      this.theSift$nodeCenterOffset = (int)(mob.getBbWidth() + 1.0F) * 0.5;
   }

   @Redirect(
      method = {"findPath(Lnet/minecraft/util/profiling/ProfilerFiller;Lnet/minecraft/world/level/pathfinder/Node;Ljava/util/Map;FIF)Lnet/minecraft/world/level/pathfinder/Path;"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/pathfinder/NodeEvaluator;getNeighbors([Lnet/minecraft/world/level/pathfinder/Node;Lnet/minecraft/world/level/pathfinder/Node;)I"
      )
   )
   private int theSift$preferPathsAroundRifts(NodeEvaluator evaluator, Node[] neighbors, Node current) {
      int count = evaluator.getNeighbors(neighbors, current);
      if (this.theSift$riftDangerZones.isEmpty()) {
         return count;
      } else {
         for (int index = 0; index < count; index++) {
            Node node = neighbors[index];

            for (AABB zone : this.theSift$riftDangerZones) {
               if (zone.contains(node.x + this.theSift$nodeCenterOffset, node.y, node.z + this.theSift$nodeCenterOffset)) {
                  node.costMalus = Math.max(node.costMalus, 8.0F);
                  break;
               }
            }
         }

         return count;
      }
   }

   @Inject(
      method = {"findPath(Lnet/minecraft/world/level/PathNavigationRegion;Lnet/minecraft/world/entity/Mob;Ljava/util/Set;FIF)Lnet/minecraft/world/level/pathfinder/Path;"},
      at = {@At("RETURN")}
   )
   private void theSift$clearRiftAvoidance(CallbackInfoReturnable<Path> cir) {
      this.theSift$riftDangerZones = List.of();
   }
}
