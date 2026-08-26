package com.league_enchant.enchantment;

import com.league_enchant.config.ModConfig;
import com.league_enchant.registry.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class SpellVampEnchantment extends Enchantment {
    public SpellVampEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinPower(int level) {
        return 35 + (level - 1) * 12;
    }

    @Override
    public int getMaxPower(int level) {
        return getMinPower(level) + 30;
    }

    @Override
    public int getMaxLevel() {
        return ModConfig.INSTANCE.spell_vamp.max_level;
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
        return super.canAccept(other) && other != ModEnchantments.VAMPIRIC;
    }
}
