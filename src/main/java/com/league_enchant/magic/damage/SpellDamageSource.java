package com.league_enchant.magic.damage;

import com.league_enchant.magic.AbstractSpell;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;

public class SpellDamageSource {
    public static DamageSource createSpellDamage(Entity attacker, AbstractSpell.SpellSchool school) {
        if (attacker != null) {
            return attacker.getDamageSources().magic();
        }
        return null;
    }
}
