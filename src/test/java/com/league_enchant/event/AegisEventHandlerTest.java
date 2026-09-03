package com.league_enchant.event;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AegisEventHandlerTest {

    @Test
    public void testCalculateAegisBonusDamage() {
        // 20 armor, 10 toughness, level 2, 0.10 ratio per level = (20 + 10) * (2 * 0.10) = 6.0
        float bonus = AegisEventHandler.calculateAegisBonusDamage(20.0f, 10.0f, 2, 0.10f);
        assertEquals(6.0f, bonus, 0.0001f);
    }

    @Test
    public void testZeroLevelOrZeroStats() {
        assertEquals(0.0f, AegisEventHandler.calculateAegisBonusDamage(0.0f, 0.0f, 2, 0.10f), 0.0001f);
        assertEquals(0.0f, AegisEventHandler.calculateAegisBonusDamage(20.0f, 10.0f, 0, 0.10f), 0.0001f);
    }

    @Test
    public void testCalculateCappedDamageBelowCap() {
        // Raw 100 damage, calculated 20 damage (80% reduction), max reduction 90% -> returns 20.0 (no change)
        float damage = AegisEventHandler.calculateCappedDamage(100.0f, 20.0f, 0.90f);
        assertEquals(20.0f, damage, 0.0001f);
    }

    @Test
    public void testCalculateCappedDamageAboveCap() {
        // Raw 100 damage, calculated 5 damage (95% reduction), max reduction 90% -> returns 10.0 (capped at 90% reduction, min 10% damage)
        float damage = AegisEventHandler.calculateCappedDamage(100.0f, 5.0f, 0.90f);
        assertEquals(10.0f, damage, 0.0001f);
    }

    @Test
    public void testCalculateCappedDamageFullImmunity() {
        // Raw 100 damage, calculated 0 damage (100% reduction), max reduction 90% -> returns 10.0
        float damage = AegisEventHandler.calculateCappedDamage(100.0f, 0.0f, 0.90f);
        assertEquals(10.0f, damage, 0.0001f);
    }
}
