package com.league_enchant.enchantment;

import com.league_enchant.config.ModConfig;
import com.league_enchant.registry.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class VampiricEnchantment extends Enchantment {
    public VampiricEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinPower(int level) {
        // High Eterna requirement for Zenith Enchanting Table (starts at 35)
        return 35 + (level - 1) * 12;
    }

    @Override
    public int getMaxPower(int level) {
        return getMinPower(level) + 30;
    }

    @Override
    public int getMaxLevel() {
        return ModConfig.INSTANCE.vampiric.max_level;
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
        return super.canAccept(other) && other != ModEnchantments.SPELL_VAMP;
    }
}
