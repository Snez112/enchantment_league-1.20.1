package com.league_enchant.event;

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
            if (source.isBuiltin() && LootTables.END_CITY_TREASURE_CHEST.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1))
                    .conditionally(RandomChanceLootCondition.builder(0.05f)) // 5% drop chance per chest
                    .with(ItemEntry.builder(Items.ENCHANTED_BOOK)
                        .weight(5)
                        .apply(new SetEnchantmentsLootFunction.Builder()
                            .enchantment(ModEnchantments.VAMPIRIC, UniformLootNumberProvider.create(1, 5))
                        )
                    )
                    .with(ItemEntry.builder(Items.ENCHANTED_BOOK)
                        .weight(5)
                        .apply(new SetEnchantmentsLootFunction.Builder()
                            .enchantment(ModEnchantments.LETHALITY, UniformLootNumberProvider.create(1, 3))
                        )
                    )
                    .with(ItemEntry.builder(Items.ENCHANTED_BOOK)
                        .weight(3)
                        .apply(new SetEnchantmentsLootFunction.Builder()
                            .enchantment(ModEnchantments.INFINITY_ASPECT, UniformLootNumberProvider.create(1, 2))
                        )
                    );

                tableBuilder.pool(poolBuilder);
            }
        });
    }
}
