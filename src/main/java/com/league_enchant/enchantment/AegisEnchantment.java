package com.league_enchant.enchantment;

import com.league_enchant.config.ModConfig;
import com.league_enchant.registry.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class AegisEnchantment extends Enchantment {
    public AegisEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentTarget.ARMOR, new EquipmentSlot[]{
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        });
    }

    @Override
    public int getMinPower(int level) {
        return 40 + (level - 1) * 15;
    }

    @Override
    public int getMaxPower(int level) {
        return getMinPower(level) + 30;
    }

    @Override
    public int getMaxLevel() {
        return ModConfig.INSTANCE.aegis.max_level;
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return false;
    }

    @Override
    protected boolean canAccept(Enchantment other) {
        if (!super.canAccept(other) || other instanceof JuggernautEnchantment || other == ModEnchantments.JUGGERNAUT) {
            return false;
        }
        try {
            Identifier id = Registries.ENCHANTMENT.getId(other);
            if (id != null) {
                String path = id.getPath().toLowerCase();
                if (path.contains("warding") || path.contains("reckless")) {
                    return false;
                }
            }
        } catch (Throwable ignored) {
        }
        String className = other.getClass().getSimpleName().toLowerCase();
        return !className.contains("warding") && !className.contains("reckless");
    }
}
