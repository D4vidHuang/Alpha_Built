package com.ecadi.alphabuiltbackend.intercommunication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


class InterMessageTest {

    private InterMessageType type;
    private int userId;
    private int projectId;
    private List<MeshMetadata> meshMetadata;

    private InterMessage interMessage;

    @BeforeEach
    void setUp() {
        type = InterMessageType.HELLO;
        userId = 1;
        projectId = 1;
        meshMetadata = new ArrayList<>();
        interMessage = new InterMessage(type, userId, projectId, meshMetadata);
    }

    @Test
    void getType() {
        assertArrayEquals(
                new InterMessageType[]{
                    InterMessageType.HELLO,
                    InterMessageType.BYE,
                    InterMessageType.GEO,
                    InterMessageType.HELLO_RESPONSE,
                    InterMessageType.BYE_RESPONSE,
                    InterMessageType.CTRLZ},
                InterMessageType.values());
    }

    @Test
    void getUserId() {
        assertEquals(1, interMessage.getUserId());
    }

    @Test
    void getProjectId() {
        assertEquals(1, interMessage.getProjectId());
    }

    @Test
    void getMeshMetadata() {
        assertEquals(new ArrayList<>(), interMessage.getMeshMetadata());
    }

    @Test
    void setType() {
        interMessage.setType(InterMessageType.BYE);
        assertEquals(InterMessageType.BYE, interMessage.getType());
    }

    @Test
    void setUserId() {
        interMessage.setUserId(2);
        assertEquals(2, interMessage.getUserId());
    }

    @Test
    void setProjectId() {
        interMessage.setProjectId(2);
        assertEquals(2, interMessage.getProjectId());
    }

    @Test
    void setMeshMetadata() {
        List<MeshMetadata> meshMetadataList = new ArrayList<>();
        meshMetadataList.add(new MeshMetadata(1, MeshAction.CREATE, null));
        interMessage.setMeshMetadata(meshMetadataList);
        assertEquals(meshMetadataList, interMessage.getMeshMetadata());
    }

    @Test
    void testEquals() {
        InterMessage interMessage1 = new InterMessage(InterMessageType.HELLO, 1, 1, new ArrayList<>());
        InterMessage interMessage2 = new InterMessage(InterMessageType.HELLO, 1, 1, new ArrayList<>());
        assertEquals(interMessage1, interMessage2);
        InterMessage interMessage3 = new InterMessage(InterMessageType.BYE, 1, 1, new ArrayList<>());
        assertNotEquals(interMessage1, interMessage3);
        InterMessage interMessage4 = new InterMessage(InterMessageType.HELLO, 2, 1, new ArrayList<>());
        assertNotEquals(interMessage1, interMessage4);
        InterMessage interMessage5 = new InterMessage(InterMessageType.HELLO, 1, 2, new ArrayList<>());
        assertNotEquals(interMessage1, interMessage5);
        InterMessage interMessage6 = new InterMessage(InterMessageType.HELLO, 1, 1, meshMetadata);
        assertEquals(interMessage1, interMessage6);
        InterMessage interMessage7 = new InterMessage(InterMessageType.HELLO, 1, 1, meshMetadata);
        assertEquals(interMessage6, interMessage7);
    }

    @Test
    void canEqual() {
        InterMessage interMessage1 = new InterMessage(InterMessageType.HELLO, 1, 1, new ArrayList<>());
        InterMessage interMessage2 = new InterMessage(InterMessageType.HELLO, 1, 1, new ArrayList<>());
        assertEquals(interMessage1, interMessage2);
    }

    @Test
    void testHashCode() {
        InterMessage interMessage1 = new InterMessage(InterMessageType.HELLO, 1, 1, new ArrayList<>());
        InterMessage interMessage2 = new InterMessage(InterMessageType.HELLO, 1, 1, new ArrayList<>());
        assertEquals(interMessage1.hashCode(), interMessage2.hashCode());
    }

    @Test
    void testToString() {
        assertEquals("InterMessage(type=HELLO, userId=1, projectId=1, meshMetadata=[])", interMessage.toString());
    }
}