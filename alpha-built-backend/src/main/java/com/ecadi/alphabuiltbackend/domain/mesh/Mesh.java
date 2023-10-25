package com.ecadi.alphabuiltbackend.domain.mesh;

import com.ecadi.alphabuiltbackend.domain.project.Project;
import com.ecadi.alphabuiltbackend.domain.project.ProjectDatabaseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Entity representing a Mesh.
 */
public class Mesh {

    private int meshId;

    private Project project;

    private int projectId;

    private ObjectNode properties;

    /**
     * Constructs a Mesh with the specified mesh ID and properties.
     *
     * @param meshId The mesh ID.
     * @param properties The properties of the mesh.
     */
    public Mesh(int meshId, ObjectNode properties) {
        this.meshId = meshId;
        this.properties = properties;
    }

    /**
     * Constructs a Mesh with the specified mesh ID and project, and creates an empty set of properties.
     *
     * @param meshId The mesh ID.
     * @param project The project that the mesh belongs to.
     */
    public Mesh(int meshId, Project project) {
        this.meshId = meshId;
        this.project = project;
        this.projectId = project.getProjectId();
        this.properties = new ObjectMapper().createObjectNode();
    }

    /**
     * Constructs a Mesh with the specified mesh ID, project, and properties.
     *
     * @param meshId The mesh ID.
     * @param project The project that the mesh belongs to.
     * @param properties The properties of the mesh.
     */
    public Mesh(int meshId, Project project, ObjectNode properties) {
        this.meshId = meshId;
        this.project = project;
        this.properties = properties;
        this.projectId = project.getProjectId();
    }

    /**
     * Default constructor. Creates an empty set of properties.
     */
    public Mesh() {
        this.properties = new ObjectMapper().createObjectNode();
    }


    /**
     * Returns the mesh ID of the mesh.
     *
     * @return The mesh ID.
     */
    public int getMeshId() {
        return meshId;
    }

    /**
     * Returns the properties of the mesh.
     *
     * @return The properties of the mesh.
     */
    public ObjectNode getProperties() {
        return properties;
    }

    /**
     * Returns the project ID of the project that the mesh belongs to.
     *
     * @return The project ID.
     */
    public int getProjectId() {
        return projectId;
    }

    /**
     * Creates a deep copy of the Mesh object.
     *
     * @return A new Mesh object with the same properties as the current object.
     */
    public Mesh deepCopy() {
        return new Mesh(this.meshId, this.project, this.properties.deepCopy());
    }

    /**
     * Creates a snapshot of the current state of the Mesh object.
     *
     * @param timeStamp The time at which the snapshot is taken.
     * @param projectId The ID of the project that the mesh belongs to.
     * @return A new MeshSnapshot object representing the current state of the Mesh object.
     */
    public MeshSnapshot snapshotMesh(int timeStamp, int projectId) {
        return new MeshSnapshot(meshId, timeStamp, projectId, properties);
    }

    /**
     * Checks if the project associated with the Mesh object has already been initialized.
     *
     * @throws ProjectDatabaseException.ProjectAlreadyInitialisedException If the project has already been initialized.
     */
    private void checkProjectUninitialised() {
        if (project != null) {
            throw new ProjectDatabaseException.ProjectAlreadyInitialisedException();
        }
    }

    /**
     * Sets the project that the mesh belongs to.
     * This method is restricted to be called only if the project hasn't been initialized.
     *
     * @param project The project that the mesh belongs to.
     */
    public void setProjectRestricted(Project project) {
        checkProjectUninitialised();
        this.project = project;
        this.projectId = project.getProjectId();
    }

    /**
     * Returns a string representation of the Mesh object.
     *
     * @return A string representation of the Mesh object.
     */
    @Override
    public String toString() {
        return "Mesh{"
                + "meshId=" + meshId
                + ", project=" + project
                + ", projectId=" + projectId
                + ", properties=" + properties + '}';
    }
}

