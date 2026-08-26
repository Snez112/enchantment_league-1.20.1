package com.league_enchant.event;

import com.league_enchant.config.ModConfig;
import com.league_enchant.registry.ModEnchantments;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AttributeSyncHandler {
    private static final UUID VAMPIRIC_UUID = UUID.fromString("8b091410-d003-4b92-8086-13d85449df61");
    private static final UUID SPELL_VAMP_UUID = UUID.fromString("9b091410-d003-4b92-8086-13d85449df62");
    private static final UUID INFINITY_ASPECT_UUID = UUID.fromString("ab091410-d003-4b92-8086-13d85449df63");

    private static final List<EntityAttribute> DYNAMIC_LIFESTEAL_ATTRIBUTES = new ArrayList<>();
    private static final List<EntityAttribute> DYNAMIC_SPELL_VAMP_ATTRIBUTES = new ArrayList<>();
    private static final List<EntityAttribute> DYNAMIC_CRIT_DAMAGE_ATTRIBUTES = new ArrayList<>();
    private static boolean registryScanned = false;

    private static int tickCounter = 0;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(AttributeSyncHandler::onServerTick);
    }

    private static synchronized void scanRegistryIfNeeded() {
        if (registryScanned) return;
        registryScanned = true;

        for (Identifier id : Registries.ATTRIBUTE.getIds()) {
            String path = id.getPath().toLowerCase();
            EntityAttribute attribute = Registries.ATTRIBUTE.get(id);
            if (attribute == null) continue;

            if (path.contains("lifesteal") || path.contains("vampiric") || path.contains("life_leech")) {
                if (path.contains("spell") || path.contains("magic")) {
                    DYNAMIC_SPELL_VAMP_ATTRIBUTES.add(attribute);
                } else {
                    DYNAMIC_LIFESTEAL_ATTRIBUTES.add(attribute);
                }
            } else if (path.contains("spell_vamp") || path.contains("spell_lifesteal")) {
                DYNAMIC_SPELL_VAMP_ATTRIBUTES.add(attribute);
            }

            if ((path.contains("crit") && path.contains("damage"))
                    || path.contains("crit_multiplier")
                    || path.contains("critical_damage")
                    || path.contains("crit_damage")) {
                DYNAMIC_CRIT_DAMAGE_ATTRIBUTES.add(attribute);
            }
        }
    }

    private static void onServerTick(MinecraftServer server) {
        scanRegistryIfNeeded();

        tickCounter++;
        if (tickCounter % 10 == 0) {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                syncPlayerAttributes(player);
            }
        }
    }

    public static void syncPlayerAttributes(ServerPlayerEntity player) {
        if (player == null) return;

        // 1. Sync Vampiric (Lifesteal)
        int vampiricLevel = EnchantmentHelper.getLevel(ModEnchantments.VAMPIRIC, player.getMainHandStack());
        float lifestealRatio = vampiricLevel * ModConfig.INSTANCE.vampiric.lifesteal_per_level;
        syncAttributesList(player, DYNAMIC_LIFESTEAL_ATTRIBUTES, VAMPIRIC_UUID, "League Enchant Vampiric", lifestealRatio);

        // 2. Sync Spell Vamp
        int spellVampLevel = EnchantmentHelper.getEquipmentLevel(ModEnchantments.SPELL_VAMP, player);
        float spellVampRatio = spellVampLevel * ModConfig.INSTANCE.spell_vamp.spell_lifesteal_per_level;
        syncAttributesList(player, DYNAMIC_SPELL_VAMP_ATTRIBUTES, SPELL_VAMP_UUID, "League Enchant Spell Vamp", spellVampRatio);

        // 3. Sync Infinity Aspect (Crit Damage)
        int infinityLevel = EnchantmentHelper.getLevel(ModEnchantments.INFINITY_ASPECT, player.getMainHandStack());
        float critDamageRatio = 0.0f;
        if (infinityLevel > 0) {
            critDamageRatio = switch (infinityLevel) {
                case 1 -> ModConfig.INSTANCE.infinity_aspect.crit_bonus_level_1;
                case 2 -> ModConfig.INSTANCE.infinity_aspect.crit_bonus_level_2;
                case 3 -> ModConfig.INSTANCE.infinity_aspect.crit_bonus_level_3;
                default -> ModConfig.INSTANCE.infinity_aspect.crit_bonus_level_4;
            };
        }
        syncAttributesList(player, DYNAMIC_CRIT_DAMAGE_ATTRIBUTES, INFINITY_ASPECT_UUID, "League Enchant Infinity Aspect", critDamageRatio);
    }

    private static void syncAttributesList(ServerPlayerEntity player, List<EntityAttribute> attributes, UUID uuid, String name, float ratio) {
        for (EntityAttribute attribute : attributes) {
            EntityAttributeInstance instance = player.getAttributeInstance(attribute);
            if (instance != null) {
                // Adjust scale dynamically: 100-base vs 1.0/0.0-base
                float finalValue = (float) (attribute.getDefaultValue() > 10.0 ? ratio * 100.0f : ratio);
                EntityAttributeModifier oldMod = instance.getModifier(uuid);
                if (finalValue > 0) {
                    if (oldMod == null || Math.abs(oldMod.getValue() - finalValue) > 0.0001) {
                        instance.removeModifier(uuid);
                        instance.addTemporaryModifier(new EntityAttributeModifier(
                            uuid, name, finalValue, EntityAttributeModifier.Operation.ADDITION
                        ));
                    }
                } else if (oldMod != null) {
                    instance.removeModifier(uuid);
                }
            }
        }
    }
}

