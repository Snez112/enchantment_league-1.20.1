package com.league_enchant.magic.spells.blood;

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

public class BloodSlashSpell extends AbstractSpell {
    public BloodSlashSpell() {
        super("blood_slash", "Chém Máu (Blood Slash)", SpellSchool.VOID);
    }

    @Override
    public float getManaCost(int spellLevel) {
        return 25.0f;
    }

    @Override
    public int getCooldownTicks(int spellLevel) {
        return 50;
    }

    @Override
    public int getCastTimeTicks(int spellLevel) {
        return 0;
    }

    @Override
    public boolean cast(ServerWorld world, ServerPlayerEntity player, PlayerMagicData magicData, int spellLevel) {
        Vec3d startPos = player.getEyePos();
        Vec3d look = player.getRotationVector();
        float damage = 12.0f + (spellLevel * 5.0f);

        for (int i = 1; i <= 10; i++) {
            Vec3d point = startPos.add(look.multiply(i * 0.8));
            world.spawnParticles(ParticleTypes.CRIMSON_SPORE, point.x, point.y, point.z, 10, 0.3, 0.3, 0.3, 0.05);

            List<LivingEntity> targets = world.getEntitiesByClass(
                LivingEntity.class,
                new Box(point.add(-1.2, -1.2, -1.2), point.add(1.2, 1.2, 1.2)),
                e -> e != player && e.isAlive()
            );

            for (LivingEntity target : targets) {
                if (target.damage(world.getDamageSources().playerAttack(player), damage)) {
                    // Heal player for 30% of blood slash damage (lifesteal)
                    player.heal(damage * 0.30f);
                }
            }
        }

        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.2f, 0.7f);
        return true;
    }
}
