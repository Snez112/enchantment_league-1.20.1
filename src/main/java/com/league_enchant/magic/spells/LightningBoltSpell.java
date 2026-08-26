package com.league_enchant.magic.spells;

import com.league_enchant.magic.AbstractSpell;
import com.league_enchant.magic.PlayerMagicData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class LightningBoltSpell extends AbstractSpell {
    public LightningBoltSpell() {
        super("lightning_bolt", "Tia Sấm Sét (Lightning Bolt)", SpellSchool.LIGHTNING);
    }

    @Override
    public float getManaCost(int spellLevel) {
        return 30.0f;
    }

    @Override
    public int getCooldownTicks(int spellLevel) {
        return 60; // 3 seconds
    }

    @Override
    public int getCastTimeTicks(int spellLevel) {
        return 0;
    }

    @Override
    public boolean cast(ServerWorld world, ServerPlayerEntity player, PlayerMagicData magicData, int spellLevel) {
        HitResult hitResult = player.raycast(25.0, 0.0f, false);
        Vec3d targetPos = hitResult.getPos();

        LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(world);
        if (lightning != null) {
            lightning.refreshPositionAfterTeleport(targetPos.x, targetPos.y, targetPos.z);
            lightning.setChanneler(player);
            world.spawnEntity(lightning);
            return true;
        }

        return false;
    }
}
