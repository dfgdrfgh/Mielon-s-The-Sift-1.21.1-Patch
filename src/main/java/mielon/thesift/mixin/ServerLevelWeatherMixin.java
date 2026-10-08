package mielon.thesift.mixin;

import mielon.thesift.block.ModBlocks;
import mielon.thesift.world.TheSiftDimension;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({ServerLevel.class})
public abstract class ServerLevelWeatherMixin {
   @Redirect(
      method = {"tickPrecipitation"},
      at = @At(
         value = "FIELD",
         target = "Lnet/minecraft/world/level/block/Blocks;SNOW:Lnet/minecraft/world/level/block/Block;"
      )
   )
   private Block theSift$useIchorSnowDuringPrecipitation() {
      ServerLevel self = (ServerLevel)(Object)this;
      return (Block)(self.dimension().equals(TheSiftDimension.LEVEL_KEY) ? ModBlocks.ICHOR_SNOW : Blocks.SNOW);
   }

   @Redirect(
      method = {"tickPrecipitation"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/biome/Biome;shouldSnow(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z"
      )
   )
   private boolean theSift$restrictSnowToIchorSnowyPeaks(Biome biome, LevelReader level, BlockPos pos) {
      ServerLevel self = (ServerLevel)(Object)this;
      if (self.dimension().equals(TheSiftDimension.LEVEL_KEY)) {
         if (!self.getBiome(pos).is(TheSiftDimension.ICHOR_SNOWY_PEAKS)) {
            return false;
         } else {
            BlockState state = level.getBlockState(pos);
            return !level.isOutsideBuildHeight(pos.getY())
               && level.getBrightness(LightLayer.BLOCK, pos) < 10
               && (state.isAir() || state.is(ModBlocks.ICHOR_SNOW))
               && ModBlocks.ICHOR_SNOW.defaultBlockState().canSurvive(level, pos);
         }
      } else {
         return biome.shouldSnow(level, pos);
      }
   }

   @ModifyArg(
      method = {"tickPrecipitation"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"
      ),
      index = 1
   )
   private BlockState theSift$capIchorSnowLayers(BlockState state) {
      ServerLevel self = (ServerLevel)(Object)this;
      return self.dimension().equals(TheSiftDimension.LEVEL_KEY) && state.is(ModBlocks.ICHOR_SNOW) && state.getValue(SnowLayerBlock.LAYERS) > 2
         ? (BlockState)state.setValue(SnowLayerBlock.LAYERS, 2)
         : state;
   }
}
