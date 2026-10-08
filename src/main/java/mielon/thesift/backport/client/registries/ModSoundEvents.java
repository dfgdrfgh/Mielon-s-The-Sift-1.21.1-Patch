package mielon.thesift.backport.client.registries;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
public final class ModSoundEvents {
 private static Holder<SoundEvent> sound(String name){ ResourceLocation id=ResourceLocation.fromNamespaceAndPath("the_sift",name);return Holder.direct(Registry.register(BuiltInRegistries.SOUND_EVENT,id,SoundEvent.createVariableRangeEvent(id)));}
 private static final SoundEvent SHELF_ACTIVATE_VALUE=sound("backport.shelf_activate").value(); public static final Supplier<SoundEvent> SHELF_ACTIVATE=()->SHELF_ACTIVATE_VALUE;
 private static final SoundEvent SHELF_DEACTIVATE_VALUE=sound("backport.shelf_deactivate").value(); public static final Supplier<SoundEvent> SHELF_DEACTIVATE=()->SHELF_DEACTIVATE_VALUE;
 private static final SoundEvent SHELF_TAKE_ITEM_VALUE=sound("backport.shelf_take_item").value(); public static final Supplier<SoundEvent> SHELF_TAKE_ITEM=()->SHELF_TAKE_ITEM_VALUE;
 private static final SoundEvent SHELF_SINGLE_SWAP_VALUE=sound("backport.shelf_single_swap").value(); public static final Supplier<SoundEvent> SHELF_SINGLE_SWAP=()->SHELF_SINGLE_SWAP_VALUE;
 private static final SoundEvent SHELF_PLACE_ITEM_VALUE=sound("backport.shelf_place_item").value(); public static final Supplier<SoundEvent> SHELF_PLACE_ITEM=()->SHELF_PLACE_ITEM_VALUE;
 private static final SoundEvent SHELF_MULTI_SWAP_VALUE=sound("backport.shelf_multi_swap").value(); public static final Supplier<SoundEvent> SHELF_MULTI_SWAP=()->SHELF_MULTI_SWAP_VALUE;
 public static final Holder<SoundEvent> SPEAR_USE=sound("backport.spear_use");
 public static final Holder<SoundEvent> SPEAR_HIT=sound("backport.spear_hit");
 public static final Holder<SoundEvent> SPEAR_ATTACK=sound("backport.spear_attack");
 public static final Holder<SoundEvent> SPEAR_WOOD_USE=sound("backport.spear_wood_use");
 public static final Holder<SoundEvent> SPEAR_WOOD_HIT=sound("backport.spear_wood_hit");
 public static final Holder<SoundEvent> SPEAR_WOOD_ATTACK=sound("backport.spear_wood_attack");
}
