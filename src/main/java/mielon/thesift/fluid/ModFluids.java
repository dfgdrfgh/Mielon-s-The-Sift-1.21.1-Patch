/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.FluidTags
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.LiquidBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.material.FlowingFluid
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.FluidState
 *  net.neoforged.neoforge.common.NeoForgeMod
 *  net.neoforged.neoforge.fluids.FluidInteractionRegistry
 *  net.neoforged.neoforge.fluids.FluidInteractionRegistry$InteractionInformation
 *  net.neoforged.neoforge.fluids.FluidType
 *  net.neoforged.neoforge.fluids.FluidType$Properties
 *  net.neoforged.neoforge.registries.NeoForgeRegistries
 */
package mielon.thesift.fluid;

import mielon.thesift.block.ModBlocks;
import mielon.thesift.fluid.IchorBucketItem;
import mielon.thesift.fluid.IchorFluid;
import mielon.thesift.fluid.IchorLiquidBlock;
import mielon.thesift.fluid.IchorState;
import mielon.thesift.fluid.IchorWaterlogging;
import mielon.thesift.platform.ModRegistration;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;





public final class ModFluids {
    public static final ResourceKey<Fluid> ICHOR_KEY = ResourceKey.create((ResourceKey)BuiltInRegistries.FLUID.key(), (ResourceLocation)ModFluids.id("ichor"));
    public static final ResourceKey<Fluid> FLOWING_ICHOR_KEY = ResourceKey.create((ResourceKey)BuiltInRegistries.FLUID.key(), (ResourceLocation)ModFluids.id("flowing_ichor"));
    public static final FlowingFluid ICHOR = (FlowingFluid)ModRegistration.register(BuiltInRegistries.FLUID, ICHOR_KEY, new IchorFluid.Source());
    public static final FlowingFluid FLOWING_ICHOR = (FlowingFluid)ModRegistration.register(BuiltInRegistries.FLUID, FLOWING_ICHOR_KEY, new IchorFluid.Flowing());
    public static final ResourceKey<Block> ICHOR_BLOCK_KEY = ResourceKey.create((ResourceKey)BuiltInRegistries.BLOCK.key(), (ResourceLocation)ModFluids.id("ichor"));
    public static final LiquidBlock ICHOR_BLOCK = ModRegistration.register(BuiltInRegistries.BLOCK, ICHOR_BLOCK_KEY, new IchorLiquidBlock(ICHOR, BlockBehaviour.Properties.ofFullCopy((BlockBehaviour)Blocks.WATER).lightLevel(state -> 12).noLootTable()));
    public static final ResourceKey<Item> ICHOR_BUCKET_KEY = ResourceKey.create((ResourceKey)BuiltInRegistries.ITEM.key(), (ResourceLocation)ModFluids.id("ichor_bucket"));
    public static final Item ICHOR_BUCKET = (Item)ModRegistration.register(BuiltInRegistries.ITEM, ICHOR_BUCKET_KEY, new IchorBucketItem((Fluid)ICHOR, new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)));

    private ModFluids() {
    }

    public static void initialize() {
        IchorState.source = ICHOR.getSource(false);
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath((String)"the_sift", (String)path);
    }
}

