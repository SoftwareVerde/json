package com.softwareverde.json.coercer;

import com.softwareverde.json.Json;
import com.softwareverde.json.Jsonable;
import com.softwareverde.logging.Logger;
import com.softwareverde.util.Util;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Coercer {
    protected Integer _coerceInteger(final Object obj, final Integer defaultValue) {
        if (obj == null) { return defaultValue; }
        if (obj instanceof Integer) { return (Integer) obj; }
        if (obj instanceof Long) { return ((Long) obj).intValue(); }
        if (obj instanceof Double) { return ((Double) obj).intValue(); }
        if (obj instanceof Float) { return ((Float) obj).intValue(); }
        if (obj instanceof BigInteger) { return ((BigInteger) obj).intValue(); }
        if (obj instanceof BigDecimal) { return ((BigDecimal) obj).intValue(); }
        if (obj instanceof String) {
            final String value = obj.toString();
            if (! Util.isInt(value)) { return defaultValue; }
            return Util.parseInt(value);
        }

        return defaultValue;
    }

    protected Long _coerceLong(final Object obj, final Long defaultValue) {
        if (obj == null) { return defaultValue; }
        if (obj instanceof Long) { return (Long) obj; }
        if (obj instanceof Integer) { return ((Integer) obj).longValue(); }
        if (obj instanceof Double) { return ((Double) obj).longValue(); }
        if (obj instanceof Float) { return ((Float) obj).longValue(); }
        if (obj instanceof BigInteger) { return ((BigInteger) obj).longValue(); }
        if (obj instanceof BigDecimal) { return ((BigDecimal) obj).longValue(); }
        if (obj instanceof String) {
            final String value = obj.toString();
            if (! Util.isLong(value)) { return defaultValue; }
            return Util.parseLong(value);
        }

        return defaultValue;
    }

    protected Float _coerceFloat(final Object obj, final Float defaultValue) {
        if (obj == null) { return defaultValue; }
        if (obj instanceof Float) { return (Float) obj; }
        if (obj instanceof Double) { return ((Double) obj).floatValue(); }
        if (obj instanceof Integer) { return Float.valueOf((Integer) obj); }
        if (obj instanceof Long) { return Float.valueOf((Long) obj); }
        if (obj instanceof BigInteger) { return ((BigInteger) obj).floatValue(); }
        if (obj instanceof BigDecimal) { return ((BigDecimal) obj).floatValue(); }
        if (obj instanceof String) {
            final String value = obj.toString();
            if (! Util.isFloat(value)) { return defaultValue; }
            return Util.parseFloat(value);
        }

        return defaultValue;
    }

    protected Double _coerceDouble(final Object obj, final Double defaultValue) {
        if (obj == null) { return defaultValue; }
        if (obj instanceof Double) { return (Double) obj; }
        if (obj instanceof Float) { return ((Float) obj).doubleValue(); }
        if (obj instanceof Integer) { return Double.valueOf((Integer) obj); }
        if (obj instanceof Long) { return Double.valueOf((Long) obj); }
        if (obj instanceof BigInteger) { return ((BigInteger) obj).doubleValue(); }
        if (obj instanceof BigDecimal) { return ((BigDecimal) obj).doubleValue(); }
        if (obj instanceof String) {
            final String value = obj.toString();
            if (! Util.isDouble(value)) { return defaultValue; }
            return Util.parseDouble(value);
        }

        return defaultValue;
    }

    protected Boolean _coerceBoolean(final Object obj, final Boolean defaultValue) {
        if (obj == null) { return defaultValue; }
        if (obj instanceof Boolean) { return (Boolean) obj; }
        if (obj instanceof Integer) { return ((Integer) obj > 0); }
        if (obj instanceof Long) { return (((Long) obj) > 0L); }
        if (obj instanceof Double) { return ((Double) obj) > 0D; }
        if (obj instanceof Float) { return ((Float) obj) > 0F; }
        if (obj instanceof BigInteger) { return ((BigInteger) obj).longValue() > 0L; }
        if (obj instanceof BigDecimal) { return ((BigDecimal) obj).doubleValue() > 0D; }
        if (obj instanceof String) {
            final String value = obj.toString();
            if (! Util.isBool(value)) { return defaultValue; }
            return Util.parseBool(value);
        }

        return defaultValue;
    }

    protected Json _coerceJson(final Object obj, final Json defaultValue) {
        if (obj == null) { return defaultValue; }
        if (obj instanceof Json) { return ((Json) obj); }
        if (obj instanceof Jsonable) { return ((Jsonable) obj).toJson(); }
        if (obj instanceof JSONObject) { return Json.wrap((JSONObject) obj); }
        if (obj instanceof JSONArray) { return Json.wrap((JSONArray) obj); }
        if (obj instanceof String) {
            final String value = obj.toString();
            if (! Json.isJson(value)) { return defaultValue; }
            return Json.parse(value);
        }

        return defaultValue;
    }

    protected String _coerceString(final Object obj, final String defaultValue) {
        if (obj == null) { return defaultValue; }
        if (obj instanceof String) { return (String) obj; }
        if (obj == JSONObject.NULL) { return defaultValue; }
        return obj.toString();
    }

    @SuppressWarnings("unchecked")
    public <T> T coerce(final Object obj, final T defaultValue) throws Exception {
        if (defaultValue instanceof String)     { return (T) _coerceString(obj,     (String) defaultValue); }
        if (defaultValue instanceof Integer)    { return (T) _coerceInteger(obj,    (Integer) defaultValue); }
        if (defaultValue instanceof Long)       { return (T) _coerceLong(obj,       (Long) defaultValue); }
        if (defaultValue instanceof Double)     { return (T) _coerceDouble(obj,     (Double) defaultValue); }
        if (defaultValue instanceof Float)      { return (T) _coerceFloat(obj,      (Float) defaultValue); }
        if (defaultValue instanceof Boolean)    { return (T) _coerceBoolean(obj,    (Boolean) defaultValue); }
        if (defaultValue instanceof Json)       { return (T) _coerceJson(obj,       ((Json) defaultValue)); }
        if (defaultValue instanceof Jsonable)   { return (T) _coerceJson(obj,       ((Jsonable) defaultValue).toJson()); }

        Logger.warn(Coercer.class, new RuntimeException("Unknown object type: "+ defaultValue));
        return null;
    }
}
