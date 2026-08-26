package com.league_enchant.magic.spells.void_school;

import com.league_enchant.magic.AbstractSpell;
import com.league_enchant.magic.PlayerMagicData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class StarfallSpell extends AbstractSpell {
    public StarfallSpell() {
        super("starfall", "Mưa Sao Bằng (Starfall)", SpellSchool.VOID);
    }

    @Override
    public float getManaCost(int spellLevel) {
        return 65.0f;
    }

    @Override
    public int getCooldownTicks(int spellLevel) {
        return 160;
    }

    @Override
    public int getCastTimeTicks(int spellLevel) {
        return 15;
    }

    @Override
    public boolean cast(ServerWorld world, ServerPlayerEntity player, PlayerMagicData magicData, int spellLevel) {
        HitResult hit = player.raycast(25.0, 0.0f, false);
        Vec3d target = hit.getPos();

        for (int i = 0; i < 8; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 8.0;
            double offsetZ = (world.random.nextDouble() - 0.5) * 8.0;
            Vec3d strike = target.add(offsetX, 0, offsetZ);

            world.spawnParticles(ParticleTypes.PORTAL, strike.x, strike.y + 10, strike.z, 20, 0.2, 3.0, 0.2, 0.2);
            world.spawnParticles(ParticleTypes.FIREWORK, strike.x, strike.y + 0.5, strike.z, 15, 0.4, 0.4, 0.4, 0.1);

            List<LivingEntity> targets = world.getEntitiesByClass(
                LivingEntity.class,
                new Box(strike.add(-2.0, -1.0, -2.0), strike.add(2.0, 3.0, 2.0)),
                e -> e != player && e.isAlive()
            );

            for (LivingEntity entity : targets) {
                entity.damage(world.getDamageSources().magic(), 12.0f + spellLevel * 3.0f);
            }
        }

        world.playSound(null, target.x, target.y, target.z, SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST, SoundCategory.PLAYERS, 2.0f, 1.2f);
        return true;
    }
}
