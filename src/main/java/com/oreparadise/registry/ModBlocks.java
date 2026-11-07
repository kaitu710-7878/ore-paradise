package com.oreparadise.registry;

import com.oreparadise.OreParadise;
import com.oreparadise.block.MultiPhaseOreExtractorBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, OreParadise.MOD_ID);

    public static final RegistryObject<Block> MULTI_PHASE_ORE_EXTRACTOR =
            BLOCKS.register("multi_phase_ore_extractor",
                    () -> new MultiPhaseOreExtractorBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(3.5f, 6.0f)
                            .sound(SoundType.METAL)
                            .requiresCorrectToolForDrops()));
}
