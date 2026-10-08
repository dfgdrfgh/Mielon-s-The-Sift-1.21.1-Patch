/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.neoforged.neoforge.network.PacketDistributor
 *  net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
 */
package mielon.thesift.network;

import mielon.thesift.world.SiftTeleportManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;



public record SiftIntroReadyPayload() implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<SiftIntroReadyPayload> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"the_sift", (String)"intro_ready"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SiftIntroReadyPayload> CODEC = StreamCodec.unit(new SiftIntroReadyPayload());

    public CustomPacketPayload.Type<SiftIntroReadyPayload> type() {
        return TYPE;
    }

    public static void register() {
        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.playC2S().register(TYPE, CODEC);
        net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.registerGlobalReceiver(TYPE, (payload, context) -> context.server().execute(() -> SiftTeleportManager.playFirstIntro(context.player())));
    }

    public static void send() {
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new SiftIntroReadyPayload());
    }
}

