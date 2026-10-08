package mielon.thesift.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import mielon.thesift.platform.NeoEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({ServerPlayerGameMode.class})
public abstract class SiftAfterBlockBreakMixin {
   @Shadow
   protected ServerLevel level;
   @Shadow
   @Final
   protected ServerPlayer player;

   @WrapMethod(
      method = {"destroyBlock"}
   )
   private boolean theSift$afterActualBreak(BlockPos pos, Operation<Boolean> original) {
      BlockState state = this.level.getBlockState(pos);
      BlockEntity blockEntity = this.level.getBlockEntity(pos);
      boolean broken = (Boolean)original.call(new Object[]{pos});
      if (broken) {
         NeoEvents.afterBlockBreak(this.level, this.player, pos, state, blockEntity);
      }

      return broken;
   }
}
