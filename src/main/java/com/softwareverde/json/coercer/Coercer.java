package com.softwareverde.json.coercer;

import com.softwareverde.json.Json;
import com.softwareverde.json.Jsonable;
import com.softwareverde.log.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class Coercer extends Logger {
    protected final Logger _logger;

    protected void _emitWarning(final Exception exception) {
        if (_logger == null) { return; }
        _logger.emitWarning(exception);
    }

    public Coercer(final Logger logger) {
        _logger = logger;
    }

    public Coercer() {
        _logger = null;
    }


    protected Integer _coerceInteger(final Object obj, final Integer defaultValue) {
        if (obj == null) { return defaultValue; }
        if (obj instanceof Integer) { return (Integer) obj; }
        if (obj instanceof Long) { return ((Long) obj).intValue(); }
        if (obj instanceof String) {
            try { return Integer.parseInt((String) obj); }
            catch (final Exception exception) {
                _emitWarning(exception);
            }
        }

        return defaultValue;
    }

    protected Long _coerceLong(final Object obj, final Long defaultValue) {
        if (obj == null) { return defaultValue; }
        if (obj instanceof Long) { return (Long) obj; }
        if (obj instanceof Integer) { return ((Integer) obj).longValue(); }
        if (obj instanceof String) {
            try { return Long.parseLong((String) obj); }
            catch (final Exception exception) {
                _emitWarning(exception);
            }
        }

        return defaultValue;
    }

    protected Float _coerceFloat(final Object obj, final Float defaultValue) {
        if (obj == null) { return defaultValue; }
        if (obj instanceof Float) { return (Float) obj; }
        if (obj instanceof Double) { return ((Double) obj).floatValue(); }
        if (obj instanceof Integer) { return Float.valueOf((Integer) obj); }
        if (obj instanceof Long) { return Float.valueOf((Long) obj); }
        if (obj instanceof String) {
            try { return Float.parseFloat((String) obj); }
            catch (final Exception exception) {
                _emitWarning(exception);
            }
        }

        return defaultValue;
    }

    protected Double _coerceDouble(final Object obj, final Double defaultValue) {
        if (obj == null) { return defaultValue; }
        if (obj instanceof Double) { return (Double) obj; }
        if (obj instanceof Float) { return ((Float) obj).doubleValue(); }
        if (obj instanceof Integer) { return Double.valueOf((Integer) obj); }
        if (obj instanceof Long) { return Double.valueOf((Long) obj); }
        if (obj instanceof String) {
            try { return Double.parseDouble((String) obj); }
            catch (final Exception exception) {
                _emitWarning(exception);
            }
        }

        return defaultValue;
    }

    protected Boolean _coerceBoolean(final Object obj, final Boolean defaultValue) {
        if (obj == null) { return defaultValue; }
        if (obj instanceof Boolean) { return (Boolean) obj; }
        if (obj instanceof Integer) { return ((Integer) obj > 0); }
        else if (obj instanceof Long) { return (((Long) obj) > 0L); }
        else if (obj instanceof String) {
            try { return (Integer.parseInt((String) obj) > 0); }
            catch (final Exception exception) {
                _emitWarning(exception);
            }
        }

        return defaultValue;
    }

    protected Json _coerceJson(final Object obj, final Json defaultValue) {
        if (obj == null) { return defaultValue; }
        if (obj instanceof Jsonable) { return ((Jsonable) obj).toJson(); }
        if (obj instanceof JSONObject) { return Json.wrap((JSONObject) obj); }
        if (obj instanceof JSONArray) { return Json.wrap((JSONArray) obj); }
        if (obj instanceof String) { return Json.parse((String) obj); }

        return defaultValue;
    }

    protected String _coerceString(final Object obj, final String defaultValue) {
        if (obj == null) { return defaultValue; }
        if (obj instanceof String) { return (String) obj; }
        if (obj == JSONObject.NULL) { return defaultValue; }
        return obj.toString();
    }

    @SuppressWarnings("unchecked")
    public <T> T coerce(final Object obj, final T type) {
        if (type instanceof String)     { return (T) _coerceString(obj,     Json.Types.STRING); }
        if (type instanceof Integer)    { return (T) _coerceInteger(obj,    Json.Types.INTEGER); }
        if (type instanceof Long)       { return (T) _coerceLong(obj,       Json.Types.LONG); }
        if (type instanceof Double)     { return (T) _coerceDouble(obj,     Json.Types.DOUBLE); }
        if (type instanceof Float)      { return (T) _coerceFloat(obj,      Json.Types.FLOAT); }
        if (type instanceof Boolean)    { return (T) _coerceBoolean(obj,    Json.Types.BOOLEAN); }
        if (type instanceof Jsonable)   { return (T) _coerceJson(obj,       Json.Types.JSON); }

        _emitWarning(new RuntimeException("Unknown object type: "+ type));
        return null;
    }
}
