package com.league_enchant;

import com.league_enchant.config.ModConfig;
import com.league_enchant.event.AegisJuggernautEventHandler;
import com.league_enchant.event.AttributeSyncHandler;
import com.league_enchant.event.EndLootTableHandler;
import com.league_enchant.event.InfinityAspectEventHandler;
import com.league_enchant.event.SpellVampEventHandler;
import com.league_enchant.event.VampiricEventHandler;
import com.league_enchant.magic.MagicAttributes;
import com.league_enchant.magic.ServerMagicEventHandler;
import com.league_enchant.magic.SpellRegistry;
import com.league_enchant.magic.effect.ModEffects;
import com.league_enchant.magic.screen.ModScreenHandlers;
import com.league_enchant.registry.ModAttributes;
import com.league_enchant.registry.ModBlocks;
import com.league_enchant.registry.ModEnchantments;
import com.league_enchant.registry.ModItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LeagueEnchantment implements ModInitializer {
    public static final String MOD_ID = "league_enchantment";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing LeagueEnchantment Mod...");
        ModConfig.load();
        ModAttributes.registerModAttributes();
        MagicAttributes.register();
        ModBlocks.registerModBlocks();
        ModItems.registerModItems();
        ModScreenHandlers.register();
        ModEffects.registerModEffects();
        ModEnchantments.registerModEnchantments();
        SpellRegistry.registerSpells();
        VampiricEventHandler.register();
        SpellVampEventHandler.register();
        InfinityAspectEventHandler.register();
        AegisJuggernautEventHandler.register();
        AttributeSyncHandler.register();
        ServerMagicEventHandler.register();
        com.league_enchant.network.ModPackets.registerC2SPackets();
        EndLootTableHandler.register();
    }
}
