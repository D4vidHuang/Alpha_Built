package com.ecadi.alphabuiltbackend.domain.mesh;

import com.ecadi.alphabuiltbackend.domain.project.ProjectDatabaseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ecadi.alphabuiltbackend.domain.project.Project;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;


class MeshTest {

    @Test
    public void testMeshConstructorWithProperties() {
        ObjectNode properties = new ObjectMapper().createObjectNode();
        properties.put("key", "value");

        Mesh mesh = new Mesh(1, properties);

        assertEquals(1, mesh.getMeshId());
        assertEquals(properties, mesh.getProperties());
    }

    @Test
    public void testMeshConstructorWithProject() {
        Project project = new Project(1);

        Mesh mesh = new Mesh(1, project);

        assertEquals(1, mesh.getMeshId());
        assertEquals(project.getProjectId(), mesh.getProjectId());
    }

    @Test
    public void testMeshConstructorWithProjectAndProperties() {
        Project project = new Project(1);
        ObjectNode properties = new ObjectMapper().createObjectNode();
        properties.put("key", "value");

        Mesh mesh = new Mesh(1, project, properties);

        assertEquals(1, mesh.getMeshId());
        assertEquals(project.getProjectId(), mesh.getProjectId());
        assertEquals(properties, mesh.getProperties());
    }

    @Test
    public void testDefaultMeshConstructor() {
        Mesh mesh = new Mesh();

        assertNotNull(mesh.getProperties());
    }

    @Test
    public void testGetMeshId() {
        Mesh mesh = new Mesh(1, new ObjectMapper().createObjectNode());

        int meshId = mesh.getMeshId();

        assertEquals(1, meshId);
    }

    @Test
    public void testGetProperties() {
        ObjectNode properties = new ObjectMapper().createObjectNode();
        properties.put("key", "value");

        Mesh mesh = new Mesh(1, properties);

        ObjectNode retrievedProperties = mesh.getProperties();

        assertEquals(properties, retrievedProperties);
    }

    @Test
    public void testGetProjectId() {
        Project project = new Project(1);
        Mesh mesh = new Mesh(1, project);

        int projectId = mesh.getProjectId();

        assertEquals(project.getProjectId(), projectId);
    }

    @Test
    public void testDeepCopy() {
        ObjectNode properties = new ObjectMapper().createObjectNode();
        properties.put("key", "value");
        Mesh mesh = new Mesh(1, new Project(1), properties);

        Mesh copiedMesh = mesh.deepCopy();

        assertNotSame(mesh, copiedMesh);
        assertEquals(mesh.getMeshId(), copiedMesh.getMeshId());
        assertEquals(mesh.getProperties(), copiedMesh.getProperties());
    }

    @Test
    public void testSnapshotMesh() {
        ObjectNode properties = new ObjectMapper().createObjectNode();
        properties.put("key", "value");
        Mesh mesh = new Mesh(1, properties);

        MeshSnapshot snapshot = mesh.snapshotMesh(123, 456);

        assertEquals(mesh.getMeshId(), snapshot.getMeshId());
        assertEquals(456, snapshot.getProjectId());
        assertEquals(mesh.getProperties(), snapshot.getProperties());
    }

    @Test
    public void testSetProjectRestricted() {
        Project project = new Project(1);
        Mesh mesh = new Mesh(1, new ObjectMapper().createObjectNode());

        mesh.setProjectRestricted(project);
        assertEquals(project.getProjectId(), mesh.getProjectId());
    }

    @Test
    public void testSetProjectRestrictedWithInitializedProject() {
        Project project = new Project(1);
        Mesh mesh = new Mesh(1, project);

        assertThrows(ProjectDatabaseException.ProjectAlreadyInitialisedException.class,
                () -> mesh.setProjectRestricted(project));
    }

    @Test
    public void testToString() {
        ObjectNode properties = new ObjectMapper().createObjectNode();
        properties.put("key", "value");
        Mesh mesh = new Mesh(1, new Project(1), properties);

        String toString = mesh.toString();

        assertTrue(toString.contains("Mesh{"));
        assertTrue(toString.contains("meshId=1"));
        assertTrue(toString.contains("project=Project"));
        assertTrue(toString.contains("projectId=1"));
        assertTrue(toString.contains("properties={\"key\":\"value\"}"));
    }
}
