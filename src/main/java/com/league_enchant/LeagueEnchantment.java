package com.league_enchant;

import com.league_enchant.event.EndLootTableHandler;
import com.league_enchant.event.VampiricEventHandler;
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
        ModEnchantments.registerModEnchantments();
        VampiricEventHandler.register();
        EndLootTableHandler.register();
    }
}
