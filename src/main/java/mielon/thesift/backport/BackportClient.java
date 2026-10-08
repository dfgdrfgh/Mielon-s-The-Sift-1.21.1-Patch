package mielon.thesift.backport;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import mielon.thesift.backport.client.level.renderer.block_entity.ShelfRenderer;
public final class BackportClient {
 public static void initialize(){
  BlockEntityRendererRegistry.register(BackportContent.SHELF_ENTITY,ShelfRenderer::new);
  ModelLoadingPlugin.register(ctx->ctx.addModels(BackportContent.id("item/siftite_spear_in_hand")));
 }
}
