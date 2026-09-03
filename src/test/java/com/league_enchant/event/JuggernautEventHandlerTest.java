package com.league_enchant.event;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JuggernautEventHandlerTest {

    @Test
    public void testCalculateJuggernautBonusDamage() {
        // 20 bonus HP, 0.05 ratio = 20 * 0.05 = 1.0
        float bonus = JuggernautEventHandler.calculateJuggernautBonusDamage(20.0f, 0.05f);
        assertEquals(1.0f, bonus, 0.0001f);
    }

    @Test
    public void testCalculateBonusHp() {
        // maxHp = 106.0 (base 100.0 with 6% bonus at level 1) -> bonusHp = 106.0 - 100.0 = 6.0
        float bonusHp = JuggernautEventHandler.calculateBonusHp(106.0f, 1);
        assertEquals(6.0f, bonusHp, 0.001f);
    }

    @Test
    public void testZeroLevelOrZeroStats() {
        assertEquals(0.0f, JuggernautEventHandler.calculateJuggernautBonusDamage(0.0f, 0.05f), 0.0001f);
        assertEquals(0.0f, JuggernautEventHandler.calculateBonusHp(0.0f, 1), 0.0001f);
        assertEquals(0.0f, JuggernautEventHandler.calculateBonusHp(100.0f, 0), 0.0001f);
    }
}
