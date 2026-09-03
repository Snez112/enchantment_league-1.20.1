package com.league_enchant.event;

import com.league_enchant.config.ModConfig;
import com.league_enchant.registry.ModEnchantments;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetEnchantmentsLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;

public class EndLootTableHandler {
    public static void register() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (!source.isBuiltin()) {
                return;
            }

            ModConfig.LootConfig lootConfig = ModConfig.INSTANCE.loot;

            boolean matchesEndCity = lootConfig.enable_end_city_loot && LootTables.END_CITY_TREASURE_CHEST.equals(id);
            boolean matchesNetherBridge = lootConfig.enable_nether_bridge_loot && LootTables.NETHER_BRIDGE_CHEST.equals(id);
            boolean matchesAncientCity = lootConfig.enable_ancient_city_loot && LootTables.ANCIENT_CITY_CHEST.equals(id);

            if (matchesEndCity || matchesNetherBridge || matchesAncientCity) {
                float dropChance = matchesEndCity ? lootConfig.end_city_drop_chance
                        : matchesNetherBridge ? lootConfig.nether_bridge_drop_chance
                        : lootConfig.ancient_city_drop_chance;

                LootPool.Builder poolBuilder = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1))
                    .conditionally(RandomChanceLootCondition.builder(dropChance))
                    .with(ItemEntry.builder(Items.ENCHANTED_BOOK)
                        .weight(5)
                        .apply(new SetEnchantmentsLootFunction.Builder()
                            .enchantment(ModEnchantments.VAMPIRIC, UniformLootNumberProvider.create(1, ModConfig.INSTANCE.vampiric.max_level))
                        )
                    )
                    .with(ItemEntry.builder(Items.ENCHANTED_BOOK)
                        .weight(4)
                        .apply(new SetEnchantmentsLootFunction.Builder()
                            .enchantment(ModEnchantments.AEGIS, UniformLootNumberProvider.create(1, ModConfig.INSTANCE.aegis.max_level))
                        )
                    )
                    .with(ItemEntry.builder(Items.ENCHANTED_BOOK)
                        .weight(4)
                        .apply(new SetEnchantmentsLootFunction.Builder()
                            .enchantment(ModEnchantments.JUGGERNAUT, UniformLootNumberProvider.create(1, ModConfig.INSTANCE.juggernaut.max_level))
                        )
                    )
                    .with(ItemEntry.builder(Items.ENCHANTED_BOOK)
                        .weight(5)
                        .apply(new SetEnchantmentsLootFunction.Builder()
                            .enchantment(ModEnchantments.SPELL_VAMP, UniformLootNumberProvider.create(1, ModConfig.INSTANCE.spell_vamp.max_level))
                        )
                    );

                tableBuilder.pool(poolBuilder);
            }
        });
    }
}
