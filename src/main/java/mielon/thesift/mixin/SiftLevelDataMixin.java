package mielon.thesift.mixin;

import mielon.thesift.world.SiftLevelState;
import mielon.thesift.world.TheSiftDimension;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ServerLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ServerLevel.class})
public abstract class SiftLevelDataMixin {
   @Shadow
   @Final
   @Mutable
   private ServerLevelData serverLevelData;
   @Unique
   private SiftLevelState theSift$state;

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   private void theSift$loadDimensionData(CallbackInfo ci) {
      ServerLevel self = (ServerLevel)(Object)this;
      if (!self.dimension().equals(Level.OVERWORLD)) {
         this.theSift$state = (SiftLevelState)self.getDataStorage().computeIfAbsent(SiftLevelState.FACTORY, "the_sift_level_state");
         this.serverLevelData = new SiftLevelState.LevelData(
            self.getServer().getWorldData(), this.serverLevelData, this.theSift$state, self.dimension().equals(TheSiftDimension.LEVEL_KEY)
         );
         ((LevelDataAccess)self).theSift$setLevelData(this.serverLevelData);
         self.setRainLevel(this.serverLevelData.isRaining() ? 1.0F : 0.0F);
         self.setThunderLevel(this.serverLevelData.isThundering() ? 1.0F : 0.0F);
      }
   }

   @Inject(
      method = {"tickTime"},
      at = {@At("TAIL")}
   )
   private void theSift$tickIndependentClock(CallbackInfo ci) {
      ServerLevel self = (ServerLevel)(Object)this;
      if (this.theSift$state != null && self.dimension().equals(TheSiftDimension.LEVEL_KEY)) {
         this.theSift$state.tick(self.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT));
      }
   }
}
