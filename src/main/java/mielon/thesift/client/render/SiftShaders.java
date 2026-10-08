/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.client.event.RegisterShadersEvent
 */
package mielon.thesift.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.io.IOException;
import mielon.thesift.world.SiftDayNightCycle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;

public final class SiftShaders {
    private static ShaderInstance rift;
    private static ShaderInstance portal;
    private static ShaderInstance loading;
    private static ShaderInstance riftLoading;
    private static ShaderInstance clouds;

    public static void register(CoreShaderRegistrationCallback.RegistrationContext event) throws IOException {
        event.register(SiftShaders.id("rendertype_ichor_clouds"), DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL, s -> {
            clouds = s;
        });
        event.register(SiftShaders.id("rift"), DefaultVertexFormat.POSITION_TEX_COLOR, s -> {
            rift = s;
        });
        event.register(SiftShaders.id("sift_portal"), DefaultVertexFormat.POSITION, s -> {
            portal = s;
        });
        event.register(SiftShaders.id("sift_loading"), DefaultVertexFormat.POSITION_TEX_COLOR, s -> {
            loading = s;
        });
        event.register(SiftShaders.id("rift_loading"), DefaultVertexFormat.POSITION_TEX_COLOR, s -> {
            riftLoading = s;
        });
    }

    private static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath((String)"the_sift", (String)name);
    }

    private static ShaderInstance prepare(ShaderInstance shader) {
        if (shader == null) {
            return null;
        }
        Minecraft client = Minecraft.getInstance();
        shader.safeGetUniform("ScreenSize").set((float)client.getWindow().getWidth(), (float)client.getWindow().getHeight());
        if (client.level != null) {
            float partial = client.getTimer().getGameTimeDeltaPartialTick(false);
            float gameTime = ((float)Math.floorMod(client.level.getGameTime(), 24000L) + partial) / 24000.0f;
            shader.safeGetUniform("GameTime").set(gameTime);
        }
        return shader;
    }

    public static ShaderInstance clouds() {
        Minecraft client = Minecraft.getInstance();
        ClientLevel level = client.level;
        Vec3 camera = client.gameRenderer.getMainCamera().getPosition();
        clouds.safeGetUniform("CameraPosition").set((float)camera.x, (float)camera.y, (float)camera.z);
        clouds.safeGetUniform("RainAlpha").set(level == null ? 0.0f : level.getRainLevel(1.0f) * 0.66f);
        clouds.safeGetUniform("NightBrightness").set(level == null ? 1.0f : 1.0f - SiftDayNightCycle.nightBlend(level.getDayTime()) * 0.24f);
        return clouds;
    }

    public static ShaderInstance rift() {
        return SiftShaders.prepare(rift);
    }

    public static ShaderInstance portal() {
        return SiftShaders.prepare(portal);
    }

    public static ShaderInstance loading(boolean rift) {
        return SiftShaders.prepare(rift ? riftLoading : loading);
    }
}

