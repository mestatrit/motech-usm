package org.motechproject.mapper.util;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DateTimeConverterTest {

    @Test
    public void shouldParseDateTimeInISOFormatByDefault() {
        DateTimeConverter converter = new DateTimeConverter();

        assertEquals(new DateTime(2013, 7, 5, 15, 33, 51, 987).toDate(), converter.convert("2013-07-05T15:33:51.987+05:30").toDate());
        assertEquals(new DateTime(2013, 7, 5, 0, 0, 0), converter.convert("2013-07-05"));
    }

    @Test
    public void shouldReturnNullIfUnknownDateFormat() {
        DateTimeConverter converter = new DateTimeConverter();

        assertNull(converter.convert("2013/07/05T15:33:51.987+05:30"));
        assertNull(converter.convert("21/11/2013"));
        assertNull(converter.convert(null));
        assertNull(converter.convert(""));
        assertNull(converter.convert("  "));
    }

    @Test
    public void shouldParseWithTheFormatProvided() {
        DateTimeConverter converter = new DateTimeConverter("dd/MM/yyyy");

        assertEquals(new DateTime(2013, 11, 21, 0, 0, 0), converter.convert("21/11/2013"));

    }
}
