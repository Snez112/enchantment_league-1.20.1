package com.league_enchant.enchantment;

import net.minecraft.Bootstrap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AegisEnchantmentTest {

    @BeforeAll
    public static void setup() {
        try {
            Bootstrap.initialize();
        } catch (Throwable ignored) {
        }
    }

    @Test
    public void testCannotAcceptJuggernaut() {
        AegisEnchantment aegis = new AegisEnchantment();
        JuggernautEnchantment juggernaut = new JuggernautEnchantment();
        assertFalse(aegis.canAccept(juggernaut), "Aegis should not accept Juggernaut");
    }

    @Test
    public void testCannotAcceptWardingOrReckless() {
        AegisEnchantment aegis = new AegisEnchantment();

        class WardingEnchantmentDummy extends Enchantment {
            public WardingEnchantmentDummy() {
                super(Rarity.RARE, net.minecraft.enchantment.EnchantmentTarget.ARMOR, new EquipmentSlot[]{EquipmentSlot.CHEST});
            }
        }

        class RecklessEnchantmentDummy extends Enchantment {
            public RecklessEnchantmentDummy() {
                super(Rarity.RARE, net.minecraft.enchantment.EnchantmentTarget.ARMOR, new EquipmentSlot[]{EquipmentSlot.CHEST});
            }
        }

        assertFalse(aegis.canAccept(new WardingEnchantmentDummy()), "Aegis should not accept Warding enchantment");
        assertFalse(aegis.canAccept(new RecklessEnchantmentDummy()), "Aegis should not accept Reckless enchantment");
    }
}
