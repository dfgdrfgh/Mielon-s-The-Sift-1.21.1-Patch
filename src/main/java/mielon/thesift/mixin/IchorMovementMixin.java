package mielon.thesift.mixin;
import mielon.thesift.fluid.IchorRegeneration;
import mielon.thesift.fluid.IchorFluid;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(Entity.class)
public abstract class IchorMovementMixin {
 @Inject(method="baseTick",at=@At("TAIL"))
 private void theSift$ichorMovement(CallbackInfo ci) {Entity entity=(Entity)(Object)this;if(IchorRegeneration.isInIchorFluid(entity))IchorFluid.applyMovementDrag(entity);}
}
