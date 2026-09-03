package com.league_enchant.event;

import com.league_enchant.config.ModConfig;
import com.league_enchant.registry.ModEnchantments;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;

public class VampiricEventHandler {
    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(VampiricEventHandler::onAllowDamage);
    }

    public static float calculateHealAmount(float damageTaken, int enchantmentLevel) {
        if (damageTaken <= 0 || enchantmentLevel <= 0) {
            return 0.0f;
        }
        float lifestealPercent = enchantmentLevel * ModConfig.INSTANCE.vampiric.lifesteal_per_level;
        return damageTaken * lifestealPercent;
    }

    public static boolean isPhysicalDamage(DamageSource source) {
        if (source == null) {
            return false;
        }
        return !SpellVampEventHandler.isMagicOrSpellDamage(source);
    }

    private static boolean onAllowDamage(LivingEntity entity, DamageSource source, float amount) {
        if (amount > 0 && isPhysicalDamage(source) && source.getAttacker() instanceof LivingEntity attacker) {
            int level = EnchantmentHelper.getLevel(ModEnchantments.VAMPIRIC, attacker.getMainHandStack());
            if (level > 0) {
                float healAmount = calculateHealAmount(amount, level);
                if (healAmount > 0) {
                    attacker.heal(healAmount);
                }
            }
        }
        return true;
    }
}
