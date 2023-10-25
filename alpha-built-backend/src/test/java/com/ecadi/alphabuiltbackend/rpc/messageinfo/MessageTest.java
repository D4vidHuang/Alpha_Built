package com.ecadi.alphabuiltbackend.rpc.messageinfo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class MessageTest {
    @Test
    public void constructorAndGettersTest() {
        // Given
        MessageType messageType = MessageType.HEARTBEAT;
        ObjectMapper mapper = new ObjectMapper();
        JsonNode data = mapper.createObjectNode().put("key", "value");

        // When
        Message message = new Message(messageType, data);

        // Then
        assertEquals(messageType, message.getMessageType());
        assertEquals(data, message.getData());
    }

    @Test
    public void toStringTest() {
        // Given
        MessageType messageType = MessageType.HEARTBEAT;
        ObjectMapper mapper = new ObjectMapper();
        JsonNode data = mapper.createObjectNode().put("key", "value");

        // When
        Message message = new Message(messageType, data);

        // Then
        String expected = "Message{messageType=HEARTBEAT, data={\"key\":\"value\"}}";
        assertEquals(expected, message.toString());
    }
}