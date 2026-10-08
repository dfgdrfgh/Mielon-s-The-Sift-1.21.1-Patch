package mielon.thesift.backport.core.mixin.common.access;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
@Mixin(Entity.class)
public interface EntityAccessor {
 @Invoker("calculateViewVector") Vec3 theSift$calculateViewVector(float pitch,float yaw);
}
