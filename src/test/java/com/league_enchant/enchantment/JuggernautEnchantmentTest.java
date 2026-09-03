package com.league_enchant.enchantment;

import net.minecraft.Bootstrap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.EquipmentSlot;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JuggernautEnchantmentTest {

    @BeforeAll
    public static void setup() {
        try {
            Bootstrap.initialize();
        } catch (Throwable ignored) {
            // Environment initialization fallback if already initialized or headless
        }
    }

    @Test
    public void testCannotAcceptProtectionEnchantment() {
        JuggernautEnchantment juggernaut = new JuggernautEnchantment();
        ProtectionEnchantment protection = new ProtectionEnchantment(
            Enchantment.Rarity.COMMON,
            ProtectionEnchantment.Type.ALL,
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        );

        assertFalse(juggernaut.canAccept(protection), "Juggernaut should not accept Protection enchantment");
    }

    @Test
    public void testCannotAcceptFireProtectionEnchantment() {
        JuggernautEnchantment juggernaut = new JuggernautEnchantment();
        ProtectionEnchantment fireProtection = new ProtectionEnchantment(
            Enchantment.Rarity.UNCOMMON,
            ProtectionEnchantment.Type.FIRE,
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        );

        assertFalse(juggernaut.canAccept(fireProtection), "Juggernaut should not accept Fire Protection enchantment");
    }

    @Test
    public void testCannotAcceptBlastProtectionEnchantment() {
        JuggernautEnchantment juggernaut = new JuggernautEnchantment();
        ProtectionEnchantment blastProtection = new ProtectionEnchantment(
            Enchantment.Rarity.RARE,
            ProtectionEnchantment.Type.EXPLOSION,
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        );

        assertFalse(juggernaut.canAccept(blastProtection), "Juggernaut should not accept Blast Protection enchantment");
    }

    @Test
    public void testCannotAcceptProjectileProtectionEnchantment() {
        JuggernautEnchantment juggernaut = new JuggernautEnchantment();
        ProtectionEnchantment projProtection = new ProtectionEnchantment(
            Enchantment.Rarity.UNCOMMON,
            ProtectionEnchantment.Type.PROJECTILE,
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        );

        assertFalse(juggernaut.canAccept(projProtection), "Juggernaut should not accept Projectile Protection enchantment");
    }

    @Test
    public void testCannotAcceptFeatherFallingEnchantment() {
        JuggernautEnchantment juggernaut = new JuggernautEnchantment();
        ProtectionEnchantment featherFalling = new ProtectionEnchantment(
            Enchantment.Rarity.UNCOMMON,
            ProtectionEnchantment.Type.FALL,
            EquipmentSlot.FEET
        );

        assertFalse(juggernaut.canAccept(featherFalling), "Juggernaut should not accept Feather Falling (Fall Protection) enchantment");
    }

    @Test
    public void testCannotAcceptAegisEnchantment() {
        JuggernautEnchantment juggernaut = new JuggernautEnchantment();
        AegisEnchantment aegis = new AegisEnchantment();

        assertFalse(juggernaut.canAccept(aegis), "Juggernaut should not accept Aegis enchantment");
    }

    @Test
    public void testCanAcceptOtherEnchantment() {
        JuggernautEnchantment juggernaut = new JuggernautEnchantment();
        Enchantment other = new Enchantment(Enchantment.Rarity.COMMON, net.minecraft.enchantment.EnchantmentTarget.ARMOR, new EquipmentSlot[]{EquipmentSlot.CHEST}) {};

        assertTrue(juggernaut.canAccept(other), "Juggernaut should accept unrelated armor enchantment");
    }
}
