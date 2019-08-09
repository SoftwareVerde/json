//  ****************************************************************************
//  The MIT License (MIT)
//
//  Copyright (c) 2018 Software Verde <josh@softwareverde.com>
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

import com.softwareverde.json.coercer.Coercer;
import com.softwareverde.logging.Logger;
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
    public static class Types {
        public static final String  STRING  = "";
        public static final Integer INTEGER = 0;
        public static final Long    LONG    = 0L;
        public static final Double  DOUBLE  = 0D;
        public static final Float   FLOAT   = 0F;
        public static final Boolean BOOLEAN = false;
        public static final Json    OBJECT  = new Json(false);
        public static final Json    ARRAY   = new Json(true);
        public static final Json    JSON    = new Json();
    }

    protected static final Coercer _coercer = new Coercer();

    protected static Json _parse(final String jsonString) throws JSONException {
        final String trimmedJsonString = jsonString.trim();
        if (trimmedJsonString.length() == 0) { return new Json(); }

        if (trimmedJsonString.charAt(0) == '[') {
            return Json.wrap(new JSONArray(trimmedJsonString));
        }
        else {
            return Json.wrap(new JSONObject(trimmedJsonString));
        }
    }

    public static Boolean isJson(final String string) {
        if (string == null) { return false; }
        try {
            Json._parse(string);
            return true;
        }
        catch (final JSONException exception) {
            return false;
        }
    }

    public static Json parse(final String jsonString) {
        if (jsonString == null) { return new Json(); }
        try {
            return Json._parse(jsonString);
        }
        catch (final JSONException exception) {
            Logger.warn(Json.class, "Unable to parse json string.", exception);
            return new Json();
        }
    }

    public static Json wrap(final JSONObject jsonObject) {
        return new Json(jsonObject);
    }

    public static Json wrap(final JSONArray jsonArray) {
        return new Json(jsonArray);
    }

    protected final JSONObject _jsonObject;
    protected final JSONArray _jsonArray;
    protected Boolean _isArray = false;

    protected String _arrayToString(final Integer formatSpacesCount) {
        final JSONArray out = new JSONArray();
        for (int i = 0; i < _jsonArray.length(); i++) {
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
            catch (final JSONException exception) {
                Logger.debug(Json.class, "Unable to put json attribute during serialization.", exception);
            }
        }

        if (formatSpacesCount != null) {
            return out.toString(formatSpacesCount);
        }
        else {
            return out.toString();
        }
    }

    protected String _objectToString(final Integer formatSpacesCount) {
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
            }
            catch (final JSONException exception) {
                Logger.debug(Json.class, "Unable to put json attribute during serialization.", exception);
            }
        }

        if (formatSpacesCount != null) {
            return out.toString(formatSpacesCount);
        }
        else {
            return out.toString();
        }
    }

    /**
     * Since Json is mutable, when given a static reference (e.x. Types.JSON), the defaultValue can be mutated statically.
     *  If this is not prevented, this can cause fairly tricky-to-catch bugs.
     *  This function detects if the static reference was used, and if so, then a new instance is created.
     */
    @SuppressWarnings("unchecked")
    protected <T> T _getDefaultValueFromType(final T type) {
        if (type == Types.JSON) { return (T) new Json(); }
        if (type == Types.ARRAY) { return (T) new Json(true); }
        if (type == Types.OBJECT) { return (T) new Json(false); }
        return type;
    }

    protected Json(final JSONObject jsonObject) {
        _jsonObject = jsonObject;
        _jsonArray = new JSONArray();

        _isArray = false;
    }

    protected Json(final JSONArray jsonArray) {
        _jsonObject = new JSONObject();
        _jsonArray = jsonArray;

        _isArray = true;
    }

    public Json() {
        _jsonObject = new JSONObject();
        _jsonArray = new JSONArray();

        _isArray = true;
    }

    public Json(final Boolean isArray) {
        _jsonObject = new JSONObject();
        _jsonArray = new JSONArray();

        _isArray = isArray;
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
                    catch (final JSONException exception) {
                        Logger.debug(Json.class, "Unable to put json attribute.", exception);
                    }
                }
                else {
                    try { _jsonObject.put(key, json._jsonObject); }
                    catch (final JSONException exception) {
                        Logger.debug(Json.class, "Unable to put json attribute.", exception);
                    }
                }
            }
            else {
                try { _jsonObject.put(key, value); }
                catch (final JSONException exception) {
                    Logger.debug(Json.class, "Unable to put json attribute.", exception);
                }
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
                    catch (final JSONException exception) {
                        Logger.debug(Json.class, "Unable to put json attribute.", exception);
                    }
                }
                else {
                    try { _jsonObject.put(index.toString(), json._jsonObject); }
                    catch (final JSONException exception) {
                        Logger.debug(Json.class, "Unable to put json attribute.", exception);
                    }
                }
            }
            else {
                try { _jsonObject.put(index.toString(), (value == null ? JSONObject.NULL : value)); }
                catch (final JSONException exception) {
                    Logger.debug(Json.class, "Unable to put json attribute.", exception);
                }
            }
        }
    }

    public <T> void put(final String key, final T value) {
        // If Json has objects within array, copy them as object-values with stringified integer indexes..
        Integer index = 0;
        while (_jsonArray.length() > 0) {
            try { _jsonObject.put(index.toString(), _jsonArray.remove(0)); }
            catch (final JSONException exception) {
                Logger.debug(Json.class, "Unable to put json attribute.", exception);
            }
            index++;
        }
        _isArray = false;

        if (value instanceof Jsonable) {
            final Json json = ((Jsonable) value).toJson();
            if (json.isArray()) {
                try { _jsonObject.put(key, json._jsonArray); }
                catch (final JSONException exception) {
                    Logger.debug(Json.class, "Unable to put json attribute.", exception);
                }
            }
            else {
                try { _jsonObject.put(key, json._jsonObject); }
                catch (final JSONException exception) {
                    Logger.debug(Json.class, "Unable to put json attribute.", exception);
                }
            }
        }
        else {
            try { _jsonObject.put(key, (value == null ? JSONObject.NULL : value)); }
            catch (final JSONException exception) {
                Logger.debug(Json.class, "Unable to put json attribute.", exception);
            }
        }
    }

    public void remove(final String key) {
        if (_isArray) { return; }

        _jsonObject.remove(key);
    }

    public String getString(final String key)   { return this.get(key, Types.STRING); }
    public Integer getInteger(final String key) { return this.get(key, Types.INTEGER); }
    public Long getLong(final String key)       { return this.get(key, Types.LONG); }
    public Float getFloat(final String key)     { return this.get(key, Types.FLOAT); }
    public Double getDouble(final String key)   { return this.get(key, Types.DOUBLE); }
    public Boolean getBoolean(final String key) { return this.get(key, Types.BOOLEAN); }
    public Json get(String key)                 { return this.get(key, new Json()); }

    public <T> T get(final String key, final T type) {
        final T defaultValue = _getDefaultValueFromType(type);

        if (! _isArray) {
            if (_jsonObject.has(key)) {
                try {
                    return _coercer.coerce(_jsonObject.get(key), defaultValue);
                }
                catch (final Exception exception) {
                    Logger.debug(Json.class, "Unable to get json attribute: " + key, exception);
                }
            }
        }

        return defaultValue;
    }

    public <T> T getOrNull(final String key, final T type) {
        if (_isArray) { return null; }

        final T defaultValue = _getDefaultValueFromType(type);

        if (_jsonObject.has(key)) {
            if (_jsonObject.isNull(key)) { return null; }
            try { return _coercer.coerce(_jsonObject.get(key), defaultValue); }
            catch (final Exception exception) {
                Logger.debug(Json.class, "Unable to get json attribute: " + key, exception);
            }
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
        return this.get(index, new Json());
    }
    public <T> T get(final Integer index, final T type) {
        final T defaultValue = _getDefaultValueFromType(type);

        if (_isArray) {
            if (index < _jsonArray.length() && index >= 0) {
                try { return _coercer.coerce(_jsonArray.get(index), defaultValue); }
                catch (final Exception exception) {
                    Logger.debug(Json.class, "Unable to get json attribute: " + index, exception);
                }
            }
        }

        return defaultValue;
    }

    public <T> T getOrNull(final Integer index, final T type) {
        if (! _isArray) { return null; }

        final T defaultValue = _getDefaultValueFromType(type);

        if (index < _jsonArray.length() && index >= 0) {
            if (_jsonArray.isNull(index)) { return null; }
            try { return _coercer.coerce(_jsonArray.get(index), defaultValue); }
            catch (final Exception exception) {
                Logger.debug(Json.class, "Unable to get json attribute: " + index, exception);
            }
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

    public String toFormattedString() {
        if (_isArray) {
            return _arrayToString(4);
        }
        else {
            return _objectToString(4);
        }
    }

    public String toFormattedString(final Integer spacesIndentCount) {
        if (_isArray) {
            return _arrayToString(spacesIndentCount);
        }
        else {
            return _objectToString(spacesIndentCount);
        }
    }

    @Override
    public boolean equals(final Object object) {
        if (object == null) { return false; }
        if (! (object instanceof Json)) { return false; }

        final Json jsonObject = (Json) object;
        return this.toString().equals(jsonObject.toString());
    }

    @Override
    public String toString() {
        if (_isArray) {
            return _arrayToString(null);
        }
        else {
            return _objectToString(null);
        }
    }

    @Override
    public Json toJson() {
        return this;
    }
}
