package com.league_enchant.magic.spells.evocation;

import com.league_enchant.magic.AbstractSpell;
import com.league_enchant.magic.PlayerMagicData;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class InvisibilitySpell extends AbstractSpell {
    public InvisibilitySpell() {
        super("invisibility", "Tàng Hình (Invisibility)", SpellSchool.EVOCATION);
    }

    @Override
    public float getManaCost(int spellLevel) {
        return 25.0f;
    }

    @Override
    public int getCooldownTicks(int spellLevel) {
        return 120;
    }

    @Override
    public int getCastTimeTicks(int spellLevel) {
        return 0;
    }

    @Override
    public boolean cast(ServerWorld world, ServerPlayerEntity player, PlayerMagicData magicData, int spellLevel) {
        int duration = (10 + spellLevel * 5) * 20;
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, duration, 0));
        world.spawnParticles(ParticleTypes.ENCHANT, player.getX(), player.getBodyY(0.5), player.getZ(), 20, 0.3, 0.5, 0.3, 0.1);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ILLUSIONER_PREPARE_MIRROR, SoundCategory.PLAYERS, 1.0f, 1.2f);
        return true;
    }
}
