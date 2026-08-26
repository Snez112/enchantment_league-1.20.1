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

public class BlackHoleSpell extends AbstractSpell {
    public BlackHoleSpell() {
        super("black_hole", "Hố Đen Hư Không (Black Hole)", SpellSchool.VOID);
    }

    @Override
    public float getManaCost(int spellLevel) {
        return 70.0f;
    }

    @Override
    public int getCooldownTicks(int spellLevel) {
        return 200; // 10s
    }

    @Override
    public int getCastTimeTicks(int spellLevel) {
        return 0;
    }

    @Override
    public boolean cast(ServerWorld world, ServerPlayerEntity player, PlayerMagicData magicData, int spellLevel) {
        HitResult hit = player.raycast(20.0, 0.0f, false);
        Vec3d center = hit.getPos();

        world.spawnParticles(ParticleTypes.REVERSE_PORTAL, center.x, center.y + 1, center.z, 60, 1.0, 1.0, 1.0, 0.1);
        world.playSound(null, center.x, center.y, center.z, SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.PLAYERS, 2.0f, 0.8f);

        List<LivingEntity> targets = world.getEntitiesByClass(
            LivingEntity.class,
            new Box(center.add(-6.0, -3.0, -6.0), center.add(6.0, 6.0, 6.0)),
            e -> e != player && e.isAlive()
        );

        for (LivingEntity target : targets) {
            Vec3d pull = center.subtract(target.getPos()).normalize().multiply(0.8);
            target.addVelocity(pull.x, pull.y + 0.2, pull.z);
            target.velocityModified = true;
            target.damage(world.getDamageSources().outOfWorld(), 14.0f + spellLevel * 4.0f);
        }
        return true;
    }
}
