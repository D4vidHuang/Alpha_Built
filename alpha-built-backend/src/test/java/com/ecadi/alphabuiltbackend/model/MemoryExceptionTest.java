package com.ecadi.alphabuiltbackend.model;

import com.ecadi.alphabuiltbackend.intercommunication.MeshAction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class MemoryExceptionTest {
    @Test
    void testNoProjectLoadedException() {
        assertThrows(MemoryException.NoProjectLoadedException.class, () -> {
            throw new MemoryException.NoProjectLoadedException();
        });

        Exception exception = assertThrows(MemoryException.NoProjectLoadedException.class, () -> {
            throw new MemoryException.NoProjectLoadedException("Test NoProjectLoadedException");
        });

        String expectedMessage = "Test NoProjectLoadedException";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testDeleteProjectWithUsersException() {
        assertThrows(MemoryException.DeleteProjectWithUsersException.class, () -> {
            throw new MemoryException.DeleteProjectWithUsersException();
        });

        Exception exception = assertThrows(MemoryException.DeleteProjectWithUsersException.class, () -> {
            throw new MemoryException.DeleteProjectWithUsersException("Test DeleteProjectWithUsersException");
        });

        String expectedMessage = "Test DeleteProjectWithUsersException";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testDeleteProjectNotInMemoryException() {
        assertThrows(MemoryException.DeleteProjectNotInMemoryException.class, () -> {
            throw new MemoryException.DeleteProjectNotInMemoryException();
        });

        Exception exception = assertThrows(MemoryException.DeleteProjectNotInMemoryException.class, () -> {
            throw new MemoryException.DeleteProjectNotInMemoryException("Test DeleteProjectNotInMemoryException");
        });

        String expectedMessage = "Test DeleteProjectNotInMemoryException";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testUserAlreadyExistedException() {
        assertThrows(MemoryException.UserAlreadyExistedException.class, () -> {
            throw new MemoryException.UserAlreadyExistedException();
        });

        Exception exception = assertThrows(MemoryException.UserAlreadyExistedException.class, () -> {
            throw new MemoryException.UserAlreadyExistedException("Test UserAlreadyExistedException");
        });

        String expectedMessage = "Test UserAlreadyExistedException";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testProjectAlreadyInMemoryException() {
        assertThrows(MemoryException.ProjectAlreadyInMemoryException.class, () -> {
            throw new MemoryException.ProjectAlreadyInMemoryException();
        });

        Exception exception = assertThrows(MemoryException.ProjectAlreadyInMemoryException.class, () -> {
            throw new MemoryException.ProjectAlreadyInMemoryException("Test ProjectAlreadyInMemoryException");
        });

        String expectedMessage = "Test ProjectAlreadyInMemoryException";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testCannotLoadProjectFromDatabaseException() {
        assertThrows(MemoryException.CannotLoadProjectFromDatabaseException.class, () -> {
            throw new MemoryException.CannotLoadProjectFromDatabaseException();
        });

        Exception exception = assertThrows(MemoryException.CannotLoadProjectFromDatabaseException.class, () -> {
            throw new MemoryException.CannotLoadProjectFromDatabaseException("Test CannotLoadProjectFromDatabaseException");
        });

        String expectedMessage = "Test CannotLoadProjectFromDatabaseException";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testUserAlreadyInMemoryException() {
        assertThrows(MemoryException.UserAlreadyInMemoryException.class, () -> {
            throw new MemoryException.UserAlreadyInMemoryException();
        });

        Exception exception = assertThrows(MemoryException.UserAlreadyInMemoryException.class, () -> {
            throw new MemoryException.UserAlreadyInMemoryException("Test UserAlreadyInMemoryException");
        });

        String expectedMessage = "Test UserAlreadyInMemoryException";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testCannotLoadUserFromDatabaseException() {
        assertThrows(MemoryException.CannotLoadUserFromDatabaseException.class, () -> {
            throw new MemoryException.CannotLoadUserFromDatabaseException();
        });

        Exception exception = assertThrows(MemoryException.CannotLoadUserFromDatabaseException.class, () -> {
            throw new MemoryException.CannotLoadUserFromDatabaseException("Test CannotLoadUserFromDatabaseException");
        });

        String expectedMessage = "Test CannotLoadUserFromDatabaseException";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testUserDoesNotExistInMemoryException() {
        assertThrows(MemoryException.UserDoesNotExistInMemoryException.class, () -> {
            throw new MemoryException.UserDoesNotExistInMemoryException();
        });

        Exception exception = assertThrows(MemoryException.UserDoesNotExistInMemoryException.class, () -> {
            throw new MemoryException.UserDoesNotExistInMemoryException("Test UserDoesNotExistInMemoryException");
        });

        String expectedMessage = "Test UserDoesNotExistInMemoryException";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testDeleteActiveUserException() {
        assertThrows(MemoryException.DeleteActiveUserException.class, () -> {
            throw new MemoryException.DeleteActiveUserException();
        });

        Exception exception = assertThrows(MemoryException.DeleteActiveUserException.class, () -> {
            throw new MemoryException.DeleteActiveUserException("Test DeleteActiveUserException");
        });

        String expectedMessage = "Test DeleteActiveUserException";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testUserAlreadyCreatedException() {
        assertThrows(MemoryException.UserAlreadyCreatedException.class, () -> {
            throw new MemoryException.UserAlreadyCreatedException();
        });

        Exception exception = assertThrows(MemoryException.UserAlreadyCreatedException.class, () -> {
            throw new MemoryException.UserAlreadyCreatedException("Test UserAlreadyCreatedException");
        });

        String expectedMessage = "Test UserAlreadyCreatedException";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testProjectAlreadyCreatedException() {
        assertThrows(MemoryException.ProjectAlreadyCreatedException.class, () -> {
            throw new MemoryException.ProjectAlreadyCreatedException();
        });

        Exception exception = assertThrows(MemoryException.ProjectAlreadyCreatedException.class, () -> {
            throw new MemoryException.ProjectAlreadyCreatedException("Test ProjectAlreadyCreatedException");
        });

        String expectedMessage = "Test ProjectAlreadyCreatedException";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testUserDoesNotBelongToProjectException() {
        assertThrows(MemoryException.UserDoesNotBelongToProjectException.class, () -> {
            throw new MemoryException.UserDoesNotBelongToProjectException();
        });

        Exception exception = assertThrows(MemoryException.UserDoesNotBelongToProjectException.class, () -> {
            throw new MemoryException.UserDoesNotBelongToProjectException("Test UserDoesNotBelongToProjectException");
        });

        String expectedMessage = "Test UserDoesNotBelongToProjectException";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testMeshToAddAlreadyExistInDatabaseException() {
        assertThrows(MemoryException.MeshToAddAlreadyExistInDatabaseException.class, () -> {
            throw new MemoryException.MeshToAddAlreadyExistInDatabaseException();
        });

        Exception exception = assertThrows(MemoryException.MeshToAddAlreadyExistInDatabaseException.class, () -> {
            throw new MemoryException.MeshToAddAlreadyExistInDatabaseException(
                    "Test MeshToAddAlreadyExistInDatabaseException");
        });

        String expectedMessage = "Test MeshToAddAlreadyExistInDatabaseException";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testMeshMetadataMissingBasicPropertiesException() {
        assertThrows(MemoryException.MeshMetadataMissingBasicPropertiesException.class, () -> {
            throw new MemoryException.MeshMetadataMissingBasicPropertiesException();
        });

        Exception exception = assertThrows(MemoryException.MeshMetadataMissingBasicPropertiesException.class, () -> {
            throw new MemoryException.MeshMetadataMissingBasicPropertiesException(
                    "Test MeshMetadataMissingBasicPropertiesException");
        });

        String expectedMessage = "Test MeshMetadataMissingBasicPropertiesException";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testMeshMetadataMissingPositionPropertyException() {
        assertThrows(MemoryException.MeshMetadataMissingPositionPropertyException.class, () -> {
            throw new MemoryException.MeshMetadataMissingPositionPropertyException();
        });

        Exception exception = assertThrows(MemoryException.MeshMetadataMissingPositionPropertyException.class, () -> {
            throw new MemoryException.MeshMetadataMissingPositionPropertyException(
                    "Test MeshMetadataMissingPositionPropertyException");
        });

        String expectedMessage = "Test MeshMetadataMissingPositionPropertyException";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }


    @Test
    void testPerformActionOnNonExistingMeshException() {
        assertThrows(MemoryException.PerformActionOnNonExistingMeshException.class, () -> {
            throw new MemoryException.PerformActionOnNonExistingMeshException();
        });

        Exception msgException = assertThrows(MemoryException.PerformActionOnNonExistingMeshException.class, () -> {
            throw new MemoryException.PerformActionOnNonExistingMeshException("dummy message");
        });
        assertEquals("dummy message", msgException.getMessage());


        MeshAction meshAction = MeshAction.CREATE;
        Exception exception = assertThrows(MemoryException.PerformActionOnNonExistingMeshException.class, () -> {
            throw new MemoryException.PerformActionOnNonExistingMeshException(
                    meshAction,
                    "Test PerformActionOnNonExistingMeshException");
        });

        String expectedMessage = meshAction.toString() + " | Test PerformActionOnNonExistingMeshException";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testConvertFailedMeshMetadataToActionLog() {
        assertThrows(MemoryException.ConvertFailedMeshMetadataToActionLog.class, () -> {
            throw new MemoryException.ConvertFailedMeshMetadataToActionLog();
        });

        Exception exception = assertThrows(MemoryException.ConvertFailedMeshMetadataToActionLog.class, () -> {
            throw new MemoryException.ConvertFailedMeshMetadataToActionLog("Test ConvertFailedMeshMetadataToActionLog");
        });

        String expectedMessage = "Test ConvertFailedMeshMetadataToActionLog";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testInvalidMeshActionException() {
        assertThrows(MemoryException.InvalidMeshActionException.class, () -> {
            throw new MemoryException.InvalidMeshActionException();
        });

        assertThrows(MemoryException.InvalidMeshActionException.class, () -> {
            throw new MemoryException.InvalidMeshActionException("Test InvalidMeshActionException");
        });
    }

}