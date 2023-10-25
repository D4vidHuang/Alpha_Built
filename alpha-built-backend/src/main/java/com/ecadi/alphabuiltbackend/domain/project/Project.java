package com.ecadi.alphabuiltbackend.domain.project;

import com.ecadi.alphabuiltbackend.domain.mesh.Mesh;
import com.ecadi.alphabuiltbackend.domain.mesh.MeshPro;
import com.ecadi.alphabuiltbackend.domain.mesh.MeshSnapshot;
import com.ecadi.alphabuiltbackend.domain.user.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.Objects;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Represents a Project entity.
 */
//@Entity
@Component
public class Project {

    /**
     * Represents a timestamp-meshID pair within a project.
     * This is a nested static class within the Project class.
     */
    static class ProjectPair {

        private final int timeStamp;
        private final int meshId;

        /**
         * Constructor for creating a new ProjectPair.
         *
         * @param timeStamp The timestamp of the pair.
         * @param meshId The ID of the mesh for the pair.
         */
        public ProjectPair(int timeStamp, int meshId) {
            this.timeStamp = timeStamp;
            this.meshId = meshId;
        }
    }

    /**
     * Logger for the Project class.
     */
    static Logger logger = LoggerFactory.getLogger("Project Logger");

    // Class attributes are not typically documented in the Javadoc manner
    private int projectId;
    private int prevStoreTimestamp;
    private List<MeshPro> meshProList;
    private List<User> activeUserList;
    private ConcurrentLinkedDeque<ProjectPair> projectPairStack;
    private AtomicInteger timeStamp = new AtomicInteger(0);
    private Set<Integer> activeUserIdSet;

    /**
     * Initializes collections within the project including the activeUserList,
     * meshProList, projectPairStack, and activeUserIdSet.
     */
    private void initialiseCollection() {
        activeUserList = Collections.synchronizedList(new ArrayList<>(512));
        meshProList = Collections.synchronizedList(new ArrayList<>(512));
        projectPairStack = new ConcurrentLinkedDeque<>();
        activeUserIdSet = ConcurrentHashMap.newKeySet();
    }


    /**
     * Default constructor. Initializes activeUserList, meshList, activeUserIdSet.
     */
    public Project() {
        this.projectId = 0;
        this.prevStoreTimestamp = 0;
        this.timeStamp = new AtomicInteger(0);
        initialiseCollection();
    }


    /**
     * Constructor. Initializes activeUserList, meshList, activeUserIdSet and sets the projectId.
     *
     * @param projectId Project ID.
     */
    public Project(int projectId) {
        this.projectId = projectId;
        this.prevStoreTimestamp = 0;
        this.timeStamp = new AtomicInteger(0);
        initialiseCollection();
    }

    /**
     * Constructor. Initializes the project with a given ID, a timestamp and a persisted mesh list.
     * The mesh list is converted to a MeshPro list.
     *
     * @param projectId The ID of the project.
     * @param timeStamp The initial timestamp of the project.
     * @param persistedMeshList The list of meshes that need to be persisted.
     */
    public Project(int projectId, int timeStamp, List<Mesh> persistedMeshList) {
        this.projectId = projectId;
        this.timeStamp = new AtomicInteger(timeStamp);
        this.prevStoreTimestamp = timeStamp;
        initialiseCollection();
        this.meshProList = persistedMeshList.stream()
                .map(mesh -> new MeshPro(mesh, timeStamp)).collect(Collectors.toList());
    }

    /**
     * Adds a new user to the active users of the current project and to the activeUserIdSet.
     *
     * @param user The user to be added.
     */
    public void addActiveUser(User user) {
        activeUserList.add(user);
        activeUserIdSet.add(user.getUserId());
    }

    /**
     * Removes the user with the given ID from the active users list.
     *
     * @param userId The ID of the user to remove.
     * @throws ProjectDatabaseException.ProjectContainsMultipleUsersWithSameUserIdException
     *      If more than one user with the same ID is found.
     */
    public void removeActiveUser(int userId) {
        List<User> users = activeUserList
                .stream()
                .filter(user1 -> user1.getUserId() == userId)
                .collect(Collectors.toList());
        if (users.size() > 1) {
            String errMessage = String
                    .format(
                            "Project with project id %d contains multiple users with user id %d.",
                            projectId,
                            userId
                    );
            throw new ProjectDatabaseException.ProjectContainsMultipleUsersWithSameUserIdException(errMessage);
        }
        User userToRemove = users.get(0);
        activeUserList.remove(userToRemove);
        activeUserIdSet.remove(userId);
    }

    /**
     * Checks if a user is active in the project.
     *
     * @param user The user to check.
     * @return true if the user is active, false otherwise.
     */
    public boolean containActiveUser(User user) {
        assert (user.getProjectId() == projectId);
        return activeUserIdSet.contains(user.getUserId());
    }

    /**
     * Checks if a user with the given ID is active in the project.
     *
     * @param userId The ID of the user to check.
     * @return true if the user is active, false otherwise.
     */
    public boolean containActiveUser(int userId) {
        return activeUserIdSet.contains(userId);
    }

    /**
     * Returns the list of active users in the project.
     *
     * @return List of active users.
     */
    public List<User> getActiveUserList() {
        return activeUserList;
    }

    /**
     * Checks if the project has any active users.
     *
     * @return true if there are active users, false otherwise.
     */
    public boolean hasActiveUser() {
        return !activeUserList.isEmpty();
    }

    /**
     * Returns the number of active users in the project.
     *
     * @return The number of active users.
     */
    public int countActiveUser() {
        return activeUserList.size();
    }

    /**
     * Returns the project ID.
     *
     * @return The project ID.
     */
    public int getProjectId() {
        return projectId;
    }

    /**
     * Returns the set of active user IDs.
     *
     * @return The set of active user IDs.
     */
    public Set<Integer> getActiveUserIdSet() {
        return activeUserIdSet;
    }

    /**
     * Determines if this project is equal to another object. A project is equal to another
     * object if the object is a Project and they have the same projectId.
     *
     * @param o The object to compare this project against.
     * @return true if the given object represents a Project equivalent to this project, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Project project = (Project) o;
        return projectId == project.projectId;
    }

    /**
     * Generates a hash code for this project. The hash code is based on the projectId of this project.
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(projectId);
    }

    /**
     * Adds a new mesh to the project.
     *
     * @param mesh The mesh to add.
     * @throws ProjectMeshException.MeshIndexErrorException
     *      If the mesh ID does not match the current size of the mesh list.
     */
    public void addMesh(Mesh mesh) {
        if (mesh.getMeshId() != meshProList.size()) {
            String errMessage = String
                    .format(
                            "Inserting mesh with mesh id %d into project "
                                    + "with project id %d which current has %d meshes",
                            mesh.getMeshId(),
                            projectId,
                            meshProList.size()
                    );
            logger.error(errMessage);
            throw new ProjectMeshException.MeshIndexErrorException(errMessage);
        }
        timeStamp.incrementAndGet();
        MeshPro newMeshPro = new MeshPro(meshProList.size());
        newMeshPro.addMeshAndTimeStamp(mesh, timeStamp.get());
        projectPairStack.push(new ProjectPair(timeStamp.get(), mesh.getMeshId()));
        meshProList.add(newMeshPro);
    }

    /**
     * Adds a MeshPro object to the project's list of MeshPro objects.
     *
     * @param meshPro The MeshPro object to add.
     */
    void addMeshPro(MeshPro meshPro) {
        meshProList.add(meshPro);
    }

    // Think
    /**
     * Updates the properties of the mesh with the given ID.
     *
     * @param meshId The ID of the mesh to update.
     * @param properties The properties to update.
     * @throws ProjectMeshException.MeshIndexErrorException
     *      If the mesh ID is greater than or equal to the current size of the mesh list.
     * @throws ProjectMeshException.MeshDoesNotContainInPropertyException
     *      If the property key does not exist in the current properties.
     */
    public void updateMeshProperties(int meshId, ObjectNode properties) {
        if (meshId >= meshProList.size()) {
            String errMessage = String
                    .format(
                            "Updating mesh with mesh id %d into project with "
                                    + "project id %d which currently has %d meshes",
                            meshId,
                            projectId,
                            meshProList.size()
                    );
            logger.error(errMessage);
            throw new ProjectMeshException.MeshIndexErrorException(errMessage);
        }
        timeStamp.incrementAndGet();
        MeshPro meshPro = meshProList.get(meshId);
        Mesh currMesh = meshProList.get(meshId).getLatestMesh();
        Mesh newMesh = currMesh.deepCopy();
        ObjectNode currProperties = newMesh.getProperties();
        properties.fields().forEachRemaining(pair -> {
            String key = pair.getKey();
            JsonNode value = pair.getValue();
            if (currProperties.get(key) == null) {
                String errMessage = String
                        .format(
                                "Update mesh with mesh id %d of project with"
                                        + " project id %d that does not contain property key %s",
                                meshId,
                                projectId,
                                key
                        );
                logger.error(errMessage);
                throw new ProjectMeshException.MeshDoesNotContainInPropertyException(errMessage);
            }
            currProperties.set(key, value);
        });
        meshPro.addMeshAndTimeStamp(newMesh, timeStamp.get());
        projectPairStack.push(new ProjectPair(timeStamp.get(), newMesh.getMeshId()));
    }

    /**
     * Reverts the state of a MeshPro object with the given ID to a state before or equal to the specified timestamp.
     * This method removes all states of the MeshPro object that have a timestamp greater than the specified timestamp.
     *
     * @param meshProId The ID of the MeshPro object to revert.
     * @param timestamp The timestamp to revert to.
     * @return The Mesh object that corresponds to the state of the MeshPro object at the given timestamp.
     * @throws AssertionError if the MeshPro object is empty after reverting.
     */
    public Mesh revertMesh(int meshProId, int timestamp) {
        MeshPro meshPro = meshProList.get(meshProId);
        while (meshPro.getLatestTimeStamp() > timestamp) {
            meshPro.popMeshProPair();
        }
        assert !meshPro.isEmpty();
        MeshPro.MeshProPair meshProPair = meshPro.getMeshProPair();
        return meshProPair.getMesh();
    }

    /**
     * Reverts the state of a MeshPro object with the given ID to a state before or equal to the specified timestamp.
     * This revert doesn't specify the specific timestamp state only one step is reverted.
     *
     * @param meshProId The ID of the MeshPro object to revert.
     * @return The Mesh object that corresponds to the state of the MeshPro object at the given timestamp.
     * @throws AssertionError if the MeshPro object is empty after reverting.
     */
    public Mesh revertMesh(int meshProId) {
        MeshPro meshPro = meshProList.get(meshProId);
        assert !meshPro.isEmpty();
        MeshPro.MeshProPair meshProPair = meshPro.revertMesh();
        return meshProPair.getMesh();
    }

    /**
     * Redo the state of a MeshPro object with the given ID to a state that reverted by the current timestamp.
     * This redo doesn't specify the specific timestamp state only one step after reverted
     *
     * @param meshProId The ID of MeshPro object to redo.
     * @return The Mesh object that corresponds to the state of the MeshPro object at the given timestamp.
     */
    public Mesh redoMesh(int meshProId) {
        MeshPro meshPro = meshProList.get(meshProId);
        assert !meshPro.isEmpty();
        MeshPro.MeshProPair meshProPair = meshPro.redoMesh();
        return meshProPair.getMesh();
    }


    /**
     * Adds new properties to the mesh with the given ID.
     *
     * @param meshId The ID of the mesh to add properties to.
     * @param properties The properties to add.
     * @throws ProjectMeshException.MeshIndexErrorException
     *      If the mesh ID is greater than or equal to the current size of the mesh list.
     * @throws ProjectMeshException.MeshAlreadyContainInPropertyException
     *      If the property key already exists in the current properties.
     */
    public void addMeshProperties(int meshId, ObjectNode properties) {
        if (meshId >= meshProList.size()) {
            String errMessage = String
                    .format(
                            "Adding mesh with mesh id %d into project with "
                                    + "project id %d which currently has %d meshes",
                            meshId,
                            projectId,
                            meshProList.size()
                    );
            logger.error(errMessage);
            throw new ProjectMeshException.MeshIndexErrorException(errMessage);
        }
        timeStamp.incrementAndGet();
        MeshPro meshPro = meshProList.get(meshId);
        Mesh currMesh = meshProList.get(meshId).getLatestMesh();
        Mesh newMesh = currMesh.deepCopy();
        ObjectNode currProperties = newMesh.getProperties();
        properties.fields().forEachRemaining(pair -> {
            String key = pair.getKey();
            JsonNode value = pair.getValue();
            if (currProperties.get(key) != null) {
                String errMessage = String
                        .format(
                                "Update mesh with mesh id %d of project with "
                                        + "project id %d that already contain property key %s",
                                meshId,
                                projectId,
                                key
                        );
                logger.error(errMessage);
                throw new ProjectMeshException.MeshAlreadyContainInPropertyException(errMessage);
            }
            currProperties.put(key, value);
        });
        meshPro.addMeshAndTimeStamp(newMesh, timeStamp.get());
        projectPairStack.push(new ProjectPair(timeStamp.get(), newMesh.getMeshId()));
    }

    /**
     * Returns the maximum mesh ID in the project.
     *
     * @return The maximum mesh ID.
     */
    public int getMaximumMeshId() {
        return meshProList.size();
    }

    /**
     * Retrieves the mesh with the specified mesh ID from memory.
     *
     * @param meshId The ID of the mesh to retrieve.
     * @return The mesh with the specified ID.
     */
    public Mesh getMeshInMemoryByMeshId(int meshId) {
        return meshProList.get(meshId).getLatestMesh();
    }

    /**
     * Returns the list of meshes in the project.
     *
     * @return The list of meshes.
     */
    public List<Mesh> getMeshList() {
        return meshProList.stream().map(MeshPro::getLatestMesh).collect(Collectors.toList());
    }

    /**
     * Returns the latest timestamp of the project.
     *
     * @return The latest timestamp.
     */
    public int getLatestTimeStamp() {
        return timeStamp.get();
    }

    /**
     * Returns a MeshPro object corresponding to the given mesh ID.
     *
     * @param meshId The ID of the mesh to retrieve.
     * @return The MeshPro object corresponding to the given ID.
     */
    public MeshPro getMeshProByMeshId(int meshId) {
        return meshProList.get(meshId);
    }

    /**
     * Creates a snapshot of the current project. The snapshot includes project ID,
     * timestamp and a list of non-empty mesh snapshots.
     *
     * @return A snapshot of the current project.
     */
    public ProjectSnapshot snapshotProject() {
        List<MeshSnapshot> meshList = meshProList.stream().filter(meshPro -> !meshPro.isEmpty())
                .map(MeshPro::copyLatestMesh)
                .map(mesh -> mesh.snapshotMesh(meshProList.get(mesh.getMeshId()).getLatestTimeStamp(), projectId))
                .collect(Collectors.toList());
        return new ProjectSnapshot(projectId, timeStamp.get(), meshList);
    }

    /**
     * Returns a string representation of the Project object.
     *
     * @return A string representing the Project object.
     */
    @Override
    public String toString() {
        return "Project{"
                + "projectId=" + projectId
                + ", meshProList=" + meshProList
                + ", activeUserList=" + activeUserList
                + ", projectPairStack=" + projectPairStack
                + ", timeStamp=" + timeStamp
                + ", activeUserIdSet=" + activeUserIdSet
                + '}';
    }

    /**
     * Retrieves an active user from the project given a user ID. This method needs optimization.
     * Current implementation filters the list of active users to match the given ID and expects a single match.
     *
     * @param userId The ID of the user to retrieve.
     * @return The User object corresponding to the given ID.
     * @throws AssertionError if more than one user with the given ID exists in the activeUserList.
     */
    //TODO: Should be optimised
    public User getActiveUser(int userId) {
        List<User> userList = activeUserList.stream().filter(user -> user.getUserId() == userId)
                .collect(Collectors.toList());
        assert userList.size() == 1;
        return userList.get(0);
    }

    /**
     * Set the prevStoreTimestamp to an most updated value.
     *
     * @implNote Should be called only when the updated snapshot value is stored in the database.
     * @param time Most updated time.
     * */
    public void setPrevStoreTimestamp(int time) {
        this.prevStoreTimestamp = time;
    }

    /**
     * Get the most updated store timestamp.
     * */
    public int getPrevStoreTimestamp() {
        return this.prevStoreTimestamp;
    }
}
