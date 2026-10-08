package mielon.thesift.client.entity;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
public final class WillowBoatRenderer extends BoatRenderer {
 private final ResourceLocation texture;
 public WillowBoatRenderer(Context context,boolean chest){super(context,chest);texture=ResourceLocation.fromNamespaceAndPath("the_sift","textures/entity/"+(chest?"chest_boat":"boat")+"/overgrown_willow.png");var access=(mielon.thesift.client.mixin.BoatResourcesAccessor)(Object)this;var resources=new java.util.EnumMap<Boat.Type,com.mojang.datafixers.util.Pair<ResourceLocation,net.minecraft.client.model.ListModel<Boat>>>(Boat.Type.class);access.theSift$getBoatResources().forEach((type,pair)->resources.put(type,com.mojang.datafixers.util.Pair.of(texture,pair.getSecond())));access.theSift$setBoatResources(resources);}
 public ResourceLocation getTextureLocation(Boat boat){return texture;}
}
