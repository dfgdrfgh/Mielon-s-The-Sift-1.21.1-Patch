package mielon.thesift.platform;

import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;

public final class SiftGroundNavigation extends GroundPathNavigation {
   public SiftGroundNavigation(Mob mob, Level level) {
      super(mob, level);
   }

   public Path createPath(BlockPos target, int accuracy) {
      return super.createPath(Set.of(target), 8, false, accuracy);
   }
}
