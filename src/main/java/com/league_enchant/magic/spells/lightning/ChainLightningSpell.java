package com.league_enchant.magic.spells.lightning;

import com.league_enchant.magic.AbstractSpell;
import com.league_enchant.magic.PlayerMagicData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class ChainLightningSpell extends AbstractSpell {
    public ChainLightningSpell() {
        super("chain_lightning", "Sét Dây Chuyền (Chain Lightning)", SpellSchool.LIGHTNING);
    }

    @Override
    public float getManaCost(int spellLevel) {
        return 45.0f;
    }

    @Override
    public int getCooldownTicks(int spellLevel) {
        return 100;
    }

    @Override
    public int getCastTimeTicks(int spellLevel) {
        return 0;
    }

    @Override
    public boolean cast(ServerWorld world, ServerPlayerEntity player, PlayerMagicData magicData, int spellLevel) {
        Vec3d pos = player.getEyePos();
        List<LivingEntity> targets = world.getEntitiesByClass(
            LivingEntity.class,
            new Box(pos.add(-12.0, -6.0, -12.0), pos.add(12.0, 6.0, 12.0)),
            e -> e != player && e.isAlive()
        );

        int chainLimit = 3 + spellLevel;
        int count = 0;
        for (LivingEntity target : targets) {
            if (count >= chainLimit) break;
            LightningEntity bolt = EntityType.LIGHTNING_BOLT.create(world);
            if (bolt != null) {
                bolt.refreshPositionAfterTeleport(target.getX(), target.getY(), target.getZ());
                bolt.setChanneler(player);
                world.spawnEntity(bolt);
            }
            world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, target.getX(), target.getBodyY(0.5), target.getZ(), 20, 0.3, 0.3, 0.3, 0.1);
            count++;
        }

        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.PLAYERS, 1.5f, 1.0f);
        return true;
    }
}
