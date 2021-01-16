package com.ijson.rest.proxy.util;

import com.google.gson.*;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Created by cuiyongxu on 26/12/2016.
 */
public class JsonUtil {

    private static Gson gson = new GsonBuilder().
            registerTypeAdapter(Double.class, (JsonSerializer<Double>) (src, typeOfSrc, context) -> {
                if (src == src.longValue()) {
                    return new JsonPrimitive(src.longValue());
                }
                return new JsonPrimitive(src);
            }).create();
    private static Gson withNullGson = new GsonBuilder().serializeNulls().create();
    private static Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static String toPrettyJson(Object obj) {
        return prettyGson.toJson(obj);
    }

    public static String toJsonWithNull(Object obj) {
        return withNullGson.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }


    public static <T> T fromFile(String file, Class<T> clazz) throws IOException {
        return fromJson(IOUtils.toString(JsonUtil.class.getClassLoader().getResourceAsStream(file), "UTF-8"), clazz);
    }

    public static <T> T fromJson(String json, Type clazz) {
        return gson.fromJson(json, clazz);
    }


}
