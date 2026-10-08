package mielon.thesift.backport;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
public final class SpearLunge {
 public static final ResourceKey<Enchantment> KEY=ResourceKey.create(Registries.ENCHANTMENT,ResourceLocation.fromNamespaceAndPath("the_sift","lunge"));
 public static void apply(LivingEntity user){
  if(user.isInWater()||user.isPassenger()||user.isFallFlying())return;
  if(user instanceof Player p && !p.isCreative() && p.getFoodData().getFoodLevel()<7)return;
  var holder=user.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(KEY.location());if(holder.isEmpty())return;
  int level=EnchantmentHelper.getItemEnchantmentLevel(holder.get(),user.getMainHandItem());if(level<=0)return;
  Vec3 dir=user.getViewVector(1).multiply(1,0,1).scale(.458*level);
  user.setDeltaMovement(user.getDeltaMovement().add(dir));user.hurtMarked=true;user.hasImpulse=true;
  user.getMainHandItem().hurtAndBreak(1,user,EquipmentSlot.MAINHAND);
  if(user instanceof Player p)p.causeFoodExhaustion(4*level);
  if(user instanceof ServerPlayer p)p.connection.send(new ClientboundSetEntityMotionPacket(p));
  user.level().playSound(null,user.blockPosition(),BackportContent.LUNGE_SOUNDS[Math.min(2,level-1)],user.getSoundSource(),1,1);
 }
}
