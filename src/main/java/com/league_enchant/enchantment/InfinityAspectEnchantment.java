package com.league_enchant.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;

public class InfinityAspectEnchantment extends Enchantment {
    public InfinityAspectEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinPower(int level) {
        // Ultra-high Eterna requirement for Zenith Enchanting Table (starts at 55)
        return 55 + (level - 1) * 20;
    }

    @Override
    public int getMaxPower(int level) {
        return getMinPower(level) + 35;
    }

    @Override
    public int getMaxLevel() {
        return 2;
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
    public float getAttackDamage(int level, EntityGroup group) {
        return level * 2.5f;
    }
}
