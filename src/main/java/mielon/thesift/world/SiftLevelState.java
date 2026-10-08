package mielon.thesift.world;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedData.Factory;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;

public final class SiftLevelState extends SavedData {
   public static final Factory<SiftLevelState> FACTORY = new Factory<>(SiftLevelState::new, SiftLevelState::load, null);
   long dayTime;
   int clearTime;
   int rainTime;
   int thunderTime;
   boolean raining;
   boolean thundering;
   long transitionStart;
   long transitionTarget;
   int transitionTicks = -1;

   private static SiftLevelState load(CompoundTag tag, Provider registries) {
      SiftLevelState state = new SiftLevelState();
      state.dayTime = tag.getLong("DayTime");
      state.clearTime = tag.getInt("ClearTime");
      state.rainTime = tag.getInt("RainTime");
      state.thunderTime = tag.getInt("ThunderTime");
      state.raining = tag.getBoolean("Raining");
      state.thundering = tag.getBoolean("Thundering");
      state.transitionStart = tag.getLong("TransitionStart");
      state.transitionTarget = tag.getLong("TransitionTarget");
      state.transitionTicks = tag.contains("TransitionTicks") ? tag.getInt("TransitionTicks") : -1;
      return state;
   }

   public CompoundTag save(CompoundTag tag, Provider registries) {
      tag.putLong("DayTime", this.dayTime);
      tag.putInt("ClearTime", this.clearTime);
      tag.putInt("RainTime", this.rainTime);
      tag.putInt("ThunderTime", this.thunderTime);
      tag.putBoolean("Raining", this.raining);
      tag.putBoolean("Thundering", this.thundering);
      tag.putLong("TransitionStart", this.transitionStart);
      tag.putLong("TransitionTarget", this.transitionTarget);
      tag.putInt("TransitionTicks", this.transitionTicks);
      return tag;
   }

   public void tick(boolean advanceTime) {
      if (this.transitionTicks >= 0) {
         if (++this.transitionTicks >= 60) {
            this.dayTime = this.transitionTarget;
            this.transitionTicks = -1;
         } else {
            this.dayTime = this.transitionStart + this.transitionTicks;
         }

         this.setDirty();
      } else if (advanceTime) {
         this.dayTime++;
         this.setDirty();
      }
   }

   public void setTime(long target) {
      boolean currentNight = SiftDayNightCycle.nightBlend(this.dayTime) >= 0.5F;
      boolean nextNight = SiftDayNightCycle.nightBlend(target) >= 0.5F;
      if (currentNight != nextNight && Math.abs(target - this.dayTime) > 1L) {
         long period = target - Math.floorMod(target, 24120L);
         this.transitionStart = nextNight ? period + 12000L : (period >= 24120L ? period - 60L : 24060L);
         this.transitionTarget = target;
         this.transitionTicks = 0;
         this.dayTime = this.transitionStart;
      } else {
         this.transitionTicks = -1;
         this.dayTime = target;
      }

      this.setDirty();
   }

   public static final class LevelData extends DerivedLevelData {
      private final SiftLevelState state;
      private final boolean sift;

      public LevelData(WorldData world, ServerLevelData original, SiftLevelState state, boolean sift) {
         super(world, original);
         this.state = state;
         this.sift = sift;
      }

      public long getDayTime() {
         return this.sift ? this.state.dayTime : super.getDayTime();
      }

      public void setDayTime(long value) {
         if (this.sift) {
            this.state.setTime(value);
         }
      }

      public int getClearWeatherTime() {
         return this.state.clearTime;
      }

      public void setClearWeatherTime(int value) {
         this.state.clearTime = value;
         this.state.setDirty();
      }

      public int getRainTime() {
         return this.state.rainTime;
      }

      public void setRainTime(int value) {
         this.state.rainTime = value;
         this.state.setDirty();
      }

      public int getThunderTime() {
         return this.state.thunderTime;
      }

      public void setThunderTime(int value) {
         this.state.thunderTime = value;
         this.state.setDirty();
      }

      public boolean isRaining() {
         return this.state.raining;
      }

      public void setRaining(boolean value) {
         this.state.raining = value;
         this.state.setDirty();
      }

      public boolean isThundering() {
         return this.state.thundering;
      }

      public void setThundering(boolean value) {
         this.state.thundering = value;
         this.state.setDirty();
      }
   }
}
