package org.motechproject.mapper.service;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NonNullPersonFieldUpdateStrategyTest {
    @Test
    public void shouldReturnFalseIfFieldValueIsNull() {
        NonNullPersonFieldUpdateStrategy strategy = new NonNullPersonFieldUpdateStrategy();
        assertTrue(strategy.canUpdateField(null, "fieldValue"));
        assertTrue(strategy.canUpdateField(null, ""));
        assertTrue(strategy.canUpdateField(null, "  "));
        assertFalse(strategy.canUpdateField(null, null));
    }
}
