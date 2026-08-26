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
        assertEquals(0.05f, config.vampiric.lifesteal_per_level, 0.0001f);
        assertEquals(5, config.vampiric.max_level);

        // Lethality defaults
        assertEquals(1.5f, config.lethality.base_bonus_damage_per_level, 0.0001f);
        assertEquals(0.2f, config.lethality.armor_scaling_per_level, 0.0001f);
        assertEquals(6.0f, config.lethality.max_armor_damage_cap, 0.0001f);
        assertEquals(3, config.lethality.max_level);

        // Infinity Aspect defaults
        assertEquals(0.20f, config.infinity_aspect.crit_bonus_level_1, 0.0001f);
        assertEquals(0.30f, config.infinity_aspect.crit_bonus_level_2, 0.0001f);
        assertEquals(0.40f, config.infinity_aspect.crit_bonus_level_3, 0.0001f);
        assertEquals(0.50f, config.infinity_aspect.crit_bonus_level_4, 0.0001f);
        assertEquals(4, config.infinity_aspect.max_level);
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
