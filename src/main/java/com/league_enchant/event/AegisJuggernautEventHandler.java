package com.league_enchant.event;

import com.league_enchant.config.ModConfig;
import com.league_enchant.registry.ModEnchantments;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class AegisJuggernautEventHandler {
    private static final UUID AEGIS_HEALTH_UUID = UUID.fromString("7a091410-d003-4b92-8086-13d85449df51");
    private static final UUID AEGIS_ARMOR_UUID = UUID.fromString("6a091410-d003-4b92-8086-13d85449df52");
    private static final UUID JUGGERNAUT_HEALTH_UUID = UUID.fromString("5a091410-d003-4b92-8086-13d85449df53");
    private static final UUID JUGGERNAUT_ARMOR_UUID = UUID.fromString("4a091410-d003-4b92-8086-13d85449df54");

    private static final ThreadLocal<Boolean> IS_PROCESSING = ThreadLocal.withInitial(() -> false);
    private static int tickCounter = 0;

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(AegisJuggernautEventHandler::onAllowDamage);
        ServerTickEvents.END_SERVER_TICK.register(AegisJuggernautEventHandler::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        tickCounter++;
        if (tickCounter % 10 == 0) {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                updatePlayerAttributes(player);
            }
        }
    }

    public static float calculateAegisBonusDamage(float totalArmor, int level, float ratioPerLevel) {
        if (totalArmor <= 0 || level <= 0 || ratioPerLevel <= 0) {
            return 0.0f;
        }
        return totalArmor * (level * ratioPerLevel);
    }

    public static float calculateJuggernautBonusDamage(float maxHp, int level, float ratioPerLevel) {
        if (maxHp <= 0 || level <= 0 || ratioPerLevel <= 0) {
            return 0.0f;
        }
        return maxHp * (level * ratioPerLevel);
    }

    private static int getTotalArmorEnchantmentLevel(LivingEntity entity, net.minecraft.enchantment.Enchantment enchantment) {
        int totalLevel = 0;
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack stack = entity.getEquippedStack(slot);
            if (!stack.isEmpty()) {
                totalLevel += EnchantmentHelper.getLevel(enchantment, stack);
            }
        }
        return totalLevel;
    }

    public static void updatePlayerAttributes(LivingEntity entity) {
        if (entity == null || entity.getWorld().isClient()) {
            return;
        }

        int aegisLevel = getTotalArmorEnchantmentLevel(entity, ModEnchantments.AEGIS);
        int juggernautLevel = getTotalArmorEnchantmentLevel(entity, ModEnchantments.JUGGERNAUT);

        EntityAttributeInstance healthInstance = entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        EntityAttributeInstance armorInstance = entity.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);

        if (healthInstance != null) {
            EntityAttributeModifier oldAegisHp = healthInstance.getModifier(AEGIS_HEALTH_UUID);
            EntityAttributeModifier oldJuggHp = healthInstance.getModifier(JUGGERNAUT_HEALTH_UUID);

            float targetAegisHpReduction = aegisLevel > 0 ? aegisLevel * ModConfig.INSTANCE.aegis.hp_reduction_per_level : 0.0f;
            float targetJuggHpBonus = juggernautLevel > 0 ? juggernautLevel * ModConfig.INSTANCE.juggernaut.hp_bonus_per_level : 0.0f;

            boolean needHealthUpdate = false;
            if (oldAegisHp == null && aegisLevel > 0) {
                needHealthUpdate = true;
            } else if (oldAegisHp != null && (aegisLevel == 0 || Math.abs(oldAegisHp.getValue() - (-targetAegisHpReduction)) > 0.001)) {
                needHealthUpdate = true;
            }

            if (oldJuggHp == null && juggernautLevel > 0) {
                needHealthUpdate = true;
            } else if (oldJuggHp != null && (juggernautLevel == 0 || Math.abs(oldJuggHp.getValue() - targetJuggHpBonus) > 0.001)) {
                needHealthUpdate = true;
            }

            if (needHealthUpdate) {
                healthInstance.removeModifier(AEGIS_HEALTH_UUID);
                healthInstance.removeModifier(JUGGERNAUT_HEALTH_UUID);

                if (aegisLevel > 0) {
                    healthInstance.addTemporaryModifier(new EntityAttributeModifier(
                        AEGIS_HEALTH_UUID, "Aegis HP Reduction", -targetAegisHpReduction, EntityAttributeModifier.Operation.ADDITION
                    ));
                }

                if (juggernautLevel > 0) {
                    healthInstance.addTemporaryModifier(new EntityAttributeModifier(
                        JUGGERNAUT_HEALTH_UUID, "Juggernaut HP Bonus", targetJuggHpBonus, EntityAttributeModifier.Operation.ADDITION
                    ));
                }

                if (entity.getHealth() > entity.getMaxHealth()) {
                    entity.setHealth(entity.getMaxHealth());
                }
            }
        }

        if (armorInstance != null) {
            EntityAttributeModifier oldAegisArmor = armorInstance.getModifier(AEGIS_ARMOR_UUID);
            EntityAttributeModifier oldJuggArmor = armorInstance.getModifier(JUGGERNAUT_ARMOR_UUID);

            float targetAegisArmorBonus = aegisLevel > 0 ? aegisLevel * ModConfig.INSTANCE.aegis.armor_bonus_per_level : 0.0f;
            float targetJuggArmorReduction = juggernautLevel > 0 ? juggernautLevel * ModConfig.INSTANCE.juggernaut.armor_reduction_per_level : 0.0f;

            boolean needArmorUpdate = false;
            if (oldAegisArmor == null && aegisLevel > 0) {
                needArmorUpdate = true;
            } else if (oldAegisArmor != null && (aegisLevel == 0 || Math.abs(oldAegisArmor.getValue() - targetAegisArmorBonus) > 0.001)) {
                needArmorUpdate = true;
            }

            if (oldJuggArmor == null && juggernautLevel > 0) {
                needArmorUpdate = true;
            } else if (oldJuggArmor != null && (juggernautLevel == 0 || Math.abs(oldJuggArmor.getValue() - (-targetJuggArmorReduction)) > 0.001)) {
                needArmorUpdate = true;
            }

            if (needArmorUpdate) {
                armorInstance.removeModifier(AEGIS_ARMOR_UUID);
                armorInstance.removeModifier(JUGGERNAUT_ARMOR_UUID);

                if (aegisLevel > 0) {
                    armorInstance.addTemporaryModifier(new EntityAttributeModifier(
                        AEGIS_ARMOR_UUID, "Aegis Armor Bonus", targetAegisArmorBonus, EntityAttributeModifier.Operation.ADDITION
                    ));
                }

                if (juggernautLevel > 0) {
                    armorInstance.addTemporaryModifier(new EntityAttributeModifier(
                        JUGGERNAUT_ARMOR_UUID, "Juggernaut Armor Reduction", -targetJuggArmorReduction, EntityAttributeModifier.Operation.ADDITION
                    ));
                }
            }
        }
    }

    private static boolean onAllowDamage(LivingEntity entity, DamageSource source, float amount) {
        if (IS_PROCESSING.get()) {
            return true;
        }

        if (source.getAttacker() instanceof LivingEntity attacker) {
            updatePlayerAttributes(attacker);
            updatePlayerAttributes(entity);

            if (amount > 0) {
                int aegisLevel = getTotalArmorEnchantmentLevel(attacker, ModEnchantments.AEGIS);
                int juggernautLevel = getTotalArmorEnchantmentLevel(attacker, ModEnchantments.JUGGERNAUT);

                float bonusDamage = 0.0f;

                if (aegisLevel > 0) {
                    float totalArmor = (float) attacker.getArmor();
                    bonusDamage += calculateAegisBonusDamage(totalArmor, aegisLevel, ModConfig.INSTANCE.aegis.armor_to_damage_ratio_per_level);
                }

                if (juggernautLevel > 0) {
                    float maxHp = attacker.getMaxHealth();
                    bonusDamage += calculateJuggernautBonusDamage(maxHp, juggernautLevel, ModConfig.INSTANCE.juggernaut.hp_to_damage_ratio_per_level);
                }

                if (bonusDamage > 0) {
                    IS_PROCESSING.set(true);
                    try {
                        entity.damage(attacker.getDamageSources().magic(), bonusDamage);
                    } finally {
                        IS_PROCESSING.set(false);
                    }
                }
            }
        }
        return true;
    }
}
