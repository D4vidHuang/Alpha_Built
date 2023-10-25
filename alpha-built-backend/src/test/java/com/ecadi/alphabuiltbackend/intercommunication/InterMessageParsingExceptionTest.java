package com.ecadi.alphabuiltbackend.intercommunication;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterMessageParsingExceptionTest {

    @Test
    public void testUndefinedMessageTypeException() {
        String message = "Undefined message type error";
        InterMessageParsingException.UndefinedMessageTypeException ex =
                new InterMessageParsingException.UndefinedMessageTypeException(message);
        assertEquals(message, ex.getMessage());
    }

    @Test
    public void testUndefinedUserIdException() {
        String message = "Undefined user ID error";
        InterMessageParsingException.UndefinedUserIdException ex =
                new InterMessageParsingException.UndefinedUserIdException(message);
        assertEquals(message, ex.getMessage());
    }

    @Test
    public void testUndefinedProjectIdException() {
        String message = "Undefined project ID error";
        InterMessageParsingException.UndefinedProjectIdException ex =
                new InterMessageParsingException.UndefinedProjectIdException(message);
        assertEquals(message, ex.getMessage());
    }

    @Test
    public void testUndefinedMeshMetadataException() {
        String message = "Undefined mesh metadata error";
        InterMessageParsingException.UndefinedMeshMetadataException ex =
                new InterMessageParsingException.UndefinedMeshMetadataException(message);
        assertEquals(message, ex.getMessage());
    }
}
