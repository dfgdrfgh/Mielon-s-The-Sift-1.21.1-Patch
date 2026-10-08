package mielon.thesift.backport.common.api.extensions.entity.movement;

import mielon.thesift.backport.core.mixin.common.access.EntityAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public interface MotionAwareEntity {
   default Vec3 theSift$getHeadLookAngle() {
      Entity self = (Entity)(Object)this;
      return ((EntityAccessor)self).theSift$calculateViewVector(self.getXRot(), self.getYHeadRot());
   }

   Vec3 theSift$getKnownSpeed();

   void theSift$computeSpeed();
}
