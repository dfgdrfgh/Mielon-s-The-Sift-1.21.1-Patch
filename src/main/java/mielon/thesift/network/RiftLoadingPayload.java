/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.neoforged.neoforge.network.PacketDistributor
 *  net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
 */
package mielon.thesift.network;

import java.util.function.Consumer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;



public record RiftLoadingPayload(boolean start, boolean rift) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<RiftLoadingPayload> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"the_sift", (String)"rift_loading"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RiftLoadingPayload> CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.BOOL, RiftLoadingPayload::start, (StreamCodec)ByteBufCodecs.BOOL, RiftLoadingPayload::rift, RiftLoadingPayload::new);
    public static Consumer<RiftLoadingPayload> clientHandler = payload -> {};

    public CustomPacketPayload.Type<RiftLoadingPayload> type() {
        return TYPE;
    }

    public static void register() {
        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.playS2C().register(TYPE, CODEC);
    }

    public static void send(ServerPlayer player, boolean start) {
        RiftLoadingPayload.send(player, start, true);
    }

    public static void send(ServerPlayer player, boolean start, boolean rift) {
        net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(player, new RiftLoadingPayload(start, rift));
    }
}

