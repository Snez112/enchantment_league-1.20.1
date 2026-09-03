package com.league_enchant.event;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SpellVampEventHandlerTest {

    @Test
    public void testCalculateHealAmountLevel1() {
        // 20 magic damage, level 1, 0.01 per level = 20 * 0.01 = 0.2
        float heal = SpellVampEventHandler.calculateHealAmount(20.0f, 1, 0.01f);
        assertEquals(0.2f, heal, 0.0001f);
    }

    @Test
    public void testCalculateHealAmountLevel5() {
        // 40 magic damage, level 5, 0.01 per level = 40 * 0.05 = 2.0
        float heal = SpellVampEventHandler.calculateHealAmount(40.0f, 5, 0.01f);
        assertEquals(2.0f, heal, 0.0001f);
    }

    @Test
    public void testCalculateHealAmountZeroLevelOrZeroDamage() {
        assertEquals(0.0f, SpellVampEventHandler.calculateHealAmount(0.0f, 5, 0.05f), 0.0001f);
        assertEquals(0.0f, SpellVampEventHandler.calculateHealAmount(20.0f, 0, 0.05f), 0.0001f);
    }
}
