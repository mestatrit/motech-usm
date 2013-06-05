package org.motechproject.mapper.util;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;


public class ConverterFactoryTest {

    @Test
    public void shouldCreateBooleanExpression() {
        Converter converter = ConverterFactory.getConverter(Boolean.class, "{}");
        assertTrue(converter instanceof BooleanConverter);
    }

    @Test
    public void shouldCacheExpressionsOnceCreated(){
        assertEquals(ConverterFactory.getConverter(Boolean.class, "{}"), ConverterFactory.getConverter(Boolean.class, "{}"));
        assertNotSame(ConverterFactory.getConverter(Boolean.class, "{}"), ConverterFactory.getConverter(Boolean.class, "{a: \"b\"}"));
    }

    @Test
    public void shouldCreateBooleanConverterFromJSON() {
        BooleanConverter converter = (BooleanConverter) ConverterFactory.getConverter(Boolean.class, "{true: ['yeS', 'aye'], false: ['nay', 'nope'], null: [null, ''], default: false}");

        assertTrue(converter.convert("Yes"));
        assertTrue(converter.convert("aye"));

        converter = (BooleanConverter) ConverterFactory.getConverter(Boolean.class, "{true: ['yeS', 'aye'], false: ['nay', 'nope'], null: [null, ''], default: true}");

        assertFalse(converter.convert("nay"));
        assertFalse(converter.convert("nope"));

        assertNull(converter.convert(null));
        assertNull(converter.convert(""));
        assertTrue(converter.convert("something"));
    }


}
