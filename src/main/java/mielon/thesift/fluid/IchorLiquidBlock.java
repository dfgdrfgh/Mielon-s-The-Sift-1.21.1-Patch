package mielon.thesift.fluid;

import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.FlowingFluid;

public final class IchorLiquidBlock extends LiquidBlock {
   public IchorLiquidBlock(FlowingFluid fluid, Properties properties) {
      super(fluid, properties);
   }
}
