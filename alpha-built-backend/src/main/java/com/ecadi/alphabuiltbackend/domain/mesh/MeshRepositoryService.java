package com.ecadi.alphabuiltbackend.domain.mesh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for performing operations on the Mesh repository.
 */
@Service
public class MeshRepositoryService {

    Logger logger = LoggerFactory.getLogger("Mesh Logger");
    MeshRepository meshRepository;

    /**
     * Sets the Mesh repository.
     *
     * @param meshRepository The Mesh repository to be set.
     */
    @Autowired
    public void setMeshRepository(MeshRepository meshRepository) {
        this.meshRepository = meshRepository;
    }

    /**
     * Retrieves a Mesh by its mesh ID and project ID.
     *
     * @param meshId The mesh ID of the Mesh to retrieve.
     * @param projectId The project ID of the Mesh to retrieve.
     * @return The Mesh with the given mesh ID and project ID.
     * @throws MeshDatabaseException.MeshDoesNotExistInDatabaseException
     *      If the Mesh does not exist in the database.
     */
    public MeshSnapshot getMeshByMeshIdAndProjectId(int meshId, int projectId) {
        MeshSnapshot mesh = meshRepository.getMeshByMeshIdAndProjectId(meshId, projectId);
        if (mesh == null) {
            String errMessage = String
                    .format(
                            "Mesh with mesh id %d does not exist in project with project id %d",
                            meshId,
                            projectId
                    );
            logger.error(errMessage);
            throw new MeshDatabaseException.MeshDoesNotExistInDatabaseException(errMessage);
        }
        return mesh;
    }

    /**
     * Checks if a Mesh with the given mesh ID and project ID exists.
     *
     * @param meshId The mesh ID of the Mesh to check for.
     * @param projectId The project ID of the Mesh to check for.
     * @return True if a Mesh with the given mesh ID and project ID exists, false otherwise.
     */
    public boolean existMeshByMeshIdAndProjectId(int meshId, int projectId) {
        return meshRepository.existsMeshByMeshIdAndProjectId(meshId, projectId);
    }

    /**
     * Saves a Mesh to the database.
     *
     * @param mesh The Mesh to save.
     * @throws MeshDatabaseException.MeshExistInDatabaseException
     *      If the Mesh already exists in the database.
     */
    public void saveMesh(MeshSnapshot mesh) {
        if (meshRepository.existsMeshByMeshIdAndProjectId(mesh.getMeshId(), mesh.getProjectId())) {
            String errMessage = String
                    .format(
                            "Mesh with id %d has already existed in database.",
                            mesh.getMeshId()
                    );
            logger.error(errMessage);
            throw new MeshDatabaseException.MeshExistInDatabaseException(errMessage);
        }
        meshRepository.save(mesh);
    }

    /**
     * Updates a Mesh in the database.
     *
     * @param mesh The Mesh to update.
     * @throws MeshDatabaseException.MeshDoesNotExistInDatabaseException
     *      If the Mesh does not exist in the database.
     */
    void updateMesh(MeshSnapshot mesh) {
        if (!meshRepository.existsMeshByMeshIdAndProjectId(mesh.getMeshId(), mesh.getProjectId())) {
            String errMessage = String
                    .format(
                            "Mesh with id %d does not exist in database.",
                            mesh.getMeshId()
                    );
            logger.error(errMessage);
            throw new MeshDatabaseException.MeshDoesNotExistInDatabaseException(errMessage);
        }
        meshRepository.save(mesh);
    }

    /**
     * Stores the state of a given MeshSnapshot in the database. If a mesh with the same
     * mesh ID and project ID already exists, it updates the existing mesh. Otherwise,
     * it saves the new mesh.
     *
     * @param meshSnapshot The MeshSnapshot object whose state is to be stored in the database.
     * @throws MeshDatabaseException.MeshExistInDatabaseException
     *      If an attempt is made to save a Mesh that already exists in the database.
     * @throws MeshDatabaseException.MeshDoesNotExistInDatabaseException
     *      If an attempt is made to update a Mesh that does not exist in the database.
     */
    public void storeMeshState(MeshSnapshot meshSnapshot) {
        if (meshRepository.existsMeshByMeshIdAndProjectId(meshSnapshot.getMeshId(), meshSnapshot.getProjectId())) {
            updateMesh(meshSnapshot);
        } else {
            saveMesh(meshSnapshot);
        }
    }

    /**
     * Deletes all Meshes from the database.
     */
    public void clearDatabase() {
        meshRepository.deleteAll();
    }

    /**
     * Get the whole mesh repository.
     *
     * @return The whole mesh repository.
     */
    public MeshRepository getMeshRepository() {
        return meshRepository;
    }
}
