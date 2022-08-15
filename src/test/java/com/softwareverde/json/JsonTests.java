package com.softwareverde.json;

import org.junit.Assert;
import org.junit.Test;

public class JsonTests {
    @Test
    public void should_serialize_proper_json_object() {
        // Setup
        final String jsonString = "{\"integer\":1,\"string\":\"String\",\"boolean\":true,\"float\":3.1415,\"array\":[\"One\",\"Two\",\"Three\"],\"object\":{\"key1\":1,\"key2\":\"value2\",\"key3\":[]}}";

        // Action
        final Json json = Json.parse(jsonString);

        // Assert
        Assert.assertEquals(Integer.valueOf(1), json.get("integer", Json.Types.INTEGER));
        Assert.assertEquals("1", json.get("integer", Json.Types.STRING));
        Assert.assertEquals("String", json.get("string", Json.Types.STRING));
        Assert.assertEquals(Boolean.TRUE, json.get("boolean", Json.Types.BOOLEAN));
        final Float f = json.get("float", Json.Types.FLOAT);
        Assert.assertEquals(Float.valueOf(3.1415F), f);

        final Json jsonArray = json.get("array", Json.Types.ARRAY);
        Assert.assertEquals(Integer.valueOf(3), jsonArray.length());
        Assert.assertEquals("One", jsonArray.get(0, Json.Types.STRING));
        Assert.assertEquals("Two", jsonArray.get(1, Json.Types.STRING));
        Assert.assertEquals("Three", jsonArray.get(2, Json.Types.STRING));

        final Json jsonObject = json.get("object", Json.Types.OBJECT);
        Assert.assertEquals(Integer.valueOf(3), jsonObject.length());
        Assert.assertEquals("1", jsonObject.get("key1", Json.Types.STRING));
        Assert.assertEquals("value2", jsonObject.get("key2", Json.Types.STRING));
        Assert.assertEquals(new Json(true), jsonObject.get("key3", Json.Types.ARRAY));
    }

    @Test
    public void should_deserialize_complex_json_object() {
        // Setup
        final String expectedJsonString = "{\"boolean\":true,\"string\":\"String\",\"array\":[\"One\",\"Two\",\"Three\"],\"integer\":1,\"float\":3.1415,\"object\":{\"key1\":1,\"key2\":\"value2\",\"key3\":[]}}";

        final Json json = new Json();
        json.put("integer", 1);
        json.put("string", "String");
        json.put("boolean", true);
        json.put("float", 3.1415F);

        final Json jsonArray = new Json();
        jsonArray.add("One");
        jsonArray.add("Two");
        jsonArray.add("Three");
        json.put("array", jsonArray);

        final Json jsonObject = new Json();
        jsonObject.put("key1", 1);
        jsonObject.put("key2", "value2");
        jsonObject.put("key3", new Json(true));
        json.put("object", jsonObject);

        // Action
        final String jsonString = json.toString();

        // Assert
        Assert.assertEquals(expectedJsonString, jsonString);
    }

    @Test
    public void should_serialize_simple_array() {
        // Setup
        final String jsonString = "\n[\"One\",\n\"Two\",\n\" Three \"]";

        // Action
        final Json json = Json.parse(jsonString);

        // Assert
        Assert.assertEquals(Integer.valueOf(3), json.length());
        Assert.assertEquals("One", json.get(0, Json.Types.STRING));
        Assert.assertEquals("Two", json.get(1, Json.Types.STRING));
        Assert.assertEquals(" Three ", json.get(2, Json.Types.STRING));
    }

    @Test
    public void should_return_empty_string_if_null() {
        // Setup
        final String jsonString = "{\"nullString\":null}";

        // Action
        final Json json = Json.parse(jsonString);

        // Assert
        Assert.assertEquals("", json.get("nullString", Json.Types.STRING));
        Assert.assertEquals(null, json.getOrNull("nullString", Json.Types.STRING));
    }

    @Test
    public void should_return_true_for_boolean_when_string_true() {
        // Setup
        final String jsonString = "{\"trueBoolean\":true}";

        // Action
        final Json json = Json.parse(jsonString);

        // Assert
        Assert.assertEquals("", json.get("nullString", Json.Types.STRING));
        Assert.assertEquals(null, json.getOrNull("nullString", Json.Types.STRING));
    }

    @Test
    public void should_not_change_static_json_types_when_used_as_default_value() {
        // Setup
        final Json json = new Json();
        final Json expectedValue = new Json();

        // Action
        final Json defaultedJson = json.get("non-existent");
        defaultedJson.put("key", Json.parse("{\"key\": \"value\"}"));

        final Json unrelatedDefaultedJson = json.get("non-existent-2");
        final Json value = unrelatedDefaultedJson.get("key");

        // Assert
        Assert.assertEquals(expectedValue, value);
    }

    @Test
    public void should_not_change_static_json_types_when_used_as_default_value_via_getJson() {
        // Setup
        final Json json = new Json();
        final Json expectedValue = new Json();

        // Action
        final Json defaultedJson = json.get("non-existent", Json.Types.JSON);
        defaultedJson.put("key", Json.parse("{\"key\": \"value\"}"));

        final Json unrelatedDefaultedJson = json.get("non-existent-2", Json.Types.JSON);
        final Json value = unrelatedDefaultedJson.get("key");

        // Assert
        Assert.assertEquals(expectedValue, value);
    }

    @Test
    public void should_not_change_static_json_types_when_used_as_default_value_via_getJsonObject() {
        // Setup
        final Json json = new Json();
        final Json expectedValue = new Json();

        // Action
        final Json defaultedJson = json.get("non-existent", Json.Types.OBJECT);
        defaultedJson.put("key", Json.parse("{\"key\": \"value\"}"));

        final Json unrelatedDefaultedJson = json.get("non-existent-2", Json.Types.OBJECT);
        final Json value = unrelatedDefaultedJson.get("key");

        // Assert
        Assert.assertEquals(expectedValue, value);
    }

    @Test
    public void should_not_change_static_json_types_when_used_as_default_value_via_getJsonArray() {
        // Setup
        final Json json = new Json();
        final Json expectedValue = new Json();

        // Action
        final Json defaultedJson = json.get("non-existent", Json.Types.ARRAY);
        defaultedJson.put("key", Json.parse("{\"key\": \"value\"}"));

        final Json unrelatedDefaultedJson = json.get("non-existent-2", Json.Types.ARRAY);
        final Json value = unrelatedDefaultedJson.get("key");

        // Assert
        Assert.assertEquals(expectedValue, value);
    }

    @Test
    public void should_return_default_value_for_nonexistent_json() {
        // Setup
        final Json json = new Json();
        final Json expectedValue = Json.parse("{\"key\": \"value\"}");

        // Action
        final Json defaultedJson = json.get("non-existent", expectedValue);

        // Assert
        Assert.assertEquals(expectedValue, defaultedJson);
    }

    @Test
    public void should_return_default_value_for_invalid_integer() {
        // Setup
        final Json json = new Json();
        json.put("key", "seven");
        final Integer expectedValue = 7;

        // Action
        final Integer defaultedValue = json.get("key", expectedValue);

        // Assert
        Assert.assertEquals(expectedValue, defaultedValue);
    }

    @Test
    public void should_return_default_value_for_invalid_double() {
        // Setup
        final Json json = new Json();
        json.put("key", "value");
        final Double expectedValue = 7D;

        // Action
        final Double defaultedValue = json.get("key", expectedValue);

        // Assert
        Assert.assertEquals(expectedValue, defaultedValue);
    }

    @Test
    public void should_return_default_value_for_invalid_float() {
        // Setup
        final Json json = new Json();
        json.put("key", "value");
        final Float expectedValue = 7F;

        // Action
        final Float defaultedValue = json.get("key", expectedValue);

        // Assert
        Assert.assertEquals(expectedValue, defaultedValue);
    }

    @Test
    public void should_return_default_value_for_invalid_boolean() {
        // Setup
        final Json json = new Json();
        json.put("key", "value");
        final Boolean expectedValue = false;

        // Action
        final Boolean defaultedValue = json.get("key", expectedValue);

        // Assert
        Assert.assertEquals(expectedValue, defaultedValue);
    }

    @Test
    public void should_return_default_value_for_invalid_true_boolean() {
        // Setup
        final Json json = new Json();
        json.put("key", "value");
        final Boolean expectedValue = true;

        // Action
        final Boolean defaultedValue = json.get("key", expectedValue);

        // Assert
        Assert.assertEquals(expectedValue, defaultedValue);
    }

    @Test
    public void should_return_default_value_for_invalid_string() {
        // Setup
        final Json json = new Json();
        json.put("key", null);
        final String expectedValue = "string";

        // Action
        final String defaultedValue = json.get("key", expectedValue);

        // Assert
        Assert.assertEquals(expectedValue, defaultedValue);
    }

    @Test
    public void should_return_default_value_for_nonexistent_string() {
        // Setup
        final Json json = new Json();
        final String expectedValue = "string";

        // Action
        final String defaultedValue = json.get("key", expectedValue);

        // Assert
        Assert.assertEquals(expectedValue, defaultedValue);
    }

    @Test
    public void should_return_original_instance_when_key_exists() {
        // Setup
        final Json embeddedJsonArray = new Json(true);
        embeddedJsonArray.add(Json.parse("{originalKey: \"originalValue\"}"));

        final Json json = new Json();
        json.put("key", embeddedJsonArray);

        // Action
        json.get("key").get(0).put("newKey", "newValue");

        // Assert
        Assert.assertEquals("originalValue", json.get("key").get(0).getString("originalKey"));
        Assert.assertEquals("newValue", json.get("key").get(0).getString("newKey"));
    }
}
