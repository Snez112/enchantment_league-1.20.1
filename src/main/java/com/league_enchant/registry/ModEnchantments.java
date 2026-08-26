package com.league_enchant.registry;

import com.league_enchant.LeagueEnchantment;
import com.league_enchant.enchantment.AegisEnchantment;
import com.league_enchant.enchantment.InfinityAspectEnchantment;
import com.league_enchant.enchantment.JuggernautEnchantment;
import com.league_enchant.enchantment.LethalityEnchantment;
import com.league_enchant.enchantment.SpellVampEnchantment;
import com.league_enchant.enchantment.VampiricEnchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEnchantments {
    public static final Enchantment VAMPIRIC = register("vampiric", new VampiricEnchantment());
    public static final Enchantment LETHALITY = register("lethality", new LethalityEnchantment());
    public static final Enchantment INFINITY_ASPECT = register("infinity_aspect", new InfinityAspectEnchantment());
    public static final Enchantment AEGIS = register("aegis", new AegisEnchantment());
    public static final Enchantment JUGGERNAUT = register("juggernaut", new JuggernautEnchantment());
    public static final Enchantment SPELL_VAMP = register("spell_vamp", new SpellVampEnchantment());

    private static Enchantment register(String name, Enchantment enchantment) {
        return Registry.register(
            Registries.ENCHANTMENT,
            new Identifier(LeagueEnchantment.MOD_ID, name),
            enchantment
        );
    }

    public static void registerModEnchantments() {
        LeagueEnchantment.LOGGER.info("Registering Mod Enchantments for " + LeagueEnchantment.MOD_ID);
    }
}
