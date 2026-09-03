package com.league_enchant.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ModConfigTest {

    @Test
    public void testDefaultConfigValues() {
        ModConfig config = new ModConfig();

        // Loot defaults
        assertTrue(config.loot.enable_end_city_loot);
        assertEquals(0.05f, config.loot.end_city_drop_chance, 0.0001f);
        assertFalse(config.loot.enable_nether_bridge_loot);
        assertFalse(config.loot.enable_ancient_city_loot);

        // Vampiric defaults
        assertEquals(0.01f, config.vampiric.lifesteal_per_level, 0.0001f);
        assertEquals(5, config.vampiric.max_level);

        // Spell Vamp defaults
        assertEquals(0.01f, config.spell_vamp.spell_lifesteal_per_level, 0.0001f);
        assertEquals(5, config.spell_vamp.max_level);

        // Aegis defaults
        assertEquals(0.05f, config.aegis.hp_reduction_base, 0.0001f);
        assertEquals(0.01f, config.aegis.hp_reduction_per_level, 0.0001f);
        assertEquals(5.0f, config.aegis.armor_bonus_base, 0.0001f);
        assertEquals(2.0f, config.aegis.armor_bonus_per_level, 0.0001f);
        assertEquals(2.0f, config.aegis.armor_toughness_base, 0.0001f);
        assertEquals(1.0f, config.aegis.armor_toughness_per_level, 0.0001f);
        assertEquals(0.90f, config.aegis.max_damage_reduction, 0.0001f);
        assertEquals(4, config.aegis.max_level);

        // Juggernaut defaults
        assertEquals(0.06f, config.juggernaut.hp_bonus_base, 0.0001f);
        assertEquals(0.015f, config.juggernaut.hp_bonus_per_level, 0.0001f);
        assertEquals(0.06f, config.juggernaut.armor_reduction_base, 0.0001f);
        assertEquals(0.01f, config.juggernaut.armor_reduction_per_level, 0.0001f);
        assertEquals(0.06f, config.juggernaut.armor_toughness_reduction_base, 0.0001f);
        assertEquals(0.01f, config.juggernaut.armor_toughness_reduction_per_level, 0.0001f);
        assertEquals(0.05f, config.juggernaut.hp_to_damage_ratio, 0.0001f);
        assertEquals(4, config.juggernaut.max_level);
    }

    @Test
    public void testJsonSerialization() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        ModConfig config = new ModConfig();
        config.loot.end_city_drop_chance = 0.10f;
        config.vampiric.lifesteal_per_level = 0.08f;

        String json = gson.toJson(config);
        assertTrue(json.contains("\"end_city_drop_chance\": 0.1"));
        assertTrue(json.contains("\"lifesteal_per_level\": 0.08"));

        ModConfig deserialized = gson.fromJson(json, ModConfig.class);
        assertEquals(0.10f, deserialized.loot.end_city_drop_chance, 0.0001f);
        assertEquals(0.08f, deserialized.vampiric.lifesteal_per_level, 0.0001f);
    }
}
