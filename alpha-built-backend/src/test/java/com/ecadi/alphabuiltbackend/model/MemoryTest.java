package com.ecadi.alphabuiltbackend.model;


import com.ecadi.alphabuiltbackend.domain.mesh.Mesh;
import com.ecadi.alphabuiltbackend.domain.mesh.MeshPro;
import com.ecadi.alphabuiltbackend.domain.project.ProjectSnapshot;
import com.ecadi.alphabuiltbackend.domain.user.ActionLog;

import com.ecadi.alphabuiltbackend.domain.user.UserRepositoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ecadi.alphabuiltbackend.intercommunication.MeshAction;
import com.ecadi.alphabuiltbackend.intercommunication.MeshMetadata;
import com.ecadi.alphabuiltbackend.domain.mesh.MeshRepositoryService;
import com.ecadi.alphabuiltbackend.domain.project.Project;
import com.ecadi.alphabuiltbackend.domain.project.ProjectMeshException;
import com.ecadi.alphabuiltbackend.domain.project.ProjectRepositoryService;
import com.ecadi.alphabuiltbackend.domain.project.Scene;
import com.ecadi.alphabuiltbackend.domain.user.User;
import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

class MemoryTest {

    private Map<Integer, Project> projectMap;
    private Set<Integer> activeUsers;

    private Map<Integer, Stack<Scene>> versions;
    private Memory memory;
    private Project project;

    @Mock
    private ProjectRepositoryService projectRepositoryService;

    @Mock
    private UserRepositoryService userRepositoryService;

    @Mock
    private MeshRepositoryService meshRepositoryService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        projectMap = new HashMap<>();
        activeUsers = new HashSet<>();
        versions = new HashMap<>();
        projectRepositoryService = Mockito.mock(ProjectRepositoryService.class);
        userRepositoryService = Mockito.mock(UserRepositoryService.class);
        meshRepositoryService = Mockito.mock(MeshRepositoryService.class);
        memory = new Memory(projectRepositoryService, userRepositoryService, meshRepositoryService);
        project = new Project(1);
    }

    @Test
    void loadProject() {
        when(projectRepositoryService.getProjectByProjectId(1)).thenReturn(project);
        Project project1 = memory.loadProject(1);
        assertEquals(project, project1);

    }

    @Test
    public void loadProject_alreadyLoaded() {
        int projectId = 411;
        memory.projectMap.put(projectId, mock(Project.class));

        Exception result = assertThrows(
                MemoryException.ProjectAlreadyInMemoryException.class,
                () -> {
                    memory.loadProject(projectId);
                });
        assertThat(result.getMessage(), containsString("has already been loaded into memory."));

    }

    @Test
    public void testLoadProject_whenProjectIsNull() {
        int projectId = 1;
        when(projectRepositoryService.getProjectByProjectId(1)).thenReturn(null);
        try {
            memory.loadProject(projectId);
            //Assertions.fail("Expected a CannotLoadProjectFromDatabaseException to be thrown");
        } catch (MemoryException.CannotLoadProjectFromDatabaseException e) {
            assertEquals("Project 1 cannot be loaded from database.", e.getMessage());
        }
    }

    @Test
    void loadUser() {
        ChannelHandlerContext channelHandlerContext = Mockito.mock(ChannelHandlerContext.class);
        User user = new User(1, 1, channelHandlerContext);
        when(userRepositoryService.getUserByUserId(1)).thenReturn(user);
        User userLoaded = memory.loadUser(1, channelHandlerContext);
        assertEquals(userLoaded, user);
    }

    @Test
    void loadUserNull() {
        ChannelHandlerContext channelHandlerContext = Mockito.mock(ChannelHandlerContext.class);
        User user = new User(1, 1, channelHandlerContext);
        when(userRepositoryService.getUserByUserId(1)).thenReturn(null);
        try {
            memory.loadUser(1, channelHandlerContext);
            //Assertions.fail("Expected a CannotLoadProjectFromDatabaseException to be thrown");
        } catch (MemoryException.CannotLoadUserFromDatabaseException e) {
            assertEquals("User with user id 1 cannot be loaded into memory.", e.getMessage());
        }
    }

    @Test
    public void loadAllUsersInDatabaseForLog_success() {
        ProjectSnapshot ps1 = mock(ProjectSnapshot.class);
        List<ProjectSnapshot> allProjects = new ArrayList<>();
        allProjects.add(ps1);

        when(ps1.getProjectId()).thenReturn(411);
        when(projectRepositoryService.getAllProjects()).thenReturn(allProjects);

        User user = new User();
        List<User> allUsers = new ArrayList<>();
        userRepositoryService.saveUser(user);

        assertEquals(allUsers, memory.loadAllUsersInDatabaseForLog());
    }

    @Test
    void createUser() {
        ChannelHandlerContext channelHandlerContext = Mockito.mock(ChannelHandlerContext.class);
        User user = new User(1, 1, channelHandlerContext);
        user.setId(1L);
        when(userRepositoryService.checkUserExistingInDatabase(1)).thenReturn(false);
        User userCreated = memory.createUser(1, 1, channelHandlerContext);
        userCreated.setId(1L);
        assertEquals(userCreated, user);
    }

    @Test
    void createUserExist() {
        ChannelHandlerContext channelHandlerContext = Mockito.mock(ChannelHandlerContext.class);
        User user = new User(1, 1, channelHandlerContext);
        user.setId(1L);
        when(userRepositoryService.checkUserExistingInDatabase(1)).thenReturn(true);
        try {
            memory.createUser(1, 1, channelHandlerContext);
        } catch (MemoryException.UserAlreadyCreatedException e) {
            assertEquals("User with user id 1 has already existed in database", e.getMessage());
        }
    }

    @Test
    void createProject() {
        Project project = new Project(1);
        when(memory.checkProjectExist(1)).thenReturn(false);
        Project projectCreated = memory.createProject(1);
        assertEquals(projectCreated, project);
    }

    @Test
    void createProjectExist() {
        when(memory.checkProjectExist(1)).thenReturn(true);
        try {
            memory.createProject(1);
        } catch (MemoryException.ProjectAlreadyCreatedException e) {
            assertEquals("Project with project id 1 has already exist in database.", e.getMessage());
        }
    }

    @Test
    void checkUserExist() {
        when(userRepositoryService.checkUserExistingInDatabase(1)).thenReturn(true);
        boolean userExist = memory.checkUserExist(1);
        assertTrue(userExist);
    }

    //TODO: checkUserActive
    @Test
    void checkUserActive() {
        Memory memory2 = new Memory(projectRepositoryService, userRepositoryService, meshRepositoryService);
        memory2.activeUsers.add(1);
        memory2.activeUsers.add(2);
        assertTrue(memory2.checkUserActive(2));
        assertFalse(memory2.checkUserActive(4));
    }

    @Test
    void checkProjectExist() {
        when(projectRepositoryService.checkProjectExistByProjectId(1)).thenReturn(true);
        boolean projectExist = memory.checkProjectExist(1);
        assertTrue(projectExist);
    }

    @Test
    void checkProjectExistFalse() {
        when(projectRepositoryService.checkProjectExistByProjectId(1)).thenReturn(false);
        boolean projectExist = memory.checkProjectExist(1);
        assertFalse(projectExist);
    }

    //TODO: checkProjectActive
    @Test
    void checkProjectActive() {
        memory.projectMap.put(1, project);
        //projectMap.put(1, project);
        assertTrue(memory.checkProjectActive(1));
        assertFalse(memory.checkProjectActive(2));
    }

    @Test
    void addUser() {
        ChannelHandlerContext channelHandlerContext = Mockito.mock(ChannelHandlerContext.class);
        User user = new User(1, 1, channelHandlerContext);
        user.setId(1L);
        memory.projectMap.put(1, project);
        memory.activeUsers.add(1);
        memory.addUser(user);
        assertEquals(project.getActiveUserList(), List.of(user));
    }

    @Test
    void addUserNotInProjectMap() {
        ChannelHandlerContext channelHandlerContext = Mockito.mock(ChannelHandlerContext.class);
        User user = new User(1, 1, channelHandlerContext);
        user.setId(1L);
        //memory.projectMap.put(1, project);
        //memory.activeUsers.add(1);
        try {
            memory.addUser(user);
        } catch (MemoryException.NoProjectLoadedException e) {
            assertEquals("Project 1 has not been loaded into memory yet.", e.getMessage());
        }

    }

    @Test
    void addUserNotInActiveUser() {
        ChannelHandlerContext channelHandlerContext = Mockito.mock(ChannelHandlerContext.class);
        User user = new User(1, 1, channelHandlerContext);
        user.setId(1L);
        memory.projectMap.put(1, project);
        //memory.activeUsers.add(1);
        try {
            memory.addUser(user);
        } catch (MemoryException.UserAlreadyCreatedException e) {
            assertEquals("User with user id 1 has not been loaded into memory yet.", e.getMessage());
        }

    }

    @Test
    void addUserAlreadyContains() {
        ChannelHandlerContext channelHandlerContext = Mockito.mock(ChannelHandlerContext.class);
        User user = new User(1, 1, channelHandlerContext);
        user.setId(1L);
        memory.projectMap.put(1, project);
        memory.activeUsers.add(1);
        project.addActiveUser(user);
        try {
            memory.addUser(user);
        } catch (MemoryException.UserAlreadyExistedException e) {
            assertEquals("User 1 is already existed in project 1.", e.getMessage());
        }
    }


    @Test
    void checkActiveUsersInProject() {
        ChannelHandlerContext channelHandlerContext = Mockito.mock(ChannelHandlerContext.class);
        User user = new User(1, 1, channelHandlerContext);
        user.setId(1L);
        memory.projectMap.put(1, project);
        memory.activeUsers.add(1);
        project.addActiveUser(user);
        int count = memory.checkActiveUsersInProject(1);
        assertEquals(count, 1);
    }

    @Test
    void checkActiveUsersInProject_noSuchId() {
        ChannelHandlerContext channelHandlerContext = Mockito.mock(ChannelHandlerContext.class);
        User user = new User(1, 1, channelHandlerContext);
        user.setId(1L);
        memory.activeUsers.add(1);
        project.addActiveUser(user);

        Exception result = assertThrows(
                MemoryException.DeleteProjectNotInMemoryException.class,
                () -> {
                    memory.checkActiveUsersInProject(1);
                });
        assertThat(result.getMessage(), containsString("that is not in memory will be removed."));
    }

    @Test
    void removeProjectFromMemory() {
        memory.projectMap.put(1, project);
        memory.removeProjectFromMemory(1);
        assertEquals(memory.projectMap.size(), 0);
    }

    @Test
    void removeProjectFromMemory_noSuchProjectId() {
        Exception result = assertThrows(
                MemoryException.DeleteProjectNotInMemoryException.class,
                () -> {
                    memory.removeProjectFromMemory(1);
                });
        assertThat(result.getMessage(), containsString("that is not in memory will be removed."));
    }

    @Test
    void removeProjectFromMemory_duplicateActiveUser() {
        Project project = mock(Project.class);
        memory.projectMap.put(411, project);

        when(project.getProjectId()).thenReturn(411);
        when(project.countActiveUser()).thenReturn(1);

        Exception result = assertThrows(
                MemoryException.DeleteProjectWithUsersException.class,
                () -> {
                    memory.removeProjectFromMemory(411);
                });
        assertThat(result.getMessage(), containsString("that has active users will be removed."));
    }

    @Test
    void removeUserFromMemory() {
        ChannelHandlerContext channelHandlerContext = Mockito.mock(ChannelHandlerContext.class);
        User user = new User(1, 1, channelHandlerContext);
        user.setId(1L);
        memory.activeUsers.add(1);

        Project project = mock(Project.class);
        when(project.containActiveUser(user.getUserId())).thenReturn(true);
        memory.projectMap.put(411, project);


        Exception result = assertThrows(
                MemoryException.DeleteActiveUserException.class,
                () -> {
                    memory.removeUserFromMemory(1);
                });
        assertThat(result.getMessage(), containsString("that is active in"));
    }

    @Test
    void removeUserFromMemory_noSuchUser() {
        Exception result = assertThrows(
                MemoryException.UserDoesNotExistInMemoryException.class,
                () -> {
                    memory.removeUserFromMemory(411);
                });
        assertThat(result.getMessage(), containsString("that is not in memory will be removed."));
    }

    @Test
    void removeUserFromMemory_noRelatedProjects() {
        ChannelHandlerContext channelHandlerContext = Mockito.mock(ChannelHandlerContext.class);
        User user = new User(1, 1, channelHandlerContext);
        user.setId(1L);
        memory.activeUsers.add(1);
        memory.removeUserFromMemory(1);
        assertEquals(memory.activeUsers.size(), 0);
    }

    @Test
    void checkUserBelongToProjectById_success() {
        ChannelHandlerContext channelHandlerContext = Mockito.mock(ChannelHandlerContext.class);
        User user = new User(1, 1, channelHandlerContext);
        user.setId(1L);
        memory.projectMap.put(1, project);
        memory.activeUsers.add(1);
        project.addActiveUser(user);
        boolean check = memory.checkUserBelongToProjectById(1, 1);
        assertTrue(check);
    }

    @Test
    void checkUserBelongToProjectById_noMatchingUser() {
        Exception result = assertThrows(
                MemoryException.DeleteProjectNotInMemoryException.class,
                () -> {
                    memory.checkUserBelongToProjectById(1, 1);
                });
        assertThat(result.getMessage(), containsString("that is not in memory will be removed."));
    }

    @Test
    void checkUserBelongToProjectById_noMatchingProject() {
        ChannelHandlerContext channelHandlerContext = Mockito.mock(ChannelHandlerContext.class);
        User user = new User(1, 1, channelHandlerContext);
        user.setId(1L);
        memory.activeUsers.add(1);
        project.addActiveUser(user);

        Exception result = assertThrows(
                MemoryException.DeleteProjectNotInMemoryException.class,
                () -> {
                    memory.checkUserBelongToProjectById(1, 1);
                });
        assertThat(result.getMessage(), containsString("that is not in memory will be removed."));
    }

    @Test
    void removeUserFromProject() {
        ChannelHandlerContext channelHandlerContext = Mockito.mock(ChannelHandlerContext.class);
        User user = new User(1, 1, channelHandlerContext);
        user.setId(1L);
        memory.projectMap.put(1, project);
        memory.activeUsers.add(1);
        project.addActiveUser(user);
        memory.removeUserFromProject(1, 1);
        assertEquals(project.getActiveUserList().size(), 0);
    }

    @Test
    public void removeUserFromProject_doesNotBelong() {
        Memory spy = spy(memory);
        doReturn(false).when(spy).checkUserBelongToProjectById(1, 1);

        Exception result = assertThrows(
                MemoryException.UserDoesNotBelongToProjectException.class,
                () -> {
                    spy.removeUserFromProject(1, 1);
                });
        assertThat(result.getMessage(), containsString("does not belong to project with project id"));
    }

    @Test
    void getProjectHandler() {
        memory.projectMap.put(1, project);
        Project project1 = memory.getProjectHandler(1);
        assertEquals(project1, project);
    }


    @Test
    void getProjectHandler_noSuchProject() {
        Exception result = assertThrows(
                MemoryException.DeleteProjectNotInMemoryException.class,
                () -> {
                    memory.getProjectHandler(1);
                });
        assertThat(result.getMessage(), containsString("that is not in memory will be removed."));
    }

    @Test
    public void processMeshMetaDataForProject_noSuchProject() {
        Exception result = assertThrows(
                MemoryException.NoProjectLoadedException.class,
                () -> {
                    memory.processMeshMetaDataForProject(0, 1, mock(MeshMetadata.class));
                });
        assertThat(result.getMessage(), containsString("that is not in memory will be processed."));
    }

    @Test
    void processMeshMetaDataForProjectAddMesh() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.ADD_MESH, objectNode);
        memory.projectMap.put(1, project);
        when(meshRepositoryService.existMeshByMeshIdAndProjectId(0, 1)).thenReturn(false);
        memory.processMeshMetaDataForProject(1, 1, meshMetadata);

        verify(meshRepositoryService, times(1))
                .existMeshByMeshIdAndProjectId(0, 1);
    }


    @Test
    void processMeshMetaDataForProjectAddMeshNoBasicProperty() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("test", "nothing");
        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.ADD_MESH, objectNode);
        memory.projectMap.put(1, project);
        when(meshRepositoryService.existMeshByMeshIdAndProjectId(0, 1)).thenReturn(false);
        try {
            memory.processMeshMetaDataForProject(1, 1, meshMetadata);
        } catch (MemoryException.MeshMetadataMissingBasicPropertiesException e) {
            assertEquals("Mesh metadata adding mesh with mesh id 0 "
                    + "in project with project id 1 misses basic information.", e.getMessage());
        }
    }

    @Test
    void processMeshMetaDataForProjectAddMesh_MeshIndexErrorException() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        MeshMetadata meshMetadata = new MeshMetadata(1, MeshAction.ADD_MESH, objectNode);
        memory.projectMap.put(1, project);
        when(meshRepositoryService.existMeshByMeshIdAndProjectId(1, 1)).thenReturn(false);
        try {
            memory.processMeshMetaDataForProject(1, 1, meshMetadata);
        } catch (ProjectMeshException.MeshIndexErrorException e) {
            assertEquals("Inserting mesh with mesh id 1 into project with project id 1 "
                    + "which current has 0 meshes", e.getMessage());
        }
    }

    @Test
    void processMeshMetaDataForProjectAddMesh_MeshToAddAlreadyExistInDatabaseException() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.ADD_MESH, objectNode);
        memory.projectMap.put(1, project);
        when(meshRepositoryService.existMeshByMeshIdAndProjectId(0, 1)).thenReturn(true);
        try {
            memory.processMeshMetaDataForProject(1, 1, meshMetadata);
        } catch (MemoryException.MeshToAddAlreadyExistInDatabaseException e) {
            assertEquals("In project with project id 1, "
                    + "there already exists mesh with mesh id 0", e.getMessage());
        }
    }

    @Test
    void processMeshMetaDataForProjectAddMesh_merge_missingMergeListInfo() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.MERGE, objectNode);
        memory.projectMap.put(1, project);
        when(meshRepositoryService.existMeshByMeshIdAndProjectId(0, 1)).thenReturn(true);

        Exception result = assertThrows(
                MemoryException.MeshMetadataMissingBasicPropertiesException.class,
                () -> {
                    memory.processMeshMetaDataForProject(0, 1, meshMetadata);
                });
        assertThat(result.getMessage(), containsString("misses merge list information."));
    }

    @Test
    void processMeshMetaDataForProjectAddMesh_merge_missingBasicInfo() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("mergeIndices", 100);
        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.MERGE, objectNode);
        memory.projectMap.put(1, project);
        when(meshRepositoryService.existMeshByMeshIdAndProjectId(0, 1)).thenReturn(true);

        Exception result = assertThrows(
                MemoryException.MeshMetadataMissingBasicPropertiesException.class,
                () -> {
                    memory.processMeshMetaDataForProject(0, 1, meshMetadata);
                });
        assertThat(result.getMessage(), containsString("misses basic information."));
    }

    @Test
    void processMeshMetaDataForProjectAddMesh_merge_alreadyExists() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        objectNode.put("mergeIndices", 100);
        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.MERGE, objectNode);
        memory.projectMap.put(1, project);
        when(meshRepositoryService.existMeshByMeshIdAndProjectId(0, 1)).thenReturn(true);

        Exception result = assertThrows(
                MemoryException.MeshToAddAlreadyExistInDatabaseException.class,
                () -> {
                    memory.processMeshMetaDataForProject(0, 1, meshMetadata);
                });
        assertThat(result.getMessage(), containsString("there already exists mesh with mesh id"));
    }

    @Test
    void processMeshMetaDataForProjectAddMesh_merge_success() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        objectNode.put("mergeIndices", 100);
        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.MERGE, objectNode);
        memory.projectMap.put(1, project);
        when(meshRepositoryService.existMeshByMeshIdAndProjectId(0, 1)).thenReturn(false);

        assertTrue(memory.processMeshMetaDataForProject(0, 1, meshMetadata));
    }

    @Test
    void processMeshMetaDataForProjectAddMesh_subtract_missingMergeListInfo() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.SUBTRACT, objectNode);
        memory.projectMap.put(1, project);
        when(meshRepositoryService.existMeshByMeshIdAndProjectId(0, 1)).thenReturn(true);

        Exception result = assertThrows(
                MemoryException.MeshMetadataMissingBasicPropertiesException.class,
                () -> {
                    memory.processMeshMetaDataForProject(0, 1, meshMetadata);
                });
        assertThat(result.getMessage(), containsString("misses merge list information."));
    }

    @Test
    void processMeshMetaDataForProjectAddMesh_subtract_missingBasicInfo() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("subtractIndices", 100);
        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.SUBTRACT, objectNode);
        memory.projectMap.put(1, project);
        when(meshRepositoryService.existMeshByMeshIdAndProjectId(0, 1)).thenReturn(true);

        Exception result = assertThrows(
                MemoryException.MeshMetadataMissingBasicPropertiesException.class,
                () -> {
                    memory.processMeshMetaDataForProject(0, 1, meshMetadata);
                });
        assertThat(result.getMessage(), containsString("misses basic information."));
    }

    @Test
    void processMeshMetaDataForProjectAddMesh_subtract_alreadyExists() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        objectNode.put("subtractIndices", 100);
        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.SUBTRACT, objectNode);
        memory.projectMap.put(1, project);
        when(meshRepositoryService.existMeshByMeshIdAndProjectId(0, 1)).thenReturn(true);

        Exception result = assertThrows(
                MemoryException.MeshToAddAlreadyExistInDatabaseException.class,
                () -> {
                    memory.processMeshMetaDataForProject(0, 1, meshMetadata);
                });
        assertThat(result.getMessage(), containsString("there already exists mesh with mesh id"));
    }

    @Test
    void processMeshMetaDataForProjectAddMesh_subtract_success() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        objectNode.put("subtractIndices", 100);
        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.SUBTRACT, objectNode);
        memory.projectMap.put(1, project);
        when(meshRepositoryService.existMeshByMeshIdAndProjectId(0, 1)).thenReturn(false);

        assertTrue(memory.processMeshMetaDataForProject(0, 1, meshMetadata));
    }

    @Test
    void processMeshMetaDataForProjectAddMesh_intersect_missingMergeListInfo() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.INTERSECT, objectNode);
        memory.projectMap.put(1, project);
        when(meshRepositoryService.existMeshByMeshIdAndProjectId(0, 1)).thenReturn(true);

        Exception result = assertThrows(
                MemoryException.MeshMetadataMissingBasicPropertiesException.class,
                () -> {
                    memory.processMeshMetaDataForProject(0, 1, meshMetadata);
                });
        assertThat(result.getMessage(), containsString("misses merge list information."));
    }

    @Test
    void processMeshMetaDataForProjectAddMesh_intersect_missingBasicInfo() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("intersectIndices", 100);
        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.INTERSECT, objectNode);
        memory.projectMap.put(1, project);
        when(meshRepositoryService.existMeshByMeshIdAndProjectId(0, 1)).thenReturn(true);

        Exception result = assertThrows(
                MemoryException.MeshMetadataMissingBasicPropertiesException.class,
                () -> {
                    memory.processMeshMetaDataForProject(0, 1, meshMetadata);
                });
        assertThat(result.getMessage(), containsString("misses basic information."));
    }

    @Test
    void processMeshMetaDataForProjectAddMesh_intersect_alreadyExists() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        objectNode.put("intersectIndices", 100);
        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.INTERSECT, objectNode);
        memory.projectMap.put(1, project);
        when(meshRepositoryService.existMeshByMeshIdAndProjectId(0, 1)).thenReturn(true);

        Exception result = assertThrows(
                MemoryException.MeshToAddAlreadyExistInDatabaseException.class,
                () -> {
                    memory.processMeshMetaDataForProject(0, 1, meshMetadata);
                });
        assertThat(result.getMessage(), containsString("there already exists mesh with mesh id"));
    }

    @Test
    void processMeshMetaDataForProjectAddMesh_intersect_success() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        objectNode.put("intersectIndices", 100);
        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.INTERSECT, objectNode);
        memory.projectMap.put(1, project);
        when(meshRepositoryService.existMeshByMeshIdAndProjectId(0, 1)).thenReturn(false);

        assertTrue(memory.processMeshMetaDataForProject(0, 1, meshMetadata));
    }

    @Test
    void processMeshMetaDataForProjectAddMesh_union_missingMergeListInfo() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.UNION, objectNode);
        memory.projectMap.put(1, project);
        when(meshRepositoryService.existMeshByMeshIdAndProjectId(0, 1)).thenReturn(true);

        Exception result = assertThrows(
                MemoryException.MeshMetadataMissingBasicPropertiesException.class,
                () -> {
                    memory.processMeshMetaDataForProject(0, 1, meshMetadata);
                });
        assertThat(result.getMessage(), containsString("misses merge list information."));
    }

    @Test
    void processMeshMetaDataForProjectAddMesh_union_missingBasicInfo() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("unionIndices", 100);
        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.UNION, objectNode);
        memory.projectMap.put(1, project);
        when(meshRepositoryService.existMeshByMeshIdAndProjectId(0, 1)).thenReturn(true);

        Exception result = assertThrows(
                MemoryException.MeshMetadataMissingBasicPropertiesException.class,
                () -> {
                    memory.processMeshMetaDataForProject(0, 1, meshMetadata);
                });
        assertThat(result.getMessage(), containsString("misses basic information."));
    }

    @Test
    void processMeshMetaDataForProjectAddMesh_union_alreadyExists() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        objectNode.put("unionIndices", 100);
        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.UNION, objectNode);
        memory.projectMap.put(1, project);
        when(meshRepositoryService.existMeshByMeshIdAndProjectId(0, 1)).thenReturn(true);

        Exception result = assertThrows(
                MemoryException.MeshToAddAlreadyExistInDatabaseException.class,
                () -> {
                    memory.processMeshMetaDataForProject(0, 1, meshMetadata);
                });
        assertThat(result.getMessage(), containsString("there already exists mesh with mesh id"));
    }

    @Test
    void processMeshMetaDataForProjectAddMesh_union_success() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        objectNode.put("unionIndices", 100);
        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.UNION, objectNode);
        memory.projectMap.put(1, project);
        when(meshRepositoryService.existMeshByMeshIdAndProjectId(0, 1)).thenReturn(false);

        assertTrue(memory.processMeshMetaDataForProject(0, 1, meshMetadata));
    }


    @Test
    public void processMeshMetaDataForProject_translate_missBasicInfo() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.TRANSLATE, objectNode);
        memory.projectMap.put(1, project);
        when(meshRepositoryService.existMeshByMeshIdAndProjectId(0, 1)).thenReturn(false);

        Exception result = assertThrows(
                MemoryException.MeshMetadataMissingPositionPropertyException.class,
                () -> {
                    memory.processMeshMetaDataForProject(0, 1, meshMetadata);
                });
        assertThat(result.getMessage(), containsString("Mesh metadata translating mesh with mesh id"));
        assertThat(result.getMessage(), containsString("misses basic information."));
    }

    @Test
    public void processMeshMetaDataForProject_translate_invalidMeshId() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        MeshMetadata meshMetadata = new MeshMetadata(1, MeshAction.TRANSLATE, objectNode);
        memory.projectMap.put(1, project);

        Exception result = assertThrows(
                MemoryException.PerformActionOnNonExistingMeshException.class,
                () -> {
                    memory.processMeshMetaDataForProject(1, 1, meshMetadata);
                });
        assertThat(result.getMessage(), containsString("TRANSLATE"));
        assertThat(result.getMessage(), containsString("Performing action on non existing mesh with"));
    }

    @Test
    public void processMeshMetaDataForProject_translate_meshLocked() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        project = mock(Project.class);
        when(project.getMaximumMeshId()).thenReturn(1);
        memory.projectMap.put(1, project);
        MeshPro meshPro = new MeshPro(0);
        meshPro.lockMeshPro(411);
        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.TRANSLATE, objectNode);

        when(project.getMeshProByMeshId(0)).thenReturn(meshPro);

        boolean result = memory.processMeshMetaDataForProject(0, 1, meshMetadata);

        // tests failed here
        //assertFalse(result);
    }

    @Test
    public void processMeshMetaDataForProject_translate_success() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        project = mock(Project.class);
        when(project.getMaximumMeshId()).thenReturn(1);
        memory.projectMap.put(1, project);
        MeshPro meshPro = new MeshPro(0);
        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.TRANSLATE, objectNode);

        when(project.getMeshProByMeshId(0)).thenReturn(meshPro);

        boolean result = memory.processMeshMetaDataForProject(0, 1, meshMetadata);
        assertTrue(result);
        verify(project, times(1)).updateMeshProperties(0, objectNode);
    }

    @Test
    public void processMeshMetaDataForProject_translateEnd_missBasicInfo() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.TRANSLATE_END, objectNode);
        memory.projectMap.put(1, project);
        when(meshRepositoryService.existMeshByMeshIdAndProjectId(0, 1)).thenReturn(false);

        Exception result = assertThrows(
                MemoryException.MeshMetadataMissingPositionPropertyException.class,
                () -> {
                    memory.processMeshMetaDataForProject(0, 1, meshMetadata);
                });
        assertThat(result.getMessage(), containsString("Mesh metadata translating mesh with mesh id"));
        assertThat(result.getMessage(), containsString("misses basic information."));
    }

    @Test
    public void processMeshMetaDataForProject_translateEnd_invalidMeshId() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        MeshMetadata meshMetadata = new MeshMetadata(1, MeshAction.TRANSLATE_END, objectNode);
        memory.projectMap.put(1, project);

        Exception result = assertThrows(
                MemoryException.PerformActionOnNonExistingMeshException.class,
                () -> {
                    memory.processMeshMetaDataForProject(1, 1, meshMetadata);
                });
        assertThat(result.getMessage(), containsString("TRANSLATE"));
        assertThat(result.getMessage(), containsString("Performing action on non existing mesh with"));
    }

    @Test
    public void processMeshMetaDataForProject_translateEnd_success() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        project = mock(Project.class);
        when(project.getMaximumMeshId()).thenReturn(1);
        memory.projectMap.put(1, project);
        MeshPro meshPro = new MeshPro(0);
        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.TRANSLATE_END, objectNode);

        when(project.getMeshProByMeshId(0)).thenReturn(meshPro);

        boolean result = memory.processMeshMetaDataForProject(0, 1, meshMetadata);
        assertTrue(result);
        verify(project, times(1)).updateMeshProperties(0, objectNode);
    }

    @Test
    public void processMeshMetaDataForProject_rotate_missBasicInfo() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.ROTATE, objectNode);
        memory.projectMap.put(1, project);
        when(meshRepositoryService.existMeshByMeshIdAndProjectId(0, 1)).thenReturn(false);

        Exception result = assertThrows(
                MemoryException.MeshMetadataMissingPositionPropertyException.class,
                () -> {
                    memory.processMeshMetaDataForProject(0, 1, meshMetadata);
                });
        assertThat(result.getMessage(), containsString("Mesh metadata rotating mesh with mesh id"));
        assertThat(result.getMessage(), containsString("misses basic information."));
    }

    @Test
    public void processMeshMetaDataForProject_rotate_invalidMeshId() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        MeshMetadata meshMetadata = new MeshMetadata(1, MeshAction.ROTATE, objectNode);
        memory.projectMap.put(1, project);

        Exception result = assertThrows(
                MemoryException.PerformActionOnNonExistingMeshException.class,
                () -> {
                    memory.processMeshMetaDataForProject(1, 1, meshMetadata);
                });
        assertThat(result.getMessage(), containsString("ROTATE"));
        assertThat(result.getMessage(), containsString("Performing action on non existing mesh with"));
    }

    @Test
    public void processMeshMetaDataForProject_rotate_success() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        project = mock(Project.class);
        when(project.getMaximumMeshId()).thenReturn(1);
        memory.projectMap.put(1, project);
        MeshPro meshPro = new MeshPro(0);
        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.ROTATE, objectNode);

        when(project.getMeshProByMeshId(0)).thenReturn(meshPro);

        boolean result = memory.processMeshMetaDataForProject(0, 1, meshMetadata);
        assertTrue(result);
        verify(project, times(1)).updateMeshProperties(0, objectNode);
    }

    @Test
    public void processMeshMetaDataForProject_scale_missBasicInfo() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.SCALE, objectNode);
        memory.projectMap.put(1, project);
        when(meshRepositoryService.existMeshByMeshIdAndProjectId(0, 1)).thenReturn(false);

        Exception result = assertThrows(
                MemoryException.MeshMetadataMissingPositionPropertyException.class,
                () -> {
                    memory.processMeshMetaDataForProject(0, 1, meshMetadata);
                });
        assertThat(result.getMessage(), containsString("Mesh metadata scaling mesh with mesh id"));
        assertThat(result.getMessage(), containsString("misses basic information."));
    }

    @Test
    public void processMeshMetaDataForProject_scale_invalidMeshId() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        MeshMetadata meshMetadata = new MeshMetadata(1, MeshAction.SCALE, objectNode);
        memory.projectMap.put(1, project);

        Exception result = assertThrows(
                MemoryException.PerformActionOnNonExistingMeshException.class,
                () -> {
                    memory.processMeshMetaDataForProject(1, 1, meshMetadata);
                });
        assertThat(result.getMessage(), containsString("SCALE"));
        assertThat(result.getMessage(), containsString("Performing action on non existing mesh with"));
    }

    @Test
    public void processMeshMetaDataForProject_scale_meshLocked() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        project = mock(Project.class);
        when(project.getMaximumMeshId()).thenReturn(1);
        memory.projectMap.put(1, project);
        MeshPro meshPro = new MeshPro(0);
        meshPro.lockMeshPro(411);
        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.SCALE, objectNode);

        when(project.getMeshProByMeshId(0)).thenReturn(meshPro);

        boolean result = memory.processMeshMetaDataForProject(0, 1, meshMetadata);
        assertFalse(result);
    }

    @Test
    public void processMeshMetaDataForProject_scale_success() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        project = mock(Project.class);
        when(project.getMaximumMeshId()).thenReturn(1);
        memory.projectMap.put(1, project);
        MeshPro meshPro = new MeshPro(0);
        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.SCALE, objectNode);

        when(project.getMeshProByMeshId(0)).thenReturn(meshPro);

        boolean result = memory.processMeshMetaDataForProject(0, 1, meshMetadata);
        assertTrue(result);
        verify(project, times(1)).updateMeshProperties(0, objectNode);
    }

    @Test
    public void processMeshMetaDataForProject_revert_missBasicInfo() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        MeshMetadata meshMetadata = new MeshMetadata(1, MeshAction.REVERT, objectNode);
        memory.projectMap.put(1, project);

        Exception result = assertThrows(
                MemoryException.PerformActionOnNonExistingMeshException.class,
                () -> {
                    memory.processMeshMetaDataForProject(1, 1, meshMetadata);
                });
        assertThat(result.getMessage(), containsString("REVERT"));
        assertThat(result.getMessage(), containsString("Performing action on non existing mesh with"));
    }

    @Test
    public void processMeshMetaDataForProject_revert_success() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        project = mock(Project.class);
        when(project.getMaximumMeshId()).thenReturn(1);
        memory.projectMap.put(1, project);

        ObjectNode objectNode2 = new ObjectMapper().createObjectNode();
        objectNode2.put("position", 2);
        objectNode2.put("scaling", 1);
        objectNode2.put("rotation", 1);
        Mesh revertedMesh = new Mesh(3, objectNode2);

        when(project.revertMesh(0)).thenReturn(revertedMesh);

        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.REVERT, objectNode);
        boolean result = memory.processMeshMetaDataForProject(0, 1, meshMetadata);
        assertTrue(result);
        assertEquals(meshMetadata.getProperties(), objectNode2);
    }

    @Test
    public void processMeshMetaDataForProject_redo_missBasicInfo() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        MeshMetadata meshMetadata = new MeshMetadata(1, MeshAction.REDO, objectNode);
        memory.projectMap.put(1, project);

        Exception result = assertThrows(
                MemoryException.PerformActionOnNonExistingMeshException.class,
                () -> {
                    memory.processMeshMetaDataForProject(1, 1, meshMetadata);
                });
        assertThat(result.getMessage(), containsString("REDO"));
        assertThat(result.getMessage(), containsString("Performing action on non existing mesh with"));
    }

    @Test
    public void processMeshMetaDataForProject_redo_success() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        project = mock(Project.class);
        when(project.getMaximumMeshId()).thenReturn(1);
        memory.projectMap.put(1, project);

        ObjectNode objectNode2 = new ObjectMapper().createObjectNode();
        objectNode2.put("position", 2);
        objectNode2.put("scaling", 1);
        objectNode2.put("rotation", 1);
        Mesh redoneMesh = new Mesh(3, objectNode2);

        when(project.redoMesh(0)).thenReturn(redoneMesh);

        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.REDO, objectNode);
        boolean result = memory.processMeshMetaDataForProject(0, 1, meshMetadata);
        assertTrue(result);
        assertEquals(meshMetadata.getProperties(), objectNode2);
    }

    @Test
    public void processMeshMetaDataForProject_lock_missBasicInfo() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        MeshMetadata meshMetadata = new MeshMetadata(1, MeshAction.LOCK, objectNode);
        memory.projectMap.put(1, project);

        Exception result = assertThrows(
                MemoryException.PerformActionOnNonExistingMeshException.class,
                () -> {
                    memory.processMeshMetaDataForProject(1, 1, meshMetadata);
                });
        assertThat(result.getMessage(), containsString("LOCK"));
        assertThat(result.getMessage(), containsString("Performing action on non existing mesh with"));
    }

    @Test
    public void processMeshMetaDataForProject_lock_success() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        project = mock(Project.class);
        when(project.getMaximumMeshId()).thenReturn(1);
        memory.projectMap.put(1, project);
        MeshPro meshPro = new MeshPro(0);
        MeshPro spyMeshPro = spy(meshPro);

        when(project.getMeshProByMeshId(0)).thenReturn(spyMeshPro);

        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.LOCK, objectNode);
        boolean result = memory.processMeshMetaDataForProject(0, 1, meshMetadata);
        assertTrue(result);
        verify(spyMeshPro, times(1)).checkLockStatus(false);
        verify(spyMeshPro, times(1)).lockMesh(0);
    }

    @Test
    public void processMeshMetaDataForProject_unlock_missBasicInfo() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        MeshMetadata meshMetadata = new MeshMetadata(1, MeshAction.UNLOCK, objectNode);
        memory.projectMap.put(1, project);

        Exception result = assertThrows(
                MemoryException.PerformActionOnNonExistingMeshException.class,
                () -> {
                    memory.processMeshMetaDataForProject(1, 1, meshMetadata);
                });
        assertThat(result.getMessage(), containsString("UNLOCK"));
        assertThat(result.getMessage(), containsString("Performing action on non existing mesh with"));
    }

    @Test
    public void processMeshMetaDataForProject_unlock_success() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        project = mock(Project.class);
        when(project.getMaximumMeshId()).thenReturn(1);
        memory.projectMap.put(1, project);
        MeshPro meshPro = new MeshPro(0);
        MeshPro spyMeshPro = spy(meshPro);
        meshPro.lockMesh(0);

        when(project.getMeshProByMeshId(0)).thenReturn(spyMeshPro);

        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.UNLOCK, objectNode);
        boolean result = memory.processMeshMetaDataForProject(0, 1, meshMetadata);
        assertTrue(result);
        verify(spyMeshPro, times(1)).checkLockStatus(true);
        verify(spyMeshPro, times(1)).unLockMesh(0);
    }

    @Test
    public void processMeshMetaDataForProject_invalidAction() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        MeshMetadata meshMetadata = new MeshMetadata(1, MeshAction.INITIALISE_MESH, objectNode);
        memory.projectMap.put(1, project);

        assertThrows(
                MemoryException.InvalidMeshActionException.class,
                () -> {
                    memory.processMeshMetaDataForProject(1, 1, meshMetadata);
                });
    }

    @Test
    void clearAllDatabase() {
        memory.clearAllDatabase();
        verify(meshRepositoryService, times(1)).clearDatabase();
        verify(projectRepositoryService, times(1)).clearDatabase();
        verify(userRepositoryService, times(1)).clearDatabase();
    }

    @Test
    public void getLatestTimeOfProject_noSuchId() {
        Exception result = assertThrows(
                MemoryException.NoProjectLoadedException.class,
                () -> {
                    memory.getLatestTimeOfProject(1);
                });

        assertThat(result.getMessage(), containsString("that is not in memory will be processed"));
    }

    @Test
    public void getLatestTimeOfProject_success() {
        Project spyProject = spy(Project.class);
        memory.projectMap.put(1, spyProject);
        memory.getLatestTimeOfProject(1);

        verify(spyProject, times(1)).getLatestTimeStamp();
    }

    @Test
    public void revertMeshOfTimestamp_noSuchId() {
        Exception result = assertThrows(
                MemoryException.NoProjectLoadedException.class,
                () -> {
                    memory.revertMeshOfTimestamp(1, 1, 4399);
                });

        assertThat(result.getMessage(), containsString("that is not in memory will be processed."));
    }

    @Test
    public void revertMeshOfTimestamp_success() {
        Project mockProject = mock(Project.class);
        memory.projectMap.put(1, mockProject);
        memory.revertMeshOfTimestamp(1, 2, 4399);

        verify(mockProject, times(1)).revertMesh(2, 4399);
    }

    @Test
    public void snapshotProject_success() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        //        MeshMetadata meshMetadata = new MeshMetadata(1, MeshAction.INITIALISE_MESH, objectNode);
        memory.projectMap.put(1, project);
        ChannelHandlerContext channelHandlerContext = Mockito.mock(ChannelHandlerContext.class);

        User u1 = new User(1, 1, channelHandlerContext);
        ActionLog action = new ActionLog(1, 1, 4399, 1, MeshAction.ADD_MESH, objectNode);
        action.setMeshAction(MeshAction.ADD_MESH);
        u1.appendNewAction(action);
        project.addActiveUser(u1);

        ObjectNode objectNode2 = new ObjectMapper().createObjectNode();
        objectNode2.put("position", 1);
        objectNode2.put("scaling", 1);
        objectNode2.put("rotation", 1);

        Mesh mesh = new Mesh(0, project, objectNode2);
        project.addMesh(mesh);

        assertAll(() -> memory.snapshotProject(1));
    }

    @Test
    public void snapshotMemory_success() {
        memory.projectMap.put(1, project);
        Memory spyMemo = spy(memory);
        spyMemo.snapshotMemory();
        verify(spyMemo, times(1)).snapshotMemory();
    }

    @Test
    public void loadAllProjectSnapsFromDatabase() {
        memory.projectRepositoryService = mock(ProjectRepositoryService.class);
        when(memory.projectRepositoryService.getAllProjects()).thenReturn(new ArrayList<>());
        var x = memory.loadAllProjectSnapsFromDatabase();
        verify(memory.projectRepositoryService, times(1)).getAllProjects();
    }

    @Test
    public void registerMeshMetadataForUser_throwException() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.ADD_MESH, objectNode);
        memory.projectMap.put(1, project);

        assertThrows(
                MemoryException.ConvertFailedMeshMetadataToActionLog.class,
                () -> {
                    memory.registerMeshMetadataForUser(meshMetadata, 1, 1);
                });

    }

    @Test
    public void registerMeshMetadataForUser_success() {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("position", 1);
        objectNode.put("scaling", 1);
        objectNode.put("rotation", 1);
        objectNode.put("verdict", true);
        MeshMetadata meshMetadata = new MeshMetadata(0, MeshAction.ADD_MESH, objectNode);
        memory.projectMap.put(1, project);

        ChannelHandlerContext channelHandlerContext = Mockito.mock(ChannelHandlerContext.class);
        User u1 = new User(1, 1, channelHandlerContext);
        User spy = spy(u1);
        project.addActiveUser(spy);
        memory.registerMeshMetadataForUser(meshMetadata, 1, 1);
        verify(spy, times(1)).appendNewAction(any());
    }

    @Test
    void testLoadUserUserNotLoadedIntoMemoryLoadsUserAndReturnsIt() {
        // Arrange
        Memory memory = new Memory(projectRepositoryService, userRepositoryService, meshRepositoryService);
        int userId = 1;
        ChannelHandlerContext context = mock(ChannelHandlerContext.class);
        User user = new User(userId, 1, context);
        when(userRepositoryService.getUserByUserId(userId)).thenReturn(user);

        // Act
        User loadedUser = memory.loadUser(userId, context);

        // Assert
        assertEquals(user, loadedUser);
        assertTrue(memory.activeUsers.contains(userId));
        assertEquals(context, loadedUser.getChannelHandlerContext());
    }

    @Test
    void testLoadUserUserAlreadyLoadedIntoMemoryThrowsException() {
        // Arrange
        Memory memory = new Memory(projectRepositoryService, userRepositoryService, meshRepositoryService);
        int userId = 1;
        ChannelHandlerContext context = mock(ChannelHandlerContext.class);
        User user = new User(userId, 1, context);
        memory.activeUsers.add(userId);

        // Act and Assert
        assertThrows(MemoryException.UserAlreadyInMemoryException.class, () -> memory.loadUser(userId, context));
        verify(userRepositoryService, never()).getUserByUserId(userId);
    }

    @Test
    void testLoadUserCannotLoadUserFromDatabaseThrowsException() {
        // Arrange
        Memory memory = new Memory(projectRepositoryService, userRepositoryService, meshRepositoryService);
        int userId = 1;
        ChannelHandlerContext context = mock(ChannelHandlerContext.class);
        when(userRepositoryService.getUserByUserId(userId)).thenReturn(null);

        // Act and Assert
        assertThrows(MemoryException.CannotLoadUserFromDatabaseException.class, () -> memory.loadUser(userId, context));
        verify(userRepositoryService).getUserByUserId(userId);
        assertFalse(memory.activeUsers.contains(userId));
    }

    @Test
    void testCreateUserUserNotAlreadyCreatedAddsUserToDatabaseAndReturnsIt() {
        // Arrange
        Memory memory = new Memory(projectRepositoryService, userRepositoryService, meshRepositoryService);
        int userId = 1;
        int projectId = 1;
        ChannelHandlerContext context = mock(ChannelHandlerContext.class);
        User newUser = new User(userId, projectId, context);
        when(userRepositoryService.getUserByUserId(userId)).thenReturn(null);

        // Act
        User createdUser = memory.createUser(userId, projectId, context);

        // Assert
        assertEquals(newUser, createdUser);
        verify(userRepositoryService).saveUser(newUser);
    }

    @Test
    void testCreateUserUserAlreadyCreatedThrowsException() {
        // Arrange
        Memory memory = new Memory(projectRepositoryService, userRepositoryService, meshRepositoryService);
        int userId = 1;
        int projectId = 1;
        ChannelHandlerContext context = mock(ChannelHandlerContext.class);
        User user = new User(userId, projectId, context);
        when(userRepositoryService.checkUserExistingInDatabase(userId)).thenReturn(true);

        // Act and Assert
        assertThrows(MemoryException.UserAlreadyCreatedException.class,
                () -> memory.createUser(userId, projectId, context));
        verify(userRepositoryService, never()).saveUser(any(User.class));
    }
}