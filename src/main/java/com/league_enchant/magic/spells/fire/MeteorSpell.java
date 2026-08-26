package com.league_enchant.magic.spells.fire;

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

public class MeteorSpell extends AbstractSpell {
    public MeteorSpell() {
        super("meteor", "Thiên Thạch Rơi (Meteor)", SpellSchool.FIRE);
    }

    @Override
    public float getManaCost(int spellLevel) {
        return 60.0f;
    }

    @Override
    public int getCooldownTicks(int spellLevel) {
        return 160; // 8s
    }

    @Override
    public int getCastTimeTicks(int spellLevel) {
        return 20;
    }

    @Override
    public boolean cast(ServerWorld world, ServerPlayerEntity player, PlayerMagicData magicData, int spellLevel) {
        HitResult hit = player.raycast(30.0, 0.0f, false);
        Vec3d target = hit.getPos();

        world.spawnParticles(ParticleTypes.EXPLOSION_EMITTER, target.x, target.y + 1, target.z, 5, 0.5, 0.5, 0.5, 0.1);
        world.playSound(null, target.x, target.y, target.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 2.0f, 0.8f);

        float damage = 25.0f + (spellLevel * 10.0f);
        List<LivingEntity> targets = world.getEntitiesByClass(
            LivingEntity.class,
            new Box(target.add(-4.0, -2.0, -4.0), target.add(4.0, 4.0, 4.0)),
            e -> e != player && e.isAlive()
        );

        for (LivingEntity entity : targets) {
            entity.damage(world.getDamageSources().explosion(player, player), damage);
            entity.setOnFireFor(5 + spellLevel);
        }
        return true;
    }
}
