package com.ecadi.alphabuiltbackend.intercommunication;

import com.ecadi.alphabuiltbackend.domain.mesh.Mesh;
import com.ecadi.alphabuiltbackend.domain.project.Project;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.stream.Collectors;

public class MeshMetadataUtil {
    /**
     * Creates a list of initial MeshMetadata objects based on the meshes in a project.
     *
     * @param project The project containing the meshes.
     * @return A list of initial MeshMetadata objects.
     */
    public static List<MeshMetadata> createInitialMeshMetadataList(Project project) {
        List<Mesh> meshList = project.getMeshList();
        List<MeshMetadata> meshMetadataList = meshList
                                                    .stream()
                                                    .map(MeshMetadataUtil::createInitialMeshMetadata)
                                                    .collect(Collectors.toList());
        return meshMetadataList;
    }

    /**
     * Creates an initial MeshMetadata object for a given mesh.
     *
     * @param mesh The mesh to create the metadata for.
     * @return The initial MeshMetadata object.
     */
    public static MeshMetadata createInitialMeshMetadata(Mesh mesh) {
        int meshId = mesh.getMeshId();
        MeshAction meshAction = MeshAction.INITIALISE_MESH;
        ObjectNode metadataProperties = new ObjectMapper().createObjectNode();
        ObjectNode meshProperties = mesh.getProperties();
        meshProperties.fields().forEachRemaining(field -> {
            String key = field.getKey();
            JsonNode value = field.getValue();
            metadataProperties.put(key, value);
        });
        MeshMetadata meshMetadata = new MeshMetadata(meshId, meshAction, meshProperties);
        meshMetadata.addVerdict(true);
        return meshMetadata;
    }


}
