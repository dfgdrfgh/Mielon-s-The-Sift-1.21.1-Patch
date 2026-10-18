package mielon.thesift.backport;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import mielon.thesift.item.ModItems;
import mielon.thesift.block.ModBlocks;
import mielon.thesift.backport.common.level.items.spear.SpearItem;
import mielon.thesift.backport.common.level.blocks.ShelfBlock;
import mielon.thesift.backport.common.level.block_entities.ShelfBlockEntity;
public final class BackportContent {
 public static final ShelfBlock SHELF=Registry.register(BuiltInRegistries.BLOCK,id("overgrown_willow_shelf"),new ShelfBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHISELED_BOOKSHELF)));
 public static final Item SHELF_ITEM=Registry.register(BuiltInRegistries.ITEM,id("overgrown_willow_shelf"),new BlockItem(SHELF,new Item.Properties()));
 public static final BlockEntityType<ShelfBlockEntity> SHELF_ENTITY=Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,id("overgrown_willow_shelf"),FabricBlockEntityTypeBuilder.create(ShelfBlockEntity::new,SHELF).build());
 public static final Item SIFTITE_SPEAR=Registry.register(BuiltInRegistries.ITEM,id("siftite_spear"),new SpearItem(ModItems.SIFTITE_TOOL_MATERIAL,1.25f,1.2f,.4f,2.5f,9,5.5f,5.1f,8.75f,4.6f,new Item.Properties().fireResistant()));
 public static final List<Item> SPEARS=List.of(SIFTITE_SPEAR);
 public static final SoundEvent[] LUNGE_SOUNDS={sound("lunge_1"),sound("lunge_2"),sound("lunge_3")};
 public static ResourceLocation id(String name){return ResourceLocation.fromNamespaceAndPath("the_sift",name);}
 private static SoundEvent sound(String name){return Registry.register(BuiltInRegistries.SOUND_EVENT,id("backport.spear_"+name),SoundEvent.createVariableRangeEvent(id("backport.spear_"+name)));}
 public static void initialize(){FlammableBlockRegistry.getDefaultInstance().add(SHELF,5,20);SpearAttackPayload.register();}
}
