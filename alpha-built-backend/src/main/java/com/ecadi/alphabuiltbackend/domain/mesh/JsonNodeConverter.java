package com.ecadi.alphabuiltbackend.domain.mesh;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


/**
 * Converter class to map JsonNode to String and vice versa.
 * This is used for storing JsonNode as a String in the database.
 */
@Converter
public class JsonNodeConverter implements AttributeConverter<JsonNode, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Converts a JsonNode to a String for database storage.
     *
     * @param jsonNode The JsonNode to convert.
     * @return The String representation of the JsonNode, or null if there was an exception.
     */
    @Override
    public String convertToDatabaseColumn(JsonNode jsonNode) {
        try {
            return objectMapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            System.out.println("Error converting JsonNode to String.");
        }
        return null;
    }

    /**
     * Converts a String from the database to a JsonNode.
     *
     * @param jsonString The String to convert.
     * @return The JsonNode representation of the String, or null if there was an exception.
     */
    @Override
    public JsonNode convertToEntityAttribute(String jsonString) {
        try {
            return objectMapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            // Handle exception appropriately
        }
        return null;
    }
}
