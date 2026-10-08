/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.fml.ModList
 */
package mielon.thesift.client.compat;

import java.lang.reflect.Method;
import mielon.thesift.TheSiftMod;
import net.fabricmc.loader.api.FabricLoader;

public final class SiftShaderCompat {
    private static boolean initialized;
    private static Object irisApi;
    private static Method shaderPackInUse;
    private static long lastShaderCheckNanos;
    private static boolean lastShaderCheck;

    private SiftShaderCompat() {
    }

    public static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        if (!FabricLoader.getInstance().isModLoaded("iris")) {
            return;
        }
        try {
            Class<?> api = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            irisApi = api.getMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
            shaderPackInUse = api.getMethod("isShaderPackInUse", new Class[0]);
            TheSiftMod.LOGGER.info("Iris detected; The Sift shader compatibility is enabled.");
        }
        catch (LinkageError | ReflectiveOperationException exception) {
            TheSiftMod.LOGGER.warn("Could not initialize the optional Iris API", exception);
        }
    }

    public static boolean isShaderPackInUse() {
        SiftShaderCompat.initialize();
        if (irisApi == null || shaderPackInUse == null) {
            return false;
        }
        long now = System.nanoTime();
        if (now - lastShaderCheckNanos < 16000000L) {
            return lastShaderCheck;
        }
        try {
            lastShaderCheck = Boolean.TRUE.equals(shaderPackInUse.invoke(irisApi, new Object[0]));
        }
        catch (ReflectiveOperationException | RuntimeException exception) {
            lastShaderCheck = false;
        }
        lastShaderCheckNanos = now;
        return lastShaderCheck;
    }
}

