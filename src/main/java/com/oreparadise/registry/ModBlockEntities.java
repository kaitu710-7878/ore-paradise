package com.oreparadise.registry;

import com.oreparadise.OreParadise;
import com.oreparadise.blockentity.MultiPhaseOreExtractorBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, OreParadise.MOD_ID);

    public static final RegistryObject<BlockEntityType<MultiPhaseOreExtractorBlockEntity>> MULTI_PHASE_ORE_EXTRACTOR =
            BLOCK_ENTITIES.register("multi_phase_ore_extractor",
                    () -> BlockEntityType.Builder.of(MultiPhaseOreExtractorBlockEntity::new,
                            ModBlocks.MULTI_PHASE_ORE_EXTRACTOR.get()).build(null));
}
