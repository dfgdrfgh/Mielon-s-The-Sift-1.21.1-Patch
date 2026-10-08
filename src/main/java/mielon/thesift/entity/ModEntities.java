package mielon.thesift.entity;

import mielon.thesift.platform.ModRegistration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;

public final class ModEntities {
   public static final EntityType<SingerEntity> SINGER = register("singer", Builder.of(SingerEntity::new, MobCategory.MISC).sized(0.8F, 4.0F));
   public static final EntityType<EchoGolemEntity> ECHO_GOLEM = register(
      "echo_golem", Builder.of(EchoGolemEntity::new, MobCategory.CREATURE).sized(1.15F, 1.65F).eyeHeight(1.17F).clientTrackingRange(10)
   );
   public static final EntityType<DarkSnifferEntity> DARK_SNIFFER = register(
      "dark_sniffer",
      Builder.of(DarkSnifferEntity::new, MobCategory.MONSTER)
         .sized(1.9F, 1.75F)
         .eyeHeight(1.05F)
         .passengerAttachments(new float[]{2.09375F})
         .nameTagOffset(2.05F)
         .clientTrackingRange(10)
   );
   public static final EntityType<BlubEntity> BLUB = register(
      "blub", Builder.of(BlubEntity::new, MobCategory.CREATURE).sized(0.8F, 0.9F).eyeHeight(0.62F).clientTrackingRange(10)
   );
   public static final EntityType<SifterEntity> SIFTER = register(
      "sifter", Builder.of(SifterEntity::new, MobCategory.CREATURE).sized(0.9F, 0.95F).eyeHeight(0.72F).clientTrackingRange(10)
   );
   public static final EntityType<RiftEntity> RIFT = register(
      "rift", Builder.of(RiftEntity::new, MobCategory.MISC).sized(1.0F, 4.25F).clientTrackingRange(12).updateInterval(1)
   );
   public static final EntityType<MiniRiftEntity> MINI_RIFT = register(
      "mini_rift", Builder.of(MiniRiftEntity::new, MobCategory.MISC).sized(0.1F, 0.1F).clientTrackingRange(8).updateInterval(1)
   );
   public static final EntityType<SiftiteReturnEntity> SIFTITE_RETURN = register(
      "siftite_return", Builder.of(SiftiteReturnEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(8).updateInterval(1)
   );
   public static final EntityType<Boat> OVERGROWN_WILLOW_BOAT = register(
      "overgrown_willow_boat", Builder.<Boat>of(WillowBoat::new, MobCategory.MISC).sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10)
   );
   public static final EntityType<ChestBoat> OVERGROWN_WILLOW_CHEST_BOAT = register(
      "overgrown_willow_chest_boat", Builder.<ChestBoat>of(WillowChestBoat::new, MobCategory.MISC).sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10)
   );

   private ModEntities() {
   }

   private static ResourceKey<EntityType<?>> key(String path) {
      return ResourceKey.create(BuiltInRegistries.ENTITY_TYPE.key(), ResourceLocation.fromNamespaceAndPath("the_sift", path));
   }

   private static <T extends Entity> EntityType<T> register(String path, Builder<T> builder) {
      ResourceKey<EntityType<?>> key = key(path);
      return ModRegistration.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key.location().toString()));
   }

   public static void initialize() {
   }
}
