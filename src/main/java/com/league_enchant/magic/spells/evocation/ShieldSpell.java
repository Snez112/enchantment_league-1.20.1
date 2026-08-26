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

public class ShieldSpell extends AbstractSpell {
    public ShieldSpell() {
        super("shield", "Giáp Khiên Ảo Thuật (Shield)", SpellSchool.EVOCATION);
    }

    @Override
    public float getManaCost(int spellLevel) {
        return 30.0f;
    }

    @Override
    public int getCooldownTicks(int spellLevel) {
        return 100;
    }

    @Override
    public int getCastTimeTicks(int spellLevel) {
        return 0;
    }

    @Override
    public boolean cast(ServerWorld world, ServerPlayerEntity player, PlayerMagicData magicData, int spellLevel) {
        int duration = (12 + spellLevel * 4) * 20;
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, duration, 1));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, duration, spellLevel));
        world.spawnParticles(ParticleTypes.CRIT, player.getX(), player.getBodyY(0.5), player.getZ(), 30, 0.5, 0.8, 0.5, 0.1);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, SoundCategory.PLAYERS, 1.2f, 1.0f);
        return true;
    }
}
