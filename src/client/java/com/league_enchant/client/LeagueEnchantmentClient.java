package com.league_enchant.client;

import com.league_enchant.registry.ModEnchantments;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Map;

public class LeagueEnchantmentClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register custom tooltip descriptions for enchanted books and items
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            Map<Enchantment, Integer> enchantments;
            if (stack.isOf(Items.ENCHANTED_BOOK)) {
                enchantments = EnchantmentHelper.fromNbt(EnchantedBookItem.getEnchantmentNbt(stack));
            } else {
                enchantments = EnchantmentHelper.get(stack);
            }

            if (enchantments.containsKey(ModEnchantments.VAMPIRIC)) {
                lines.add(Text.translatable("enchantment.league_enchantment.vampiric.desc").formatted(Formatting.GRAY));
            }
            if (enchantments.containsKey(ModEnchantments.LETHALITY)) {
                lines.add(Text.translatable("enchantment.league_enchantment.lethality.desc").formatted(Formatting.GRAY));
            }
            if (enchantments.containsKey(ModEnchantments.INFINITY_ASPECT)) {
                lines.add(Text.translatable("enchantment.league_enchantment.infinity_aspect.desc").formatted(Formatting.GRAY));
            }
        });
    }
}
