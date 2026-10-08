package mielon.thesift.platform;
import net.minecraft.world.level.block.Block;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
public final class BlockProperties { public static void registerStrippable(Block original, Block stripped) { StrippableBlockRegistry.register(original,stripped); } }
