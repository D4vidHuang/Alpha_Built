package com.ecadi.alphabuiltbackend.rpc.messageinfo;

import com.fasterxml.jackson.databind.JsonNode;

public class Message {
    private MessageType messageType;
    private JsonNode data;

    public Message(MessageType messageType, JsonNode data) {
        this.messageType = messageType;
        this.data = data;
    }

    public Message() {
        // Default constructor
    }


    @Override
    public String toString() {
        return "Message{"
                + "messageType=" + messageType
                + ", data=" + data
                + '}';
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public JsonNode getData() {
        return data;
    }
}
