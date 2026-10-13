/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.DimensionSpecialEffects
 *  net.minecraft.client.renderer.ItemBlockRenderTypes
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.entity.ThrownItemRenderer
 *  net.minecraft.client.renderer.item.ItemProperties
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.SpawnEggItem
 *  net.minecraft.world.item.component.BlockItemStateProperties
 *  net.minecraft.world.item.component.CustomModelData
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.BushBlock
 *  net.minecraft.world.level.block.CrossCollisionBlock
 *  net.minecraft.world.level.block.DoorBlock
 *  net.minecraft.world.level.block.LeavesBlock
 *  net.minecraft.world.level.block.SignBlock
 *  net.minecraft.world.level.block.TrapDoorBlock
 *  net.minecraft.world.level.block.VineBlock
 *  net.minecraft.world.level.material.Fluid
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.fml.common.EventBusSubscriber$Bus
 *  net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
 *  net.neoforged.neoforge.client.event.EntityRenderersEvent$RegisterRenderers
 *  net.neoforged.neoforge.client.event.RegisterColorHandlersEvent$Item
 *  net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent
 *  net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent
 *  net.neoforged.neoforge.client.event.RegisterShadersEvent
 *  net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent
 */
package mielon.thesift.client;

import java.io.IOException;
import mielon.thesift.block.ModBlocks;
import mielon.thesift.block.SonorousDeepslateBlock;
import mielon.thesift.client.RiftClientInitializer;
import mielon.thesift.client.compat.SiftShaderCompat;
import mielon.thesift.client.entity.BlubRenderer;
import mielon.thesift.client.entity.DarkSnifferRenderer;
import mielon.thesift.client.entity.EchoGolemRenderer;
import mielon.thesift.client.entity.MiniRiftRenderer;
import mielon.thesift.client.entity.RiftRenderer;
import mielon.thesift.client.entity.SifterRenderer;
import mielon.thesift.client.entity.SingerRenderer;
import mielon.thesift.client.entity.WillowBoatRenderer;
import mielon.thesift.client.light.TheSiftDynamicLightsInitializer;
import mielon.thesift.client.particle.IchorFluidParticle;
import mielon.thesift.client.particle.IchorSurfaceParticle;
import mielon.thesift.client.particle.SiftNoteParticle;
import mielon.thesift.client.particle.SiftParallaxParticle;
import mielon.thesift.client.particle.SingerSoundWaveParticle;
import mielon.thesift.client.particle.SoulParticle;
import mielon.thesift.client.particle.SoundWaveParticle;
import mielon.thesift.client.particle.WillowLeafParticle;
import mielon.thesift.client.render.IchorFluidRendering;
import mielon.thesift.client.render.SiftDimensionEffects;
import mielon.thesift.client.render.SiftPortalRenderer;
import mielon.thesift.client.render.SiftShaders;
import mielon.thesift.client.render.SonorousBeamRenderer;
import mielon.thesift.entity.ModEntities;
import mielon.thesift.fluid.ModFluids;
import mielon.thesift.item.ModItems;
import mielon.thesift.particle.ModParticles;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.material.Fluid;











public final class TheSiftClient implements net.fabricmc.api.ClientModInitializer {
    public void onInitializeClient() {
        mielon.thesift.backport.BackportClient.initialize();
        itemColors(); setup(); effects(); renderers(); particles(); IchorFluidRendering.register();
        net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback.EVENT.register(SiftShaders::register);
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver(mielon.thesift.network.RiftLoadingPayload.TYPE,(payload,context)->context.client().execute(()->mielon.thesift.network.RiftLoadingPayload.clientHandler.accept(payload)));
    }
    public static void itemColors() {
        // These sprites already contain their colors; generated-item layer0 is tinted by default.
        net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry.ITEM.register(
            (stack, tintIndex) -> -1,
            ModItems.ECHO_GOLEM_SPAWN_EGG, ModItems.DARK_SNIFFER_SPAWN_EGG,
            ModItems.SINGER_SPAWN_EGG, ModItems.SIFTER_SPAWN_EGG,
            mielon.thesift.item.BlubItems.BLUB_SPAWN_EGG);
    }

    public static void setup() {
        {
            SiftShaderCompat.initialize();
            RiftClientInitializer.initialize();
            TheSiftDynamicLightsInitializer.initialize();
            net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap.INSTANCE.putFluid((Fluid)ModFluids.ICHOR, (RenderType)RenderType.translucent());
            net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap.INSTANCE.putFluid((Fluid)ModFluids.FLOWING_ICHOR, (RenderType)RenderType.translucent());
            net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap.INSTANCE.putBlock((Block)ModBlocks.ICHOR_GLASS, (RenderType)RenderType.translucent());
            net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap.INSTANCE.putBlock((Block)ModBlocks.ICHOR_GLASS_PANE, (RenderType)RenderType.translucent());
            for (Block block : BuiltInRegistries.BLOCK) {
                if (!BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals("the_sift") || block == ModBlocks.ICHOR_GLASS_PANE || !(block instanceof BushBlock) && !(block instanceof VineBlock) && !(block instanceof LeavesBlock) && !(block instanceof DoorBlock) && !(block instanceof TrapDoorBlock) && !(block instanceof CrossCollisionBlock) && !(block instanceof SignBlock)) continue;
                net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap.INSTANCE.putBlock((Block)block, (RenderType)RenderType.cutoutMipped());
            }
            ItemProperties.register((Item)ModBlocks.SONOROUS_DEEPSLATE.asItem(), (ResourceLocation)ResourceLocation.fromNamespaceAndPath((String)"the_sift", (String)"mode"), (stack, level, entity, seed) -> {
                BlockItemStateProperties properties = (BlockItemStateProperties)stack.get(DataComponents.BLOCK_STATE);
                return properties != null && properties.get(SonorousDeepslateBlock.MODE) == SonorousDeepslateBlock.Mode.NOTE ? 1.0f : 0.0f;
            });
        }
    }

    public static void effects() {
        net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry.registerSkyRenderer(mielon.thesift.world.TheSiftDimension.LEVEL_KEY,context->{com.mojang.blaze3d.vertex.PoseStack poses=new com.mojang.blaze3d.vertex.PoseStack();poses.mulPose(context.positionMatrix());mielon.thesift.client.render.SiftProceduralSkyRenderer.render(poses,context.world(),context.projectionMatrix());});
        net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry.registerDimensionEffects(ResourceLocation.fromNamespaceAndPath((String)"the_sift", (String)"the_sift"), (DimensionSpecialEffects)new SiftDimensionEffects());
    }

    public static void renderers() {
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(ModEntities.SINGER, SingerRenderer::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(ModEntities.ECHO_GOLEM, EchoGolemRenderer::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(ModEntities.DARK_SNIFFER, DarkSnifferRenderer::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(ModEntities.BLUB, BlubRenderer::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(ModEntities.SIFTER, SifterRenderer::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(ModEntities.RIFT, RiftRenderer::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(ModEntities.MINI_RIFT, MiniRiftRenderer::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(ModEntities.SIFTITE_RETURN, ThrownItemRenderer::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(ModEntities.OVERGROWN_WILLOW_BOAT, context -> new WillowBoatRenderer(context, false));
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(ModEntities.OVERGROWN_WILLOW_CHEST_BOAT, context -> new WillowBoatRenderer(context, true));
        net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry.register(ModBlocks.SIFT_PORTAL_BLOCK_ENTITY, SiftPortalRenderer::new);
        net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry.register(ModBlocks.SONOROUS_DEEPSLATE_BLOCK_ENTITY, SonorousBeamRenderer::new);
    }

    public static void particles() {
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(ModParticles.WILLOW_LEAF, WillowLeafParticle.Provider::new);
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(ModParticles.SIFT_PARALLAX, SiftParallaxParticle.Provider::new);
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(ModParticles.SIFT_NOTE, SiftNoteParticle.Provider::new);
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(ModParticles.SOUND_WAVE, SoundWaveParticle.Provider::new);
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(ModParticles.SINGER_SOUND_WAVE, SingerSoundWaveParticle.Provider::new);
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(ModParticles.CANYON_SOUL, SoulParticle.RiseProvider::new);
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(ModParticles.SOUL_FRAGMENT, SoulParticle.FragmentProvider::new);
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(ModParticles.ICHOR_SURFACE_MIST, IchorSurfaceParticle.Provider::new);
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(ModParticles.ICHOR_BUBBLE, IchorFluidParticle.BubbleProvider::new);
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(ModParticles.ICHOR_SPLASH, IchorFluidParticle.SplashProvider::new);
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(ModParticles.ICHOR_RAIN_SPLASH, IchorFluidParticle.RainSplashProvider::new);
    }
}

