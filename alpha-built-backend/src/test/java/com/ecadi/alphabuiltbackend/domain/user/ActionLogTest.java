package com.ecadi.alphabuiltbackend.domain.user;

import com.ecadi.alphabuiltbackend.intercommunication.MeshAction;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ActionLogTest {

    private ActionLog actionLog;
    private ObjectNode properties;

    @BeforeEach
    public void setup() {
        ActionLog emptyConstructor = new ActionLog();
        properties = JsonNodeFactory.instance.objectNode();
        properties.put("key", "value");

        actionLog = new ActionLog(1, 2, 123, 3, MeshAction.CREATE, properties);
    }

    @Test
    public void testActionLogConstructorAndGetters() {
        assertEquals(1, actionLog.getProjectId());
        assertEquals(2, actionLog.getUserId());
        assertEquals(123, actionLog.getTimestamp());
        assertEquals(3, actionLog.getMeshId());
        assertEquals(MeshAction.CREATE, actionLog.getMeshAction());
        assertEquals(properties, actionLog.getProperties());
    }

    @Test
    public void testSetters() {
        actionLog.setProjectId(10);
        assertEquals(10, actionLog.getProjectId());

        actionLog.setUserId(20);
        assertEquals(20, actionLog.getUserId());

        actionLog.setTimestamp(456);
        assertEquals(456, actionLog.getTimestamp());

        actionLog.setMeshId(30);
        assertEquals(30, actionLog.getMeshId());

        ObjectNode newProperties = JsonNodeFactory.instance.objectNode();
        newProperties.put("newKey", "newValue");
        actionLog.setProperties(newProperties);
        assertEquals(newProperties, actionLog.getProperties());

        actionLog.setMeshAction(MeshAction.ADD_MESH);
        assertEquals(MeshAction.ADD_MESH, actionLog.getMeshAction());
    }

    @Test
    public void testToString() {
        String expected = "ActionLog{id=0, projectId=1, userId=2, timestamp=123, meshId=3, properties={"
                + "\"key\":\"value\"}}";
        assertEquals(expected, actionLog.toString());
    }
}