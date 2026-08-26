package com.league_enchant.magic.spells.eldritch;

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

public class EldritchBlastSpell extends AbstractSpell {
    public EldritchBlastSpell() {
        super("eldritch_blast", "Chưởng Cổ Đại (Eldritch Blast)", SpellSchool.ELDRITCH);
    }

    @Override
    public float getManaCost(int spellLevel) {
        return 45.0f;
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
        Vec3d start = player.getEyePos();
        Vec3d dir = player.getRotationVector();

        for (int i = 1; i <= 25; i++) {
            Vec3d pos = start.add(dir.multiply(i));
            world.spawnParticles(ParticleTypes.SCULK_SOUL, pos.x, pos.y, pos.z, 5, 0.2, 0.2, 0.2, 0.05);

            List<LivingEntity> targets = world.getEntitiesByClass(
                LivingEntity.class,
                new Box(pos.add(-1.2, -1.2, -1.2), pos.add(1.2, 1.2, 1.2)),
                e -> e != player && e.isAlive()
            );

            for (LivingEntity target : targets) {
                target.damage(world.getDamageSources().magic(), 20.0f + spellLevel * 6.0f);
                target.addVelocity(dir.x * 1.5, 0.3, dir.z * 1.5);
                target.velocityModified = true;
            }
        }

        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.PLAYERS, 1.5f, 1.0f);
        return true;
    }
}
