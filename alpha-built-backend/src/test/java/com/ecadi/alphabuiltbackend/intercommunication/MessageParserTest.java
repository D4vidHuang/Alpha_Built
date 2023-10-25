package com.ecadi.alphabuiltbackend.intercommunication;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageParserTest {

    private JsonNode jsonNode;

    private ArrayNode arrayNode;

    @BeforeEach
    void setUp() {
        jsonNode = new ObjectMapper().createObjectNode();
    }

    @Test
    void parseInterMessage_emptyType() {
        JsonNode node = mock(JsonNode.class);
        when(node.get("type")).thenReturn(null);
        Exception result = assertThrows(
                InterMessageParsingException.UndefinedMessageTypeException.class,
                () -> {
                    MessageParser.parseInterMessage(node);
                });
        assertThat(result.getMessage(), containsString("Message does not have type in message"));
    }

    @Test
    void parseInterMessage_emptyUserId() {
        JsonNode typeNode = mock(JsonNode.class);
        JsonNode rawNode = mock(JsonNode.class);
        when(rawNode.get("type")).thenReturn(typeNode);

        Exception result = assertThrows(
                InterMessageParsingException.UndefinedUserIdException.class,
                () -> {
                    MessageParser.parseInterMessage(rawNode);
                });
        assertThat(result.getMessage(), containsString("Message does not have user id in message"));
    }

    @Test
    void parseInterMessage_emptyProjectId() {
        JsonNode rawNode = mock(JsonNode.class);
        JsonNode typeNode = mock(JsonNode.class);
        JsonNode userIdNode = mock(JsonNode.class);

        when(rawNode.get("type")).thenReturn(typeNode);
        when(rawNode.get("userId")).thenReturn(userIdNode);

        Exception result = assertThrows(
                InterMessageParsingException.UndefinedProjectIdException.class,
                () -> {
                    MessageParser.parseInterMessage(rawNode);
                });
        assertThat(result.getMessage(), containsString("Message does have project id in message"));
    }

    @Test
    void parseInterMessage_emptyMeshMetadata() {
        JsonNode rawNode = mock(JsonNode.class);
        JsonNode typeNode = mock(JsonNode.class);
        JsonNode userIdNode = mock(JsonNode.class);
        JsonNode projectIdNode = mock(JsonNode.class);

        when(rawNode.get("type")).thenReturn(typeNode);
        when(rawNode.get("userId")).thenReturn(userIdNode);
        when(rawNode.get("projectId")).thenReturn(projectIdNode);

        Exception result = assertThrows(
                InterMessageParsingException.UndefinedMeshMetadataException.class,
                () -> {
                    MessageParser.parseInterMessage(rawNode);
                });
        assertThat(result.getMessage(), containsString("Message does not have mesh metadata in message"));
    }

    @Test
    void parseInterMessage_meshMetadataNotOfArrayNode() {
        JsonNode rawNode = mock(JsonNode.class);
        JsonNode typeNode = mock(JsonNode.class);
        JsonNode userIdNode = mock(JsonNode.class);
        JsonNode projectIdNode = mock(JsonNode.class);

        when(rawNode.get("type")).thenReturn(typeNode);
        when(rawNode.get("userId")).thenReturn(userIdNode);
        when(rawNode.get("projectId")).thenReturn(projectIdNode);
        when(rawNode.get("meshMetaData")).thenReturn(projectIdNode);

        Exception result = assertThrows(
                InterMessageParsingException.UndefinedMeshMetadataException.class,
                () -> {
                    MessageParser.parseInterMessage(rawNode);
                });
        assertThat(result.getMessage(), containsString("Message mesh metadatas are not of array type in message"));
    }

    //    @Test
    //    void parseInterMessage_success() {
    //        JsonNode rawNode = mock(JsonNode.class);
    //        JsonNode typeNode = mock(JsonNode.class);
    //        JsonNode userIdNode = mock(JsonNode.class);
    //        ArrayNode projectIdNode = mock(ArrayNode.class);
    //
    //        lenient().when(rawNode.get("type")).thenReturn(typeNode);
    //        lenient().when(rawNode.get("userId")).thenReturn(userIdNode);
    //        lenient().when(rawNode.get("projectId")).thenReturn(projectIdNode);
    //        lenient().when(rawNode.get("meshMetaData")).thenReturn(projectIdNode);
    //        lenient().when(typeNode.asText()).thenReturn("HELLO");
    //
    //        Mockito.withSettings().defaultAnswer(Mockito.CALLS_REAL_METHODS);
    //        try (MockedStatic<MessageParser> mockParser = Mockito.mockStatic(MessageParser.class)) {
    //            mockParser.when(() -> MessageParser.parseMeshMetadataList(any()))
    //                    .thenReturn(null);
    //            assertAll(() -> MessageParser.parseInterMessage(rawNode));
    //        }
    //    }


    @Test
    void parseMeshMetadataList_actionEmpty() {
        ArrayNode rawMeshActionList = mock(ArrayNode.class);
        JsonNode e1 = mock(JsonNode.class);
        ArrayList<JsonNode> list = new ArrayList<>();
        list.add(e1);

        when(rawMeshActionList.elements()).thenReturn(list.iterator());

        Exception result = assertThrows(
                InterMessageParsingException.UndefinedMeshMetadataException.class,
                () -> {
                    MessageParser.parseMeshMetadataList(rawMeshActionList);
                });
        assertThat(result.getMessage(), containsString("does not have action."));

    }

    @Test
    void parseMeshMetadataList_emptyId() {
        ArrayNode rawMeshActionList = mock(ArrayNode.class);
        JsonNode e1 = mock(JsonNode.class);
        JsonNode mockAction = mock(JsonNode.class);

        ArrayList<JsonNode> list = new ArrayList<>();
        list.add(e1);

        when(rawMeshActionList.elements()).thenReturn(list.iterator());
        when(e1.get("meshAction")).thenReturn(mockAction);
        when(mockAction.asText()).thenReturn("CREATE");

        Exception result = assertThrows(
                InterMessageParsingException.UndefinedMeshMetadataException.class,
                () -> {
                    MessageParser.parseMeshMetadataList(rawMeshActionList);
                });
        assertThat(result.getMessage(), containsString("does not have mesh id."));
    }

    @Test
    void parseMeshMetadataList_emptyProperty() {
        ArrayNode rawMeshActionList = mock(ArrayNode.class);
        JsonNode e1 = mock(JsonNode.class);
        JsonNode mockAction = mock(JsonNode.class);
        ArrayList<JsonNode> list = new ArrayList<>();
        list.add(e1);

        when(rawMeshActionList.elements()).thenReturn(list.iterator());
        when(e1.get("meshAction")).thenReturn(mockAction);
        when(mockAction.asText()).thenReturn("CREATE");

        JsonNode mockMeshId = mock(JsonNode.class);
        when(e1.get("meshId")).thenReturn(mockMeshId);

        Exception result = assertThrows(
                InterMessageParsingException.UndefinedMeshMetadataException.class,
                () -> {
                    MessageParser.parseMeshMetadataList(rawMeshActionList);
                });
        assertThat(result.getMessage(), containsString("does not have mesh properties"));
    }

    @Test
    void parseMeshMetadataList_success() {
        ArrayNode rawMeshActionList = mock(ArrayNode.class);
        JsonNode e1 = mock(JsonNode.class);
        JsonNode mockAction = mock(JsonNode.class);
        ArrayList<JsonNode> list = new ArrayList<>();
        list.add(e1);

        when(rawMeshActionList.elements()).thenReturn(list.iterator());
        when(e1.get("meshAction")).thenReturn(mockAction);
        when(mockAction.asText()).thenReturn("CREATE");

        JsonNode mockMeshId = mock(JsonNode.class);
        when(e1.get("meshId")).thenReturn(mockMeshId);
        when(mockMeshId.asInt()).thenReturn(411);

        JsonNode mockProperty = mock(ObjectNode.class);
        when(e1.get("properties")).thenReturn(mockProperty);

        List<MeshMetadata> resList = MessageParser.parseMeshMetadataList(rawMeshActionList);
        assertEquals(resList.get(0).getMeshAction(), MeshAction.CREATE);
        assertEquals(resList.get(0).getMeshId(), 411);
        assertEquals(resList.get(0).getProperties(), (ObjectNode) mockProperty);
    }

    @Test
    void getMeshAction_create() {
        String name = "CREATE";
        MeshAction res = MessageParser.getMeshAction(name);
        assertEquals(res, MeshAction.CREATE);
    }

    @Test
    void getMeshAction_tranlate() {
        String name = "TRANSLATE";
        MeshAction res = MessageParser.getMeshAction(name);
        assertEquals(res, MeshAction.TRANSLATE);
    }

    @Test
    void getMeshAction_scale() {
        String name = "SCALE";
        MeshAction res = MessageParser.getMeshAction(name);
        assertEquals(res, MeshAction.SCALE);
    }

    @Test
    void getMeshAction_rotate() {
        String name = "ROTATE";
        MeshAction res = MessageParser.getMeshAction(name);
        assertEquals(res, MeshAction.ROTATE);
    }

    @Test
    void getMeshAction_remove() {
        String name = "REMOVE";
        MeshAction res = MessageParser.getMeshAction(name);
        assertEquals(res, MeshAction.REMOVE);
    }

    @Test
    void getMeshAction_addMesh() {
        String name = "ADD_MESH";
        MeshAction res = MessageParser.getMeshAction(name);
        assertEquals(res, MeshAction.ADD_MESH);
    }

    @Test
    void getMeshAction_revert() {
        String name = "REVERT";
        MeshAction res = MessageParser.getMeshAction(name);
        assertEquals(res, MeshAction.REVERT);
    }

    @Test
    void getMeshAction_redo() {
        String name = "REDO";
        MeshAction res = MessageParser.getMeshAction(name);
        assertEquals(res, MeshAction.REDO);
    }

    @Test
    void getMeshAction_lock() {
        String name = "LOCK";
        MeshAction res = MessageParser.getMeshAction(name);
        assertEquals(res, MeshAction.LOCK);
    }

    @Test
    void getMeshAction_unlock() {
        String name = "UNLOCK";
        MeshAction res = MessageParser.getMeshAction(name);
        assertEquals(res, MeshAction.UNLOCK);
    }

    @Test
    void getMeshAction_undefined() {
        String name = "abcdefg";
        Exception result = assertThrows(
                InterMessageParsingException.UndefinedMeshMetadataException.class,
                () -> {
                    MeshAction res = MessageParser.getMeshAction(name);
                });

        assertThat(result.getMessage(), containsString("is undefined."));
    }
}