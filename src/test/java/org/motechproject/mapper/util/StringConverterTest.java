package org.motechproject.mapper.util;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class StringConverterTest {
    @Test
    public void shouldReturnDefaultValueWhileConvertingNull() {
        String expectedEmptyValue = "emptyValue";
        String expectedMissingValue = "missingValue";
        StringConverter stringConverter = new StringConverter(expectedEmptyValue, expectedMissingValue);

        assertEquals(expectedEmptyValue, stringConverter.convert(null));
        assertEquals(expectedMissingValue, stringConverter.missing());
    }

    @Test
    public void shouldReturnNullForMissingAndNullValuesAsDefaultBehavior() {
        StringConverter converter = new StringConverter();
        assertNull(converter.convert(null));
        assertNull(converter.missing());
    }
}
