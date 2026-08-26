package com.league_enchant.magic.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class EvasionEffect extends StatusEffect {
    public EvasionEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x55FF55);
    }
}
