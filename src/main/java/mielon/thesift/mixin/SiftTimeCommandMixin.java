package mielon.thesift.mixin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.List;
import java.util.stream.StreamSupport;
import mielon.thesift.world.TheSiftDimension;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.TimeCommand;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({TimeCommand.class})
public abstract class SiftTimeCommandMixin {
   @Inject(
      method = {"register"},
      at = {@At("RETURN")}
   )
   private static void theSift$queryDay(CommandDispatcher<CommandSourceStack> dispatcher, CallbackInfo ci) {
      dispatcher.register((LiteralArgumentBuilder)Commands.literal("time").then(Commands.literal("query").then(Commands.literal("day").executes(context -> {
         CommandSourceStack source = (CommandSourceStack)context.getSource();
         ServerLevel level = source.getLevel();
         long period = level.dimension().equals(TheSiftDimension.LEVEL_KEY) ? 24120L : 24000L;
         int day = (int)(level.getDayTime() / period % 2147483647L);
         source.sendSuccess(() -> Component.translatable("commands.time.query", new Object[]{day}), false);
         return day;
      }))));
   }

   @Redirect(
      method = {"setTime", "addTime"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/server/MinecraftServer;getAllLevels()Ljava/lang/Iterable;"
      )
   )
   private static Iterable<ServerLevel> theSift$selectClock(MinecraftServer server, CommandSourceStack source, int time) {
      return source.getLevel().dimension().equals(TheSiftDimension.LEVEL_KEY)
         ? List.of(source.getLevel())
         : StreamSupport.<ServerLevel>stream(server.getAllLevels().spliterator(), false)
            .filter(level -> !level.dimension().equals(TheSiftDimension.LEVEL_KEY))
            .toList();
   }

   @Inject(
      method = {"getDayTime"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void theSift$queryClock(ServerLevel level, CallbackInfoReturnable<Integer> cir) {
      if (level.dimension().equals(TheSiftDimension.LEVEL_KEY)) {
         cir.setReturnValue((int)Math.floorMod(level.getDayTime(), 24120L));
      }
   }
}
