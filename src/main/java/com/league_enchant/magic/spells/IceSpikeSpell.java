package com.league_enchant.magic.spells;

import com.league_enchant.magic.AbstractSpell;
import com.league_enchant.magic.PlayerMagicData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class IceSpikeSpell extends AbstractSpell {
    public IceSpikeSpell() {
        super("ice_spike", "Chông Băng (Ice Spike)", SpellSchool.ICE);
    }

    @Override
    public float getManaCost(int spellLevel) {
        return 20.0f;
    }

    @Override
    public int getCooldownTicks(int spellLevel) {
        return 40; // 2 seconds
    }

    @Override
    public int getCastTimeTicks(int spellLevel) {
        return 0;
    }

    @Override
    public boolean cast(ServerWorld world, ServerPlayerEntity player, PlayerMagicData magicData, int spellLevel) {
        Vec3d startPos = player.getEyePos();
        Vec3d look = player.getRotationVector();
        float damage = 10.0f + (spellLevel * 4.0f);

        for (int i = 1; i <= 15; i++) {
            Vec3d point = startPos.add(look.multiply(i));
            world.spawnParticles(ParticleTypes.SNOWFLAKE, point.x, point.y, point.z, 8, 0.2, 0.2, 0.2, 0.05);

            List<LivingEntity> targets = world.getEntitiesByClass(
                LivingEntity.class,
                new Box(point.add(-1.0, -1.0, -1.0), point.add(1.0, 1.0, 1.0)),
                e -> e != player && e.isAlive()
            );

            for (LivingEntity target : targets) {
                target.damage(world.getDamageSources().freeze(), damage);
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 2));
            }
        }

        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 1.0f, 1.5f);
        return true;
    }
}
