/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.client.event.ClientTickEvent$Post
 *  net.neoforged.neoforge.common.NeoForge
 */
package mielon.thesift.client.light;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import mielon.thesift.client.light.EchoGolemLightBridge;
import mielon.thesift.client.light.SingerHeadLightBridge;
import mielon.thesift.entity.EchoGolemEntity;
import mielon.thesift.entity.SingerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;



public final class TheSiftDynamicLightsInitializer {
    private static final Map<Entity, Source> SOURCES = new HashMap<Entity, Source>();
    private static volatile Source[] lights = new Source[0];

    public static void initialize() {
        SingerHeadLightBridge.install((entity, pos) -> TheSiftDynamicLightsInitializer.update((Entity)entity, pos, 15));
        EchoGolemLightBridge.install((entity, pos) -> TheSiftDynamicLightsInitializer.update((Entity)entity, pos, entity.hasSoulBlock() ? 14 : 0));
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(TheSiftDynamicLightsInitializer::tick);
    }

    private static void update(Entity entity, Vec3 pos, int light) {
        Source old = SOURCES.get(entity);
        if (light == 0) {
            if (old != null) {
                SOURCES.remove(entity);
                TheSiftDynamicLightsInitializer.publish();
                TheSiftDynamicLightsInitializer.dirty(old.pos);
            }
            return;
        }
        if (old != null && old.light == light && (entity.tickCount - old.tick < 2 || old.pos.distanceToSqr(pos) < 0.01)) {
            return;
        }
        SOURCES.put(entity, new Source(pos, entity.position(), light, entity.tickCount));
        TheSiftDynamicLightsInitializer.publish();
        if (old != null) {
            TheSiftDynamicLightsInitializer.dirty(old.pos);
        }
        TheSiftDynamicLightsInitializer.dirty(pos);
    }

    private static void tick(Minecraft event) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            SOURCES.clear();
            TheSiftDynamicLightsInitializer.publish();
            return;
        }
        ArrayList<Vec3> removed = new ArrayList<Vec3>();
        SOURCES.entrySet().removeIf(entry -> {
            EchoGolemEntity golem;
            Entity entity = (Entity)entry.getKey();
            if (entity.isRemoved() || entity.level() != level || entity instanceof EchoGolemEntity && !(golem = (EchoGolemEntity)entity).hasSoulBlock()) {
                removed.add(((Source)entry.getValue()).pos);
                return true;
            }
            return false;
        });
        if (!removed.isEmpty()) {
            TheSiftDynamicLightsInitializer.publish();
            removed.forEach(TheSiftDynamicLightsInitializer::dirty);
        }
        for (Entity entity : level.entitiesForRendering()) {
            EchoGolemEntity golem;
            int strength = entity instanceof SingerEntity ? 15 : (entity instanceof EchoGolemEntity && (golem = (EchoGolemEntity)entity).hasSoulBlock() ? 14 : 0);
            if (strength == 0) continue;
            Source old = SOURCES.get(entity);
            if (old == null) {
                TheSiftDynamicLightsInitializer.update(entity, entity.position().add(0.0, 2.125, 0.0), strength);
                continue;
            }
            if (entity.tickCount - old.tick < 4) continue;
            TheSiftDynamicLightsInitializer.update(entity, old.pos.add(entity.position().subtract(old.entityPos)), strength);
        }
    }

    private static void publish() {
        lights = (Source[])SOURCES.values().toArray(Source[]::new);
    }

    private static void dirty(Vec3 pos) {
        Minecraft.getInstance().levelRenderer.setBlocksDirty((int)Math.floor(pos.x) - 15, (int)Math.floor(pos.y) - 15, (int)Math.floor(pos.z) - 15, (int)Math.floor(pos.x) + 15, (int)Math.floor(pos.y) + 15, (int)Math.floor(pos.z) + 15);
    }

    public static int light(BlockPos pos, int packed) {
        double light = (double)(packed & 0xFFFF) / 16.0;
        if (light >= 15.0) {
            return packed;
        }
        for (Source source : lights) {
            double x = source.pos.x - (double)pos.getX() - 0.5;
            double y = source.pos.y - (double)pos.getY() - 0.5;
            double z = source.pos.z - (double)pos.getZ() - 0.5;
            light = Math.max(light, (double)source.light - Math.sqrt(x * x + y * y + z * z));
        }
        return packed & 0xFFFF0000 | Math.min(240, (int)Math.max(0.0, light * 16.0));
    }

    private record Source(Vec3 pos, Vec3 entityPos, int light, int tick) {
    }
}

