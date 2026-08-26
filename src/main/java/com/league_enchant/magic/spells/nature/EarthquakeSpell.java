package com.league_enchant.magic.spells.nature;

import com.league_enchant.magic.AbstractSpell;
import com.league_enchant.magic.PlayerMagicData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class EarthquakeSpell extends AbstractSpell {
    public EarthquakeSpell() {
        super("earthquake", "Động Đất (Earthquake)", SpellSchool.NATURE);
    }

    @Override
    public float getManaCost(int spellLevel) {
        return 50.0f;
    }

    @Override
    public int getCooldownTicks(int spellLevel) {
        return 140;
    }

    @Override
    public int getCastTimeTicks(int spellLevel) {
        return 10;
    }

    @Override
    public boolean cast(ServerWorld world, ServerPlayerEntity player, PlayerMagicData magicData, int spellLevel) {
        Vec3d center = player.getPos();
        double radius = 6.0 + spellLevel * 2.0;

        world.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()), center.x, center.y + 0.2, center.z, 100, radius / 2, 0.2, radius / 2, 0.1);
        world.playSound(null, center.x, center.y, center.z, SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.PLAYERS, 2.0f, 0.5f);

        List<LivingEntity> targets = world.getEntitiesByClass(
            LivingEntity.class,
            new Box(center.add(-radius, -2.0, -radius), center.add(radius, 3.0, radius)),
            e -> e != player && e.isAlive()
        );

        for (LivingEntity target : targets) {
            target.damage(world.getDamageSources().playerAttack(player), 14.0f + spellLevel * 4.0f);
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 3));
            target.addVelocity(0, 0.4, 0);
            target.velocityModified = true;
        }
        return true;
    }
}
