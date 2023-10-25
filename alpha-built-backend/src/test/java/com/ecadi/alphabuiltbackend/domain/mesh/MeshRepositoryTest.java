package com.ecadi.alphabuiltbackend.domain.mesh;

import com.ecadi.alphabuiltbackend.domain.project.Project;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MeshRepositoryTest {

    @Mock
    private MeshRepository meshRepository;

    private Project project;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        project = new Project(1);
    }

    @Test
    void getMeshByMeshIdAndProjectId() {
        Mesh mesh = new Mesh(1, project);  // assuming Mesh has a constructor that accepts meshId and projectId
        ObjectNode properties = new ObjectNode(null);
        MeshSnapshot meshSnapshot = new MeshSnapshot(1, 1, 1, properties);
        when(meshRepository.getMeshByMeshIdAndProjectId(1, 1)).thenReturn(meshSnapshot);

        MeshSnapshot retrievedMesh = meshRepository.getMeshByMeshIdAndProjectId(1, 1);

        assertEquals(1, retrievedMesh.getMeshId());
        assertEquals(1, retrievedMesh.getProjectId());
        verify(meshRepository, times(1)).getMeshByMeshIdAndProjectId(1, 1);
    }

    @Test
    void existsMeshByMeshIdAndProjectId() {
        when(meshRepository.existsMeshByMeshIdAndProjectId(1, 1)).thenReturn(true);

        boolean exists = meshRepository.existsMeshByMeshIdAndProjectId(1, 1);

        assertTrue(exists);
        verify(meshRepository, times(1)).existsMeshByMeshIdAndProjectId(1, 1);
    }

    @Test
    public void doesNotExistMeshByMeshIdAndProjectIdTest() {
        when(meshRepository.existsMeshByMeshIdAndProjectId(1, 1)).thenReturn(false);

        boolean exists = meshRepository.existsMeshByMeshIdAndProjectId(1, 1);

        assertFalse(exists);
        verify(meshRepository, times(1)).existsMeshByMeshIdAndProjectId(1, 1);
    }

    @Test
    public void existsMeshByIdTest() {
        when(meshRepository.existsMeshById(1)).thenReturn(true);

        boolean exists = meshRepository.existsMeshById(1);

        assertTrue(exists);
        verify(meshRepository, times(1)).existsMeshById(1);
    }

    @Test
    public void doesNotExistMeshByIdTest() {
        when(meshRepository.existsMeshById(1)).thenReturn(false);

        boolean exists = meshRepository.existsMeshById(1);

        assertFalse(exists);
        verify(meshRepository, times(1)).existsMeshById(1);
    }
}