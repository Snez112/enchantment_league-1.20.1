package com.league_enchant.magic.spells.holy;

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

public class DivineSmiteSpell extends AbstractSpell {
    public DivineSmiteSpell() {
        super("divine_smite", "Thánh Trừng (Divine Smite)", SpellSchool.HOLY);
    }

    @Override
    public float getManaCost(int spellLevel) {
        return 40.0f;
    }

    @Override
    public int getCooldownTicks(int spellLevel) {
        return 90;
    }

    @Override
    public int getCastTimeTicks(int spellLevel) {
        return 0;
    }

    @Override
    public boolean cast(ServerWorld world, ServerPlayerEntity player, PlayerMagicData magicData, int spellLevel) {
        HitResult hit = player.raycast(20.0, 0.0f, false);
        Vec3d target = hit.getPos();

        for (int y = 0; y <= 15; y++) {
            world.spawnParticles(ParticleTypes.END_ROD, target.x, target.y + y * 0.5, target.z, 6, 0.2, 0.2, 0.2, 0.01);
            world.spawnParticles(ParticleTypes.GLOW, target.x, target.y + y * 0.5, target.z, 4, 0.3, 0.3, 0.3, 0.02);
        }

        world.playSound(null, target.x, target.y, target.z, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.PLAYERS, 1.5f, 1.5f);

        List<LivingEntity> entities = world.getEntitiesByClass(
            LivingEntity.class,
            new Box(target.add(-2.0, -1.0, -2.0), target.add(2.0, 3.0, 2.0)),
            e -> e != player && e.isAlive()
        );

        for (LivingEntity entity : entities) {
            float damage = 15.0f + (spellLevel * 6.0f);
            if (entity.isUndead()) {
                damage *= 1.5f; // Extra damage against undead mobs!
            }
            entity.damage(world.getDamageSources().magic(), damage);
        }
        return true;
    }
}
