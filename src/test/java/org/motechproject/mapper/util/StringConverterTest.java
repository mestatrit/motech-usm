package org.motechproject.mapper.util;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class StringConverterTest {
    @Test
    public void shouldReturnDefaultValueWhileConvertingNull() {
        String expected = "myDefaultValue";
        assertEquals(expected, new StringConverter(expected).convert(null));
    }
}
