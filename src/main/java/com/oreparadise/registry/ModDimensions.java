package com.oreparadise.registry;

import com.oreparadise.OreParadise;
import com.oreparadise.worldgen.OreDimensionChunkGenerator;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModDimensions {
    public static final DeferredRegister<Codec<? extends ChunkGenerator>> CHUNK_GENERATORS =
            DeferredRegister.create(Registries.CHUNK_GENERATOR, OreParadise.MOD_ID);

    public static final RegistryObject<Codec<OreDimensionChunkGenerator>> ORE_CHUNK_GENERATOR =
            CHUNK_GENERATORS.register("ore_chunk_generator", () -> OreDimensionChunkGenerator.CODEC);

    public static final ResourceKey<Level> ORE_DIMENSION_LEVEL_KEY =
            ResourceKey.create(Registries.DIMENSION, new ResourceLocation(OreParadise.MOD_ID, "ore_dimension"));

    public static final ResourceKey<DimensionType> ORE_DIMENSION_TYPE_KEY =
            ResourceKey.create(Registries.DIMENSION_TYPE, new ResourceLocation(OreParadise.MOD_ID, "ore_dimension_type"));
}
