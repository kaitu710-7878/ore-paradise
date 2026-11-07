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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class MultiPhaseOreExtractorBlock extends Block implements EntityBlock {
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
        if (blockEntityType == ModBlockEntities.MULTI_PHASE_ORE_EXTRACTOR.get()) {
            return (lvl, p, st, be) -> {
                if (be instanceof MultiPhaseOreExtractorBlockEntity extractorBe) {
                    MultiPhaseOreExtractorBlockEntity.serverTick(lvl, p, st, extractorBe);
                }
            };
        }
        return null;
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
            // Return to departure dimension (Overworld etc.)
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
                // Preload destination chunk
                targetLevel.getChunk(returnTargetPos);

                // Find a safe spot next to the extractor so the player doesn't clip/stick into the extractor
                BlockPos safePos = findSafeAdjacentPos(targetLevel, returnTargetPos);
                if (safePos != null) {
                    targetX = safePos.getX() + 0.5;
                    targetY = safePos.getY() + 0.05;
                    targetZ = safePos.getZ() + 0.5;
                } else {
                    // Fallback to directly on top of extractor with slight Y offset to avoid collision box sticking
                    targetX = returnTargetPos.getX() + 0.5;
                    targetY = returnTargetPos.getY() + 1.05;
                    targetZ = returnTargetPos.getZ() + 0.5;
                    // Ensure the player standing space is clear
                    BlockPos abovePos = returnTargetPos.above();
                    if (!targetLevel.getBlockState(abovePos).isAir()) {
                        targetLevel.destroyBlock(abovePos, true);
                    }
                }
            } else {
                BlockPos respawn = player.getRespawnPosition();
                if (respawn != null) {
                    targetX = respawn.getX() + 0.5;
                    targetY = respawn.getY() + 0.05;
                    targetZ = respawn.getZ() + 0.5;
                } else {
                    BlockPos sharedSpawn = targetLevel.getSharedSpawnPos();
                    targetX = sharedSpawn.getX() + 0.5;
                    targetY = sharedSpawn.getY() + 0.05;
                    targetZ = sharedSpawn.getZ() + 0.5;
                }
            }

            executeSafeTeleport(player, targetLevel, targetX, targetY, targetZ);
            player.sendSystemMessage(Component.translatable("message.ore_paradise.return_success"));
        } else {
            // Teleport to Ore Dimension
            ServerLevel oreDimension = server.getLevel(ModDimensions.ORE_DIMENSION_LEVEL_KEY);
            if (oreDimension == null) {
                player.sendSystemMessage(Component.translatable("message.ore_paradise.dimension_not_found"));
                return;
            }

            int spawnX = pos.getX();
            int spawnZ = pos.getZ();
            BlockPos playerSpawnPos = new BlockPos(spawnX, 64, spawnZ);

            // Preload chunk
            oreDimension.getChunk(playerSpawnPos);

            // Ensure player spawn area is clear (y=64, 65)
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

            executeSafeTeleport(player, oreDimension, spawnX + 0.5, 64.05, spawnZ + 0.5);
            player.sendSystemMessage(Component.translatable("message.ore_paradise.teleport_success"));
        }
    }

    private static void executeSafeTeleport(ServerPlayer player, ServerLevel targetLevel, double x, double y, double z) {
        player.setDeltaMovement(Vec3.ZERO);
        player.resetFallDistance();
        player.teleportTo(targetLevel, x, y, z, player.getYRot(), player.getXRot());
        player.connection.teleport(x, y, z, player.getYRot(), player.getXRot());
    }

    @Nullable
    private static BlockPos findSafeAdjacentPos(Level level, BlockPos origin) {
        Direction[] cardinals = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
        for (Direction dir : cardinals) {
            BlockPos candidate = origin.relative(dir);
            BlockPos above = candidate.above();
            BlockPos below = candidate.below();

            // Candidate and above must be air/non-solid, below must have solid ground
            boolean isAir = level.getBlockState(candidate).isAir() || !level.getBlockState(candidate).blocksMotion();
            boolean isAboveAir = level.getBlockState(above).isAir() || !level.getBlockState(above).blocksMotion();
            boolean isBelowSolid = level.getBlockState(below).isSolid();

            if (isAir && isAboveAir && isBelowSolid) {
                return candidate;
            }
        }
        return null;
    }
}
