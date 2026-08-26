package com.league_enchant.magic.spells.void_school;

import com.league_enchant.magic.AbstractSpell;
import com.league_enchant.magic.PlayerMagicData;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class TeleportSpell extends AbstractSpell {
    public TeleportSpell() {
        super("teleport", "Dịch Chuyển (Ender Teleport)", SpellSchool.VOID);
    }

    @Override
    public float getManaCost(int spellLevel) {
        return 40.0f;
    }

    @Override
    public int getCooldownTicks(int spellLevel) {
        return 80; // 4 seconds
    }

    @Override
    public int getCastTimeTicks(int spellLevel) {
        return 0;
    }

    @Override
    public boolean cast(ServerWorld world, ServerPlayerEntity player, PlayerMagicData magicData, int spellLevel) {
        double maxDist = 15.0 + (spellLevel * 5.0);
        HitResult hitResult = player.raycast(maxDist, 0.0f, false);
        Vec3d targetPos = hitResult.getPos();

        // Particles at old location
        world.spawnParticles(ParticleTypes.PORTAL, player.getX(), player.getY(), player.getZ(), 30, 0.5, 0.5, 0.5, 0.1);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0f, 1.2f);

        // Teleport player
        player.teleport(targetPos.x, targetPos.y, targetPos.z, true);

        // Particles at new location
        world.spawnParticles(ParticleTypes.PORTAL, targetPos.x, targetPos.y, targetPos.z, 30, 0.5, 0.5, 0.5, 0.1);
        world.playSound(null, targetPos.x, targetPos.y, targetPos.z, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0f, 1.0f);

        return true;
    }
}
