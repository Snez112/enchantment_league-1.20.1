package com.league_enchant.magic.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class FrostbiteEffect extends StatusEffect {
    public FrostbiteEffect() {
        super(StatusEffectCategory.HARMFUL, 0x00AAFF);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.getWorld().isClient()) return;
        entity.setFrozenTicks(Math.min(entity.getMinFreezeDamageTicks() + 40, entity.getFrozenTicks() + 5));
    }
}
