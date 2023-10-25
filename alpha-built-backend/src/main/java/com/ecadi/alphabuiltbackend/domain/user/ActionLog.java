package com.ecadi.alphabuiltbackend.domain.user;

import com.ecadi.alphabuiltbackend.intercommunication.MeshAction;
import com.ecadi.alphabuiltbackend.domain.mesh.JsonNodeConverter;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;

/**
 * Entity representing an action log. This log records user actions in the system including
 * the project and mesh that were acted upon, and additional properties related to the action.
 * This entity is mapped to a database table.
 */
@Entity
public class ActionLog {

    /**
     * The unique identifier for this ActionLog, generated automatically.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private long id;

    /**
     * The project index associated with this action log. Cannot be null.
     */
    @Column(name = "project_index", nullable = false)
    private int projectId;

    /**
     * The user index associated with this action log. Cannot be null.
     */
    @Column(name = "user_index", nullable = false)
    private int userId;

    /**
     * The timestamp for this action log, representing the point in time when the action was logged. Cannot be null.
     */
    @Column(name = "time_stamp", nullable = false)
    private int timestamp;

    /**
     * The mesh index associated with this action log. Cannot be null.
     */
    @Column(name = "mesh_index", nullable = false)
    private int meshId;

    /**
     * The MeshAction associated with this action log. Cannot be null.
     */
    @Column(name = "mesh_action", nullable = false)
    private MeshAction meshAction;

    /**
     * The properties associated with this action log, represented as a JSON object node. Can be null.
     * Converted to/from a string representation for database storage/retrieval.
     */
    @Column(name = "properties", nullable = true)
    @Convert(converter = JsonNodeConverter.class)
    private ObjectNode properties;

    /**
     * Default constructor for an ActionLog object.
     */
    public ActionLog() {

    }

    /**
     * Constructs an ActionLog object with the given project index, user index, timestamp, mesh index,
     * mesh action, and properties.
     *
     * @param projectId The project index for the ActionLog.
     * @param userId The user index for the ActionLog.
     * @param timestamp The timestamp for the ActionLog.
     * @param meshId The mesh index for the ActionLog.
     * @param meshAction The MeshAction for the ActionLog.
     * @param properties The properties for the ActionLog, represented as a JSON object node.
     */
    public ActionLog(int projectId, int userId, int timestamp, int meshId, MeshAction meshAction, ObjectNode properties) {
        this.projectId = projectId;
        this.userId = userId;
        this.timestamp = timestamp;
        this.meshId = meshId;
        this.meshAction = meshAction;
        this.properties = properties;
    }

    /**
     * Returns the project index associated with this action log.
     *
     * @return The project index.
     */
    public int getProjectId() {
        return projectId;
    }

    /**
     * Returns the user index associated with this action log.
     *
     * @return The user index.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Returns the timestamp associated with this action log.
     *
     * @return The timestamp.
     */
    public int getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the mesh index associated with this action log.
     *
     * @return The mesh index.
     */
    public int getMeshId() {
        return meshId;
    }

    /**
     * Returns the properties associated with this action log as a JSON object node.
     *
     * @return The properties.
     */
    public ObjectNode getProperties() {
        return properties;
    }

    /**
     * Returns the MeshAction associated with this action log.
     *
     * @return The MeshAction.
     */
    public MeshAction getMeshAction() {
        return meshAction;
    }

    /**
     * Sets the project index for this action log.
     *
     * @param projectId The project index to set.
     */
    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    /**
     * Sets the user index for this action log.
     *
     * @param userId The user index to set.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Sets the timestamp for this action log.
     *
     * @param timestamp The timestamp to set.
     */
    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Sets the mesh index for this action log.
     *
     * @param meshId The mesh index to set.
     */
    public void setMeshId(int meshId) {
        this.meshId = meshId;
    }

    /**
     * Sets the properties for this action log as a JSON object node.
     *
     * @param properties The properties to set.
     */
    public void setProperties(ObjectNode properties) {
        this.properties = properties;
    }

    /**
     * Sets the MeshAction for this action log.
     *
     * @param meshAction The MeshAction to set.
     */
    public void setMeshAction(MeshAction meshAction) {
        this.meshAction = meshAction;
    }

    /**
     * Returns a string representation of the ActionLog object.
     *
     * @return A string representation of the ActionLog object.
     */
    @Override
    public String toString() {
        return "ActionLog{"
                + "id=" + id
                + ", projectId=" + projectId
                + ", userId=" + userId
                + ", timestamp=" + timestamp
                + ", meshId=" + meshId
                + ", properties=" + properties
                + '}';
    }
}
