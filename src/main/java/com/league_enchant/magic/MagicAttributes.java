package com.league_enchant.magic;

import com.league_enchant.LeagueEnchantment;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class MagicAttributes {
    public static final EntityAttribute MAX_MANA = Registry.register(
        Registries.ATTRIBUTE,
        new Identifier(LeagueEnchantment.MOD_ID, "max_mana"),
        new ClampedEntityAttribute("attribute.name.league_enchantment.max_mana", 100.0, 0.0, 10000.0).setTracked(true)
    );

    public static final EntityAttribute MANA_REGEN = Registry.register(
        Registries.ATTRIBUTE,
        new Identifier(LeagueEnchantment.MOD_ID, "mana_regen"),
        new ClampedEntityAttribute("attribute.name.league_enchantment.mana_regen", 1.0, 0.0, 500.0).setTracked(true)
    );

    public static final EntityAttribute SPELL_POWER = Registry.register(
        Registries.ATTRIBUTE,
        new Identifier(LeagueEnchantment.MOD_ID, "spell_power"),
        new ClampedEntityAttribute("attribute.name.league_enchantment.spell_power", 1.0, 0.0, 100.0).setTracked(true)
    );

    public static final EntityAttribute SPELL_RESIST = Registry.register(
        Registries.ATTRIBUTE,
        new Identifier(LeagueEnchantment.MOD_ID, "spell_resist"),
        new ClampedEntityAttribute("attribute.name.league_enchantment.spell_resist", 1.0, 0.0, 100.0).setTracked(true)
    );

    public static final EntityAttribute COOLDOWN_REDUCTION = Registry.register(
        Registries.ATTRIBUTE,
        new Identifier(LeagueEnchantment.MOD_ID, "cooldown_reduction"),
        new ClampedEntityAttribute("attribute.name.league_enchantment.cooldown_reduction", 0.0, 0.0, 0.90).setTracked(true)
    );

    public static final EntityAttribute CAST_TIME_REDUCTION = Registry.register(
        Registries.ATTRIBUTE,
        new Identifier(LeagueEnchantment.MOD_ID, "cast_time_reduction"),
        new ClampedEntityAttribute("attribute.name.league_enchantment.cast_time_reduction", 0.0, 0.0, 0.90).setTracked(true)
    );

    public static void register() {
        LeagueEnchantment.LOGGER.info("Registering Magic & Spell Attributes for " + LeagueEnchantment.MOD_ID);
    }
}
