package com.ecadi.alphabuiltbackend.domain.project;

import com.ecadi.alphabuiltbackend.domain.mesh.MeshSnapshot;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Entity representing a snapshot of a project.
 * This snapshot includes a list of MeshSnapshots and a timestamp
 *      representing the point in time when the snapshot was taken.
 * This entity is mapped to a database table.
 */
@Entity
@Getter
@Setter
public class ProjectSnapshot {

    /**
     * The unique identifier for this ProjectSnapshot, generated automatically.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private int projectId;

    /**
     * The timestamp for this ProjectSnapshot, representing the point in time when the snapshot was taken.
     * Cannot be null.
     */
    @Column(name = "time_stamp", nullable = false)
    private int timeStamp;

    /**
     * The list of MeshSnapshot objects associated with this ProjectSnapshot.
     * This list is fetched eagerly, meaning it is fetched
     *      from the database as soon as the ProjectSnapshot object is fetched.
     */
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    List<MeshSnapshot> meshList;

    /**
     * Constructs a ProjectSnapshot object with the given project index, timestamp, and list of MeshSnapshot objects.
     *
     * @param projectId The project index for the ProjectSnapshot.
     * @param timeStamp The timestamp for the ProjectSnapshot.
     * @param meshList The list of MeshSnapshot objects for the ProjectSnapshot.
     */
    public ProjectSnapshot(int projectId, int timeStamp, List<MeshSnapshot> meshList) {
        this.projectId = projectId;
        this.timeStamp = timeStamp;
        this.meshList = meshList;
    }

    /**
     * Default constructor for a ProjectSnapshot object.
     */
    public ProjectSnapshot() {

    }

    /**
     * Retrieves the project index of this ProjectSnapshot.
     *
     * @return The project index of this ProjectSnapshot.
     */
    public int getProjectId() {
        return projectId;
    }

    /**
     * Retrieves the timestamp of this ProjectSnapshot.
     *
     * @return The timestamp of this ProjectSnapshot.
     */
    public int getTimeStamp() {
        return timeStamp;
    }

    /**
     * Retrieves the list of MeshSnapshot objects associated with this ProjectSnapshot.
     *
     * @return The list of MeshSnapshot objects associated with this ProjectSnapshot.
     */
    public List<MeshSnapshot> getMeshList() {
        return meshList;
    }

    /**
     * Returns a string representation of the ProjectSnapshot object.
     *
     * @return A string representation of the ProjectSnapshot object.
     */
    @Override
    public String toString() {
        return "ProjectSnapshot{"
                + ", projectId=" + projectId
                + ", timeStamp=" + timeStamp
                + ", meshList=" + meshList
                + '}';
    }
}
