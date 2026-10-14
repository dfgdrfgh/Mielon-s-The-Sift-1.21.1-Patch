package mielon.thesift.check;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalLong;
import mielon.thesift.world.TheSiftDimension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.PlayerList;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.ServerLevelData;
import sun.misc.Unsafe;

/** Native weather-cycle/packet regression; no world generation or test code in the mod JAR. */
public final class WeatherIsolationCheck {
    private static final Unsafe UNSAFE;
    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            UNSAFE = (Unsafe)field.get(null);
        } catch (Exception error) { throw new ExceptionInInitializerError(error); }
    }

    public static void run(Minecraft client) throws Exception {
        // Only unrelated construction/storage is skipped. Weather methods, mixins,
        // command handlers and client packet handling are Minecraft's actual code.
        WeatherPlayers players = allocate(WeatherPlayers.class);
        players.listeners = new HashMap<>();
        IntegratedServer server = allocate(IntegratedServer.class);
        set(server, "playerList", players);
        ServerLevel overworld = world(server, Level.OVERWORLD);
        ServerLevel sift = world(server, TheSiftDimension.LEVEL_KEY);
        ClientLevel overworldClient = endpoint(client, players, Level.OVERWORLD);
        ClientLevel siftClient = endpoint(client, players, TheSiftDimension.LEVEL_KEY);

        // Natural Sift rain begins while the Overworld remains clear. This triggered
        // global START_RAINING/intensity packets and reproduced the reported symptom.
        sift.getLevelData().setRaining(false);
        ((ServerLevelData)sift.getLevelData()).setRainTime(1);
        tick(overworld, sift, 150);
        require(siftClient.isRaining(), "Natural Sift rain must reach Sift players");
        dry(overworldClient, "Natural Sift rain leaked into the Overworld");
        command(overworld, "setClear");
        tick(overworld, sift, 150);
        dry(overworldClient, "/weather clear failed while Sift was raining");
        require(siftClient.isRaining(), "Overworld clear incorrectly cleared Sift rain");

        command(sift, "setThunder");
        tick(overworld, sift, 150);
        require(siftClient.getThunderLevel(1.0F) > 0.99F, "Sift thunder was not delivered");
        dry(overworldClient, "Sift thunder leaked into the Overworld");
        command(sift, "setClear");
        tick(overworld, sift, 150);
        dry(siftClient, "One Sift clear command must clear rain and thunder");

        // Reverse directions, then repeat clear on an already clear server.
        command(overworld, "setThunder");
        tick(overworld, sift, 150);
        require(overworldClient.isRaining() && overworldClient.getThunderLevel(1.0F) > 0.99F,
            "Overworld thunder was not delivered");
        dry(siftClient, "Overworld weather leaked into The Sift");
        command(overworld, "setClear");
        tick(overworld, sift, 150);
        command(overworld, "setClear");
        tick(overworld, sift, 150);
        dry(overworldClient, "One Overworld clear command must clear rain and thunder");
        dry(siftClient, "Clearing the Overworld changed Sift weather");
        require(players.globalPackets == 0, "Weather transitions used global broadcast");
        require(players.dimensionPackets > 500, "Native weather packet paths were not exercised");
        Files.writeString(Path.of("weather-isolation-pass.txt"),
            "Natural rain, independent dimensions, thunder and single/repeated clear commands passed; "
            + players.dimensionPackets + " native packets delivered; zero global weather broadcasts.\n");
        System.out.println("SIFT_WEATHER_ISOLATION_PASS packets=" + players.dimensionPackets);
    }

    private static ServerLevel world(IntegratedServer server, ResourceKey<Level> dimension) throws Exception {
        Map<String, Object> weather = new HashMap<>();
        weather.put("RainTime", 10000);
        weather.put("ThunderTime", 10000);
        weather.put("ClearWeatherTime", 0);
        weather.put("Raining", false);
        weather.put("Thundering", false);
        GameRules rules = new GameRules();
        ServerLevelData data = (ServerLevelData)Proxy.newProxyInstance(
            WeatherIsolationCheck.class.getClassLoader(), new Class<?>[]{ServerLevelData.class},
            (proxy, method, args) -> {
                String name = method.getName();
                if (name.equals("getGameRules")) return rules;
                if (name.startsWith("set")) { weather.put(name.substring(3), args[0]); return null; }
                if (name.startsWith("get")) return weather.get(name.substring(3));
                if (name.startsWith("is")) return weather.get(name.substring(2));
                throw new IllegalStateException("Unexpected level data access: " + name);
            });
        DimensionType type = new DimensionType(OptionalLong.empty(), true, false, false, true,
            1.0, true, false, 0, 256, 256, BlockTags.INFINIBURN_OVERWORLD,
            ResourceLocation.withDefaultNamespace("overworld"), 0.0F,
            new DimensionType.MonsterSettings(false, true, ConstantInt.of(0), 0));
        ServerLevel level = allocate(ServerLevel.class);
        set(level, "server", server);
        set(level, "serverLevelData", data);
        set(level, "levelData", data);
        set(level, "dimension", dimension);
        set(level, "dimensionTypeRegistration", Holder.direct(type));
        set(level, "random", RandomSource.create(42));
        return level;
    }

    private static ClientLevel endpoint(Minecraft client, WeatherPlayers players, ResourceKey<Level> dimension) throws Exception {
        ClientLevel level = allocate(ClientLevel.class);
        ClientLevel.ClientLevelData data = new ClientLevel.ClientLevelData(Difficulty.NORMAL, false, false);
        set(level, "levelData", data);
        set(level, "clientLevelData", data);
        ClientPacketListener listener = allocate(ClientPacketListener.class);
        set(listener, "minecraft", client);
        set(listener, "level", level);
        players.listeners.put(dimension, listener);
        return level;
    }

    private static void tick(ServerLevel overworld, ServerLevel sift, int count) throws Exception {
        Method cycle = ServerLevel.class.getDeclaredMethod("advanceWeatherCycle");
        cycle.setAccessible(true);
        for (int i = 0; i < count; i++) { cycle.invoke(overworld); cycle.invoke(sift); }
    }

    private static void command(ServerLevel level, String method) throws Exception {
        CommandSourceStack source = allocate(CommandSourceStack.class);
        set(source, "source", CommandSource.NULL);
        set(source, "level", level);
        set(source, "server", level.getServer());
        set(source, "silent", true);
        Method handler = net.minecraft.server.commands.WeatherCommand.class.getDeclaredMethod(method, CommandSourceStack.class, int.class);
        handler.setAccessible(true);
        require((Integer)handler.invoke(null, source, 10000) == 10000, "Weather command result changed");
    }

    private static void dry(ClientLevel level, String message) {
        require(!level.getLevelData().isRaining() && level.getRainLevel(1.0F) < 0.001F
            && level.getThunderLevel(1.0F) < 0.001F, message);
    }
    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
    private static <T> T allocate(Class<T> type) throws InstantiationException {
        return type.cast(UNSAFE.allocateInstance(type));
    }
    private static void set(Object object, String name, Object value) throws Exception {
        for (Class<?> type = object.getClass(); type != null; type = type.getSuperclass()) {
            try {
                Field field = type.getDeclaredField(name);
                field.setAccessible(true);
                field.set(object, value);
                return;
            } catch (NoSuchFieldException ignored) {}
        }
        throw new NoSuchFieldException(name);
    }

    private static final class WeatherPlayers extends PlayerList {
        private Map<ResourceKey<Level>, ClientPacketListener> listeners;
        private int globalPackets, dimensionPackets;
        private WeatherPlayers() { super(null, null, null, 1); }
        @Override public void broadcastAll(Packet<?> packet) {
            globalPackets++;
            for (ClientPacketListener listener : listeners.values()) deliver(packet, listener);
        }
        @Override public void broadcastAll(Packet<?> packet, ResourceKey<Level> dimension) {
            dimensionPackets++;
            ClientPacketListener listener = listeners.get(dimension);
            if (listener != null) deliver(packet, listener);
        }
        private static void deliver(Packet<?> packet, ClientPacketListener listener) {
            if (!(packet instanceof ClientboundGameEventPacket weather))
                throw new IllegalStateException("Unexpected weather packet: " + packet);
            weather.handle(listener);
        }
    }
}
