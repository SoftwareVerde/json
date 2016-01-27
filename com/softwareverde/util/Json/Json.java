//  ****************************************************************************
//  The MIT License (MIT)
//
//  Copyright (c) 2014 Joshua Green <joshmg@gmail.com>
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

package com.softwareverde.util;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.Collection;
import java.util.Set;
import java.util.Map;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/*
    - Json is a wrapper for JSONObject and JSONArray, and is Jsonable-aware:
        This means that Jsonable objects have their toJson() method invoked
        before they are compiled into Json.

    - Json is intelligently either an array or an object. By invoking the
        put(key, value) method, the Json object will become an Object. If
        values exist within the Json object before an invocation of put(),
        then the values will be arbitrarily indexed as stringified-integers;
        the index used will be smallest unused non-negative integer.
*/

public class Json implements Jsonable {
    private synchronized static void debug(String str) {
        System.out.println("com.softwareverde.util :: Json :: "+ str);
    }

    public static Json fromString(String string) {
        Json json = new Json();
        string = string.trim();

        if (string.length() > 0) {
            try {
                if (string.charAt(0) == '[') {
                    json._jsonArray = new JSONArray(string);
                    json._isArray = true;
                }
                else {
                    json._jsonObject = new JSONObject(string);
                    json._isArray = false;
                }
            } catch (JSONException e) {
                Json.debug("Exception 0: " + e.getMessage());
            }
        }

        return json;
    }

    private boolean _isArray;
    private JSONObject _jsonObject;
    private JSONArray _jsonArray;

    public Json() {
        this._jsonObject = new JSONObject();
        this._jsonArray = new JSONArray();

        this._isArray = true;
    }
    public <T> Json(Collection<T> c) {
        this._jsonObject = new JSONObject();
        this._jsonArray = new JSONArray();

        for (T item : c) {
            if (item instanceof Jsonable) {
                Json json = ((Jsonable) item).toJson();
                if (json.isArray()) {
                    this._jsonArray.put(json._jsonArray);
                }
                else {
                    this._jsonArray.put(json._jsonObject);
                }
            }
            else {
                this._jsonArray.put(item);
            }
        }
        this._isArray = true;
    }
    public <T> Json(Map<String, T> c) {
        this._jsonObject = new JSONObject();
        this._jsonArray = new JSONArray();

        for (Map.Entry<String, T> item : c.entrySet()) {
            if (item instanceof Jsonable) {
                Json json = ((Jsonable) item).toJson();
                if (json.isArray()) {
                    try {
                        this._jsonObject.put(item.getKey(), json._jsonArray);
                    } catch (JSONException e) { Json.debug("Exception: "+ e.getMessage()); }
                }
                else {
                    try {
                        this._jsonObject.put(item.getKey(), json._jsonObject);
                    } catch (JSONException e) { Json.debug("Exception: "+ e.getMessage()); }
                }
            }
            else {
                try {
                    this._jsonObject.put(item.getKey(), item.getValue());
                } catch (JSONException e) { Json.debug("Exception: "+ e.getMessage()); }
            }
        }
        this._isArray = false;
    }

    public boolean isArray() {
        return this._isArray;
    }

    public int length() {
        if (this.isArray()) {
            return this._jsonArray.length();
        }
        else {
            return this._jsonObject.length();
        }
    }

    public <T> void add(T value) {
        if (this.isArray()) {
            if (value instanceof Jsonable) {
                Json json = ((Jsonable) value).toJson();
                if (json.isArray()) {
                    this._jsonArray.put(json._jsonArray);
                }
                else {
                    this._jsonArray.put(json._jsonObject);
                }
            }
            else {
                this._jsonArray.put(value);
            }
        }
        else {
            // Append the item while keeping the object-form.. Index will be a stringified int of the lowest possible non-negative value.
            Integer index = 0;
            while (this._jsonObject.has(index.toString())) index++;
            if (value instanceof Jsonable) {
                Json json = ((Jsonable) value).toJson();
                if (json.isArray()) {
                    try {
                        this._jsonObject.put(index.toString(), json._jsonArray);
                    } catch (JSONException e) { Json.debug("Exception: "+ e.getMessage()); }
                }
                else {
                    try {
                        this._jsonObject.put(index.toString(), json._jsonObject);
                    } catch (JSONException e) { Json.debug("Exception: "+ e.getMessage()); }
                }
            }
            else {
                try {
                    this._jsonObject.put(index.toString(), value);
                } catch (JSONException e) { Json.debug("Exception: "+ e.getMessage()); }
            }
        }
    }
    public <T> void put(String key, T value) {
        // If Json has objects within array, copy them as object-values with stringified integer indexes..
        Integer index = 0;
        while (this._jsonArray.length() > 0) {
            try {
                this._jsonObject.put(index.toString(), this._jsonArray.remove(0));
            } catch (JSONException e) { Json.debug("Exception: "+ e.getMessage()); }
            index++;
        }
        this._isArray = false;

        if (value instanceof Jsonable) {
            Json json = ((Jsonable) value).toJson();
            if (json.isArray()) {
                try {
                    this._jsonObject.put(key, json._jsonArray);
                } catch (JSONException e) { Json.debug("Exception: "+ e.getMessage()); }
            }
            else {
                try {
                    this._jsonObject.put(key, json._jsonObject);
                } catch (JSONException e) { Json.debug("Exception: "+ e.getMessage()); }
            }
        }
        else {
            try {
                this._jsonObject.put(key, value);
            } catch (JSONException e) { Json.debug("Exception: "+ e.getMessage()); }
        }
    }

    public static class Types {
        public static final String     STRING  = new String("") ;
        public static final Integer    INTEGER = new Integer(0);
        public static final Double     DOUBLE  = new Double(0);
        public static final Boolean    BOOLEAN = new Boolean(false);
        public static final Json       OBJECT  = new Json();
        public static final Json       ARRAY   = new Json();
        public static final Json       JSON    = new Json();
    }
    private static final <T> T _convert(Object obj, T type) {
        T value = null;
        if (type instanceof String) {
            if (obj instanceof String) {
                value = (T) obj;
            }
            else {
                value = (T) obj.toString();
            }
        }
        else if (type instanceof Integer) {
            if (obj instanceof Integer) {
                value = (T) obj;
            }
            else if (obj.getClass() == int.class) {
                value = (T) obj;
            }
            else if (obj instanceof String) {
                try {
                    value = (T) new Integer(Integer.parseInt((String) obj));
                }
                catch (Exception e) { Json.debug("Exception 1: "+ e.getMessage()); }
            }
            else {
                Json.debug("WARNING: Returning null for Json._convert. (Integer)");
            }
        }
        else if (type instanceof Double) {
            if (obj instanceof Double) {
                value = (T) obj;
            }
            else if (obj.getClass() == double.class) {
                value = (T) obj;
            }
            else if (obj instanceof String) {
                try {
                    value = (T) new Double(Double.parseDouble((String) obj));
                }
                catch (Exception e) { Json.debug("Exception 2: "+ e.getMessage()); }
            }
            else {
                Json.debug("WARNING: Returning null for Json._convert. (Double)");
            }
        }
        else if (type instanceof Boolean) {
            if (obj instanceof Boolean) {
                value = (T) obj;
            }
            else if (obj.getClass() == boolean.class) {
                value = (T) obj;
            }
            else if (obj instanceof String) {
                try {
                    value = (T) new Boolean((Integer.parseInt((String) obj) > 0));
                }
                catch (Exception e) { Json.debug("Exception 3: "+ e.getMessage()); }
            }
            else {
                Json.debug("WARNING: Returning null for Json._convert. (Boolean)");
            }
        }
        else if (type instanceof Json) {
            if (obj instanceof Json) {
                value = (T) obj;
            }
            else if (obj instanceof JSONObject) {
                Json json = new Json();
                json._jsonObject = (JSONObject) obj;
                json._isArray = false;
                value = (T) json;
            }
            else if (obj instanceof JSONArray) {
                Json json = new Json();
                json._jsonArray = (JSONArray) obj;
                json._isArray = true;
                value = (T) json;
            }
            else if (obj instanceof String) {
                value = (T) Json.fromString((String) obj);
            }
            else {
                Json.debug("WARNING: Returning null for Json._convert. (Json)");
            }
        }
        return value;
    }
    private static final <T> T _getInstance(T type) {
        if (type instanceof String) {
            return (T) new String();
        }
        else if (type instanceof Integer) {
            return (T) new Integer(0);
        }
        else if (type instanceof Double) {
            return (T) new Double(0);
        }
        else if (type instanceof Boolean) {
            return (T) new Boolean(false);
        }
        else if (type instanceof Json) {
            return (T) new Json();
        }
        else return null;
    }

    public Json get(String key) { return this.get(key, Json.Types.JSON); }
    public <T> T get(String key, T type) {
        if (! this.isArray()) {
            if (this._jsonObject.has(key)) {
                try {
                    return Json._convert(this._jsonObject.get(key), type);
                }
                catch (Exception e) { Json.debug("Exception 4: "+ e.getMessage()); }
            }
        }

        if (type instanceof Json) return (T) new Json();
        else return (T) _getInstance(type);
    }

    public Json get(int index) {
        return this.get(index, Json.Types.JSON);
    }
    public <T> T get(int index, T type) {
        if (this.isArray()) {
            if (index < this._jsonArray.length() && index >= 0) {
                try {
                    return _convert(this._jsonArray.get(index), type);
                }
                catch (Exception e) { Json.debug("Exception 5: "+ e.getMessage()); }
            }
        }

        if (type instanceof Json) return (T) new Json();
        else return (T) _getInstance(type);
    }

    public Boolean hasKey(String key) {
        if (this.isArray()) {
            return false;
        }

        return (this._jsonObject.has(key));
    }

    public List<String> getKeys() {
        List<String> keys = new ArrayList<String>();

        if (this.isArray()) {
            for (Integer i=0; i<this.length(); i++) {
                keys.add(i.toString());
            }
        }
        else {
            Iterator<String> keysIt = this._jsonObject.keys();
            while (keysIt.hasNext()) {
                keys.add(keysIt.next());
            }
        }
        return keys;
    }

    @Override
    public String toString() {
        if (this.isArray()) {
            JSONArray out = new JSONArray();
            for (int i=0; i<this._jsonArray.length(); i++) {
                try {
                    if (this._jsonArray.get(i) instanceof Jsonable) {
                        Json j = ((Jsonable) this._jsonArray.get(i)).toJson();
                        if (j.isArray()) {
                            out.put(j._jsonArray);
                        }
                        else {
                            out.put(j._jsonObject);
                        }
                    }
                    else {
                        out.put(this._jsonArray.get(i));
                    }
                } catch (JSONException e) { Json.debug("Exception: "+ e.getMessage()); }
            }
            return out.toString();
        }
        else {
            JSONObject out = new JSONObject();
            // String[] keys = JSONObject.getNames(this._jsonObject);
            Iterator<String> it = this._jsonObject.keys();
            // for (int i=0; i<keys.length; i++) {
                // String key = keys[i];
            while (it.hasNext()) {
                String key = it.next();
                try {
                    if (this._jsonObject.get(key) instanceof Jsonable) {
                        Json j = ((Jsonable) this._jsonObject.get(key)).toJson();
                        if (j.isArray()) {
                            out.put(key, j._jsonArray);
                        }
                        else {
                            out.put(key, j._jsonObject);
                        }
                    }
                    else {
                        out.put(key, this._jsonObject.get(key));
                    }
                } catch (JSONException e) { Json.debug("Exception: "+ e.getMessage()); }
            }
            return out.toString();
        }
    }

    @Override
    public void setJsonParameters(String key, String value) { }

    @Override
    public Json toJson() {
        return this;
    }
}
