package org.motechproject.mapper.util;


public interface Converter<T> {
    T convert(String value);
}
