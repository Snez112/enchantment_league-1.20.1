package com.league_enchant.event;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InfinityAspectEventHandlerTest {

    @Test
    public void testCalculateBonusCritDamageLevel1() {
        float bonus = InfinityAspectEventHandler.calculateBonusCritDamage(10.0f, 1);
        assertEquals(2.0f, bonus, 0.0001f, "Level 1 should add 20% bonus crit damage");
    }

    @Test
    public void testCalculateBonusCritDamageLevel2() {
        float bonus = InfinityAspectEventHandler.calculateBonusCritDamage(10.0f, 2);
        assertEquals(3.0f, bonus, 0.0001f, "Level 2 should add 30% bonus crit damage");
    }

    @Test
    public void testCalculateBonusCritDamageLevel3() {
        float bonus = InfinityAspectEventHandler.calculateBonusCritDamage(10.0f, 3);
        assertEquals(4.0f, bonus, 0.0001f, "Level 3 should add 40% bonus crit damage");
    }

    @Test
    public void testCalculateBonusCritDamageLevel4() {
        float bonus = InfinityAspectEventHandler.calculateBonusCritDamage(10.0f, 4);
        assertEquals(5.0f, bonus, 0.0001f, "Level 4 should add 50% bonus crit damage");
    }

    @Test
    public void testCalculateBonusCritDamageZeroLevel() {
        float bonus = InfinityAspectEventHandler.calculateBonusCritDamage(10.0f, 0);
        assertEquals(0.0f, bonus, 0.0001f, "Zero level should yield zero bonus crit damage");
    }

    @Test
    public void testCheckIsCriticalVanillaJump() {
        assertEquals(true, InfinityAspectEventHandler.checkIsCritical(true, 10.0f, 10.0f));
    }

    @Test
    public void testCheckIsCriticalModpackGroundCrit() {
        // Ground hit where damage (15.0) >= 1.2 * baseAttack (10.0) -> True
        assertEquals(true, InfinityAspectEventHandler.checkIsCritical(false, 15.0f, 10.0f));
    }

    @Test
    public void testCheckIsCriticalModpackGroundNormalHit() {
        // Ground hit where damage (10.0) < 1.2 * baseAttack (10.0) -> False
        assertEquals(false, InfinityAspectEventHandler.checkIsCritical(false, 10.0f, 10.0f));
    }
}
