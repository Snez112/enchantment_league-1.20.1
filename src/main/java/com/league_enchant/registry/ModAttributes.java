package com.league_enchant.registry;

import com.league_enchant.LeagueEnchantment;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModAttributes {
    public static final EntityAttribute CRIT_DAMAGE = Registry.register(
        Registries.ATTRIBUTE,
        new Identifier(LeagueEnchantment.MOD_ID, "crit_damage"),
        new ClampedEntityAttribute("attribute.name.league_enchantment.crit_damage", 0.0, 0.0, 1024.0).setTracked(true)
    );

    public static void registerModAttributes() {
        LeagueEnchantment.LOGGER.info("Registering Mod Attributes for " + LeagueEnchantment.MOD_ID);
    }
}
