package org.motechproject.mapper.util;


import org.motechproject.commcare.domain.FormNode;

public class ExpressionUtil {

    public static <T extends Object> T resolve(String expression, CommcareFormSegment beneficiarySegment, Class<T> T) {
        String[] split = expression.split("::", 2);

        String rawValue = getRawValue(split[0], beneficiarySegment);

        String converterConfig = null;
        if(split.length == 2) {
            converterConfig = split[1];
        }

        Converter<T> converter = ConverterFactory.getConverter(T, converterConfig);

        return converter.convert(rawValue);
    }

    public static String resolve(String expression, CommcareFormSegment beneficiarySegment) {
        return resolve(expression, beneficiarySegment, String.class);
    }

    private static String getRawValue(String lookupPath, CommcareFormSegment beneficiarySegment) {
        FormNode formNode = beneficiarySegment.search(lookupPath);
        return formNode == null ? null : formNode.getValue();
    }


}
