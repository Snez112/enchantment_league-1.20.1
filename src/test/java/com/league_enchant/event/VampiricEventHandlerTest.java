package com.league_enchant.event;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class VampiricEventHandlerTest {

    @Test
    public void testCalculateHealAmountLevel1() {
        float heal = VampiricEventHandler.calculateHealAmount(10.0f, 1);
        assertEquals(0.1f, heal, 0.0001f, "Level 1 should give 1% lifesteal (0.1 heal on 10 damage)");
    }

    @Test
    public void testCalculateHealAmountLevel5() {
        float heal = VampiricEventHandler.calculateHealAmount(20.0f, 5);
        assertEquals(1.0f, heal, 0.0001f, "Level 5 should give 5% lifesteal (1.0 heal on 20 damage)");
    }

    @Test
    public void testCalculateHealAmountZeroDamage() {
        float heal = VampiricEventHandler.calculateHealAmount(0.0f, 3);
        assertEquals(0.0f, heal, 0.0001f, "Zero damage should yield zero heal");
    }

    @Test
    public void testCalculateHealAmountZeroLevel() {
        float heal = VampiricEventHandler.calculateHealAmount(10.0f, 0);
        assertEquals(0.0f, heal, 0.0001f, "Zero level should yield zero heal");
    }

    @Test
    public void testIsPhysicalDamageNull() {
        org.junit.jupiter.api.Assertions.assertFalse(VampiricEventHandler.isPhysicalDamage(null));
    }
}
