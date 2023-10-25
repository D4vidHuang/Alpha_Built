package com.ecadi.alphabuiltbackend.domain.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.util.StringUtils;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Converter
public class ProjectListConverter implements AttributeConverter<List<ProjectIdAndUserIdPair>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<ProjectIdAndUserIdPair> attribute) {
        try {
            if (attribute == null || attribute.isEmpty()) {
                return null;
            }
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting project list to JSON", e);
        }
    }

    @Override
    public List<ProjectIdAndUserIdPair> convertToEntityAttribute(String dbData) {
        try {
            if (StringUtils.isEmpty(dbData)) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(dbData, new TypeReference<List<ProjectIdAndUserIdPair>>() {});
        } catch (IOException e) {
            throw new IllegalArgumentException("Error converting JSON to project list", e);
        }
    }
}
