package com.league_enchant.event;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AegisJuggernautTest {

    @Test
    public void testCalculateAegisBonusDamage() {
        // 20 armor, level 2, 0.10 ratio per level = 20 * (2 * 0.10) = 4.0
        float bonus = AegisJuggernautEventHandler.calculateAegisBonusDamage(20.0f, 2, 0.10f);
        assertEquals(4.0f, bonus, 0.0001f);
    }

    @Test
    public void testCalculateJuggernautBonusDamage() {
        // 40 max HP, level 2, 0.05 ratio per level = 40 * (2 * 0.05) = 4.0
        float bonus = AegisJuggernautEventHandler.calculateJuggernautBonusDamage(40.0f, 2, 0.05f);
        assertEquals(4.0f, bonus, 0.0001f);
    }

    @Test
    public void testZeroLevelOrZeroStats() {
        assertEquals(0.0f, AegisJuggernautEventHandler.calculateAegisBonusDamage(0.0f, 2, 0.10f), 0.0001f);
        assertEquals(0.0f, AegisJuggernautEventHandler.calculateAegisBonusDamage(20.0f, 0, 0.10f), 0.0001f);

        assertEquals(0.0f, AegisJuggernautEventHandler.calculateJuggernautBonusDamage(0.0f, 2, 0.05f), 0.0001f);
        assertEquals(0.0f, AegisJuggernautEventHandler.calculateJuggernautBonusDamage(40.0f, 0, 0.05f), 0.0001f);
    }
}
