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
    public LethalityConfig lethality = new LethalityConfig();
    public InfinityAspectConfig infinity_aspect = new InfinityAspectConfig();

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
        public float lifesteal_per_level = 0.05f;
        public int max_level = 5;
    }

    public static class SpellVampConfig {
        public float spell_lifesteal_per_level = 0.05f;
        public int max_level = 5;
    }

    public static class LethalityConfig {
        public float base_bonus_damage_per_level = 1.5f;
        public float armor_scaling_per_level = 0.2f;
        public float max_armor_damage_cap = 6.0f;
        public int max_level = 3;
    }

    public static class InfinityAspectConfig {
        public float crit_bonus_level_1 = 0.20f;
        public float crit_bonus_level_2 = 0.30f;
        public float crit_bonus_level_3 = 0.40f;
        public float crit_bonus_level_4 = 0.50f;
        public int max_level = 4;
    }

    public static class AegisConfig {
        public float hp_reduction_per_level = 2.0f;
        public float armor_bonus_per_level = 3.0f;
        public float armor_to_damage_ratio_per_level = 0.10f;
        public int max_level = 4;
    }

    public static class JuggernautConfig {
        public float hp_bonus_per_level = 4.0f;
        public float armor_reduction_per_level = 2.0f;
        public float hp_to_damage_ratio_per_level = 0.05f;
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
