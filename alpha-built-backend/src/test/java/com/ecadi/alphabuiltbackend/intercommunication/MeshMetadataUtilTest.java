package com.ecadi.alphabuiltbackend.intercommunication;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ecadi.alphabuiltbackend.domain.mesh.Mesh;
import com.ecadi.alphabuiltbackend.domain.project.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MeshMetadataUtilTest {

    private MeshMetadataUtil meshMetadataUtil;

    private MeshMetadata meshMetadata;

    private int meshId;

    private MeshAction meshAction;

    private ObjectNode properties;

    private Project project;

    /**
     * run needed setups before each test.
     */
    @BeforeEach
    public void setUp() {
        meshId = 1;
        meshAction = MeshAction.CREATE;
        properties = new ObjectMapper().createObjectNode();
        meshMetadata = new MeshMetadata(meshId, meshAction, properties);
        project = new Project(1);
        meshMetadataUtil = new MeshMetadataUtil();
    }

    @Test
    public void testCreateInitialMeshMetadataList() {
        assertEquals(new ArrayList<>(), meshMetadataUtil.createInitialMeshMetadataList(project));
    }

    @Test
    public void testCreateInitialMeshMetadata() {
        properties.put("verdict", true);
        meshMetadata.setMeshAction(MeshAction.INITIALISE_MESH);
        Mesh mesh = new Mesh(meshId, project, properties);
        assertEquals(meshMetadata, meshMetadataUtil.createInitialMeshMetadata(mesh));
    }
}
