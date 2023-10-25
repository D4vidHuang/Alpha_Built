package com.ecadi.alphabuiltbackend.domain.project;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class ProjectMeshExceptionTest {
    @Test
    public void testMeshIndexErrorException() {
        assertThrows(ProjectMeshException.MeshIndexErrorException.class, () -> {
            throw new ProjectMeshException.MeshIndexErrorException();
        });
    }

    @Test
    public void testMeshDoesNotContainInPropertyException() {
        assertThrows(ProjectMeshException.MeshDoesNotContainInPropertyException.class, () -> {
            throw new ProjectMeshException.MeshDoesNotContainInPropertyException();
        });
    }

    @Test
    public void testMeshAlreadyContainInPropertyException() {
        assertThrows(ProjectMeshException.MeshAlreadyContainInPropertyException.class, () -> {
            throw new ProjectMeshException.MeshAlreadyContainInPropertyException();
        });
    }

    @Test
    public void testDefaultConstructor() {
        ProjectMeshException.MeshAlreadyContainInPropertyException exception =
                new ProjectMeshException.MeshAlreadyContainInPropertyException();
        assertEquals(null, exception.getMessage());
    }

    @Test
    public void testConstructorWithCustomMessage() {
        String message = "Test message";
        ProjectMeshException.MeshAlreadyContainInPropertyException exception =
                new ProjectMeshException.MeshAlreadyContainInPropertyException(message);
        assertEquals(message, exception.getMessage());
    }

}