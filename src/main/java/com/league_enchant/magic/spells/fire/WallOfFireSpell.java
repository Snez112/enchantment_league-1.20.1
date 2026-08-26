package com.league_enchant.magic.spells.fire;

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

public class WallOfFireSpell extends AbstractSpell {
    public WallOfFireSpell() {
        super("wall_of_fire", "Tường Lửa (Wall of Fire)", SpellSchool.FIRE);
    }

    @Override
    public float getManaCost(int spellLevel) {
        return 40.0f;
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
        Vec3d look = player.getRotationVector();
        Vec3d perp = new Vec3d(-look.z, 0, look.x).normalize();
        Vec3d start = player.getEyePos().add(look.multiply(3.0));

        for (int i = -4; i <= 4; i++) {
            Vec3d pos = start.add(perp.multiply(i * 1.0));
            world.spawnParticles(ParticleTypes.FLAME, pos.x, pos.y, pos.z, 20, 0.2, 1.5, 0.2, 0.05);

            List<LivingEntity> targets = world.getEntitiesByClass(
                LivingEntity.class,
                new Box(pos.add(-1.0, -1.0, -1.0), pos.add(1.0, 3.0, 1.0)),
                e -> e != player && e.isAlive()
            );

            for (LivingEntity target : targets) {
                target.damage(world.getDamageSources().inFire(), 8.0f + spellLevel * 2.0f);
                target.setOnFireFor(6);
            }
        }

        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 1.5f, 1.0f);
        return true;
    }
}
