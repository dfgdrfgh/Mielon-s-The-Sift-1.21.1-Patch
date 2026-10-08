package mielon.thesift.client.loading;

import mielon.thesift.network.SiftIntroReadyPayload;
import mielon.thesift.world.TheSiftDimension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;

public final class SiftIntroClient {
   private static ClientLevel trackedLevel;
   private static int readyTicks;
   private static boolean reported;

   private SiftIntroClient() {
   }

   public static void tick() {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.player != null && minecraft.level != null && minecraft.level.dimension().equals(TheSiftDimension.LEVEL_KEY)) {
         if (trackedLevel != minecraft.level) {
            trackedLevel = minecraft.level;
            readyTicks = 0;
            reported = false;
         }

         if (!reported) {
            if (minecraft.screen == null && !SiftLoadingScreenState.isActive() && hasGroundBelow(minecraft)) {
               if (++readyTicks >= 20) {
                  reported = true;
                  SiftIntroReadyPayload.send();
               }
            } else {
               readyTicks = 0;
            }
         }
      } else {
         trackedLevel = null;
         readyTicks = 0;
         reported = false;
      }
   }

   private static boolean hasGroundBelow(Minecraft minecraft) {
      BlockPos feet = minecraft.player.blockPosition();

      for (int depth = 1; depth <= 8; depth++) {
         BlockPos below = feet.below(depth);
         if (!minecraft.level.hasChunkAt(below)) {
            return false;
         }

         if (!minecraft.level.getBlockState(below).isAir()) {
            return true;
         }
      }

      return false;
   }
}
