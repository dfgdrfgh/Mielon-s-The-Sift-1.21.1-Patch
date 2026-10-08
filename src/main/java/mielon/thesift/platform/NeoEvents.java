/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  net.minecraft.commands.CommandBuildContext
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands$CommandSelection
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.neoforged.neoforge.common.NeoForge
 */
package mielon.thesift.platform;

import com.mojang.brigadier.CommandDispatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.world.InteractionResultHolder;

public final class NeoEvents {
    private static final List<BlockBreak> BREAKS = new ArrayList<BlockBreak>();
    private static final List<AllowDeath> ALLOW_DEATHS = new ArrayList<AllowDeath>();
    private static final List<Death> DEATHS = new ArrayList<Death>();

    public static void afterBlockBreak(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        BREAKS.forEach(callback -> callback.after(level, player, pos, state, blockEntity));
    }

    public static boolean allowDeath(LivingEntity entity, DamageSource source) {
        if (entity.level().isClientSide) {
            return true;
        }
        for (AllowDeath callback : ALLOW_DEATHS) {
            if (callback.allow(entity, source, Float.MAX_VALUE)) continue;
            return false;
        }
        return true;
    }

    public static void afterDeath(LivingEntity entity, DamageSource source) {
        DEATHS.forEach(callback -> callback.after(entity, source));
    }

    public static interface AllowDeath {
        public boolean allow(LivingEntity var1, DamageSource var2, float var3);
    }

    public static interface Death {
        public void after(LivingEntity var1, DamageSource var2);
    }

    public static interface BlockBreak {
        public void after(Level var1, Player var2, BlockPos var3, BlockState var4, BlockEntity var5);
    }

    public static final class CommandRegistrationCallback {
        public static final Event<CommandRegistration> EVENT = new Event<>(callback -> net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback.EVENT.register(callback::register));
    }

    public static interface CommandRegistration {
        public void register(CommandDispatcher<CommandSourceStack> var1, CommandBuildContext var2, Commands.CommandSelection var3);
    }

    public static final class ServerPlayerEvents {
        public static final Event<Respawn> AFTER_RESPAWN = new Event<>(callback -> net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents.AFTER_RESPAWN.register(callback::after));
        public static final Event<Consumer<ServerPlayer>> JOIN = new Event<>(callback -> ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> callback.accept(handler.player)));
    }

    public static interface Respawn {
        public void after(ServerPlayer var1, ServerPlayer var2, boolean var3);
    }

    public static final class ServerLivingEntityEvents {
        public static final Event<Death> AFTER_DEATH = new Event<>(callback -> net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents.AFTER_DEATH.register(callback::after));
        public static final Event<AllowDeath> ALLOW_DEATH = new Event<>(callback -> net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents.ALLOW_DEATH.register(callback::allow));
    }

    public static final class PlayerBlockBreakEvents {
        public static final Event<BlockBreak> AFTER = new Event<BlockBreak>(BREAKS::add);
    }

    public static final class UseItemCallback {
        public static final Event<ItemUse> EVENT = new Event<>(callback -> net.fabricmc.fabric.api.event.player.UseItemCallback.EVENT.register((player, level, hand) -> new InteractionResultHolder<>(callback.use(player, level, hand), player.getItemInHand(hand))));
    }

    public static interface ItemUse {
        public InteractionResult use(Player var1, Level var2, InteractionHand var3);
    }

    public static final class UseBlockCallback {
        public static final Event<BlockUse> EVENT = new Event<>(callback -> net.fabricmc.fabric.api.event.player.UseBlockCallback.EVENT.register(callback::use));
    }

    public static interface BlockUse {
        public InteractionResult use(Player var1, Level var2, InteractionHand var3, BlockHitResult var4);
    }

    public static final class ServerLifecycleEvents {
        public static final Event<Consumer<MinecraftServer>> SERVER_STOPPING = new Event<>(callback -> net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STOPPING.register(callback::accept));
        public static final Event<Consumer<MinecraftServer>> SERVER_STOPPED = new Event<>(callback -> net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STOPPED.register(callback::accept));
    }

    public static final class ServerTickEvents {
        public static final Event<Consumer<MinecraftServer>> START_SERVER_TICK = new Event<>(callback -> net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.START_SERVER_TICK.register(callback::accept));
        public static final Event<Consumer<MinecraftServer>> END_SERVER_TICK = new Event<>(callback -> net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(callback::accept));
    }

    public record Event<T>(Consumer<T> registrar) {
        public void register(T callback) {
            this.registrar.accept(callback);
        }
    }
}

