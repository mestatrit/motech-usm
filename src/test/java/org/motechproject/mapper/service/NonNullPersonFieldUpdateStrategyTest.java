package org.motechproject.mapper.service;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NonNullPersonFieldUpdateStrategyTest {
    @Test
    public void shouldReturnFalseIfFieldValueIsNull() {
        NonNullPersonFieldUpdateStrategy strategy = new NonNullPersonFieldUpdateStrategy();
        assertTrue(strategy.canUpdate(null, "fieldValue"));
        assertTrue(strategy.canUpdate(null, ""));
        assertTrue(strategy.canUpdate(null, "  "));
        assertFalse(strategy.canUpdate(null, null));
    }


}
