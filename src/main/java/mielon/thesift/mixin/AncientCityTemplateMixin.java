package mielon.thesift.mixin;

import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Keep The Sift's custom Ancient City center templates independent of the
 * priority of other mods' minecraft: structure data packs.
 *
 * Other Ancient City pieces, structure pools, and structure placement are untouched.
 */
@Mixin(StructureTemplateManager.class)
public abstract class AncientCityTemplateMixin {
    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private void the_sift$loadSonorousCityCenter(ResourceLocation id,
            CallbackInfoReturnable<Optional<StructureTemplate>> cir) {
        if (!"minecraft".equals(id.getNamespace())) {
            return;
        }

        // Enumerate exact vanilla IDs instead of applying a broad Ancient City override.
        String piece = switch (id.getPath()) {
            case "ancient_city/city_center/city_center_1" -> "city_center_1";
            case "ancient_city/city_center/city_center_2" -> "city_center_2";
            case "ancient_city/city_center/city_center_3" -> "city_center_3";
            default -> null;
        };
        if (piece == null) {
            return;
        }

        ResourceLocation owned = ResourceLocation.fromNamespaceAndPath(
                "the_sift", "ancient_city/city_center/" + piece);
        Optional<StructureTemplate> template = ((StructureTemplateManager) (Object) this).get(owned);
        // An incomplete/broken installation must still be able to use the vanilla template.
        if (template.isPresent()) {
            cir.setReturnValue(template);
        }
    }
}
