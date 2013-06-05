package org.motechproject.mapper.util;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class BooleanConverter implements Converter<Boolean> {

    @SerializedName("true")
    private List<String> trueValues =  new ArrayList<>();

    @SerializedName("false")
    private List<String> falseValues =  new ArrayList<>();

    @SerializedName("null")
    private List<String> nullValues =  new ArrayList<>();

    @SerializedName("default")
    private Boolean defaultValue =  Boolean.FALSE;

    public BooleanConverter(List<String> trueValues, List<String> falseValues, List<String> nullValues, Boolean defaultValue) {
        this.trueValues = trueValues;
        this.falseValues = falseValues;
        this.nullValues = nullValues;
        this.defaultValue = defaultValue;
    }

    public BooleanConverter() {
        trueValues.add("true");
    }

    public Boolean convert(String value) {
        if(caseInsensitiveContains(trueValues, value)) {
            return Boolean.TRUE;
        }
        if(caseInsensitiveContains(falseValues, value)) {
            return Boolean.FALSE;
        }
        if(caseInsensitiveContains(nullValues, value)) {
            return null;
        }
        return defaultValue;
    }

    private boolean caseInsensitiveContains(List<String> list, String search) {
        for(String listEntry: list) {
            if(listEntry == null) {
                if(search == null) {
                    return true;
                }
                continue;
            }
            if(listEntry.equalsIgnoreCase(search)) {
                return true;
            }
        }
        return false;
    }
}

