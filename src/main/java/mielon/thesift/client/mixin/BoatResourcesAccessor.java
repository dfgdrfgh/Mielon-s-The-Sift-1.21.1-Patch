package mielon.thesift.client.mixin;
import java.util.Map;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(BoatRenderer.class)
public interface BoatResourcesAccessor {
 @Accessor("boatResources") Map<Boat.Type,Pair<ResourceLocation,ListModel<Boat>>> theSift$getBoatResources();
 @Mutable @Accessor("boatResources") void theSift$setBoatResources(Map<Boat.Type,Pair<ResourceLocation,ListModel<Boat>>> resources);
}
