package com.ecadi.alphabuiltbackend.model;

import com.ecadi.alphabuiltbackend.intercommunication.MeshAction;
import com.ecadi.alphabuiltbackend.intercommunication.MeshMetadata;
import com.ecadi.alphabuiltbackend.domain.mesh.Mesh;
import com.ecadi.alphabuiltbackend.domain.mesh.MeshPro;
import com.ecadi.alphabuiltbackend.domain.mesh.MeshRepositoryService;
import com.ecadi.alphabuiltbackend.domain.mesh.MeshSnapshot;
import com.ecadi.alphabuiltbackend.domain.project.Project;
import com.ecadi.alphabuiltbackend.domain.project.ProjectRepositoryService;
import com.ecadi.alphabuiltbackend.domain.project.ProjectSnapshot;
import com.ecadi.alphabuiltbackend.domain.project.Scene;
import com.ecadi.alphabuiltbackend.domain.user.ActionLog;
import com.ecadi.alphabuiltbackend.domain.user.User;
import com.ecadi.alphabuiltbackend.domain.user.UserRepositoryService;

import com.ecadi.alphabuiltbackend.intercommunication.MeshMetadataUtil;
import io.netty.channel.ChannelHandlerContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

/**
 * Represents the Memory service, where in-memory objects are placed.
 * This includes a map of projects and a set of active users.
 * Provides methods for interacting with these objects, including
 * loading, creating, and removing projects and users, as well as
 * processing metadata for a project's mesh.
 *
 */
@Service
public class Memory {

    ConcurrentHashMap<Integer, Project> projectMap;
    ConcurrentSkipListSet<Integer> activeUsers;
    ProjectRepositoryService projectRepositoryService;
    UserRepositoryService userRepositoryService;
    MeshRepositoryService meshRepositoryService;
    Logger logger = LoggerFactory.getLogger("Memory Logger");
    ConcurrentHashMap<Integer, Stack<Scene>> versions;

    /**
     * Constructs a new Memory object with the specified services.
     *
     * @param projectRepositoryService The service for managing projects in the database.
     * @param userRepositoryService   The service for managing users in the database.
     * @param meshRepositoryService   The service for managing meshes in the database.
     */
    public Memory(
            ProjectRepositoryService projectRepositoryService,
            UserRepositoryService userRepositoryService,
            MeshRepositoryService meshRepositoryService
    ) {
        projectMap = new ConcurrentHashMap<>();
        this.projectRepositoryService = projectRepositoryService;
        this.userRepositoryService = userRepositoryService;
        this.meshRepositoryService = meshRepositoryService;
        activeUsers = new ConcurrentSkipListSet<>();
        versions = new ConcurrentHashMap<>();
    }

    /**
     * Loads a project into memory.
     *
     * @param projectId The ID of the project to load.
     * @return The loaded Project object.
     * @throws MemoryException.ProjectAlreadyInMemoryException
     *      if the project is already loaded into memory or cannot be loaded from the database.
     */
    public Project loadProject(int projectId) {
        if (projectMap.containsKey(projectId)) {
            String errMessage = String.format("Project %d has already been loaded into memory.", projectId);
            logger.error(errMessage);
            throw new MemoryException.ProjectAlreadyInMemoryException(errMessage);
        }

        Project projectLoaded = projectRepositoryService.getProjectByProjectId(projectId);

        if (projectLoaded == null) {
            logger.info("New project created");
            projectLoaded = new Project(projectId);
        }

        projectMap.put(projectId, projectLoaded);
        logger.info(String.format("New project with project id %d has been loaded into memory.", projectId));
        return projectLoaded;
    }

    /**
     * Loads all users in the database for logging purposes.
     *
     * @return A list of all users in the database.
     */
    public List<User> loadAllUsersInDatabaseForLog() {
        List<Integer> allProjectIds = projectRepositoryService.getAllProjects().stream()
                .map(ProjectSnapshot::getProjectId)
                .distinct()
                .collect(Collectors.toList());
        return allProjectIds.stream().map(this::getAllUsersInDatabaseForLogByProjectId)
                .flatMap(List::stream).collect(Collectors.toList());
    }

    /**
     * Gets all users in the database for logging purposes by project ID.
     *
     * @param projectId The ID of the project.
     * @return A list of users in the database for the specified project.
     */
    public List<User> getAllUsersInDatabaseForLogByProjectId(int projectId) {
        return userRepositoryService.findAllUsersByProjectId(projectId);
    }

    /**
     * Loads a user into memory.
     *
     * @param userId   The ID of the user to load.
     * @param context  The ChannelHandlerContext associated with the user.
     * @return The loaded User object.
     * @throws MemoryException.CannotLoadUserFromDatabaseException
     *      if the user is already loaded into memory or cannot be loaded from the database.
     */
    public User loadUser(int userId, ChannelHandlerContext context) {
        if (activeUsers.contains(userId)) {
            String errMessage = String.format("User with user id %d has already been loaded into memory.", userId);
            logger.error(errMessage);
            throw new MemoryException.UserAlreadyInMemoryException(errMessage);
        }
        User userLoaded = userRepositoryService.getUserByUserId(userId);
        if (userLoaded == null) {
            String errMessage = String.format("User with user id %d cannot be loaded into memory.", userId);
            logger.error(errMessage);
            throw new MemoryException.CannotLoadUserFromDatabaseException(errMessage);
        }
        activeUsers.add(userId);
        userLoaded.setChannelHandlerContext(context);
        logger.info(String.format("User with user id %d has already been loaded into memory.", userId));
        return userLoaded;
    }

    /**
     * Creates a new user and adds it to the database.
     *
     * @param userId     The ID of the user to create.
     * @param projectId  The ID of the project the user belongs to.
     * @param channelHandlerContext The ChannelHandlerContext associated with the user.
     * @return The created User object.
     * @throws MemoryException.UserAlreadyCreatedException
     *      if the user with the same ID already exists in the database.
     */
    public User createUser(int userId, int projectId, ChannelHandlerContext channelHandlerContext) {
        if (checkUserExist(userId)) {
            String errMessage = String.format("User with user id %d has already existed in database", userId);
            logger.error(errMessage);
            throw new MemoryException.UserAlreadyCreatedException(errMessage);
        }

        User newUser = new User(userId, projectId, channelHandlerContext);
        userRepositoryService.saveUser(newUser);
        return newUser;
    }

    /**
     * Creates a new project.
     *
     * @param projectId The ID of the project to create.
     * @return The created Project object.
     * @throws MemoryException.ProjectAlreadyInMemoryException
     *      if the project with the same ID already exists in the database.
     */
    public Project createProject(int projectId) {
        if (checkProjectExist(projectId)) {
            String errMessage = String.format("Project with project id %d has already exist in database.", projectId);
            logger.error(errMessage);
            throw new MemoryException.ProjectAlreadyCreatedException(errMessage);
        }
        //projectRepositoryService.saveProject(newProject);
        return new Project(projectId);
    }

    /**
     * Checks if a user with the specified ID exists in the database.
     *
     * @param userId The ID of the user to check.
     * @return true if the user exists, false otherwise.
     */
    public boolean checkUserExist(int userId) {
        return userRepositoryService.checkUserExistingInDatabase(userId);
    }

    /**
     * Checks if a user with the specified ID is active in memory.
     *
     * @param userId The ID of the user to check.
     * @return true if the user is active, false otherwise.
     */
    public boolean checkUserActive(int userId) {
        return activeUsers.contains(userId);
    }

    /**
     * Checks if a project with the specified ID exists in the database.
     *
     * @param projectId The ID of the project to check.
     * @return true if the project exists, false otherwise.
     */
    public boolean checkProjectExist(int projectId) {
        return projectRepositoryService.checkProjectExistByProjectId(projectId);
    }

    /**
     * Checks if a project with the specified ID is active in memory.
     *
     * @param projectId The ID of the project to check.
     * @return true if the project is active, false otherwise.
     */
    public boolean checkProjectActive(int projectId) {
        return projectMap.containsKey(projectId);
    }

    /**
     * Adds a user to a project in memory.
     *
     * @param user The User object to add.
     * @throws MemoryException.NoProjectLoadedException
     *      if the project is not loaded into memory.
     * @throws MemoryException.UserAlreadyCreatedException
     *     if the user is already created in the database.
     * @throws MemoryException.UserAlreadyExistedException
     *     if the user already exists in the project.
     */
    public void addUser(User user) {
        int projectId = user.getProjectId();
        if (!projectMap.containsKey(projectId)) {
            String errMessage = String.format("Project %d has not been loaded into memory yet.", projectId);
            logger.error(errMessage);
            throw new MemoryException.NoProjectLoadedException(errMessage);
        }
        int userId = user.getUserId();
        if (!activeUsers.contains(userId)) {
            String errMessage = String.format("User with user id %d has not been loaded into memory yet.", userId);
            logger.error(errMessage);
            throw new MemoryException.UserAlreadyCreatedException(errMessage);
        }
        Project targetProject = projectMap.get(projectId);
        if (targetProject.containActiveUser(user)) {
            String errMessage = String.format("User %d is already existed in project %d.", user.getUserId(), projectId);
            logger.error(errMessage);
            throw new MemoryException.UserAlreadyExistedException(errMessage);
        }
        targetProject.addActiveUser(user);
    }

    /**
     * Checks the number of active users in a project.
     *
     * @param projectId The ID of the project.
     * @return The number of active users in the project.
     * @throws MemoryException.DeleteProjectNotInMemoryException
     *      if the project is not loaded into memory.
     */
    public int checkActiveUsersInProject(int projectId) {
        if (!projectMap.containsKey(projectId)) {
            String errMessage = String.format("Project %d that is not in memory will be removed.", projectId);
            logger.error(errMessage);
            throw new MemoryException.DeleteProjectNotInMemoryException(errMessage);
        }
        return projectMap.get(projectId).countActiveUser();
    }

    /**
     * Removes a project from memory.
     *
     * @param projectId The ID of the project to remove.
     * @throws MemoryException.DeleteProjectNotInMemoryException
     *      if the project is not loaded into memory or has active users.
     */
    public void removeProjectFromMemory(int projectId) {
        if (!projectMap.containsKey(projectId)) {
            String errMessage = String.format("Project %d that is not in memory will be removed.", projectId);
            logger.error(errMessage);
            throw new MemoryException.DeleteProjectNotInMemoryException(errMessage);
        }
        Project projectToDelete = projectMap.get(projectId);
        if (projectToDelete.countActiveUser() > 0) {
            String errMessage = String.format("Project %d that has active users will be removed.", projectId);
            logger.error(errMessage);
            throw new MemoryException.DeleteProjectWithUsersException(errMessage);
        }
        ProjectSnapshot projectToDeleteSnapshot = projectToDelete.snapshotProject();
        projectRepositoryService.storeProjectState(projectToDeleteSnapshot);
        projectMap.remove(projectId);
    }

    /**
     * Removes a user from memory.
     *
     * @param userId The ID of the user to remove.
     * @throws MemoryException.UserDoesNotExistInMemoryException
     *      if the user is not loaded into memory.
     */
    public void removeUserFromMemory(int userId) {
        if (!activeUsers.contains(userId)) {
            String errMessage = String.format("User with user id %d that is not in memory will be removed.", userId);
            logger.error(errMessage);
            throw new MemoryException.UserDoesNotExistInMemoryException(errMessage);
        }

        List<Project> projectListContainingTargetUser = projectMap
                    .values()
                    .stream()
                    .filter(project -> project.containActiveUser(userId))
                    .collect(Collectors.toList());

        if (!projectListContainingTargetUser.isEmpty()) {
            String errMessage = String.format("Delete user with user id %d that is active in %s", userId,
                    projectListContainingTargetUser.stream().map(Project::getProjectId));
            logger.error(errMessage);
            throw new MemoryException.DeleteActiveUserException(errMessage);
        }
        activeUsers.remove(userId);
    }

    /**
     * Checks if a user belongs to a project.
     *
     * @param userId    The ID of the user.
     * @param projectId The ID of the project.
     * @return true if the user belongs to the project, false otherwise.
     * @throws MemoryException.UserDoesNotExistInMemoryException
     *      if the user is not loaded into memory.
     * @throws MemoryException.DeleteProjectNotInMemoryException
     *     if the project is not loaded into memory.
     */
    public boolean checkUserBelongToProjectById(int userId, int projectId) {
        //        if (!activeUsers.contains(userId)) {
        //            String errMessage = String.format(
        //              "User with user id %d that is not in memory will be removed.", userId
        //            );
        //            logger.error(errMessage);
        //            throw new MemoryException.UserDoesNotExistInMemoryException(errMessage);
        //        }

        if (!projectMap.containsKey(projectId)) {
            String errMessage = String.format("Project %d that is not in memory will be removed.", projectId);
            logger.error(errMessage);
            throw new MemoryException.DeleteProjectNotInMemoryException(errMessage);
        }

        return projectMap.get(projectId).containActiveUser(userId);
    }

    /**
     * Removes a user from a project in memory.
     *
     * @param userId    The ID of the user to remove.
     * @param projectId The ID of the project.
     * @throws MemoryException.UserDoesNotBelongToProjectException
     *      if the user does not belong to the project.
     */
    public void removeUserFromProject(int userId, int projectId) {
        if (!checkUserBelongToProjectById(userId, projectId)) {
            String errMessage = String
                    .format(
                            "User with user id %d does not belong to project with project id %d.",
                            userId,
                            projectId
                    );
            logger.error(errMessage);
            throw new MemoryException.UserDoesNotBelongToProjectException(errMessage);
        }
        projectMap.get(projectId).removeActiveUser(userId);
    }

    /**
     * Retrieves the Project object for the specified project ID.
     *
     * @param projectId The ID of the project.
     * @return The Project object.
     * @throws MemoryException.DeleteProjectNotInMemoryException
     *      if the project is not loaded into memory.
     */
    public Project getProjectHandler(int projectId) {
        if (!projectMap.containsKey(projectId)) {
            String errMessage = String.format("Project %d that is not in memory will be removed.", projectId);
            logger.error(errMessage);
            throw new MemoryException.DeleteProjectNotInMemoryException(errMessage);
        }
        return projectMap.get(projectId);
    }

    /**
     * Processes mesh metadata for a project.
     *
     * @param userId       The ID of the user performing the action.
     * @param projectId    The ID of the project.
     * @param meshMetadata The metadata for the mesh action.
     * @return true if the metadata is successfully processed, false otherwise.
     * @throws MemoryException.NoProjectLoadedException
     *     if the project is not loaded into memory.
     * @throws MemoryException.MeshMetadataMissingBasicPropertiesException
     *     if the mesh metadata is missing basic properties.
     * @throws MemoryException.MeshToAddAlreadyExistInDatabaseException
     *     if the mesh to add already exists in the database.
     * @throws MemoryException.MeshMetadataMissingPositionPropertyException
     *     if the mesh metadata is missing position property.
     * @throws MemoryException.PerformActionOnNonExistingMeshException
     *     if the mesh to perform action on does not exist.
     * @throws MemoryException.InvalidMeshActionException
     *     if the mesh action is invalid.
     */
    public boolean processMeshMetaDataForProject(int userId, int projectId, MeshMetadata meshMetadata) {
        if (!projectMap.containsKey(projectId)) {
            String errMessage = String.format("Project %d that is not in memory will be processed.", projectId);
            logger.error(errMessage);
            throw new MemoryException.NoProjectLoadedException(errMessage);
        }

        Project targetProject = projectMap.get(projectId);
        switch (meshMetadata.getMeshAction()) {
            case ADD_MESH:
                if (!meshMetadata.containsBasicProperties()) {
                    String errMessage = String
                            .format(
                                    "Mesh metadata adding mesh with mesh id"
                                            + " %d in project with project id %d misses basic information.",
                                    meshMetadata.getMeshId(),
                                    projectId
                            );
                    logger.error(errMessage);
                    throw new MemoryException.MeshMetadataMissingBasicPropertiesException(errMessage);
                }
                int newMeshId = meshMetadata.getMeshId();
                Mesh newMesh = new Mesh(newMeshId, targetProject);
                targetProject.addMesh(newMesh);
                if (meshRepositoryService.existMeshByMeshIdAndProjectId(newMeshId, projectId)) {
                    String errMessage = String
                            .format(
                                    "In project with project id %d,"
                                            + " there already exists mesh with mesh id %d",
                                    projectId,
                                    newMeshId
                            );
                    logger.error(errMessage);
                    throw new MemoryException.MeshToAddAlreadyExistInDatabaseException(errMessage);
                }
                targetProject.addMeshProperties(newMeshId, meshMetadata.getProperties());
                //meshRepositoryService.saveMesh(newMesh);
                break;
            case MERGE:
                if (!meshMetadata.containsMergedListProperty()) {
                    String errMessage = String
                            .format(
                                    "Mesh metadata adding mesh with mesh id"
                                            + " %d in project with project id %d misses merge list information.",
                                    meshMetadata.getMeshId(),
                                    projectId
                            );
                    logger.error(errMessage);
                    throw new MemoryException.MeshMetadataMissingBasicPropertiesException(errMessage);
                }
                if (!meshMetadata.containsBasicProperties()) {
                    String errMessage = String
                            .format(
                                    "Mesh metadata adding mesh with mesh id"
                                            + " %d in project with project id %d misses basic information.",
                                    meshMetadata.getMeshId(),
                                    projectId
                            );
                    logger.error(errMessage);
                    throw new MemoryException.MeshMetadataMissingBasicPropertiesException(errMessage);
                }
                int newMeshIdMerged = meshMetadata.getMeshId();
                Mesh newMeshMerged = new Mesh(newMeshIdMerged, targetProject);
                targetProject.addMesh(newMeshMerged);
                if (meshRepositoryService.existMeshByMeshIdAndProjectId(newMeshIdMerged, projectId)) {
                    String errMessage = String
                            .format(
                                    "In project with project id %d,"
                                            + " there already exists mesh with mesh id %d",
                                    projectId,
                                    newMeshIdMerged
                            );
                    logger.error(errMessage);
                    throw new MemoryException.MeshToAddAlreadyExistInDatabaseException(errMessage);
                }
                targetProject.addMeshProperties(newMeshIdMerged, meshMetadata.getProperties());
                break;
            case SUBTRACT:
                if (!meshMetadata.containsSubtractListProperty()) {
                    String errMessage = String
                            .format(
                                    "Mesh metadata adding mesh with mesh id"
                                            + " %d in project with project id %d misses merge list information.",
                                    meshMetadata.getMeshId(),
                                    projectId
                            );
                    logger.error(errMessage);
                    throw new MemoryException.MeshMetadataMissingBasicPropertiesException(errMessage);
                }
                if (!meshMetadata.containsBasicProperties()) {
                    String errMessage = String
                            .format(
                                    "Mesh metadata adding mesh with mesh id"
                                            + " %d in project with project id %d misses basic information.",
                                    meshMetadata.getMeshId(),
                                    projectId
                            );
                    logger.error(errMessage);
                    throw new MemoryException.MeshMetadataMissingBasicPropertiesException(errMessage);
                }
                int newMeshIdSubtract = meshMetadata.getMeshId();
                Mesh newMeshSubtract = new Mesh(newMeshIdSubtract, targetProject);
                targetProject.addMesh(newMeshSubtract);
                if (meshRepositoryService.existMeshByMeshIdAndProjectId(newMeshIdSubtract, projectId)) {
                    String errMessage = String
                            .format(
                                    "In project with project id %d,"
                                            + " there already exists mesh with mesh id %d",
                                    projectId,
                                    newMeshIdSubtract
                            );
                    logger.error(errMessage);
                    throw new MemoryException.MeshToAddAlreadyExistInDatabaseException(errMessage);
                }
                targetProject.addMeshProperties(newMeshIdSubtract, meshMetadata.getProperties());
                break;
            case INTERSECT:
                if (!meshMetadata.containsIntersectListProperty()) {
                    String errMessage = String
                            .format(
                                    "Mesh metadata adding mesh with mesh id"
                                            + " %d in project with project id %d misses merge list information.",
                                    meshMetadata.getMeshId(),
                                    projectId
                            );
                    logger.error(errMessage);
                    throw new MemoryException.MeshMetadataMissingBasicPropertiesException(errMessage);
                }
                if (!meshMetadata.containsBasicProperties()) {
                    String errMessage = String
                            .format(
                                    "Mesh metadata adding mesh with mesh id"
                                            + " %d in project with project id %d misses basic information.",
                                    meshMetadata.getMeshId(),
                                    projectId
                            );
                    logger.error(errMessage);
                    throw new MemoryException.MeshMetadataMissingBasicPropertiesException(errMessage);
                }
                int newMeshIdIntersect = meshMetadata.getMeshId();
                Mesh newMeshIntersect = new Mesh(newMeshIdIntersect, targetProject);
                targetProject.addMesh(newMeshIntersect);
                if (meshRepositoryService.existMeshByMeshIdAndProjectId(newMeshIdIntersect, projectId)) {
                    String errMessage = String
                            .format(
                                    "In project with project id %d,"
                                            + " there already exists mesh with mesh id %d",
                                    projectId,
                                    newMeshIdIntersect
                            );
                    logger.error(errMessage);
                    throw new MemoryException.MeshToAddAlreadyExistInDatabaseException(errMessage);
                }
                targetProject.addMeshProperties(newMeshIdIntersect, meshMetadata.getProperties());
                break;
            case UNION:
                if (!meshMetadata.containsUnionListProperty()) {
                    String errMessage = String
                            .format(
                                    "Mesh metadata adding mesh with mesh id"
                                            + " %d in project with project id %d misses merge list information.",
                                    meshMetadata.getMeshId(),
                                    projectId
                            );
                    logger.error(errMessage);
                    throw new MemoryException.MeshMetadataMissingBasicPropertiesException(errMessage);
                }
                if (!meshMetadata.containsBasicProperties()) {
                    String errMessage = String
                            .format(
                                    "Mesh metadata adding mesh with mesh id"
                                            + " %d in project with project id %d misses basic information.",
                                    meshMetadata.getMeshId(),
                                    projectId
                            );
                    logger.error(errMessage);
                    throw new MemoryException.MeshMetadataMissingBasicPropertiesException(errMessage);
                }
                int newMeshIdUnion = meshMetadata.getMeshId();
                Mesh newMeshUnion = new Mesh(newMeshIdUnion, targetProject);
                targetProject.addMesh(newMeshUnion);
                if (meshRepositoryService.existMeshByMeshIdAndProjectId(newMeshIdUnion, projectId)) {
                    String errMessage = String
                            .format(
                                    "In project with project id %d,"
                                            + " there already exists mesh with mesh id %d",
                                    projectId,
                                    newMeshIdUnion
                            );
                    logger.error(errMessage);
                    throw new MemoryException.MeshToAddAlreadyExistInDatabaseException(errMessage);
                }
                targetProject.addMeshProperties(newMeshIdUnion, meshMetadata.getProperties());
                break;
            case TRANSLATE:
                if (!meshMetadata.containsPropertyPosition()) {
                    String errMessage = String
                            .format(
                                    "Mesh metadata translating mesh with mesh id %d "
                                            + "in project with project id %d misses basic information.",
                                    meshMetadata.getMeshId(),
                                    projectId
                            );
                    logger.error(errMessage);
                    throw new MemoryException.MeshMetadataMissingPositionPropertyException(errMessage);
                }
                int translateMeshId = meshMetadata.getMeshId();
                int maxValidMeshId = targetProject.getMaximumMeshId();
                if (translateMeshId >= maxValidMeshId) {
                    String errMessage = String
                            .format(
                                    "Performing action on non existing mesh with "
                                            + "mesh id %d in project with project id %d",
                                    translateMeshId,
                                    projectId
                            );
                    logger.error("Translation Error: " + errMessage);
                    throw new MemoryException.PerformActionOnNonExistingMeshException(MeshAction.TRANSLATE, errMessage);
                }
                MeshPro meshProToTranslate = targetProject.getMeshProByMeshId(translateMeshId);
                //if (meshProToTranslate.getLockStatus()) {
                //    return false;
                //}
                //Mesh meshToTranslate = targetProject.getMeshInMemoryByMeshId(translateMeshId);

                //used for solving translation reduction
                //Mesh meshReadyChange = meshProToTranslate.getLatestMesh();
                //MeshMetadata metadata1 = MeshMetadataUtil.createInitialMeshMetadata(meshReadyChange);
                //meshMetadata.setProperties(meshMetadata.getProperties());

                targetProject.updateMeshProperties(translateMeshId, meshMetadata.getProperties());
                //meshRepositoryService.updateMesh(meshToTranslate);
                break;
            case TRANSLATE_END:
                if (!meshMetadata.containsPropertyPosition()) {
                    String errMessage = String
                            .format(
                                    "Mesh metadata translating mesh with mesh id %d "
                                            + "in project with project id %d misses basic information.",
                                    meshMetadata.getMeshId(),
                                    projectId
                            );
                    logger.error(errMessage);
                    throw new MemoryException.MeshMetadataMissingPositionPropertyException(errMessage);
                }
                int translateMeshIdEnd = meshMetadata.getMeshId();
                int maxValidMeshIdEnd = targetProject.getMaximumMeshId();
                if (translateMeshIdEnd >= maxValidMeshIdEnd) {
                    String errMessage = String
                            .format(
                                    "Performing action on non existing mesh with "
                                            + "mesh id %d in project with project id %d",
                                    translateMeshIdEnd,
                                    projectId
                            );
                    logger.error("Translation Error: " + errMessage);
                    throw new MemoryException.PerformActionOnNonExistingMeshException(MeshAction.TRANSLATE, errMessage);
                }
                //MeshPro meshProToTranslateEnd = targetProject.getMeshProByMeshId(translateMeshIdEnd);
                //if (meshProToTranslate.getLockStatus()) {
                //    return false;
                //}
                //Mesh meshToTranslate = targetProject.getMeshInMemoryByMeshId(translateMeshId);

                targetProject.updateMeshProperties(translateMeshIdEnd, meshMetadata.getProperties());
                //meshRepositoryService.updateMesh(meshToTranslate);
                break;
            case ROTATE:
                if (!meshMetadata.containsPropertyRotation()) {
                    String errMessage = String
                            .format(
                                    "Mesh metadata rotating mesh with mesh id %d"
                                            + " in project with project id %d misses basic information.",
                                    meshMetadata.getMeshId(),
                                    projectId
                            );
                    logger.error(errMessage);
                    throw new MemoryException.MeshMetadataMissingPositionPropertyException(errMessage);
                }
                int rotateMeshId = meshMetadata.getMeshId();
                int maxValidMeshId1 = targetProject.getMaximumMeshId();
                if (rotateMeshId >= maxValidMeshId1) {
                    String errMessage = String
                            .format("Performing action on non existing mesh "
                                    + "with mesh id %d in project with project id %d",
                                    rotateMeshId,
                                    projectId
                            );
                    logger.error("Rotation Error: " + errMessage);
                    throw new MemoryException.PerformActionOnNonExistingMeshException(MeshAction.ROTATE, errMessage);
                }
                MeshPro meshProToRotate = targetProject.getMeshProByMeshId(rotateMeshId);
                Mesh meshToRotate = targetProject.getMeshInMemoryByMeshId(rotateMeshId);
                targetProject.updateMeshProperties(rotateMeshId, meshMetadata.getProperties());
                //meshRepositoryService.updateMesh(meshToRotate);
                break;
            case SCALE:
                if (!meshMetadata.containsPropertyScaling()) {
                    String errMessage = String
                            .format(
                                    "Mesh metadata scaling mesh with mesh id %d"
                                            + " in project with project id %d misses basic information.",
                                    meshMetadata.getMeshId(),
                                    projectId
                            );
                    logger.error(errMessage);
                    throw new MemoryException.MeshMetadataMissingPositionPropertyException(errMessage);
                }
                int scaleMeshId = meshMetadata.getMeshId();
                int maxValidMeshId2 = targetProject.getMaximumMeshId();
                if (scaleMeshId >= maxValidMeshId2) {
                    String errMessage = String
                            .format(
                                    "Performing action on non existing mesh with mesh id %d "
                                            + "in project with project id %d",
                                    scaleMeshId,
                                    projectId
                            );
                    logger.error("Scale Error: " + errMessage);
                    throw new MemoryException.PerformActionOnNonExistingMeshException(MeshAction.SCALE, errMessage);
                }
                MeshPro meshProToScale = targetProject.getMeshProByMeshId(scaleMeshId);
                if (meshProToScale.getLockStatus()) {
                    return false;
                }
                Mesh meshToScale = targetProject.getMeshInMemoryByMeshId(scaleMeshId);
                targetProject.updateMeshProperties(scaleMeshId, meshMetadata.getProperties());
                //meshRepositoryService.updateMesh(meshToScale);
                break;
            case REVERT:
                int revertMeshId = meshMetadata.getMeshId();
                int maxValidMeshId3 = targetProject.getMaximumMeshId();
                if (revertMeshId >= maxValidMeshId3) {
                    String errMessage = String
                            .format(
                                    "Performing action on non existing mesh with mesh id %d"
                                            + " in project with project id %d",
                                    revertMeshId,
                                    projectId
                            );
                    logger.error("Revert Error: " + errMessage);
                    throw new MemoryException.PerformActionOnNonExistingMeshException(MeshAction.REVERT, errMessage);
                }
                //int timeStampToRevert = meshMetadata.getProperties().get("timestamp").asInt();
                //targetProject.revertMesh(revertMeshId, timeStampToRevert);
                Mesh revertedMesh = targetProject.revertMesh(revertMeshId);
                MeshMetadata metadata = MeshMetadataUtil.createInitialMeshMetadata(revertedMesh);
                meshMetadata.setProperties(metadata.getProperties());
                break;
            case REDO:
                int redoMeshId = meshMetadata.getMeshId();
                int maxValidMeshId6 = targetProject.getMaximumMeshId();
                if (redoMeshId >= maxValidMeshId6) {
                    String errMessage = String
                            .format(
                                    "Performing action on non existing mesh with mesh id %d"
                                            + " in project with project id %d",
                                    redoMeshId,
                                    projectId
                            );
                    logger.error("Redo Error: " + errMessage);
                    throw new MemoryException.PerformActionOnNonExistingMeshException(MeshAction.REDO, errMessage);
                }
                //int timeStampToRevert = meshMetadata.getProperties().get("timestamp").asInt();
                //targetProject.revertMesh(revertMeshId, timeStampToRevert);
                Mesh redoedMesh = targetProject.redoMesh(redoMeshId);
                MeshMetadata redoMetadata = MeshMetadataUtil.createInitialMeshMetadata(redoedMesh);
                meshMetadata.setProperties(redoMetadata.getProperties());
                break;
            case LOCK:
                int lockMeshId = meshMetadata.getMeshId();
                int maxValidMeshId4 = targetProject.getMaximumMeshId();
                if (lockMeshId >= maxValidMeshId4) {
                    String errMessage = String
                            .format(
                                    "Performing action on non existing mesh with mesh id %d "
                                            + "in project with project id %d",
                                    lockMeshId,
                                    projectId
                            );
                    logger.error("Lock Error: " + errMessage);
                    throw new MemoryException.PerformActionOnNonExistingMeshException(MeshAction.LOCK, errMessage);
                }
                MeshPro meshProToLock = targetProject.getMeshProByMeshId(lockMeshId);
                //meshProToLock.assertLockStatus(false);
                meshProToLock.checkLockStatus(false);
                meshProToLock.lockMesh(userId);
                break;
            case UNLOCK:
                int unlockMeshId = meshMetadata.getMeshId();
                int maxValidMeshId5 = targetProject.getMaximumMeshId();
                if (unlockMeshId >= maxValidMeshId5) {
                    String errMessage = String.format("Performing action on non existing mesh with mesh id %d "
                            + "in project with project id %d",
                            unlockMeshId,
                            projectId
                    );
                    logger.error("UnLock Error: " + errMessage);
                    throw new MemoryException.PerformActionOnNonExistingMeshException(MeshAction.UNLOCK, errMessage);
                }
                MeshPro meshProToUnlock = targetProject.getMeshProByMeshId(unlockMeshId);
                meshProToUnlock.checkLockStatus(true);
                meshProToUnlock.unLockMesh(userId);
                //targetProject.updateMeshProperties(unlockMeshId, meshMetadata.getProperties());
                //Mesh unlockMesh = targetProject.getMeshInMemoryByMeshId(meshMetadata.getMeshId());
                //meshRepositoryService.updateMesh(unlockMesh);
                break;
            default:
                throw new MemoryException.InvalidMeshActionException("Invalid mesh action.");
        }
        return true;
    }

    /**
     * Clears all data in the databases.
     * Used in the initial development procedure.
     */
    public void clearAllDatabase() {
        meshRepositoryService.clearDatabase();
        projectRepositoryService.clearDatabase();
        userRepositoryService.clearDatabase();
    }

    /**
     * Retrieves the latest timestamp of a project.
     *
     * @param projectId The ID of the project.
     * @return The latest timestamp of the project.
     * @throws MemoryException.NoProjectLoadedException
     *      if the project is not loaded into memory.
     */
    public int getLatestTimeOfProject(int projectId) {
        if (!projectMap.containsKey(projectId)) {
            String errMessage = String.format("Project %d that is not in memory will be processed.", projectId);
            logger.error(errMessage);
            throw new MemoryException.NoProjectLoadedException(errMessage);
        }
        Project targetProject = projectMap.get(projectId);
        return targetProject.getLatestTimeStamp();
    }

    /**
     * Reverts a mesh to a specific timestamp in a project.
     *
     * @param projectId The ID of the project.
     * @param meshId    The ID of the mesh.
     * @param timeStamp The timestamp to revert to.
     * @return The reverted Mesh object.
     * @throws MemoryException.NoProjectLoadedException
     *      if the project is not loaded into memory or the mesh does not exist.
     */
    public Mesh revertMeshOfTimestamp(int projectId, int meshId, int timeStamp) {
        if (!projectMap.containsKey(projectId)) {
            String errMessage = String.format("Project %d that is not in memory will be processed.", projectId);
            logger.error(errMessage);
            throw new MemoryException.NoProjectLoadedException(errMessage);
        }
        Project targetProject = projectMap.get(projectId);
        return targetProject.revertMesh(meshId, timeStamp);
    }

    /**
     * Checks if a project with the specified ID is loaded into memory.
     *
     * @param projectId The ID of the project to check.
     * @throws MemoryException.NoProjectLoadedException
     *      if the project is not loaded into memory.
     */
    private void checkProjectIdInMemory(int projectId) {
        if (!projectMap.containsKey(projectId)) {
            String errMessage = String.format("Project %d that is not in memory will be processed.", projectId);
            logger.error(errMessage);
            throw new MemoryException.NoProjectLoadedException(errMessage);
        }
    }

    /**
     * Snapshots a project by storing its current state in the database.
     *
     * @param projectId The ID of the project to snapshot.
     * @throws RuntimeException if saving is not successful.
     */
    public void snapshotProject(int projectId) {
        checkProjectIdInMemory(projectId);
        Project project = projectMap.get(projectId);
        ProjectSnapshot projectSnapshot = project.snapshotProject();
        List<MeshSnapshot> meshSnapshots = projectSnapshot.getMeshList();
        // Wait for all futures to be fulfilled
        CompletableFuture.allOf(meshSnapshots.stream()
                .map(
                        meshSnapshot ->
                                CompletableFuture.runAsync(() -> meshRepositoryService.storeMeshState(meshSnapshot))
                )
                .toArray(CompletableFuture[]::new)).join();
        projectRepositoryService.storeProjectState(projectSnapshot);

        userRepositoryService.deleteActionsInProject(projectId); // No longer necessary.
        List<User> activeUsers = project.getActiveUserList();
        int targetTime = project.getPrevStoreTimestamp();
        List<CompletableFuture<Void>> futureMains = new ArrayList<>();
        activeUsers.forEach(user -> {
            user.clearStaleActionLogs(targetTime);
            CompletableFuture<Void> futureMain = CompletableFuture.runAsync(() -> {
                List<ActionLog> actionLogs = user.getActionLogList();
                List<CompletableFuture<Void>> futures = new ArrayList<>();
                actionLogs.forEach(actionLog -> {
                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        userRepositoryService.saveActionLog(actionLog);
                    });
                    futures.add(future);
                });
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
                userRepositoryService.saveUser(user);
            });
            futureMains.add(futureMain);
        });
        CompletableFuture.allOf(futureMains.toArray(new CompletableFuture[0])).join();
        project.setPrevStoreTimestamp(projectSnapshot.getTimeStamp());
    }

    /**
     * Snapshots the entire memory by storing the current state of all projects in the database.
     */
    public void snapshotMemory() {
        projectMap.keySet().forEach(this::snapshotProject);
    }

    /**
     * Loads all project snapshots from the database.
     *
     * @return A list of all project snapshots.
     */
    public List<ProjectSnapshot> loadAllProjectSnapsFromDatabase() {
        return projectRepositoryService.getAllProjects();
    }

    /**
     * Checks if the mesh metadata passes the verdict, indicating a successful conversion.
     *
     * @param meshMetadata The mesh metadata to check.
     * @throws MemoryException.ConvertFailedMeshMetadataToActionLog
     *      if the mesh metadata does not pass the verdict.
     */
    private void checkMeshMetadataPassVerdict(MeshMetadata meshMetadata) {
        var result = meshMetadata.getProperties().get("verdict");
        if (result == null || !result.isBoolean() || !result.asBoolean()) {
            throw new MemoryException.ConvertFailedMeshMetadataToActionLog();
        }
    }

    /**
     * Registers a mesh metadata verdict for a user and updates the user's action log.
     *
     * @param verdictMeshMetadata The metadata for the mesh action verdict.
     * @param projectId           The ID of the project.
     * @param userId              The ID of the user.
     * @throws MemoryException if the mesh metadata does not pass the verdict or the project is not loaded into memory.
     */
    public void registerMeshMetadataForUser(MeshMetadata verdictMeshMetadata, int projectId, int userId) {
        checkMeshMetadataPassVerdict(verdictMeshMetadata);
        int currTimestamp = getLatestTimeOfProject(projectId);
        ActionLog actionLog = new ActionLog(projectId, userId, currTimestamp, verdictMeshMetadata.getMeshId(),
                verdictMeshMetadata.getMeshAction(), verdictMeshMetadata.getProperties());
        Project currProject = getProjectHandler(projectId);
        User currUser = currProject.getActiveUser(userId);
        currUser.appendNewAction(actionLog);
    }

    /**
     * Get all projects from the database.
     *
     * @return A list of all project IDs.
     */
    public List<Integer> getAllProjects() {
        return projectRepositoryService.getAllProjects().stream().map(ProjectSnapshot::getProjectId)
                .collect(Collectors.toList());
    }

    /**
     * Gets the login user.
     *
     * @param userId The id of the user.
     * @return The login user.
     */
    public User getSpecifyUser(int userId) {
        return userRepositoryService.getUserByUserId(userId);
    }

    public Integer getNextProjectId() {
        return projectMap.size() + 1;
    }
}
