package com.oreparadise.registry;

import com.oreparadise.OreParadise;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, OreParadise.MOD_ID);

    public static final RegistryObject<Item> MULTI_PHASE_ORE_EXTRACTOR =
            ITEMS.register("multi_phase_ore_extractor",
                    () -> new BlockItem(ModBlocks.MULTI_PHASE_ORE_EXTRACTOR.get(), new Item.Properties()) {
                        @Override
                        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltips, TooltipFlag flag) {
                            super.appendHoverText(stack, level, tooltips, flag);
                            tooltips.add(Component.translatable("tooltip.ore_paradise.multi_phase_ore_extractor.desc1").withStyle(ChatFormatting.GRAY));
                            tooltips.add(Component.translatable("tooltip.ore_paradise.multi_phase_ore_extractor.desc2").withStyle(ChatFormatting.GOLD));
                        }
                    });
}
