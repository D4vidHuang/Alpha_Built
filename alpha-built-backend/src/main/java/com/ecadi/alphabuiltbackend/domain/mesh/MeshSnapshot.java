package com.ecadi.alphabuiltbackend.domain.mesh;

import com.ecadi.alphabuiltbackend.domain.project.ProjectSnapshot;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Convert;

/**
 * Entity representing a MeshSnapshot. A MeshSnapshot object contains
 * data about a specific instance of a mesh, along with a timestamp
 * of when this snapshot was taken.
 */
@Entity
public class MeshSnapshot {

    /**
     * The unique ID of the MeshSnapshot.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * The ID of the Mesh object associated with this MeshSnapshot.
     */
    @Column(name = "mesh_index", nullable = false)
    private int meshId;

    /**
     * The ProjectSnapshot object associated with this MeshSnapshot.
     */
    @ManyToOne
    private ProjectSnapshot projectSnapshot;

    /**
     * The ID of the Project object associated with this MeshSnapshot.
     */
    @Column(name = "project_index", nullable = false)
    private int projectId;

    /**
     * The properties of the Mesh object at the time of this snapshot.
     */
    @Column(name = "properties", nullable = true)
    @Convert(converter = JsonNodeConverter.class)
    private ObjectNode properties;

    /**
     * The timestamp of when this snapshot was taken.
     */
    @Column(name = "time_stamp", nullable = false)
    private int timeStamp;

    /**
     * Default constructor. Constructs a new MeshSnapshot object.
     */
    public MeshSnapshot() {

    }

    /**
     * Constructs a new MeshSnapshot object with the specified mesh ID,
     * timestamp, project ID, and properties.
     *
     * @param meshId The ID of the Mesh object.
     * @param timeStamp The timestamp of the snapshot.
     * @param projectId The ID of the Project object.
     * @param properties The properties of the Mesh object.
     */
    public MeshSnapshot(int meshId, int timeStamp, int projectId, ObjectNode properties) {
        this.meshId = meshId;
        this.timeStamp = timeStamp;
        //this.projectSnapshot = projectSnapshot;
        this.projectId = projectId;
        this.properties = properties;
    }

    /**
     * Returns the ID of the Mesh object associated with this MeshSnapshot.
     *
     * @return The Mesh object's ID.
     */
    public int getMeshId() {
        return meshId;
    }

    /**
     * Returns the properties of the Mesh object at the time of this snapshot.
     *
     * @return The properties of the Mesh object.
     */
    public ObjectNode getProperties() {
        return properties;
    }

    /**
     * Returns the ID of the Project object associated with this MeshSnapshot.
     *
     * @return The Project object's ID.
     */
    public int getProjectId() {
        return projectId;
    }

    /**
     * Returns a string representation of the MeshSnapshot object.
     *
     * @return A string representation of the MeshSnapshot object.
     */
    @Override
    public String toString() {
        return "MeshSnapshot{"
                + "id=" + id
                + ", meshId=" + meshId
                + ", projectSnapshot=" + projectSnapshot
                + ", projectId=" + projectId
                + ", properties=" + properties
                + ", timeStamp=" + timeStamp
                + '}';
    }
}
