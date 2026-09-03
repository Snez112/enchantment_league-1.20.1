package com.league_enchant.event;

import com.league_enchant.config.ModConfig;
import com.league_enchant.registry.ModEnchantments;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.joml.Vector3f;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Locale;
import java.util.UUID;

public class AegisEventHandler {
    private static final UUID AEGIS_HEALTH_UUID = UUID.fromString("7a091410-d003-4b92-8086-13d85449df51");
    private static final UUID AEGIS_ARMOR_UUID = UUID.fromString("6a091410-d003-4b92-8086-13d85449df52");
    private static final UUID AEGIS_TOUGHNESS_UUID = UUID.fromString("7a091410-d003-4b92-8086-13d85449df55");

    private static final ThreadLocal<Boolean> IS_PROCESSING = ThreadLocal.withInitial(() -> false);

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(AegisEventHandler::onAllowDamage);
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                updatePlayerAttributes(player);
            }
        });
    }

    public static float calculateAegisBonusDamage(float totalArmor, float totalToughness, int level, float ratioPerLevel) {
        if ((totalArmor <= 0 && totalToughness <= 0) || level <= 0 || ratioPerLevel <= 0) {
            return 0.0f;
        }
        return (totalArmor + totalToughness) * (level * ratioPerLevel);
    }

    public static float calculateCappedDamage(float rawDamage, float calculatedDamage, float maxReductionRatio) {
        if (rawDamage <= 0) {
            return 0.0f;
        }
        float minDamage = rawDamage * (1.0f - Math.min(maxReductionRatio, 0.99f));
        return Math.max(calculatedDamage, minDamage);
    }

    private static int getTotalArmorEnchantmentLevel(LivingEntity entity) {
        int totalLevel = 0;
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack stack = entity.getEquippedStack(slot);
            if (!stack.isEmpty()) {
                totalLevel += EnchantmentHelper.getLevel(ModEnchantments.AEGIS, stack);
            }
        }
        return totalLevel;
    }

    public static void updatePlayerAttributes(LivingEntity entity) {
        if (entity == null || entity.getWorld().isClient()) {
            return;
        }

        int aegisLevel = getTotalArmorEnchantmentLevel(entity);

        EntityAttributeInstance healthInstance = entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        EntityAttributeInstance armorInstance = entity.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);
        EntityAttributeInstance toughnessInstance = entity.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS);

        if (healthInstance != null) {
            EntityAttributeModifier currentHpMod = healthInstance.getModifier(AEGIS_HEALTH_UUID);
            if (aegisLevel > 0) {
                float hpPercent = ModConfig.INSTANCE.aegis.hp_reduction_base + (aegisLevel - 1) * ModConfig.INSTANCE.aegis.hp_reduction_per_level;
                float targetValue = -hpPercent;
                if (currentHpMod == null || Math.abs(currentHpMod.getValue() - targetValue) > 0.0001f) {
                    healthInstance.removeModifier(AEGIS_HEALTH_UUID);
                    healthInstance.addTemporaryModifier(new EntityAttributeModifier(
                        AEGIS_HEALTH_UUID, "Aegis HP Reduction", targetValue, EntityAttributeModifier.Operation.MULTIPLY_TOTAL
                    ));
                    if (entity.getHealth() > entity.getMaxHealth()) {
                        entity.setHealth(entity.getMaxHealth());
                    }
                }
            } else if (currentHpMod != null) {
                healthInstance.removeModifier(AEGIS_HEALTH_UUID);
                if (entity.getHealth() > entity.getMaxHealth()) {
                    entity.setHealth(entity.getMaxHealth());
                }
            }
        }

        if (armorInstance != null) {
            EntityAttributeModifier currentArmorMod = armorInstance.getModifier(AEGIS_ARMOR_UUID);
            if (aegisLevel > 0) {
                float armorBonus = ModConfig.INSTANCE.aegis.armor_bonus_base + (aegisLevel - 1) * ModConfig.INSTANCE.aegis.armor_bonus_per_level;
                if (currentArmorMod == null || Math.abs(currentArmorMod.getValue() - armorBonus) > 0.0001f) {
                    armorInstance.removeModifier(AEGIS_ARMOR_UUID);
                    armorInstance.addTemporaryModifier(new EntityAttributeModifier(
                        AEGIS_ARMOR_UUID, "Aegis Armor Bonus", armorBonus, EntityAttributeModifier.Operation.ADDITION
                    ));
                }
            } else if (currentArmorMod != null) {
                armorInstance.removeModifier(AEGIS_ARMOR_UUID);
            }
        }

        if (toughnessInstance != null) {
            EntityAttributeModifier currentToughnessMod = toughnessInstance.getModifier(AEGIS_TOUGHNESS_UUID);
            if (aegisLevel > 0) {
                float toughnessBonus = ModConfig.INSTANCE.aegis.armor_toughness_base + (aegisLevel - 1) * ModConfig.INSTANCE.aegis.armor_toughness_per_level;
                if (currentToughnessMod == null || Math.abs(currentToughnessMod.getValue() - toughnessBonus) > 0.0001f) {
                    toughnessInstance.removeModifier(AEGIS_TOUGHNESS_UUID);
                    toughnessInstance.addTemporaryModifier(new EntityAttributeModifier(
                        AEGIS_TOUGHNESS_UUID, "Aegis Armor Toughness Bonus", toughnessBonus, EntityAttributeModifier.Operation.ADDITION
                    ));
                }
            } else if (currentToughnessMod != null) {
                toughnessInstance.removeModifier(AEGIS_TOUGHNESS_UUID);
            }
        }
    }

    private static boolean onAllowDamage(LivingEntity entity, DamageSource source, float amount) {
        if (IS_PROCESSING.get()) {
            return true;
        }

        if (entity != null && amount > 0) {
            updatePlayerAttributes(entity);

            // Check if victim has Aegis to cap max damage reduction (prevent 100% invulnerability)
            int victimAegisLevel = getTotalArmorEnchantmentLevel(entity);
            if (victimAegisLevel > 0) {
                float totalArmor = (float) entity.getArmor();
                float toughness = (float) entity.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS);
                int epf = EnchantmentHelper.getProtectionAmount(entity.getArmorItems(), source);

                float armorReduced = net.minecraft.entity.DamageUtil.getDamageLeft(amount, totalArmor, toughness);
                float finalReduced = net.minecraft.entity.DamageUtil.getInflictedDamage(armorReduced, (float) epf);

                float cappedDamage = calculateCappedDamage(amount, finalReduced, ModConfig.INSTANCE.aegis.max_damage_reduction);
                float shortfall = cappedDamage - finalReduced;

                if (shortfall > 0.001f) {
                    IS_PROCESSING.set(true);
                    try {
                        entity.damage(entity.getDamageSources().generic(), shortfall);
                    } finally {
                        IS_PROCESSING.set(false);
                    }
                }
            }
        }

        if (source != null && source.getAttacker() instanceof LivingEntity attacker) {
            updatePlayerAttributes(attacker);

            if (amount > 0) {
                int aegisLevel = getTotalArmorEnchantmentLevel(attacker);
                if (aegisLevel > 0) {
                    float totalArmor = (float) attacker.getArmor();
                    float totalToughness = (float) attacker.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS);
                    float bonusDamage = calculateAegisBonusDamage(totalArmor, totalToughness, aegisLevel, ModConfig.INSTANCE.aegis.armor_to_damage_ratio_per_level);

                    if (bonusDamage > 0) {
                        IS_PROCESSING.set(true);
                        try {
                            entity.damage(attacker.getDamageSources().magic(), bonusDamage);

                            // Visual FX: Render silvery light-blue particle burst & floating damage text with 🛡 icon
                            if (entity.getWorld() instanceof ServerWorld serverWorld) {
                                DustParticleEffect particleEffect = new DustParticleEffect(new Vector3f(0.75f, 0.85f, 0.95f), 1.2f);
                                serverWorld.spawnParticles(particleEffect, entity.getX(), entity.getY() + entity.getHeight() * 0.5, entity.getZ(), 8, 0.25, 0.25, 0.25, 0.05);

                                ArmorStandEntity armorStand = new ArmorStandEntity(EntityType.ARMOR_STAND, serverWorld);
                                armorStand.setPosition(entity.getX(), entity.getY() + entity.getHeight() + 0.3, entity.getZ());
                                armorStand.setCustomName(Text.literal("🛡 " + String.format(Locale.ROOT, "%.1f", bonusDamage))
                                    .styled(style -> style.withColor(0xC0D8EF)));
                                armorStand.setCustomNameVisible(true);
                                armorStand.setInvisible(true);
                                armorStand.setNoGravity(true);
                                serverWorld.spawnEntity(armorStand);

                                serverWorld.getServer().execute(() -> {
                                    new Thread(() -> {
                                        try {
                                            Thread.sleep(750);
                                            if (armorStand.isAlive()) {
                                                armorStand.discard();
                                            }
                                        } catch (InterruptedException ignored) {}
                                    }).start();
                                });
                            }
                        } finally {
                            IS_PROCESSING.set(false);
                        }
                    }
                }
            }
        }
        return true;
    }
}
