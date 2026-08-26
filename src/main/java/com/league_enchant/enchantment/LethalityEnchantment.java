package com.league_enchant.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

public class LethalityEnchantment extends Enchantment {
    public LethalityEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinPower(int level) {
        // High Eterna requirement for Zenith Enchanting Table (starts at 40)
        return 40 + (level - 1) * 15;
    }

    @Override
    public int getMaxPower(int level) {
        return getMinPower(level) + 30;
    }

    @Override
    public int getMaxLevel() {
        return 3;
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
    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        if (!user.getWorld().isClient() && target instanceof LivingEntity livingTarget) {
            float bonusDamage = level * 1.5f;
            if (livingTarget.getArmor() > 0) {
                bonusDamage += Math.min(livingTarget.getArmor() * 0.2f * level, 6.0f);
            }
            DamageSource source = user.getDamageSources().magic();
            livingTarget.damage(source, bonusDamage);
        }
        super.onTargetDamaged(user, target, level);
    }
}
