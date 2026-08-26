package com.league_enchant.magic.spells.void_school;

import com.league_enchant.magic.AbstractSpell;
import com.league_enchant.magic.PlayerMagicData;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class ShadowSlashSpell extends AbstractSpell {
    public ShadowSlashSpell() {
        super("shadow_slash", "Shadow Slash", SpellSchool.VOID);
    }

    @Override
    public float getManaCost(int spellLevel) {
        return 30.0f;
    }

    @Override
    public int getCooldownTicks(int spellLevel) {
        return 80;
    }

    @Override
    public int getCastTimeTicks(int spellLevel) {
        return 0;
    }

    @Override
    public boolean cast(ServerWorld world, ServerPlayerEntity player, PlayerMagicData magicData, int spellLevel) {
        world.spawnParticles(ParticleTypes.WITCH, player.getX(), player.getBodyY(0.5), player.getZ(), 20, 0.5, 0.5, 0.5, 0.1);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ILLUSIONER_CAST_SPELL, SoundCategory.PLAYERS, 1.0f, 1.0f);
        return true;
    }
}
