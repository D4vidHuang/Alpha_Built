package com.ecadi.alphabuiltbackend.domain.mesh;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for performing operations on the Mesh table in the database.
 */
public interface MeshRepository extends JpaRepository<MeshSnapshot, Long> {
    /**
     * Returns the Mesh with the given mesh ID and project ID.
     *
     * @param meshId The mesh ID of the Mesh to retrieve.
     * @param projectId The project ID of the Mesh to retrieve.
     * @return The Mesh with the given mesh ID and project ID, or null if no such Mesh exists.
     */
    public MeshSnapshot getMeshByMeshIdAndProjectId(int meshId, int projectId);

    /**
     * Checks if a Mesh with the given mesh ID and project ID exists.
     *
     * @param meshId The mesh ID of the Mesh to check for.
     * @param projectId The project ID of the Mesh to check for.
     * @return True if a Mesh with the given mesh ID and project ID exists, false otherwise.
     */
    public boolean existsMeshByMeshIdAndProjectId(int meshId, int projectId);

    boolean existsMeshById(int i);
}
