package com.league_enchant;

import com.league_enchant.config.ModConfig;
import com.league_enchant.event.AegisEventHandler;
import com.league_enchant.event.EndLootTableHandler;
import com.league_enchant.event.JuggernautEventHandler;
import com.league_enchant.event.SpellVampEventHandler;
import com.league_enchant.event.VampiricEventHandler;
import com.league_enchant.registry.ModAttributes;
import com.league_enchant.registry.ModEnchantments;
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
        ModEnchantments.registerModEnchantments();
        VampiricEventHandler.register();
        SpellVampEventHandler.register();
        AegisEventHandler.register();
        JuggernautEventHandler.register();
        EndLootTableHandler.register();
    }
}
