package org.motechproject.mapper.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegerConverter implements Converter<Integer> {

    protected Logger logger = LoggerFactory.getLogger("commcare-mrs-mapper");

    @Override
    public Integer convert(String value) {
        if (value == null)
            return null;

        Integer integerValue = null;
        try {
            integerValue = Integer.valueOf(value);
        } catch (NumberFormatException e) {
            logger.error(String.format("Error parsing integer value: %s", value, e.getMessage()));
        }
        return integerValue;
    }

    @Override
    public Integer missing() {
        return convert(null);
    }
}
