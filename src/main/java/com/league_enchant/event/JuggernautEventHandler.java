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

public class JuggernautEventHandler {
    private static final UUID JUGGERNAUT_HEALTH_UUID = UUID.fromString("5a091410-d003-4b92-8086-13d85449df53");
    private static final UUID JUGGERNAUT_ARMOR_UUID = UUID.fromString("4a091410-d003-4b92-8086-13d85449df54");
    private static final UUID JUGGERNAUT_TOUGHNESS_UUID = UUID.fromString("4a091410-d003-4b92-8086-13d85449df56");

    private static final ThreadLocal<Boolean> IS_PROCESSING = ThreadLocal.withInitial(() -> false);

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(JuggernautEventHandler::onAllowDamage);
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                updatePlayerAttributes(player);
            }
        });
    }

    public static float calculateBonusHp(float maxHp, int juggernautLevel) {
        if (maxHp <= 0 || juggernautLevel <= 0) {
            return 0.0f;
        }
        float hpPercent = ModConfig.INSTANCE.juggernaut.hp_bonus_base + (juggernautLevel - 1) * ModConfig.INSTANCE.juggernaut.hp_bonus_per_level;
        return maxHp - (maxHp / (1.0f + hpPercent));
    }

    public static float calculateJuggernautBonusDamage(float bonusHp, float ratio) {
        if (bonusHp <= 0 || ratio <= 0) {
            return 0.0f;
        }
        return bonusHp * ratio;
    }

    private static int getTotalArmorEnchantmentLevel(LivingEntity entity) {
        int totalLevel = 0;
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack stack = entity.getEquippedStack(slot);
            if (!stack.isEmpty()) {
                totalLevel += EnchantmentHelper.getLevel(ModEnchantments.JUGGERNAUT, stack);
            }
        }
        return totalLevel;
    }

    public static void updatePlayerAttributes(LivingEntity entity) {
        if (entity == null || entity.getWorld().isClient()) {
            return;
        }

        int juggernautLevel = getTotalArmorEnchantmentLevel(entity);

        EntityAttributeInstance healthInstance = entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        EntityAttributeInstance armorInstance = entity.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);
        EntityAttributeInstance toughnessInstance = entity.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS);

        if (healthInstance != null) {
            EntityAttributeModifier currentHpMod = healthInstance.getModifier(JUGGERNAUT_HEALTH_UUID);
            if (juggernautLevel > 0) {
                float hpPercent = ModConfig.INSTANCE.juggernaut.hp_bonus_base + (juggernautLevel - 1) * ModConfig.INSTANCE.juggernaut.hp_bonus_per_level;
                if (currentHpMod == null || Math.abs(currentHpMod.getValue() - hpPercent) > 0.0001f) {
                    healthInstance.removeModifier(JUGGERNAUT_HEALTH_UUID);
                    healthInstance.addTemporaryModifier(new EntityAttributeModifier(
                        JUGGERNAUT_HEALTH_UUID, "Juggernaut HP Bonus", hpPercent, EntityAttributeModifier.Operation.MULTIPLY_TOTAL
                    ));
                }
            } else if (currentHpMod != null) {
                healthInstance.removeModifier(JUGGERNAUT_HEALTH_UUID);
                if (entity.getHealth() > entity.getMaxHealth()) {
                    entity.setHealth(entity.getMaxHealth());
                }
            }
        }

        if (armorInstance != null) {
            EntityAttributeModifier currentArmorMod = armorInstance.getModifier(JUGGERNAUT_ARMOR_UUID);
            if (juggernautLevel > 0) {
                float armorReductionPercent = ModConfig.INSTANCE.juggernaut.armor_reduction_base + (juggernautLevel - 1) * ModConfig.INSTANCE.juggernaut.armor_reduction_per_level;
                float targetValue = -armorReductionPercent;
                if (currentArmorMod == null || Math.abs(currentArmorMod.getValue() - targetValue) > 0.0001f) {
                    armorInstance.removeModifier(JUGGERNAUT_ARMOR_UUID);
                    armorInstance.addTemporaryModifier(new EntityAttributeModifier(
                        JUGGERNAUT_ARMOR_UUID, "Juggernaut Armor Reduction", targetValue, EntityAttributeModifier.Operation.MULTIPLY_TOTAL
                    ));
                }
            } else if (currentArmorMod != null) {
                armorInstance.removeModifier(JUGGERNAUT_ARMOR_UUID);
            }
        }

        if (toughnessInstance != null) {
            EntityAttributeModifier currentToughnessMod = toughnessInstance.getModifier(JUGGERNAUT_TOUGHNESS_UUID);
            if (juggernautLevel > 0) {
                float toughnessReductionPercent = ModConfig.INSTANCE.juggernaut.armor_toughness_reduction_base + (juggernautLevel - 1) * ModConfig.INSTANCE.juggernaut.armor_toughness_reduction_per_level;
                float targetValue = -toughnessReductionPercent;
                if (currentToughnessMod == null || Math.abs(currentToughnessMod.getValue() - targetValue) > 0.0001f) {
                    toughnessInstance.removeModifier(JUGGERNAUT_TOUGHNESS_UUID);
                    toughnessInstance.addTemporaryModifier(new EntityAttributeModifier(
                        JUGGERNAUT_TOUGHNESS_UUID, "Juggernaut Toughness Reduction", targetValue, EntityAttributeModifier.Operation.MULTIPLY_TOTAL
                    ));
                }
            } else if (currentToughnessMod != null) {
                toughnessInstance.removeModifier(JUGGERNAUT_TOUGHNESS_UUID);
            }
        }
    }

    private static boolean onAllowDamage(LivingEntity entity, DamageSource source, float amount) {
        if (IS_PROCESSING.get()) {
            return true;
        }

        if (entity != null && amount > 0) {
            updatePlayerAttributes(entity);
        }

        if (source != null && source.getAttacker() instanceof LivingEntity attacker) {
            updatePlayerAttributes(attacker);

            if (amount > 0) {
                int juggernautLevel = getTotalArmorEnchantmentLevel(attacker);
                if (juggernautLevel > 0) {
                    float bonusHp = calculateBonusHp(attacker.getMaxHealth(), juggernautLevel);
                    float bonusDamage = calculateJuggernautBonusDamage(bonusHp, ModConfig.INSTANCE.juggernaut.hp_to_damage_ratio);

                    if (bonusDamage > 0) {
                        IS_PROCESSING.set(true);
                        try {
                            entity.damage(attacker.getDamageSources().magic(), bonusDamage);

                            // Visual FX: Render red particle burst & floating damage text with ❤ icon
                            if (entity.getWorld() instanceof ServerWorld serverWorld) {
                                DustParticleEffect particleEffect = new DustParticleEffect(new Vector3f(1.0f, 0.2f, 0.2f), 1.2f);
                                serverWorld.spawnParticles(particleEffect, entity.getX(), entity.getY() + entity.getHeight() * 0.5, entity.getZ(), 8, 0.25, 0.25, 0.25, 0.05);

                                ArmorStandEntity armorStand = new ArmorStandEntity(EntityType.ARMOR_STAND, serverWorld);
                                armorStand.setPosition(entity.getX(), entity.getY() + entity.getHeight() + 0.3, entity.getZ());
                                armorStand.setCustomName(Text.literal("❤ " + String.format(Locale.ROOT, "%.1f", bonusDamage))
                                    .styled(style -> style.withColor(0xFF5555)));
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
