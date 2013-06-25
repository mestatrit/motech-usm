package org.motechproject.mapper.util;


import org.motechproject.commcare.domain.FormNode;

public class ExpressionUtil {

    public static <T extends Object> T resolve(String expression, CommcareFormSegment beneficiarySegment, Class<T> T) {
        String[] split = expression.split("::", 2);


        String converterConfig = null;
        if(split.length == 2) {
            converterConfig = split[1];
        }

        Converter<T> converter = ConverterFactory.getConverter(T, converterConfig);

        FormNode formNode = beneficiarySegment.search(split[0]);
        if(formNode == null) {
            return converter.missing();
        }
        return converter.convert(formNode.getValue());
    }

    public static String resolve(String expression, CommcareFormSegment beneficiarySegment) {
        return resolve(expression, beneficiarySegment, String.class);
    }
}
