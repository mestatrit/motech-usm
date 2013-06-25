package org.motechproject.mapper.util;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateTimeConverter implements Converter<DateTime> {

    protected Logger logger = LoggerFactory.getLogger("commcare-mrs-mapper");

    @Override
    public DateTime convert(String value) {
        if (value == null)
            return null;

        DateTime dateValue = null;
        try {
            dateValue = DateTime.parse(value);
        } catch (IllegalArgumentException e) {
            logger.error(String.format("Unable to parse datetime value : %s", value, e.getMessage()));
        }
        return dateValue;
    }

    @Override
    public DateTime missing() {
        return convert(null);
    }
}
