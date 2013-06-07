package org.motechproject.mapper.util;


public interface ExpressionConverter<T> {
    boolean canConvert(String expression);
    T convert(String expression, CommcareFormSegment beneficiarySegment);
}
