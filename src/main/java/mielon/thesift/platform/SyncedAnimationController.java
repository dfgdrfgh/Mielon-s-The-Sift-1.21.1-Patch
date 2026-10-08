package mielon.thesift.platform;

import java.util.function.DoubleSupplier;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationController.AnimationStateHandler;

public final class SyncedAnimationController<T extends GeoAnimatable> extends AnimationController<T> {
   private final DoubleSupplier sequenceTick;

   public SyncedAnimationController(T entity, String name, int transition, DoubleSupplier sequenceTick, AnimationStateHandler<T> handler) {
      super(entity, name, transition, handler);
      this.sequenceTick = sequenceTick;
   }

   protected double adjustTick(double tick) {
      boolean resetting = this.shouldResetTick;
      double normal = super.adjustTick(tick);
      double target = this.sequenceTick.getAsDouble();
      return !resetting && !(target < 0.0) && this.currentAnimation != null
         ? Math.min(target + (tick - Math.floor(tick)), Math.max(0.0, this.currentAnimation.animation().length() - 0.001))
         : normal;
   }
}
