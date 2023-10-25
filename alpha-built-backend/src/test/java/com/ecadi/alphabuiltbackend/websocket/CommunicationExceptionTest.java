package com.ecadi.alphabuiltbackend.websocket;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CommunicationExceptionTest {
    @Test
    public void testNullProjectExceptionDefaultConstructor() {
        Exception ex = new CommunicationException.NullProjectException();
        assertNull(ex.getMessage(), "Expected no detail message in NullProjectException.");
    }

    @Test
    public void testNullProjectExceptionWithMessage() {
        String detailMessage = "Test detail message";
        Exception ex = new CommunicationException.NullProjectException(detailMessage);
        assertEquals(detailMessage, ex.getMessage(), "Expected detail message in NullProjectException to match.");
    }

    @Test
    public void testInvalidMessageTypeExceptionDefaultConstructor() {
        Exception ex = new CommunicationException.InvalidMessageTypeException();
        assertNull(ex.getMessage(), "Expected no detail message in InvalidMessageTypeException.");
    }

    @Test
    public void testInvalidMessageTypeExceptionWithMessage() {
        String detailMessage = "Invalid message type";
        Exception ex = new CommunicationException.InvalidMessageTypeException(detailMessage);
        assertEquals(detailMessage, ex.getMessage(), "Expected detail message in InvalidMessageTypeException to match.");
    }

}