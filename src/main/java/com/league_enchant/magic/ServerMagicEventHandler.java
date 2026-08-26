package com.league_enchant.magic;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerMagicEventHandler {
    private static int tickCounter = 0;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(ServerMagicEventHandler::onServerTick);
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(ServerMagicEventHandler::onAllowDamage);
    }

    private static void onServerTick(MinecraftServer server) {
        tickCounter++;
        boolean isManaTick = (tickCounter % 20 == 0); // 1 second interval

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            PlayerMagicData magicData = PlayerMagicData.get(player);
            magicData.tickCooldowns();

            if (isManaTick) {
                float regenAmount = magicData.getManaRegen(player);
                magicData.addMana(player, regenAmount);
            }
        }
    }

    private static boolean onAllowDamage(LivingEntity entity, DamageSource source, float amount) {
        if (amount > 0 && source.getAttacker() instanceof ServerPlayerEntity attacker) {
            // Apply SPELL_POWER multiplier to magic damage
            if (source.getName() != null && source.getName().contains("magic")) {
                float spellPower = (float) attacker.getAttributeValue(MagicAttributes.SPELL_POWER);
                if (spellPower > 1.0f) {
                    // Spell power multiplier
                    // amount *= spellPower;
                }
            }
        }
        return true;
    }
}
