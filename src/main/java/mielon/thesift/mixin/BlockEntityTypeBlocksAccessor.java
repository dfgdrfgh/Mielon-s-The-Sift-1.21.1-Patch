package mielon.thesift.mixin;

import java.util.Set;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({BlockEntityType.class})
public interface BlockEntityTypeBlocksAccessor {
   @Accessor("validBlocks")
   Set<Block> theSift$getValidBlocks();

   @Mutable
   @Accessor("validBlocks")
   void theSift$setValidBlocks(Set<Block> var1);
}
