package mielon.thesift.platform;

import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;

public final class ValueOutput {
   private final CompoundTag tag;
   private final RegistryOps<Tag> ops;

   public ValueOutput(CompoundTag tag, Provider registries) {
      this.tag = tag;
      this.ops = registries.createSerializationContext(NbtOps.INSTANCE);
   }

   public void putInt(String key, int value) {
      this.tag.putInt(key, value);
   }

   public void putLong(String key, long value) {
      this.tag.putLong(key, value);
   }

   public void putDouble(String key, double value) {
      this.tag.putDouble(key, value);
   }

   public void putFloat(String key, float value) {
      this.tag.putFloat(key, value);
   }

   public void putBoolean(String key, boolean value) {
      this.tag.putBoolean(key, value);
   }

   public void putString(String key, String value) {
      this.tag.putString(key, value);
   }

   public <T> void store(String key, Codec<T> codec, T value) {
      this.tag.put(key, (Tag)codec.encodeStart(this.ops, value).getOrThrow());
   }

   public <T> ValueOutput.TypedOutputList<T> list(String key, Codec<T> codec) {
      ListTag list = new ListTag();
      this.tag.put(key, list);
      return new ValueOutput.TypedOutputList<>(list, codec, this.ops);
   }

   public record TypedOutputList<T>(ListTag list, Codec<T> codec, RegistryOps<Tag> ops) {
      public void add(T value) {
         this.list.add((Tag)this.codec.encodeStart(this.ops, value).getOrThrow());
      }
   }
}
