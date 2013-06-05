package org.motechproject.mapper.util;


import com.google.gson.annotations.SerializedName;

public class StringConverter implements Converter<String> {
    @SerializedName("default")
    private String defaultValue;

    public StringConverter() {

    }

    public StringConverter(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String convert(String value) {
        if(value == null) {
            return defaultValue;
        }
        return value;
    }
}
