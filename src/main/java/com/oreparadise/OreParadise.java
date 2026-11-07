package com.oreparadise;

import com.oreparadise.registry.ModBlockEntities;
import com.oreparadise.registry.ModBlocks;
import com.oreparadise.registry.ModCreativeTabs;
import com.oreparadise.registry.ModDimensions;
import com.oreparadise.registry.ModItems;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(OreParadise.MOD_ID)
public class OreParadise {
    public static final String MOD_ID = "ore_paradise";
    public static final Logger LOGGER = LogUtils.getLogger();

    public OreParadise() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModDimensions.CHUNK_GENERATORS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Ore Paradise initialized successfully.");
    }
}
