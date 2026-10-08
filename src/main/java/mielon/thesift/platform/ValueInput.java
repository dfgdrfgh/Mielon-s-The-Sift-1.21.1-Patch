package mielon.thesift.platform;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;

public final class ValueInput {
   private final CompoundTag tag;
   private final RegistryOps<Tag> ops;

   public ValueInput(CompoundTag tag, Provider registries) {
      this.tag = tag;
      this.ops = registries.createSerializationContext(NbtOps.INSTANCE);
   }

   public int getIntOr(String key, int fallback) {
      return this.tag.contains(key, 99) ? this.tag.getInt(key) : fallback;
   }

   public long getLongOr(String key, long fallback) {
      return this.tag.contains(key, 99) ? this.tag.getLong(key) : fallback;
   }

   public double getDoubleOr(String key, double fallback) {
      return this.tag.contains(key, 99) ? this.tag.getDouble(key) : fallback;
   }

   public float getFloatOr(String key, float fallback) {
      return this.tag.contains(key, 99) ? this.tag.getFloat(key) : fallback;
   }

   public boolean getBooleanOr(String key, boolean fallback) {
      return this.tag.contains(key, 99) ? this.tag.getBoolean(key) : fallback;
   }

   public String getStringOr(String key, String fallback) {
      return this.tag.contains(key, 8) ? this.tag.getString(key) : fallback;
   }

   public Optional<Long> getLong(String key) {
      return this.tag.contains(key, 99) ? Optional.of(this.tag.getLong(key)) : Optional.empty();
   }

   public Optional<String> getString(String key) {
      return this.tag.contains(key, 8) ? Optional.of(this.tag.getString(key)) : Optional.empty();
   }

   public <T> Optional<T> read(String key, Codec<T> codec) {
      return this.tag.contains(key) ? codec.parse(this.ops, this.tag.get(key)).result() : Optional.empty();
   }

   public <T> List<T> listOrEmpty(String key, Codec<T> codec) {
      return this.<List<T>>read(key, codec.listOf()).orElse(List.of());
   }
}
