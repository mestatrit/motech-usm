package org.motechproject.mapper.util;


import com.google.gson.annotations.SerializedName;

public class StringConverter implements Converter<String> {
    @SerializedName("empty")
    private String emptyValue;

    @SerializedName("missing")
    private String missingValue;

    public StringConverter() {

    }

    public StringConverter(String emptyValue, String missingValue) {
        this.emptyValue = emptyValue;
        this.missingValue = missingValue;
    }

    @Override
    public String convert(String value) {
        if(value == null) {
            return emptyValue;
        }
        return value;
    }

    @Override
    public String missing() {
        return missingValue;
    }
}
