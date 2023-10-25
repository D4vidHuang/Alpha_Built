package com.ecadi.alphabuiltbackend.domain.mesh;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JsonNodeConverterTest {

    private JsonNodeConverter jsonNodeConverter;
    private ObjectMapper objectMapper;
    private JsonNode jsonNode;
    private String jsonString;
    private String invalidJsonString;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        jsonNodeConverter = new JsonNodeConverter();
        jsonString = "{\"key\":\"value\"}";
        invalidJsonString = "invalid json";
        jsonNode = objectMapper.createObjectNode().put("key", "value");
    }

    @Test
    void convertToDatabaseColumn() {
        String jsonString = jsonNodeConverter.convertToDatabaseColumn(jsonNode);
        assertEquals("{\"key\":\"value\"}", jsonString);

        // Pass null to method, this return a JsonString with value "null"
        String invalidResult = jsonNodeConverter.convertToDatabaseColumn(null);
        assertEquals("null", invalidResult);
    }

    @Test
    void convertToEntityAttribute() {
        JsonNode result = jsonNodeConverter.convertToEntityAttribute(jsonString);
        assertEquals(jsonNode, result);

        // Pass invalid JSON string, expecting a null value
        result = jsonNodeConverter.convertToEntityAttribute(invalidJsonString);
        assertNull(result);
    }
}
