package com.softwareverde.json;

import org.junit.Assert;
import org.junit.Test;

public class JsonTests {
    @Test
    public void should_serialize_proper_json_object() {
        // Setup
        final String validComplexJsonString = "{\"integer\":1,\"string\":\"String\",\"boolean\":true,\"float\":3.1415,\"array\":[\"One\",\"Two\",\"Three\"],\"object\":{\"key1\":1,\"key2\":\"value2\",\"key3\":[]}}";

        // Action
        final Json json = Json.parse(validComplexJsonString);

        // Assert
        Assert.assertEquals(json.get("integer", Json.Types.INTEGER), Integer.valueOf(1));
        Assert.assertEquals(json.get("integer", Json.Types.STRING), "1");
        Assert.assertEquals(json.get("string", Json.Types.STRING), "String");
        Assert.assertEquals(json.get("boolean", Json.Types.BOOLEAN), Boolean.TRUE);
        Assert.assertEquals(json.get("float", Json.Types.FLOAT), Float.valueOf(3.1415F));

        final Json jsonArray = json.get("array", Json.Types.ARRAY);
        Assert.assertEquals(jsonArray.length(), Integer.valueOf(3));
        Assert.assertEquals(jsonArray.get(0, Json.Types.STRING), "One");
        Assert.assertEquals(jsonArray.get(1, Json.Types.STRING), "Two");
        Assert.assertEquals(jsonArray.get(2, Json.Types.STRING), "Three");

        final Json jsonObject = json.get("object", Json.Types.OBJECT);
        Assert.assertEquals(jsonObject.length(), Integer.valueOf(3));
        Assert.assertEquals(jsonObject.get("key1", Json.Types.STRING), "1");
        Assert.assertEquals(jsonObject.get("key2", Json.Types.STRING), "value2");
        Assert.assertEquals(jsonObject.get("key3", Json.Types.ARRAY), new Json(true));
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
        Assert.assertEquals(jsonString, expectedJsonString);
    }

    @Test
    public void should_serialize_simple_array() {
        // Setup
        final String validComplexJsonString = "\n[\"One\",\n\"Two\",\n\" Three \"]";

        // Action
        final Json json = Json.parse(validComplexJsonString);

        // Assert
        Assert.assertEquals(json.length(), Integer.valueOf(3));
        Assert.assertEquals(json.get(0, Json.Types.STRING), "One");
        Assert.assertEquals(json.get(1, Json.Types.STRING), "Two");
        Assert.assertEquals(json.get(2, Json.Types.STRING), " Three ");
    }
}
