package com.league_enchant.event;

import com.league_enchant.config.ModConfig;
import com.league_enchant.registry.ModEnchantments;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class SpellVampEventHandler {
    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(SpellVampEventHandler::onAllowDamage);
    }

    public static float calculateHealAmount(float magicDamage, int level, float percentPerLevel) {
        if (magicDamage <= 0 || level <= 0 || percentPerLevel <= 0) {
            return 0.0f;
        }
        return magicDamage * (level * percentPerLevel);
    }

    public static boolean isMagicOrSpellDamage(DamageSource source) {
        if (source == null) {
            return false;
        }
        try {
            if (source.isOf(DamageTypes.MAGIC) 
                    || source.isOf(DamageTypes.INDIRECT_MAGIC) 
                    || source.isIn(DamageTypeTags.WITCH_RESISTANT_TO)
                    || source.isIn(TagKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("c", "magic")))
                    || source.isIn(TagKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("c", "spells")))) {
                return true;
            }
        } catch (Throwable ignored) {
            // Unit test or non-bootstrapped environment fallback
        }
        String name = source.getName();
        return name != null && (
            name.contains("magic") || name.contains("spell") || name.contains("arcane") || name.contains("fire") || name.contains("frost") || name.contains("lightning")
        );
    }

    private static boolean onAllowDamage(LivingEntity entity, DamageSource source, float amount) {
        if (amount > 0 && isMagicOrSpellDamage(source)) {
            Entity attackerEntity = source.getAttacker();
            if (attackerEntity instanceof LivingEntity attacker) {
                int level = EnchantmentHelper.getLevel(ModEnchantments.SPELL_VAMP, attacker.getMainHandStack());
                if (level == 0) {
                    level = EnchantmentHelper.getEquipmentLevel(ModEnchantments.SPELL_VAMP, attacker);
                }
                if (level > 0) {
                    float healAmount = calculateHealAmount(amount, level, ModConfig.INSTANCE.spell_vamp.spell_lifesteal_per_level);
                    if (healAmount > 0) {
                        attacker.heal(healAmount);
                    }
                }
            }
        }
        return true;
    }
}
