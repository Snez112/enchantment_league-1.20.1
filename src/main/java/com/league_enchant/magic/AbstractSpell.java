package com.league_enchant.magic;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public abstract class AbstractSpell {
    private final String spellId;
    private final String name;
    private final SpellSchool school;

    public enum SpellSchool {
        FIRE("Fire"),
        ICE("Ice"),
        LIGHTNING("Lightning"),
        HOLY("Holy"),
        VOID("Void"),
        EVOCATION("Evocation"),
        BLOOD("Blood"),
        ELDRITCH("Eldritch"),
        NATURE("Nature");

        private final String displayName;

        SpellSchool(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public AbstractSpell(String spellId, String name, SpellSchool school) {
        this.spellId = spellId;
        this.name = name;
        this.school = school;
    }

    public String getSpellId() {
        return spellId;
    }

    public String getName() {
        return name;
    }

    public SpellSchool getSchool() {
        return school;
    }

    public abstract float getManaCost(int spellLevel);

    public abstract int getCooldownTicks(int spellLevel);

    public abstract int getCastTimeTicks(int spellLevel);

    public abstract boolean cast(ServerWorld world, ServerPlayerEntity player, PlayerMagicData magicData, int spellLevel);
}
