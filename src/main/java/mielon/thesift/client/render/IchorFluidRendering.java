package mielon.thesift.client.render;
import mielon.thesift.fluid.ModFluids;
import net.minecraft.resources.ResourceLocation;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
public final class IchorFluidRendering { public static void register() {
 FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.ICHOR,ModFluids.FLOWING_ICHOR,new SimpleFluidRenderHandler(ResourceLocation.fromNamespaceAndPath("the_sift","block/ichor_still"),ResourceLocation.fromNamespaceAndPath("the_sift","block/ichor_flow"),ResourceLocation.withDefaultNamespace("block/water_overlay"),0xffffffff));
} }
