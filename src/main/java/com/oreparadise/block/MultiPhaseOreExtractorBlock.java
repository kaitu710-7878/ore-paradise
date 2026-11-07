package com.oreparadise.block;

import com.oreparadise.blockentity.MultiPhaseOreExtractorBlockEntity;
import com.oreparadise.registry.ModBlockEntities;
import com.oreparadise.registry.ModBlocks;
import com.oreparadise.registry.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class MultiPhaseOreExtractorBlock extends BaseEntityBlock {
    public MultiPhaseOreExtractorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MultiPhaseOreExtractorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return null;
        }
        return createTickerHelper(blockEntityType, ModBlockEntities.MULTI_PHASE_ORE_EXTRACTOR.get(), MultiPhaseOreExtractorBlockEntity::serverTick);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                teleportPlayer(serverPlayer, level, pos);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        if (!level.isClientSide()) {
            player.sendSystemMessage(Component.translatable("message.ore_paradise.shift_click_hint"));
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private void teleportPlayer(ServerPlayer player, Level currentLevel, BlockPos pos) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }

        boolean isInOreDimension = currentLevel.dimension().equals(ModDimensions.ORE_DIMENSION_LEVEL_KEY);

        if (isInOreDimension) {
            // Return to previous dimension or overworld
            BlockEntity be = currentLevel.getBlockEntity(pos);
            ResourceLocation returnDim = null;
            BlockPos returnTargetPos = null;

            if (be instanceof MultiPhaseOreExtractorBlockEntity extractorBe) {
                returnDim = extractorBe.getReturnDimension();
                returnTargetPos = extractorBe.getReturnPos();
            }

            ResourceKey<Level> targetKey = returnDim != null
                    ? ResourceKey.create(Registries.DIMENSION, returnDim)
                    : Level.OVERWORLD;

            ServerLevel targetLevel = server.getLevel(targetKey);
            if (targetLevel == null) {
                targetLevel = server.getLevel(Level.OVERWORLD);
            }

            if (targetLevel == null) {
                return;
            }

            double targetX, targetY, targetZ;
            if (returnTargetPos != null) {
                targetX = returnTargetPos.getX() + 0.5;
                targetY = returnTargetPos.getY() + 1.0;
                targetZ = returnTargetPos.getZ() + 0.5;
            } else {
                BlockPos respawn = player.getRespawnPosition();
                if (respawn != null) {
                    targetX = respawn.getX() + 0.5;
                    targetY = respawn.getY();
                    targetZ = respawn.getZ() + 0.5;
                } else {
                    BlockPos sharedSpawn = targetLevel.getSharedSpawnPos();
                    targetX = sharedSpawn.getX() + 0.5;
                    targetY = sharedSpawn.getY();
                    targetZ = sharedSpawn.getZ() + 0.5;
                }
            }

            player.teleportTo(targetLevel, targetX, targetY, targetZ, player.getYRot(), player.getXRot());
            player.sendSystemMessage(Component.translatable("message.ore_paradise.return_success"));
        } else {
            // Teleport to Ore Dimension
            ServerLevel oreDimension = server.getLevel(ModDimensions.ORE_DIMENSION_LEVEL_KEY);
            if (oreDimension == null) {
                player.sendSystemMessage(Component.translatable("message.ore_paradise.dimension_not_found"));
                return;
            }

            // Spawn coordinates: height y=64 is right above ore layer (y=63)
            int spawnX = pos.getX();
            int spawnZ = pos.getZ();
            BlockPos playerSpawnPos = new BlockPos(spawnX, 64, spawnZ);

            // Ensure player spawn area is clear
            oreDimension.setBlockAndUpdate(playerSpawnPos, Blocks.AIR.defaultBlockState());
            oreDimension.setBlockAndUpdate(playerSpawnPos.above(), Blocks.AIR.defaultBlockState());

            // Generate return extractor adjacent to player spawn
            Direction lookDir = player.getDirection();
            BlockPos returnExtractorPos = playerSpawnPos.relative(lookDir);
            oreDimension.setBlockAndUpdate(returnExtractorPos, ModBlocks.MULTI_PHASE_ORE_EXTRACTOR.get().defaultBlockState());

            BlockEntity newBe = oreDimension.getBlockEntity(returnExtractorPos);
            if (newBe instanceof MultiPhaseOreExtractorBlockEntity returnExtractorBe) {
                returnExtractorBe.setReturnDestination(currentLevel.dimension().location(), pos);
            }

            // Teleport player
            player.teleportTo(oreDimension, spawnX + 0.5, 64.0, spawnZ + 0.5, player.getYRot(), player.getXRot());
            player.sendSystemMessage(Component.translatable("message.ore_paradise.teleport_success"));
        }
    }
}
