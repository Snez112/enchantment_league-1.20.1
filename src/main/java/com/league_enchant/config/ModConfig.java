package com.league_enchant.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.league_enchant.LeagueEnchantment;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static ModConfig INSTANCE = new ModConfig();

    public LootConfig loot = new LootConfig();
    public VampiricConfig vampiric = new VampiricConfig();

    public AegisConfig aegis = new AegisConfig();
    public JuggernautConfig juggernaut = new JuggernautConfig();
    public SpellVampConfig spell_vamp = new SpellVampConfig();

    public static class LootConfig {
        public boolean enable_end_city_loot = true;
        public float end_city_drop_chance = 0.05f;

        public boolean enable_nether_bridge_loot = false;
        public float nether_bridge_drop_chance = 0.05f;

        public boolean enable_ancient_city_loot = false;
        public float ancient_city_drop_chance = 0.05f;
    }

    public static class VampiricConfig {
        public float lifesteal_per_level = 0.01f;
        public int max_level = 5;
    }

    public static class SpellVampConfig {
        public float spell_lifesteal_per_level = 0.01f;
        public int max_level = 5;
    }

    public static class AegisConfig {
        public float hp_reduction_base = 0.05f;
        public float hp_reduction_per_level = 0.01f;
        public float armor_bonus_base = 5.0f;
        public float armor_bonus_per_level = 2.0f;
        public float armor_toughness_base = 2.0f;
        public float armor_toughness_per_level = 1.0f;
        public float armor_to_damage_ratio_per_level = 0.10f;
        public float max_damage_reduction = 0.90f;
        public int max_level = 4;
    }

    public static class JuggernautConfig {
        public float hp_bonus_base = 0.06f;
        public float hp_bonus_per_level = 0.015f;
        public float armor_reduction_base = 0.06f;
        public float armor_reduction_per_level = 0.01f;
        public float armor_toughness_reduction_base = 0.06f;
        public float armor_toughness_reduction_per_level = 0.01f;
        public float hp_to_damage_ratio = 0.05f;
        public int max_level = 4;
    }

    public static void load() {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        File configFile = configDir.resolve("league_enchantment.json").toFile();

        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                ModConfig loaded = GSON.fromJson(reader, ModConfig.class);
                if (loaded != null) {
                    INSTANCE = loaded;
                    LeagueEnchantment.LOGGER.info("Successfully loaded config from league_enchantment.json");
                    return;
                }
            } catch (IOException e) {
                LeagueEnchantment.LOGGER.error("Failed to load league_enchantment.json, using defaults", e);
            }
        }

        // If file doesn't exist or failed to load, save defaults
        save();
    }

    public static void save() {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        File configFile = configDir.resolve("league_enchantment.json").toFile();

        try {
            if (!configFile.getParentFile().exists()) {
                configFile.getParentFile().mkdirs();
            }
            try (FileWriter writer = new FileWriter(configFile)) {
                GSON.toJson(INSTANCE, writer);
                LeagueEnchantment.LOGGER.info("Saved default config to league_enchantment.json");
            }
        } catch (IOException e) {
            LeagueEnchantment.LOGGER.error("Failed to save config to league_enchantment.json", e);
        }
    }
}
