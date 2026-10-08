package mielon.thesift.platform;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import java.util.function.Supplier;
public record SimpleTier(TagKey<Block> incorrect, int uses, float speed, float damage, int enchantment, Supplier<Ingredient> repair) implements Tier {
 public int getUses(){return uses;} public float getSpeed(){return speed;} public float getAttackDamageBonus(){return damage;} public int getEnchantmentValue(){return enchantment;}
 public TagKey<Block> getIncorrectBlocksForDrops(){return incorrect;} public Ingredient getRepairIngredient(){return repair.get();}
}
