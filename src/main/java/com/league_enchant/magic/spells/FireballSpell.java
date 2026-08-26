package com.league_enchant.magic.spells;

import com.league_enchant.magic.AbstractSpell;
import com.league_enchant.magic.PlayerMagicData;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

public class FireballSpell extends AbstractSpell {
    public FireballSpell() {
        super("fireball", "Quả Cầu Lửa (Fireball)", SpellSchool.FIRE);
    }

    @Override
    public float getManaCost(int spellLevel) {
        return 15.0f + (spellLevel * 5.0f);
    }

    @Override
    public int getCooldownTicks(int spellLevel) {
        return Math.max(20, 60 - (spellLevel * 5)); // 3 seconds base cooldown
    }

    @Override
    public int getCastTimeTicks(int spellLevel) {
        return 0; // Instant cast
    }

    @Override
    public boolean cast(ServerWorld world, ServerPlayerEntity player, PlayerMagicData magicData, int spellLevel) {
        Vec3d look = player.getRotationVector();
        Vec3d pos = player.getEyePos().add(look.multiply(1.5));

        net.minecraft.entity.projectile.SmallFireballEntity fireball = new net.minecraft.entity.projectile.SmallFireballEntity(world, player, look.x, look.y, look.z);
        fireball.setPosition(pos.x, pos.y, pos.z);
        world.spawnEntity(fireball);

        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.0f);
        world.spawnParticles(ParticleTypes.FLAME, pos.x, pos.y, pos.z, 20, 0.2, 0.2, 0.2, 0.05);

        return true;
    }
}
