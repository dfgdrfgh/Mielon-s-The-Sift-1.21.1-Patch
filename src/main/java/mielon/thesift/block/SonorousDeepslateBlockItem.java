package mielon.thesift.block;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.Block;

public final class SonorousDeepslateBlockItem extends BlockItem {
   public SonorousDeepslateBlockItem(Block block, Properties properties) {
      super(block, properties);
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
      BlockItemStateProperties properties = (BlockItemStateProperties)stack.get(DataComponents.BLOCK_STATE);
      SonorousDeepslateBlock.Mode mode = properties == null
         ? SonorousDeepslateBlock.Mode.HORN
         : (SonorousDeepslateBlock.Mode)properties.get(SonorousDeepslateBlock.MODE);
      if (mode == null) {
         mode = SonorousDeepslateBlock.Mode.HORN;
      }

      tooltip.add(Component.literal(mode == SonorousDeepslateBlock.Mode.HORN ? "Horn" : "Note").withStyle(ChatFormatting.YELLOW));
   }
}
