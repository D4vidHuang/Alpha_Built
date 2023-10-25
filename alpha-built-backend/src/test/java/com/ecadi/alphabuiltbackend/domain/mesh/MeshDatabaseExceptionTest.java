package com.ecadi.alphabuiltbackend.domain.mesh;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MeshDatabaseExceptionTest {

    @Test
    void testMeshDoesNotExistInDatabaseException() {
        String message = "This mesh does not exist in the database.";
        MeshDatabaseException.MeshDoesNotExistInDatabaseException exception =
                new MeshDatabaseException.MeshDoesNotExistInDatabaseException(message);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testMeshExistInDatabaseException() {
        String message = "This mesh already exists in the database.";
        MeshDatabaseException.MeshExistInDatabaseException exception =
                new MeshDatabaseException.MeshExistInDatabaseException(message);
        assertEquals(message, exception.getMessage());
    }
}
