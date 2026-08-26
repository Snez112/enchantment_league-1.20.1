package com.league_enchant.magic.effect;

import com.league_enchant.LeagueEnchantment;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEffects {
    public static final StatusEffect FROSTBITE = register("frostbite", new FrostbiteEffect());
    public static final StatusEffect EVASION = register("evasion", new EvasionEffect());

    private static StatusEffect register(String name, StatusEffect effect) {
        return Registry.register(Registries.STATUS_EFFECT, new Identifier(LeagueEnchantment.MOD_ID, name), effect);
    }

    public static void registerModEffects() {
        LeagueEnchantment.LOGGER.info("Registering Magic Status Effects for " + LeagueEnchantment.MOD_ID);
    }
}
