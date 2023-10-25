package com.ecadi.alphabuiltbackend.domain.project;

import com.ecadi.alphabuiltbackend.domain.mesh.MeshPro;
import com.ecadi.alphabuiltbackend.domain.user.User;
import com.ecadi.alphabuiltbackend.domain.mesh.Mesh;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class ProjectTest {

    private Project project;
    private User user;
    private Mesh mesh;
    private MeshPro meshPro;

    @BeforeEach
    void setUp() {
        project = new Project(1);
        ChannelHandlerContext channelHandlerContext = mock(ChannelHandlerContext.class);
        user = new User(1, 1, channelHandlerContext);
        mesh = new Mesh(0, project);
        meshPro = new MeshPro(1);
    }

    @Test
    public void project_constructor() {
        List<Mesh> list = new ArrayList<>();
        list.add(mock(Mesh.class));
        Project project = new Project(1, 4399, list);

        assertEquals(project.getProjectId(), 1);
        assertEquals(project.getLatestTimeStamp(), 4399);
    }

    @Test
    void addActiveUserTest() {
        project.addActiveUser(user);
        assertTrue(project.containActiveUser(user));
    }

    @Test
    void removeActiveUser() {
        project.addActiveUser(user);
        //System.out.println(project.getActiveUserList().size());
        project.removeActiveUser(1);
        //System.out.println(project.getActiveUserList().size());
        assertFalse(project.containActiveUser(user));

        project.addActiveUser(user);
        project.addActiveUser(user);
        assertThrows(ProjectDatabaseException.ProjectContainsMultipleUsersWithSameUserIdException.class,
                () -> project.removeActiveUser(1));
    }

    @Test
    void containActiveUser() {
        project.addActiveUser(user);
        assertTrue(project.containActiveUser(user));
    }

    @Test
    void getActiveUserList() {
        project.addActiveUser(user);
        assertEquals(project.getActiveUserList().size(), 1);
    }

    @Test
    void hasActiveUser() {
        project.addActiveUser(user);
        assertTrue(project.hasActiveUser());
    }

    @Test
    void countActiveUser() {
        project.addActiveUser(user);
        assertEquals(project.countActiveUser(), 1);
    }

    @Test
    void testContainActiveUser() {
        project.addActiveUser(user);
        assertTrue(project.containActiveUser(user));
    }

    @Test
    void getProjectId() {
        assertEquals(project.getProjectId(), 1);
    }

    @Test
    void getActiveUserIdSet() {
        project.addActiveUser(user);
        assertEquals(project.getActiveUserIdSet().size(), 1);
    }

    @Test
    void equalTest() {
        Project project1 = new Project(1);
        assertEquals(project, project1);
        assertNotEquals(project, null);
    }

    @Test
    void testHashCode() {
        Project project1 = new Project(1);
        assertEquals(project.hashCode(), project1.hashCode());
    }

    @Test
    void addMesh() {
        project.addMesh(mesh);
        assertTrue(project.getMeshList().contains(mesh));
    }

    @Test
    void updateMeshProperties() {
        project.addMesh(mesh);
        project.updateMeshProperties(0, mesh.getProperties());
        assertEquals(project.getMeshList().get(0).getProperties(), mesh.getProperties());

        assertThrows(ProjectMeshException.MeshIndexErrorException.class,
                () -> project.updateMeshProperties(1, mesh.getProperties()));
    }

    @Test
    void updateMeshPropertiesException() {
        project.addMesh(mesh);
        project.updateMeshProperties(0, mesh.getProperties());
        assertEquals(project.getMeshList().get(0).getProperties(), mesh.getProperties());

        assertThrows(ProjectMeshException.MeshIndexErrorException.class,
                () -> project.updateMeshProperties(1, mesh.getProperties()));
        ObjectNode properties = mesh.getProperties();
        properties.put(null, "test exception");
        assertThrows(ProjectMeshException.MeshDoesNotContainInPropertyException.class,
                () -> project.updateMeshProperties(0, properties));
    }

    /*
    @Test
    void revertMesh() {
        meshPro.addMeshAndTimeStamp(mesh, 0);
        project.addMeshPro(meshPro);
        Mesh newmesh = project.revertMesh(0, 0);
        assertEquals(mesh, newmesh);
    }*/

    @Test
    void addMeshProperties() {
        project.addMesh(mesh);
        project.addMeshProperties(0, mesh.getProperties());
        assertEquals(project.getMeshList().get(0).getProperties(), mesh.getProperties());
    }

    @Test
    void getMaximumMeshId() {
        project.addMesh(mesh);
        assertEquals(project.getMaximumMeshId(), 1);
    }

    @Test
    void getMeshInMemoryByMeshId() {
        project.addMesh(mesh);
        assertEquals(project.getMeshInMemoryByMeshId(0), mesh);
    }

    @Test
    void getMeshList() {
        project.addMesh(mesh);
        assertEquals(project.getMeshList().size(), 1);
    }
}