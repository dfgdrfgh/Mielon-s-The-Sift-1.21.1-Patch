/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Registry
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.neoforged.neoforge.registries.RegisterEvent
 */
package mielon.thesift.platform;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;


public final class ModRegistration {


    private ModRegistration() {
    }

    public static <T, V extends T> V register(Registry<T> registry, ResourceKey<T> key, V value) {
        return ModRegistration.register(registry, key.location(), value);
    }

    public static <T, V extends T> V register(Registry<T> registry, ResourceLocation id, V value) {
        Registry.register(registry, id, value);
        return value;
    }


}

