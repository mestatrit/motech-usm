package org.motechproject.mapper.util;


import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNull;

public class BooleanConverterTest {

    @Test
    public void shouldReturnTrueIfValueIsProvidedAsTrueValue(){
        BooleanConverter converter = create(Arrays.asList("aYe", "yep"), null, null, null);
        assertTrue(converter.convert("aye"));
        assertTrue(converter.convert("yep"));
        assertTrue(converter.convert("yEP"));
    }

    @Test
    public void shouldReturnFalseIfValueIsProvidedAsFalseValue(){
        BooleanConverter converter = create(new ArrayList(), Arrays.asList("no","FaLse"), null, null);
        assertFalse(converter.convert("no"));
        assertFalse(converter.convert("false"));
        assertFalse(converter.convert("faLSE"));
    }

    @Test
    public void shouldReturnNullIfValueIsProvidedAsNullValue(){
        BooleanConverter converter = create(new ArrayList(), new ArrayList<String>(), Arrays.asList(null, ""), null);
        assertNull(converter.convert(null));
        assertNull(converter.convert(""));
    }

    @Test
    public void shouldReturnDefaultValueIsRawValueNotPresentInEitherList(){
        BooleanConverter converter = create(new ArrayList(), new ArrayList<String>(), new ArrayList<String>(), Boolean.TRUE);
        assertTrue(converter.convert("sometThinG"));

        converter = create(new ArrayList(), new ArrayList<String>(), new ArrayList<String>(), Boolean.FALSE);
        assertFalse(converter.convert("sometThinG"));
    }

    @Test
    public void shouldHaveDefaultBehaviorSimilarToBoolean() {
        BooleanConverter converter = new BooleanConverter();
        assertTrue(converter.convert("true"));
        assertTrue(converter.convert("trUE"));
        assertFalse(converter.convert("anything"));
        assertFalse(converter.convert(""));
        assertFalse(converter.convert(null));
    }


    public BooleanConverter create(List<String> trueValues, List<String> falseValues, List<String> nullValues, Boolean defaultValue) {
        BooleanConverter converter = new BooleanConverter(trueValues, falseValues, nullValues, defaultValue);
        return converter;
    }
}
