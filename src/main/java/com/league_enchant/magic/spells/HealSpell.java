package com.league_enchant.magic.spells;

import com.league_enchant.magic.AbstractSpell;
import com.league_enchant.magic.PlayerMagicData;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class HealSpell extends AbstractSpell {
    public HealSpell() {
        super("heal", "Trị Thương (Holy Heal)", SpellSchool.HOLY);
    }

    @Override
    public float getManaCost(int spellLevel) {
        return 35.0f;
    }

    @Override
    public int getCooldownTicks(int spellLevel) {
        return 100; // 5 seconds
    }

    @Override
    public int getCastTimeTicks(int spellLevel) {
        return 0;
    }

    @Override
    public boolean cast(ServerWorld world, ServerPlayerEntity player, PlayerMagicData magicData, int spellLevel) {
        float healAmount = 6.0f + (spellLevel * 4.0f);
        player.heal(healAmount);

        world.spawnParticles(ParticleTypes.HEART, player.getX(), player.getBodyY(0.5), player.getZ(), 15, 0.3, 0.5, 0.3, 0.1);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.8f, 1.5f);
        return true;
    }
}
