package com.oreparadise.blockentity;

import com.oreparadise.compat.AlmostUnifiedHelper;
import com.oreparadise.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MultiPhaseOreExtractorBlockEntity extends BlockEntity {
    public static final TagKey<Item> ORES_TAG = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "ores"));

    private static final List<Item> FALLBACK_ORE_ITEMS = List.of(
            Items.COAL,
            Items.RAW_IRON,
            Items.RAW_COPPER,
            Items.RAW_GOLD,
            Items.REDSTONE,
            Items.LAPIS_LAZULI,
            Items.DIAMOND,
            Items.EMERALD,
            Items.QUARTZ,
            Items.ANCIENT_DEBRIS,
            Items.NETHERITE_SCRAP,
            Items.IRON_ORE,
            Items.GOLD_ORE,
            Items.COPPER_ORE,
            Items.DIAMOND_ORE,
            Items.EMERALD_ORE,
            Items.LAPIS_ORE,
            Items.REDSTONE_ORE,
            Items.COAL_ORE,
            Items.NETHER_QUARTZ_ORE,
            Items.NETHER_GOLD_ORE
    );

    private static List<Item> cachedOreItems = null;
    private static long lastCacheCheck = 0;

    @Nullable
    private ResourceLocation returnDimension = null;
    @Nullable
    private BlockPos returnPos = null;

    public MultiPhaseOreExtractorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.MULTI_PHASE_ORE_EXTRACTOR.get(), pos, blockState);
    }

    public void setReturnDestination(@Nullable ResourceLocation dimension, @Nullable BlockPos pos) {
        this.returnDimension = dimension;
        this.returnPos = pos;
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Nullable
    public ResourceLocation getReturnDimension() {
        return returnDimension;
    }

    @Nullable
    public BlockPos getReturnPos() {
        return returnPos;
    }

    private static List<Item> getAvailableOreItems() {
        long now = System.currentTimeMillis();
        if (cachedOreItems != null && (now - lastCacheCheck < 5000)) {
            return cachedOreItems;
        }
        lastCacheCheck = now;

        Set<Item> unifiedSet = new LinkedHashSet<>();
        try {
            ITag<Item> tag = ForgeRegistries.ITEMS.tags().getTag(ORES_TAG);
            if (tag != null && !tag.isEmpty()) {
                tag.stream().forEach(item -> {
                    Item unified = AlmostUnifiedHelper.getUnifiedItem(item);
                    unifiedSet.add(unified);
                });
            }
        } catch (Exception ignored) {
        }

        if (unifiedSet.isEmpty()) {
            cachedOreItems = FALLBACK_ORE_ITEMS;
        } else {
            cachedOreItems = new ArrayList<>(unifiedSet);
        }
        return cachedOreItems;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MultiPhaseOreExtractorBlockEntity be) {
        if (level.isClientSide()) {
            return;
        }

        List<Item> oreItems = getAvailableOreItems();
        if (oreItems.isEmpty()) {
            return;
        }

        // Pick 1 random ore item type
        Item selectedItem = oreItems.get(level.random.nextInt(oreItems.size()));
        ItemStack stackToInsert = new ItemStack(selectedItem, 64);

        // Try inserting into adjacent storage (6 directions)
        for (Direction direction : Direction.values()) {
            if (stackToInsert.isEmpty()) {
                break;
            }

            BlockEntity adjacentBe = level.getBlockEntity(pos.relative(direction));
            if (adjacentBe == null) {
                continue;
            }

            Direction side = direction.getOpposite();
            adjacentBe.getCapability(ForgeCapabilities.ITEM_HANDLER, side).ifPresent(handler -> {
                insertIntoHandler(handler, stackToInsert);
            });
        }
    }

    private static void insertIntoHandler(IItemHandler handler, ItemStack stack) {
        ItemStack remaining = ItemHandlerHelper.insertItem(handler, stack.copy(), false);
        stack.setCount(remaining.getCount());
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (returnDimension != null) {
            tag.putString("ReturnDimension", returnDimension.toString());
        }
        if (returnPos != null) {
            tag.put("ReturnPos", NbtUtils.writeBlockPos(returnPos));
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("ReturnDimension")) {
            returnDimension = new ResourceLocation(tag.getString("ReturnDimension"));
        } else {
            returnDimension = null;
        }
        if (tag.contains("ReturnPos")) {
            returnPos = NbtUtils.readBlockPos(tag.getCompound("ReturnPos"));
        } else {
            returnPos = null;
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            load(tag);
        }
    }
}
