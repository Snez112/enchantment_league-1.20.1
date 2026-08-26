package com.league_enchant.magic.screen;

import com.league_enchant.LeagueEnchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static final ScreenHandlerType<ArcaneAnvilScreenHandler> ARCANE_ANVIL = Registry.register(
        Registries.SCREEN_HANDLER,
        new Identifier(LeagueEnchantment.MOD_ID, "arcane_anvil"),
        new ScreenHandlerType<>(ArcaneAnvilScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
    );

    public static void register() {
        LeagueEnchantment.LOGGER.info("Registering Screen Handlers for " + LeagueEnchantment.MOD_ID);
    }
}
