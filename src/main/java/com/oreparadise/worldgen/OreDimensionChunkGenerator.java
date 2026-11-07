package com.oreparadise.worldgen;

import com.oreparadise.compat.AlmostUnifiedHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.TagKey;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class OreDimensionChunkGenerator extends ChunkGenerator {
    public static final Codec<OreDimensionChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource)
            ).apply(instance, instance.stable(OreDimensionChunkGenerator::new))
    );

    public static final TagKey<Block> ORES_TAG = TagKey.create(Registries.BLOCK, new ResourceLocation("forge", "ores"));

    private static final List<BlockState> FALLBACK_ORES = List.of(
            Blocks.COAL_ORE.defaultBlockState(),
            Blocks.DEEPSLATE_COAL_ORE.defaultBlockState(),
            Blocks.IRON_ORE.defaultBlockState(),
            Blocks.DEEPSLATE_IRON_ORE.defaultBlockState(),
            Blocks.COPPER_ORE.defaultBlockState(),
            Blocks.DEEPSLATE_COPPER_ORE.defaultBlockState(),
            Blocks.GOLD_ORE.defaultBlockState(),
            Blocks.DEEPSLATE_GOLD_ORE.defaultBlockState(),
            Blocks.REDSTONE_ORE.defaultBlockState(),
            Blocks.DEEPSLATE_REDSTONE_ORE.defaultBlockState(),
            Blocks.EMERALD_ORE.defaultBlockState(),
            Blocks.DEEPSLATE_EMERALD_ORE.defaultBlockState(),
            Blocks.LAPIS_ORE.defaultBlockState(),
            Blocks.DEEPSLATE_LAPIS_ORE.defaultBlockState(),
            Blocks.DIAMOND_ORE.defaultBlockState(),
            Blocks.DEEPSLATE_DIAMOND_ORE.defaultBlockState(),
            Blocks.NETHER_QUARTZ_ORE.defaultBlockState(),
            Blocks.NETHER_GOLD_ORE.defaultBlockState(),
            Blocks.ANCIENT_DEBRIS.defaultBlockState()
    );

    private static List<BlockState> cachedOreStates = null;
    private static long lastCacheCheck = 0;

    public OreDimensionChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    private static List<BlockState> getAvailableOreStates() {
        long now = System.currentTimeMillis();
        if (cachedOreStates != null && (now - lastCacheCheck < 5000)) {
            return cachedOreStates;
        }
        lastCacheCheck = now;

        Set<BlockState> unifiedSet = new LinkedHashSet<>();
        try {
            ITag<Block> tag = ForgeRegistries.BLOCKS.tags().getTag(ORES_TAG);
            if (tag != null && !tag.isEmpty()) {
                tag.stream().forEach(block -> {
                    BlockState unifiedState = AlmostUnifiedHelper.getUnifiedBlockState(block.defaultBlockState());
                    unifiedSet.add(unifiedState);
                });
            }
        } catch (Exception ignored) {
        }

        if (unifiedSet.isEmpty()) {
            cachedOreStates = FALLBACK_ORES;
        } else {
            cachedOreStates = new ArrayList<>(unifiedSet);
        }
        return cachedOreStates;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, RandomState randomState,
                                                        StructureManager structureManager, ChunkAccess chunk) {
        return CompletableFuture.supplyAsync(() -> {
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            BlockState bedrockState = Blocks.BEDROCK.defaultBlockState();
            List<BlockState> oreStates = getAvailableOreStates();
            int oreCount = oreStates.size();

            int minX = chunk.getPos().getMinBlockX();
            int minZ = chunk.getPos().getMinBlockZ();

            for (int x = 0; x < 16; x++) {
                int worldX = minX + x;
                for (int z = 0; z < 16; z++) {
                    int worldZ = minZ + z;

                    // y = -64 is Bedrock
                    pos.set(worldX, -64, worldZ);
                    chunk.setBlockState(pos, bedrockState, false);

                    // y = -63 to 63 are unified random forge:ores
                    for (int y = -63; y <= 63; y++) {
                        pos.set(worldX, y, worldZ);
                        int hash = hashCoords(worldX, y, worldZ);
                        int index = Math.abs(hash % oreCount);
                        chunk.setBlockState(pos, oreStates.get(index), false);
                    }
                }
            }
            return chunk;
        }, executor);
    }

    private static int hashCoords(int x, int y, int z) {
        int h = x * 374761393 + y * 668265263 + z * 1013904223;
        h = (h ^ (h >>> 13)) * 1274126177;
        return h ^ (h >>> 16);
    }

    @Override
    public void buildSurface(WorldGenRegion level, StructureManager structureManager, RandomState random, ChunkAccess chunk) {
    }

    @Override
    public void applyCarvers(WorldGenRegion level, long seed, RandomState random, BiomeManager biomeManager,
                             StructureManager structureManager, ChunkAccess chunk, GenerationStep.Carving step) {
    }

    @Override
    public void applyBiomeDecoration(net.minecraft.world.level.WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager) {
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion level) {
    }

    @Override
    public int getMinY() {
        return -64;
    }

    @Override
    public int getGenDepth() {
        return 384;
    }

    @Override
    public int getSeaLevel() {
        return 63;
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level, RandomState random) {
        return 64;
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor level, RandomState random) {
        BlockState[] states = new BlockState[384];
        states[0] = Blocks.BEDROCK.defaultBlockState();
        List<BlockState> ores = getAvailableOreStates();
        int oreCount = ores.size();
        for (int i = 1; i <= 127; i++) {
            states[i] = ores.get(Math.abs(hashCoords(x, i - 64, z) % oreCount));
        }
        for (int i = 128; i < 384; i++) {
            states[i] = Blocks.AIR.defaultBlockState();
        }
        return new NoiseColumn(-64, states);
    }

    @Override
    public void addDebugScreenInfo(List<String> info, RandomState random, BlockPos pos) {
        info.add("Ore Dimension Flat Generator");
    }

    @Override
    public WeightedRandomList<MobSpawnSettings.SpawnerData> getMobsAt(Holder<Biome> biome, StructureManager structureManager,
                                                                    MobCategory category, BlockPos pos) {
        return WeightedRandomList.create();
    }
}
