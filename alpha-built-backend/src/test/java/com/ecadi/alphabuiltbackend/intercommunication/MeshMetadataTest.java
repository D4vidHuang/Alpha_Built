package com.ecadi.alphabuiltbackend.intercommunication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MeshMetadataTest {

    private MeshMetadata meshMetadata;
    private ObjectNode properties;
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        properties = mapper.createObjectNode();
        meshMetadata = new MeshMetadata(1, MeshAction.CREATE, properties);
    }

    @Test
    public void testAddVerdict() {
        assertFalse(properties.has("verdict"));
        meshMetadata.addVerdict(true);
        assertTrue(properties.has("verdict"));
        assertTrue(properties.get("verdict").asBoolean());
    }

    @Test
    public void testContainsProperty() {
        assertFalse(meshMetadata.containsPropertyPosition());
        assertFalse(meshMetadata.containsPropertyScaling());
        assertFalse(meshMetadata.containsPropertyRotation());

        properties.put("position", "positionValue");
        properties.put("scaling", "scalingValue");
        properties.put("rotation", "rotationValue");

        assertTrue(meshMetadata.containsPropertyPosition());
        assertTrue(meshMetadata.containsPropertyScaling());
        assertTrue(meshMetadata.containsPropertyRotation());
    }

    @Test
    public void testContainsBasicProperties() {
        assertFalse(meshMetadata.containsBasicProperties());

        properties.put("position", "positionValue");
        assertFalse(meshMetadata.containsBasicProperties());

        properties.put("scaling", "scalingValue");
        assertFalse(meshMetadata.containsBasicProperties());

        properties.put("rotation", "rotationValue");
        assertTrue(meshMetadata.containsBasicProperties());
    }
}
