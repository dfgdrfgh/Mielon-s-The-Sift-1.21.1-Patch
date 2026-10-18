package mielon.thesift.check;

import java.nio.file.Files;
import java.nio.file.Path;
import mielon.thesift.item.BlubItems;
import mielon.thesift.item.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomModelData;

/** Checks real client-baked models after resource reload. Not included in the release JAR. */
public final class SpawnEggRenderCheck implements ClientModInitializer {
    private boolean checked;
    @Override public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (checked || client.getOverlay()!=null) return;
            checked=true;
            try {
                WeatherIsolationCheck.run(client);
                CreativeTabsCheck.run();
                run(client);
                Files.writeString(Path.of("spawn-eggs-pass.txt"),"All five sprites, tint providers, generated outlines, hand transforms and advancement overrides passed.\n");
                Files.writeString(Path.of("creative-tabs-pass.txt"),"Creative tab and search placements, custom tab spear/shelf ordering, and Sonorous variants passed.\n");
                System.out.println("SIFT_SPAWN_EGGS_RENDER_PASS");
                client.stop();
            } catch (Throwable failure) {
                failure.printStackTrace();
                System.exit(1);
            }
        });
    }
    private static void require(boolean value,String message) {
        if (!value) throw new IllegalStateException(message);
    }
    private static void sameTransform(ItemTransform actual,ItemTransform expected,String message) {
        require(actual.rotation.equals(expected.rotation) && actual.translation.equals(expected.translation)
            && actual.scale.equals(expected.scale),message);
    }
    private static void run(Minecraft client) {
        BakedModel vanilla=client.getItemRenderer().getModel(new ItemStack(Items.PIG_SPAWN_EGG),null,null,0);
        Item[] eggs={BlubItems.BLUB_SPAWN_EGG,ModItems.DARK_SNIFFER_SPAWN_EGG,ModItems.ECHO_GOLEM_SPAWN_EGG,ModItems.SIFTER_SPAWN_EGG,ModItems.SINGER_SPAWN_EGG};
        String[] names={"blub","dark_sniffer","echo_golem","sifter","singer"};
        for(int i=0;i<eggs.length;i++) {
            ItemStack stack=new ItemStack(eggs[i]);
            BakedModel model=client.getItemRenderer().getModel(stack,null,null,0);
            ResourceLocation sprite=ResourceLocation.fromNamespaceAndPath("the_sift","item/"+names[i]+"_spawn_egg");
            require(model.getParticleIcon().contents().name().equals(sprite),names[i]+": correct custom sprite");
            require(!model.isGui3d() && !model.usesBlockLight(),names[i]+": generated item with flat GUI lighting");
            var quads=model.getQuads(null,null,RandomSource.create(0));
            require(quads.size()>2,names[i]+": generated outline sides exist");
            for(var quad:quads) {
                require(quad.getSprite().contents().name().equals(sprite),names[i]+": face texture");
                if(quad.isTinted()) require(ColorProviderRegistry.ITEM.get(stack.getItem()).getColor(stack,quad.getTintIndex())==-1,names[i]+": precolored texture has no added tint");
            }
            for(var context:new ItemDisplayContext[]{ItemDisplayContext.FIRST_PERSON_RIGHT_HAND,ItemDisplayContext.FIRST_PERSON_LEFT_HAND,ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,ItemDisplayContext.THIRD_PERSON_LEFT_HAND,ItemDisplayContext.GROUND,ItemDisplayContext.FIXED}) {
                sameTransform(model.getTransforms().getTransform(context),vanilla.getTransforms().getTransform(context),names[i]+": vanilla "+context+" transform");
            }
            System.out.println("SIFT_SPAWN_EGG_PASS "+names[i]+" quads="+quads.size());
        }
        Item[] special={ModItems.ECHO_GOLEM_SPAWN_EGG,ModItems.DARK_SNIFFER_SPAWN_EGG,ModItems.SINGER_SPAWN_EGG};
        String[] ids={"echo_golem","dark_sniffer","singer"};
        for(int i=0;i<special.length;i++) {
            ItemStack stack=new ItemStack(special[i]);stack.set(DataComponents.CUSTOM_MODEL_DATA,new CustomModelData(1101+i));
            var model=client.getItemRenderer().getModel(stack,null,null,0);
            require(model.getParticleIcon().contents().name().equals(ResourceLocation.fromNamespaceAndPath("the_sift","item/"+ids[i]+"_spawn_egg")),"advancement icon "+ids[i]);
            require(ColorProviderRegistry.ITEM.get(stack.getItem()).getColor(stack,0)==-1,"advancement color "+ids[i]);
        }
        require((ColorProviderRegistry.ITEM.get(Items.PIG_SPAWN_EGG).getColor(new ItemStack(Items.PIG_SPAWN_EGG),0)&0xFFFFFF)==((SpawnEggItem)Items.PIG_SPAWN_EGG).getColor(0),"vanilla spawn egg tint unchanged");
    }
}

