package com.ecadi.alphabuiltbackend.intercommunication;

import lombok.Data;

import java.util.List;

/**
 * Represents a message for intercommunication, containing information about the type of message,
 * user ID, project ID, and a list of Mesh metadata.
 */
@Data
public class InterMessage {
    private InterMessageType type;
    private int userId;
    private int projectId;
    private List<MeshMetadata> meshMetadata;

    /**
     * Constructs a new InterMessage with the given type, user ID, project ID, and list of Mesh metadata.
     *
     * @param type The type of the InterMessage.
     * @param userId The user ID associated with the InterMessage.
     * @param projectId The project ID associated with the InterMessage.
     * @param meshMetadataList The list of Mesh metadata associated with the InterMessage.
     */
    public InterMessage(InterMessageType type, int userId, int projectId, List<MeshMetadata> meshMetadataList) {
        this.type = type;
        this.userId = userId;
        this.projectId = projectId;
        this.meshMetadata = meshMetadataList;
    }
}
