package com.league_enchant.magic.spells;

import com.league_enchant.magic.AbstractSpell;
import com.league_enchant.magic.PlayerMagicData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class FinalSparkSpell extends AbstractSpell {
    public FinalSparkSpell() {
        super("final_spark", "Cầu Cầu Cầu / Cầu Cầu Băng Giá (Final Spark)", SpellSchool.EVOCATION);
    }

    @Override
    public float getManaCost(int spellLevel) {
        return 50.0f;
    }

    @Override
    public int getCooldownTicks(int spellLevel) {
        return 120; // 6 seconds cooldown
    }

    @Override
    public int getCastTimeTicks(int spellLevel) {
        return 0;
    }

    @Override
    public boolean cast(ServerWorld world, ServerPlayerEntity player, PlayerMagicData magicData, int spellLevel) {
        Vec3d startPos = player.getEyePos();
        Vec3d look = player.getRotationVector();

        float baseDamage = 15.0f + (spellLevel * 8.0f);

        // Raycast beam up to 30 blocks
        for (int i = 1; i <= 30; i++) {
            Vec3d point = startPos.add(look.multiply(i));
            world.spawnParticles(ParticleTypes.END_ROD, point.x, point.y, point.z, 5, 0.1, 0.1, 0.1, 0.02);
            world.spawnParticles(ParticleTypes.INSTANT_EFFECT, point.x, point.y, point.z, 3, 0.1, 0.1, 0.1, 0.01);

            List<LivingEntity> targets = world.getEntitiesByClass(
                LivingEntity.class,
                new Box(point.add(-1.5, -1.5, -1.5), point.add(1.5, 1.5, 1.5)),
                e -> e != player && e.isAlive()
            );

            for (LivingEntity target : targets) {
                target.damage(world.getDamageSources().magic(), baseDamage);
            }
        }

        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1.5f, 1.2f);
        return true;
    }
}
