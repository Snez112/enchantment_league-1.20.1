package com.league_enchant.event;

import com.league_enchant.config.ModConfig;
import com.league_enchant.registry.ModEnchantments;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;

public class InfinityAspectEventHandler {
    private static final ThreadLocal<Boolean> IS_PROCESSING = ThreadLocal.withInitial(() -> false);

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(InfinityAspectEventHandler::onAllowDamage);
    }

    public static float calculateBonusCritDamage(float damageAmount, int level) {
        if (damageAmount <= 0 || level <= 0) {
            return 0.0f;
        }
        ModConfig.InfinityAspectConfig config = ModConfig.INSTANCE.infinity_aspect;
        float bonusPercent = switch (level) {
            case 1 -> config.crit_bonus_level_1;
            case 2 -> config.crit_bonus_level_2;
            case 3 -> config.crit_bonus_level_3;
            default -> config.crit_bonus_level_4;
        };
        return damageAmount * bonusPercent;
    }

    public static boolean checkIsCritical(boolean isVanillaJumpCrit, float damageAmount, double baseAttackDamage) {
        if (isVanillaJumpCrit) {
            return true;
        }
        // Modpack Ground Crit check: when player lands a crit on ground via modpack crit chance stats,
        // damageAmount is scaled up by the crit multiplier (>= 1.2x of base attack damage).
        return baseAttackDamage > 0 && damageAmount >= baseAttackDamage * 1.2f;
    }

    public static boolean isVanillaJumpCrit(PlayerEntity player, LivingEntity target) {
        return player.fallDistance > 0.0F
                && !player.isOnGround()
                && !player.isClimbing()
                && !player.isTouchingWater()
                && !player.hasStatusEffect(StatusEffects.BLINDNESS)
                && !player.hasVehicle()
                && target != null;
    }

    private static boolean onAllowDamage(LivingEntity entity, DamageSource source, float amount) {
        if (IS_PROCESSING.get() || SpellVampEventHandler.isMagicOrSpellDamage(source)) {
            return true;
        }

        if (amount > 0 && source.getAttacker() instanceof PlayerEntity player) {
            boolean isJumpCrit = isVanillaJumpCrit(player, entity);
            double baseAttackDamage = player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);

            if (checkIsCritical(isJumpCrit, amount, baseAttackDamage)) {
                int level = EnchantmentHelper.getLevel(ModEnchantments.INFINITY_ASPECT, player.getMainHandStack());
                if (level > 0) {
                    float bonus = calculateBonusCritDamage(amount, level);
                    if (bonus > 0) {
                        IS_PROCESSING.set(true);
                        try {
                            entity.damage(player.getDamageSources().playerAttack(player), bonus);

                            if (entity.getWorld() instanceof ServerWorld serverWorld) {
                                serverWorld.spawnParticles(
                                    ParticleTypes.CRIT,
                                    entity.getX(),
                                    entity.getBodyY(0.5),
                                    entity.getZ(),
                                    12,
                                    0.25, 0.25, 0.25, 0.1
                                );
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
