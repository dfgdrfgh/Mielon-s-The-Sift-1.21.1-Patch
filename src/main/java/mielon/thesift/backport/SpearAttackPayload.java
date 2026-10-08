package mielon.thesift.backport;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import mielon.thesift.backport.common.level.items.spear.SpearItem;
import mielon.thesift.backport.common.level.components.PiercingWeapon;
import mielon.thesift.backport.common.api.extensions.entity.spear.PlayerSpearHandler;
import mielon.thesift.backport.common.api.extensions.entity.spear.MobSpearHandler;
public record SpearAttackPayload() implements CustomPacketPayload {
 public static final Type<SpearAttackPayload> TYPE=new Type<>(BackportContent.id("spear_attack"));
 public static final StreamCodec<RegistryFriendlyByteBuf,SpearAttackPayload> CODEC=StreamCodec.unit(new SpearAttackPayload());
 public Type<SpearAttackPayload> type(){return TYPE;}
 public static void register(){PayloadTypeRegistry.playC2S().register(TYPE,CODEC);ServerPlayNetworking.registerGlobalReceiver(TYPE,(payload,ctx)->ctx.server().execute(()->{
  var p=ctx.player();var stack=p.getMainHandItem();if(!(stack.getItem() instanceof SpearItem)||p.isSpectator()||!p.isAlive()||p.isUsingItem()||((PlayerSpearHandler)p).theSift$cannotAttackWithItem(stack,5))return;
  var weapon=PiercingWeapon.get(stack);if(weapon!=null){weapon.attack(p,EquipmentSlot.MAINHAND);((MobSpearHandler)p).theSift$onAttack();}
 }));}
}
