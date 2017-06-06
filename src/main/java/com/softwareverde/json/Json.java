//  ****************************************************************************
//  The MIT License (MIT)
//
//  Copyright (c) 2017 Joshua Green <josh@softwareverde.com>
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files (the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.
//  ****************************************************************************

package com.softwareverde.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Json is a wrapper for JSONObject and JSONArray.
 * Json is intelligently either an array or an object. By invoking the
 *  put(key, value) method, the Json object will become an Object. If
 *  values exist within the Json object before an invocation of put(),
 *  then the values will be indexed as stringified-integers; the index
 *  used will be smallest unused non-negative integer.
 */
public class Json implements Jsonable {
    private synchronized static void debug(final String str) {
        System.out.println("com.softwareverde.json :: "+ str);
    }

    private static Json _fromString(final String jsonString) throws JSONException {
        final Json json = new Json();
        final String trimmedJsonString = jsonString.trim();

        if (trimmedJsonString.length() > 0) {
            if (trimmedJsonString.charAt(0) == '[') {
                json._jsonArray = new JSONArray(trimmedJsonString);
                json._isArray = true;
            }
            else {
                json._jsonObject = new JSONObject(trimmedJsonString);
                json._isArray = false;
            }
        }

        return json;
    }

    private static Integer _coerceInteger(final Object obj, final Integer defaultValue) {
        if (obj instanceof Integer) { return (Integer) obj; }
        if (obj instanceof Long) { return ((Long) obj).intValue(); }
        if (obj instanceof String) {
            try { return Integer.parseInt((String) obj); }
            catch (final Exception e) { Json.debug("Exception: "+ e.getMessage()); }
        }

        return defaultValue;
    }

    private static Long _coerceLong(final Object obj, final Long defaultValue) {
        if (obj instanceof Long) { return (Long) obj; }
        if (obj instanceof Integer) { return ((Integer) obj).longValue(); }
        if (obj instanceof String) {
            try { return Long.parseLong((String) obj); }
            catch (final Exception e) { Json.debug("Exception: "+ e.getMessage()); }
        }

        return defaultValue;
    }

    private static Float _coerceFloat(final Object obj, final Float defaultValue) {
        if (obj instanceof Float) { return (Float) obj; }
        if (obj instanceof Double) { return ((Double) obj).floatValue(); }
        if (obj instanceof Integer) { return Float.valueOf((Integer) obj); }
        if (obj instanceof Long) { return Float.valueOf((Long) obj); }
        if (obj instanceof String) {
            try { return Float.parseFloat((String) obj); }
            catch (final Exception e) { Json.debug("Exception: "+ e.getMessage()); }
        }

        return defaultValue;
    }

    private static Double _coerceDouble(final Object obj, final Double defaultValue) {
        if (obj instanceof Double) { return (Double) obj; }
        if (obj instanceof Float) { return ((Float) obj).doubleValue(); }
        if (obj instanceof Integer) { return Double.valueOf((Integer) obj); }
        if (obj instanceof Long) { return Double.valueOf((Long) obj); }
        if (obj instanceof String) {
            try { return Double.parseDouble((String) obj); }
            catch (final Exception e) { Json.debug("Exception: "+ e.getMessage()); }
        }

        return defaultValue;
    }

    private static Boolean _coerceBoolean(final Object obj, final Boolean defaultValue) {
        if (obj instanceof Boolean) { return (Boolean) obj; }
        if (obj instanceof Integer) { return ((Integer) obj > 0); }
        else if (obj instanceof Long) { return (((Long) obj) > 0L); }
        else if (obj instanceof String) {
            try { return (Integer.parseInt((String) obj) > 0); }
            catch (final Exception e) { Json.debug("Exception: "+ e.getMessage()); }
        }

        return defaultValue;
    }

    private static Json _coerceJson(final Object obj, final Json defaultValue) {
        if (obj instanceof Jsonable) { return ((Jsonable) obj).toJson(); }

        if (obj instanceof JSONObject) {
            Json json = new Json();
            json._jsonObject = (JSONObject) obj;
            json._isArray = false;
            return json;
        }

        if (obj instanceof JSONArray) {
            Json json = new Json();
            json._jsonArray = (JSONArray) obj;
            json._isArray = true;
            return json;
        }

        if (obj instanceof String) { return Json.fromString((String) obj); }

        return defaultValue;
    }

    public static Boolean isJson(final String string) {
        try {
            _fromString(string);
            return true;
        }
        catch (final JSONException e) {
            return false;
        }
    }

    public static Json fromString(final String jsonString) {
        try {
            return _fromString(jsonString);
        }
        catch (final JSONException e) {
            Json.debug("Exception: " + e.getMessage());
            return new Json();
        }
    }

    private Boolean _isArray = false;
    private JSONObject _jsonObject;
    private JSONArray _jsonArray;

    private String _arrayToString() {
        final JSONArray out = new JSONArray();
        for (int i=0; i<_jsonArray.length(); i++) {
            try {
                final Object valueObject = _jsonArray.get(i);
                if (valueObject instanceof Jsonable) {
                    final Json valueJson = ((Jsonable) valueObject).toJson();
                    if (valueJson.isArray()) {
                        out.put(valueJson._jsonArray);
                    }
                    else {
                        out.put(valueJson._jsonObject);
                    }
                }
                else {
                    out.put(_jsonArray.get(i));
                }
            }
            catch (final JSONException e) { Json.debug("Exception: "+ e.getMessage()); }
        }
        return out.toString();
    }

    private String _objectToString() {
        final JSONObject out = new JSONObject();
        final Iterator<String> it = _jsonObject.keys();
        while (it.hasNext()) {
            final String key = it.next();
            try {
                final Object valueObject = _jsonObject.get(key);
                if (valueObject instanceof Jsonable) {
                    final Json valueJson = ((Jsonable) valueObject).toJson();
                    if (valueJson.isArray()) {
                        out.put(key, valueJson._jsonArray);
                    }
                    else {
                        out.put(key, valueJson._jsonObject);
                    }
                }
                else {
                    out.put(key, _jsonObject.get(key));
                }
            } catch (JSONException e) { Json.debug("Exception: "+ e.getMessage()); }
        }
        return out.toString();
    }

    public Json() {
        _jsonObject = new JSONObject();
        _jsonArray = new JSONArray();

        _isArray = true;
    }

    public <T> Json(final Collection<T> c) {
        _jsonObject = new JSONObject();
        _jsonArray = new JSONArray();

        for (final T item : c) {
            if (item instanceof Jsonable) {
                final Json json = ((Jsonable) item).toJson();

                if (json.isArray()) {
                    _jsonArray.put(json._jsonArray);
                }
                else {
                    _jsonArray.put(json._jsonObject);
                }
            }
            else {
                _jsonArray.put(item);
            }
        }
        _isArray = true;
    }

    public <T> Json(final Map<String, T> keyValueMap) {
        _jsonObject = new JSONObject();
        _jsonArray = new JSONArray();

        for (final String key : keyValueMap.keySet()) {
            final T value = keyValueMap.get(key);

            if (value instanceof Jsonable) {
                final Json json = ((Jsonable) value).toJson();
                if (json.isArray()) {
                    try { _jsonObject.put(key, json._jsonArray); }
                    catch (final JSONException e) { Json.debug("Exception: "+ e.getMessage()); }
                }
                else {
                    try { _jsonObject.put(key, json._jsonObject); }
                    catch (final JSONException e) { Json.debug("Exception: "+ e.getMessage()); }
                }
            }
            else {
                try { _jsonObject.put(key, value); }
                catch (final JSONException e) { Json.debug("Exception: "+ e.getMessage()); }
            }
        }
        _isArray = false;
    }

    public Boolean isArray() {
        return _isArray;
    }

    public Integer length() {
        if (_isArray) { return _jsonArray.length(); }
        else { return _jsonObject.length(); }
    }

    public <T> void add(final T value) {
        if (_isArray) {
            if (value instanceof Jsonable) {
                final Json json = ((Jsonable) value).toJson();

                if (json.isArray()) {
                    _jsonArray.put(json._jsonArray);
                }
                else {
                    _jsonArray.put(json._jsonObject);
                }
            }
            else {
                _jsonArray.put((value == null ? JSONObject.NULL : value));
            }
        }
        else {
            // Append the item while keeping the object-form.. Index will be a stringified int of the lowest possible non-negative value.
            Integer index = 0;
            while (_jsonObject.has(index.toString())) { index++; }
            if (value instanceof Jsonable) {
                final Json json = ((Jsonable) value).toJson();
                if (json.isArray()) {
                    try { _jsonObject.put(index.toString(), json._jsonArray); }
                    catch (final JSONException e) { Json.debug("Exception: "+ e.getMessage()); }
                }
                else {
                    try { _jsonObject.put(index.toString(), json._jsonObject); }
                    catch (final JSONException e) { Json.debug("Exception: "+ e.getMessage()); }
                }
            }
            else {
                try { _jsonObject.put(index.toString(), (value == null ? JSONObject.NULL : value)); }
                catch (final JSONException e) { Json.debug("Exception: "+ e.getMessage()); }
            }
        }
    }

    public <T> void put(final String key, final T value) {
        // If Json has objects within array, copy them as object-values with stringified integer indexes..
        Integer index = 0;
        while (_jsonArray.length() > 0) {
            try { _jsonObject.put(index.toString(), _jsonArray.remove(0)); }
            catch (final JSONException e) { Json.debug("Exception: "+ e.getMessage()); }
            index++;
        }
        _isArray = false;

        if (value instanceof Jsonable) {
            final Json json = ((Jsonable) value).toJson();
            if (json.isArray()) {
                try { _jsonObject.put(key, json._jsonArray); }
                catch (final JSONException e) { Json.debug("Exception: "+ e.getMessage()); }
            }
            else {
                try { _jsonObject.put(key, json._jsonObject); }
                catch (final JSONException e) { Json.debug("Exception: "+ e.getMessage()); }
            }
        }
        else {
            try { _jsonObject.put(key, (value == null ? JSONObject.NULL : value)); }
            catch (final JSONException e) { Json.debug("Exception: "+ e.getMessage()); }
        }
    }

    public void remove(final String key) {
        if (_isArray) { return; }

        _jsonObject.put(key, (Object) null); // Remove the entity from the JSONObject.
    }

    public static class Types {
        public static final String  STRING  = "";
        public static final Integer INTEGER = 0;
        public static final Long    LONG    = 0L;
        public static final Double  DOUBLE  = 0D;
        public static final Float   FLOAT   = 0F;
        public static final Boolean BOOLEAN = false;
        public static final Json    OBJECT  = new Json();
        public static final Json    ARRAY   = new Json();
        public static final Json    JSON    = new Json();
    }

    @SuppressWarnings("unchecked")
    private static final <T> T _coerce(final Object obj, final T type) {
        if (type instanceof String) {
            if (obj instanceof String) { return (T) obj; }
            return (T) obj.toString();
        }
        if (type instanceof Integer)    { return (T) _coerceInteger(obj,    Types.INTEGER); }
        if (type instanceof Long)       { return (T) _coerceLong(obj,       Types.LONG); }
        if (type instanceof Double)     { return (T) _coerceDouble(obj,     Types.DOUBLE); }
        if (type instanceof Float)      { return (T) _coerceFloat(obj,      Types.FLOAT); }
        if (type instanceof Boolean)    { return (T) _coerceBoolean(obj,    Types.BOOLEAN); }
        if (type instanceof Jsonable)   { return (T) _coerceJson(obj,       Types.JSON); }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T> T _getInstance(final T type) {
        if (type instanceof String)     { return (T) ""; }
        if (type instanceof Integer)    { return (T) Integer.valueOf(0); }
        if (type instanceof Long)       { return (T) Long.valueOf(0); }
        if (type instanceof Float)      { return (T) Float.valueOf(0); }
        if (type instanceof Double)     { return (T) Double.valueOf(0); }
        if (type instanceof Boolean)    { return (T) Boolean.valueOf(false); }
        if (type instanceof Jsonable)       { return (T) new Json(); }
        return null;
    }

    public String getString(final String key)   { return this.get(key, Types.STRING); }
    public Integer getInteger(final String key) { return this.get(key, Types.INTEGER); }
    public Long getLong(final String key)       { return this.get(key, Types.LONG); }
    public Float getFloat(final String key)     { return this.get(key, Types.FLOAT); }
    public Double getDouble(final String key)   { return this.get(key, Types.DOUBLE); }
    public Boolean getBoolean(final String key) { return this.get(key, Types.BOOLEAN); }
    public Json get(String key)                 { return this.get(key, Json.Types.JSON); }

    public <T> T get(String key, T type) {
        if (! _isArray) {
            if (_jsonObject.has(key)) {
                try { return Json._coerce(_jsonObject.get(key), type); }
                catch (final Exception e) { Json.debug("Exception: "+ e.getMessage()); }
            }
        }

        return type;
    }

    public <T> T getOrNull(String key, T type) {
        if (_isArray) { return null; }

        if (_jsonObject.has(key)) {
            if (_jsonObject.isNull(key)) { return null; }
            try { return Json._coerce(_jsonObject.get(key), type); }
            catch (final Exception e) { Json.debug("Exception: "+ e.getMessage()); }
        }

        return null;
    }

    public String getString(final int index)    { return this.get(index, Types.STRING); }
    public Integer getInteger(final int index)  { return this.get(index, Types.INTEGER); }
    public Long getLong(final int index)        { return this.get(index, Types.LONG); }
    public Float getFloat(final int index)      { return this.get(index, Types.FLOAT); }
    public Double getDouble(final int index)    { return this.get(index, Types.DOUBLE); }
    public Boolean getBoolean(final int index)  { return this.get(index, Types.BOOLEAN); }

    public Json get(final Integer index) {
        return this.get(index, Json.Types.JSON);
    }
    public <T> T get(final Integer index, final T type) {
        if (_isArray) {
            if (index < _jsonArray.length() && index >= 0) {
                try { return _coerce(_jsonArray.get(index), type); }
                catch (final Exception e) { Json.debug("Exception: "+ e.getMessage()); }
            }
        }

        return type;
    }

    public <T> T getOrNull(final Integer index, final T type) {
        if (! _isArray) { return null; }

        if (index < _jsonArray.length() && index >= 0) {
            if (_jsonArray.isNull(index)) { return null; }
            try { return _coerce(_jsonArray.get(index), type); }
            catch (final Exception e) { Json.debug("Exception: "+ e.getMessage()); }
        }
        return null;
    }

    public Boolean hasKey(final String key) {
        if (_isArray) { return false; }
        return (_jsonObject.has(key));
    }

    public List<String> getKeys() {
        final List<String> keys = new ArrayList<String>();

        if (_isArray) {
            for (int i=0; i<this.length(); i++) {
                keys.add(String.valueOf(i));
            }
        }
        else {
            final Iterator<String> keysIt = _jsonObject.keys();
            while (keysIt.hasNext()) {
                keys.add(keysIt.next());
            }
        }

        return keys;
    }

    @Override
    public String toString() {
        if (_isArray) {
            return _arrayToString();
        }
        else {
            return _objectToString();
        }
    }

    @Override
    public Json toJson() {
        return this;
    }
}
