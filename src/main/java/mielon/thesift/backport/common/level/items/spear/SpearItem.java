package mielon.thesift.backport.common.level.items.spear;

import mielon.thesift.backport.client.registries.ModSoundEvents;

import mielon.thesift.backport.common.level.components.AttackRange;
import mielon.thesift.backport.common.level.components.KineticWeapon;
import mielon.thesift.backport.common.level.components.PiercingWeapon;
import mielon.thesift.backport.common.level.components.SwingAnimation;
import mielon.thesift.backport.common.level.components.SwingAnimationType;
import mielon.thesift.backport.common.level.components.UseEffects;
import mielon.thesift.backport.common.registries.items.ModDataComponents;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SpearItem extends TieredItem {
   public SpearItem(
      Tier tier,
      float attackDuration,
      float damageMultiplier,
      float delay,
      float dismountTime,
      float dismountThreshold,
      float knockbackTime,
      float knockbackThreshold,
      float damageTime,
      float damageThreshold,
      Properties properties
   ) {
      super(
         tier,
         properties.durability(tier.getUses())
            .component(
               ModDataComponents.KINETIC_WEAPON.get(),
               new KineticWeapon(
                  10,
                  (int)(delay * 20.0F),
                  KineticWeapon.Condition.ofAttackerSpeed((int)(dismountTime * 20.0F), dismountThreshold),
                  KineticWeapon.Condition.ofAttackerSpeed((int)(knockbackTime * 20.0F), knockbackThreshold),
                  KineticWeapon.Condition.ofRelativeSpeed((int)(damageTime * 20.0F), damageThreshold),
                  0.38F,
                  damageMultiplier,
                  Optional.of(tier == Tiers.WOOD ? ModSoundEvents.SPEAR_WOOD_USE : ModSoundEvents.SPEAR_USE),
                  Optional.of(tier == Tiers.WOOD ? ModSoundEvents.SPEAR_WOOD_HIT : ModSoundEvents.SPEAR_HIT)
               )
            )
            .component(
               ModDataComponents.PIERCING_WEAPON.get(),
               new PiercingWeapon(
                  true,
                  false,
                  Optional.of(tier == Tiers.WOOD ? ModSoundEvents.SPEAR_WOOD_ATTACK : ModSoundEvents.SPEAR_ATTACK),
                  Optional.of(tier == Tiers.WOOD ? ModSoundEvents.SPEAR_WOOD_HIT : ModSoundEvents.SPEAR_HIT)
               )
            )
            .component(ModDataComponents.ATTACK_RANGE.get(), new AttackRange(2.0F, 4.5F, 2.0F, 6.5F, 0.125F, 0.5F))
            .component(ModDataComponents.MINIMUM_ATTACK_CHARGE.get(), 1.0F)
            .component(ModDataComponents.SWING_ANIMATION.get(), new SwingAnimation(SwingAnimationType.STAB, (int)(attackDuration * 20.0F)))
            .component(ModDataComponents.USE_EFFECTS.get(), new UseEffects(true, false, 1.0F))
            .attributes(
               ItemAttributeModifiers.builder()
                  .add(
                     Attributes.ATTACK_DAMAGE,
                     new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 0.0F + tier.getAttackDamageBonus(), Operation.ADD_VALUE),
                     EquipmentSlotGroup.MAINHAND
                  )
                  .add(
                     Attributes.ATTACK_SPEED,
                     new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, 1.0F / attackDuration - 4.0, Operation.ADD_VALUE),
                     EquipmentSlotGroup.MAINHAND
                  )
                  .build()
            )
      );
   }

   public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
      KineticWeapon kineticWeapon = (KineticWeapon)stack.get(ModDataComponents.KINETIC_WEAPON.get());
      if (kineticWeapon != null && !level.isClientSide()) {
         kineticWeapon.damageEntities(
            stack,
            remainingUseDuration,
            livingEntity,
            livingEntity.getUsedItemHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND
         );
      }
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
      ItemStack heldItem = player.getItemInHand(usedHand);
      KineticWeapon kineticWeapon = (KineticWeapon)heldItem.get(ModDataComponents.KINETIC_WEAPON.get());
      if (kineticWeapon != null) {
         player.startUsingItem(usedHand);
         kineticWeapon.makeSound(player);
         return InteractionResultHolder.consume(heldItem);
      } else {
         return InteractionResultHolder.pass(heldItem);
      }
   }

   public int getEnchantmentValue() {
      return this.getTier().getEnchantmentValue();
   }

   public UseAnim getUseAnimation(ItemStack stack) {
      return UseAnim.SPEAR;
   }

   public int getUseDuration(ItemStack stack, LivingEntity entity) {
      return 72000;
   }

   public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
      return false;
   }

   public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      return true;
   }

   public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      if (attacker instanceof Player player && !player.isCreative()) {
         stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
      }
   }
}
