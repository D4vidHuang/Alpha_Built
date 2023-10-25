package com.ecadi.alphabuiltbackend.domain.user;


import com.ecadi.alphabuiltbackend.intercommunication.MeshMetadata;

/**
 * Utility service for handling actions related to the User domain.
 * Contains methods for converting and reconstructing action logs and mesh metadata.
 */
public class UserUtilService {

    /**
     * Exception that is thrown when an error occurs during the reconstruction of an action log from metadata.
     */
    public static class ReconstructActionLogFromMetadataException extends RuntimeException {

        /**
         * Creates a new exception with no detail message.
         */
        public ReconstructActionLogFromMetadataException() {
            super();
        }

        /**
         * Creates a new exception with the specified detail message.
         *
         * @param msg The detail message.
         */
        public ReconstructActionLogFromMetadataException(String msg) {
            super(msg);
        }
    }

    /**
     * Checks whether the project ID of an action log matches a specified project ID.
     * If not, it throws a ReconstructActionLogFromMetadataException.
     *
     * @param projectId The specified project ID.
     * @param actionLog The action log to check.
     */
    private static void checkActionLogOfProject(int projectId, ActionLog actionLog) {
        if (projectId != actionLog.getProjectId()) {
            throw new ReconstructActionLogFromMetadataException();
        }
    }

    /**
     * Converts a MeshMetadata object into an ActionLog.
     *
     * @param userId The ID of the user who performed the action.
     * @param projectId The ID of the project the action belongs to.
     * @param currTimestamp The timestamp of the action.
     * @param metadata The metadata of the mesh that was acted on.
     * @return An ActionLog representing the action.
     */
    public static ActionLog convertMetadataToActionLog(int projectId, int userId,
                                                       int currTimestamp, MeshMetadata metadata) {
        return new ActionLog(projectId, userId,
                currTimestamp, metadata.getMeshId(), metadata.getMeshAction(), metadata.getProperties());
    }

    /**
     * Reconstructs a MeshMetadata object from an ActionLog.
     *
     * @param projectId The ID of the project the action belongs to.
     * @param actionLog The action log to reconstruct from.
     * @return A MeshMetadata object representing the metadata of the mesh that was acted on.
     */
    public static MeshMetadata reconstructActionLogFromMetadata(int projectId, ActionLog actionLog) {
        checkActionLogOfProject(projectId, actionLog);
        return new MeshMetadata(actionLog.getMeshId(), actionLog.getMeshAction(), actionLog.getProperties());
    }
}
