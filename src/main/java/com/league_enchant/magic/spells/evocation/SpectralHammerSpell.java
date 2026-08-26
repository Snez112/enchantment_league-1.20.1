package com.league_enchant.magic.spells.evocation;

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

public class SpectralHammerSpell extends AbstractSpell {
    public SpectralHammerSpell() {
        super("spectral_hammer", "Búa Tâm Linh (Spectral Hammer)", SpellSchool.EVOCATION);
    }

    @Override
    public float getManaCost(int spellLevel) {
        return 35.0f;
    }

    @Override
    public int getCooldownTicks(int spellLevel) {
        return 90;
    }

    @Override
    public int getCastTimeTicks(int spellLevel) {
        return 10;
    }

    @Override
    public boolean cast(ServerWorld world, ServerPlayerEntity player, PlayerMagicData magicData, int spellLevel) {
        HitResult hit = player.raycast(15.0, 0.0f, false);
        Vec3d target = hit.getPos();

        world.spawnParticles(ParticleTypes.SWEEP_ATTACK, target.x, target.y + 1, target.z, 10, 0.5, 0.5, 0.5, 0.1);
        world.spawnParticles(ParticleTypes.CRIT, target.x, target.y + 1, target.z, 30, 0.8, 0.8, 0.8, 0.2);
        world.playSound(null, target.x, target.y, target.z, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.PLAYERS, 1.5f, 0.7f);

        List<LivingEntity> targets = world.getEntitiesByClass(
            LivingEntity.class,
            new Box(target.add(-3.0, -1.0, -3.0), target.add(3.0, 3.0, 3.0)),
            e -> e != player && e.isAlive()
        );

        for (LivingEntity entity : targets) {
            entity.damage(world.getDamageSources().playerAttack(player), 18.0f + spellLevel * 5.0f);
            Vec3d knockback = entity.getPos().subtract(target).normalize().multiply(1.2);
            entity.addVelocity(knockback.x, 0.4, knockback.z);
            entity.velocityModified = true;
        }
        return true;
    }
}
