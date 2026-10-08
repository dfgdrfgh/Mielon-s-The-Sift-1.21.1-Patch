package mielon.thesift.mixin;

import mielon.thesift.fluid.IchorState;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({WallBlock.class})
public abstract class IchorWallShapeMixin {
   @ModifyVariable(
      method = {"getShape", "getCollisionShape"},
      at = @At("HEAD"),
      argsOnly = true
   )
   private BlockState theSift$wallGeometry(BlockState state) {
      return IchorState.dryDefault(state);
   }
}
