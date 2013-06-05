package org.motechproject.mapper.util;


import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;

import java.util.HashMap;

public class ConverterFactory {

    private static HashMap<String, Converter> converterCache = new HashMap<>();

    private static HashMap<Class, Class> converterClassesMap = new HashMap<Class, Class>(){{
        put(Boolean.class, BooleanConverter.class);
        put(String.class, StringConverter.class);
        put(DateTime.class, DateTimeConverter.class);
        put(Integer.class, IntegerConverter.class);
    }};

    public static <T> Converter<T> getConverter(Class<T> T) {
        return getConverter(T, null);
    }

    public static <T> Converter<T> getConverter(Class<T> T, String converterConfig) {
        if(converterConfig == null) {
            converterConfig = "{}";
        }

        Converter converter = converterCache.get(convertCacheKey(T, converterConfig));
        if(converter != null) {
            return converter;
        }
        return createConverter(T, converterConfig);
    }

    private static String convertCacheKey(Class T, String converterConfig) {
        return T.toString() + "::" + converterConfig;
    }

    private synchronized static <T> Converter<T>  createConverter(Class<T> T, String converterConfig) {
        String cacheKey = convertCacheKey(T, converterConfig);
        Converter<T> converter = converterCache.get(cacheKey);
        if(converter != null) {
            return converter;
        }
        converter = new GsonBuilder().create().fromJson(converterConfig, getConverterClass(T));
        converterCache.put(cacheKey, converter);
        return converter;
    }

    private static <T> Class<Converter> getConverterClass(Class<T> T) {
        return converterClassesMap.get(T);
    }
}
