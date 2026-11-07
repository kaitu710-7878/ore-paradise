package com.oreparadise.registry;

import com.oreparadise.OreParadise;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, OreParadise.MOD_ID);

    public static final RegistryObject<CreativeModeTab> ORE_PARADISE_TAB =
            CREATIVE_MODE_TABS.register("ore_paradise_tab",
                    () -> CreativeModeTab.builder()
                            .icon(() -> new ItemStack(ModItems.MULTI_PHASE_ORE_EXTRACTOR.get()))
                            .title(Component.translatable("itemGroup.ore_paradise"))
                            .displayItems((parameters, output) -> {
                                output.accept(ModItems.MULTI_PHASE_ORE_EXTRACTOR.get());
                            })
                            .build());
}
