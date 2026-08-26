package com.league_enchant.magic.spells.ice;

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

public class ConeOfColdSpell extends AbstractSpell {
    public ConeOfColdSpell() {
        super("cone_of_cold", "Sóng Băng Tuyết (Cone of Cold)", SpellSchool.ICE);
    }

    @Override
    public float getManaCost(int spellLevel) {
        return 35.0f;
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
        Vec3d startPos = player.getEyePos();
        Vec3d look = player.getRotationVector();

        for (int i = 1; i <= 8; i++) {
            double radius = i * 0.5;
            Vec3d pos = startPos.add(look.multiply(i));
            world.spawnParticles(ParticleTypes.SNOWFLAKE, pos.x, pos.y, pos.z, (int)(i * 8), radius, radius, radius, 0.05);

            List<LivingEntity> targets = world.getEntitiesByClass(
                LivingEntity.class,
                new Box(pos.add(-radius, -radius, -radius), pos.add(radius, radius, radius)),
                e -> e != player && e.isAlive()
            );

            for (LivingEntity target : targets) {
                target.damage(world.getDamageSources().freeze(), 8.0f + (spellLevel * 3.0f));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 80, 2));
            }
        }

        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_HURT_FREEZE, SoundCategory.PLAYERS, 1.2f, 1.2f);
        return true;
    }
}
