package com.ecadi.alphabuiltbackend.domain.project;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProjectDatabaseExceptionTest {

    @Test
    public void testProjectNotExistInDatabaseException() {
        assertThrows(ProjectDatabaseException.ProjectNotExistInDatabaseException.class, () -> {
            throw new ProjectDatabaseException.ProjectNotExistInDatabaseException();
        });
    }

    @Test
    public void testProjectExistInDatabaseException() {
        assertThrows(ProjectDatabaseException.ProjectExistInDatabaseException.class, () -> {
            throw new ProjectDatabaseException.ProjectExistInDatabaseException();
        });
    }

    @Test
    public void testProjectContainsMultipleUsersWithSameUserIdException() {
        assertThrows(ProjectDatabaseException.ProjectContainsMultipleUsersWithSameUserIdException.class, () -> {
            throw new ProjectDatabaseException.ProjectContainsMultipleUsersWithSameUserIdException();
        });
    }

    @Test
    public void testDefaultConstructor() {
        ProjectDatabaseException.ProjectAlreadyInitialisedException exception =
                new ProjectDatabaseException.ProjectAlreadyInitialisedException();
        assertEquals(null, exception.getMessage());
    }

    @Test
    public void testConstructorWithCustomMessage() {
        String message = "Test message";
        ProjectDatabaseException.ProjectAlreadyInitialisedException exception =
                new ProjectDatabaseException.ProjectAlreadyInitialisedException(message);
        assertEquals(message, exception.getMessage());
    }

}
