package mielon.thesift.mixin;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(BlockBehaviour.Properties.class)
public interface BlockPropertiesAccessor { @Accessor("drops") void theSift$setDrops(ResourceKey<LootTable> drops); }
