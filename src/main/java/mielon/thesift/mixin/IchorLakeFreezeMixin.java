package mielon.thesift.mixin;

import mielon.thesift.fluid.ModFluids;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({LakeFeature.class})
public abstract class IchorLakeFreezeMixin {
   @Redirect(
      method = {"place"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z"
      )
   )
   private boolean theSift$neverFreezeIchor(FluidState fluid, TagKey<Fluid> tag) {
      return !fluid.getType().isSame(ModFluids.ICHOR) && fluid.is(tag);
   }
}
