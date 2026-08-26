package com.league_enchant.magic.item;

import com.league_enchant.magic.MagicAttributes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rarity;

import java.util.UUID;

public class WandItem extends Item {
    private static final UUID WAND_SPELL_POWER_UUID = UUID.fromString("1c091410-d003-4b92-8086-13d85449df71");

    private final float spellPowerBonus;

    public WandItem(Settings settings, float spellPowerBonus) {
        super(settings.maxCount(1).rarity(Rarity.RARE));
        this.spellPowerBonus = spellPowerBonus;
    }

    public float getSpellPowerBonus() {
        return spellPowerBonus;
    }
}
