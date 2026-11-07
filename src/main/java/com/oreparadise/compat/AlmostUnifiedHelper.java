package com.oreparadise.compat;

import com.oreparadise.OreParadise;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AlmostUnifiedHelper {
    private static boolean initialized = false;
    private static boolean available = false;
    @Nullable
    private static Object lookupInstance = null;
    @Nullable
    private static Method isLoadedMethod = null;
    @Nullable
    private static Method getReplacementMethod = null;

    private static synchronized void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        if (!ModList.get().isLoaded("almostunified")) {
            return;
        }

        try {
            Class<?> lookupClass = Class.forName("com.almostreliable.unified.api.AlmostUnifiedLookup");
            Field instanceField = lookupClass.getField("INSTANCE");
            lookupInstance = instanceField.get(null);
            isLoadedMethod = lookupClass.getMethod("isLoaded");
            getReplacementMethod = lookupClass.getMethod("getReplacementForItem", ItemLike.class);
            available = true;
            OreParadise.LOGGER.info("Almost Unified integration hooked successfully.");
        } catch (Throwable t) {
            OreParadise.LOGGER.warn("Failed to initialize Almost Unified integration: {}", t.getMessage());
            available = false;
        }
    }

    public static boolean isAvailable() {
        if (!initialized) {
            init();
        }
        if (!available || lookupInstance == null || isLoadedMethod == null) {
            return false;
        }
        try {
            return (Boolean) isLoadedMethod.invoke(lookupInstance);
        } catch (Throwable ignored) {
            return false;
        }
    }

    @Nullable
    public static Item getReplacementForItem(ItemLike itemLike) {
        if (!isAvailable() || getReplacementMethod == null || lookupInstance == null) {
            return null;
        }
        try {
            return (Item) getReplacementMethod.invoke(lookupInstance, itemLike);
        } catch (Throwable t) {
            return null;
        }
    }

    public static Item getUnifiedItem(Item item) {
        Item replacement = getReplacementForItem(item);
        return (replacement != null && replacement != Items.AIR) ? replacement : item;
    }

    public static BlockState getUnifiedBlockState(BlockState state) {
        Item item = state.getBlock().asItem();
        if (item == Items.AIR) {
            return state;
        }
        Item replacement = getReplacementForItem(item);
        if (replacement instanceof BlockItem blockItem) {
            return blockItem.getBlock().defaultBlockState();
        }
        return state;
    }
}
