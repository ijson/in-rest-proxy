package com.ijson.rest.proxy.config;

import com.google.common.base.MoreObjects;

import java.io.Serializable;
import java.util.*;

/**
 * Created by cuiyongxu on 17/6/27.
 */
public class ExtMap<K, V> extends HashMap<K, V> implements Map<K, V>, Cloneable, Serializable {
    public boolean getBool(String key) {
        return Boolean.parseBoolean(this.get(key) + "");
    }

    public Object get(String key, Object defaultValue) {
        return MoreObjects.firstNonNull(this.get(key), defaultValue);
    }

    public String getString(String key) {
        Object obj = this.get(key);
        if (obj == null) {
            return null;
        }
        return this.get(key) + "";
    }

    public String getString(String key, String defaultValue) {
        Object obj = this.get(key);
        if (obj == null) {
            return defaultValue;
        }
        return this.get(key) + "";
    }

    public Integer getInt(String key, Integer value) {
        Object obj = this.get(key);
        if (obj != null) {
            return Integer.parseInt(obj + "");
        } else {
            return value;
        }
    }
}
