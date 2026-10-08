package mielon.thesift.backport.common.registries.items;
import mielon.thesift.backport.common.level.components.*;
import java.util.function.Supplier;
import com.mojang.serialization.Codec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
public final class ModDataComponents {
 private static <T> Supplier<DataComponentType<T>> register(String name,Codec<T> codec,StreamCodec<? super net.minecraft.network.RegistryFriendlyByteBuf,T> stream){ DataComponentType<T> type=Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,ResourceLocation.fromNamespaceAndPath("the_sift",name),DataComponentType.<T>builder().persistent(codec).networkSynchronized(stream).build()); return ()->type; }
 public static final Supplier<DataComponentType<AttackRange>> ATTACK_RANGE=register("attack_range",AttackRange.CODEC,AttackRange.STREAM_CODEC);
 public static final Supplier<DataComponentType<KineticWeapon>> KINETIC_WEAPON=register("kinetic_weapon",KineticWeapon.CODEC,KineticWeapon.STREAM_CODEC);
 public static final Supplier<DataComponentType<PiercingWeapon>> PIERCING_WEAPON=register("piercing_weapon",PiercingWeapon.CODEC,PiercingWeapon.STREAM_CODEC);
 public static final Supplier<DataComponentType<SwingAnimation>> SWING_ANIMATION=register("swing_animation",SwingAnimation.CODEC,SwingAnimation.STREAM_CODEC);
 public static final Supplier<DataComponentType<UseEffects>> USE_EFFECTS=register("use_effects",UseEffects.CODEC,UseEffects.STREAM_CODEC);
 public static final Supplier<DataComponentType<Float>> MINIMUM_ATTACK_CHARGE=register("minimum_attack_charge",Codec.FLOAT,ByteBufCodecs.FLOAT);
}
