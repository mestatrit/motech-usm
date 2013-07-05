package org.motechproject.mapper.util;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateTimeConverter implements Converter<DateTime> {

    protected Logger logger = LoggerFactory.getLogger("commcare-mrs-mapper");

    @SerializedName("pattern")
    private String pattern;

    public DateTimeConverter(String pattern) {
        this.pattern = pattern;
    }

    public DateTimeConverter() {
    }

    @Override
    public DateTime convert(String value) {
        if (value == null)
            return null;

        try {
            return StringUtils.isEmpty(pattern) ? DateTime.parse(value) : DateTime.parse(value, DateTimeFormat.forPattern(pattern));
        } catch (IllegalArgumentException e) {
            logger.error(String.format("Unable to parse datetime value : %s", value, e.getMessage()));
        }
        return null;
    }

    @Override
    public DateTime missing() {
        return convert(null);
    }
}
