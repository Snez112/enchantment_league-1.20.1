package com.league_enchant.registry;

import com.league_enchant.LeagueEnchantment;
import com.league_enchant.magic.block.ArcaneAnvilBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block ARCANE_ANVIL = registerBlock("arcane_anvil", new ArcaneAnvilBlock(
        AbstractBlock.Settings.copy(Blocks.ANVIL).nonOpaque()
    ));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(LeagueEnchantment.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(LeagueEnchantment.MOD_ID, name), new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks() {
        LeagueEnchantment.LOGGER.info("Registering Mod Blocks for " + LeagueEnchantment.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> {
            content.add(ARCANE_ANVIL);
        });
    }
}
