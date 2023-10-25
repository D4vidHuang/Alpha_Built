package com.ecadi.alphabuiltbackend.domain.mesh;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

class MeshRepositoryServiceTest {

    @Mock
    private MeshRepository meshRepository;

    @InjectMocks
    private MeshRepositoryService meshRepositoryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetMeshByMeshIdAndProjectId() {
        MeshSnapshot meshSnapshot = new MeshSnapshot(1, 100, 2, null);
        when(meshRepository.getMeshByMeshIdAndProjectId(1, 2)).thenReturn(meshSnapshot);

        MeshSnapshot result = meshRepositoryService.getMeshByMeshIdAndProjectId(1, 2);

        assertEquals(meshSnapshot, result);
    }


    @Test
    public void testGetMeshByMeshIdAndProjectId_notFound() {
        when(meshRepository.getMeshByMeshIdAndProjectId(1, 2)).thenReturn(null);

        assertThrows(MeshDatabaseException.MeshDoesNotExistInDatabaseException.class, () -> {
            meshRepositoryService.getMeshByMeshIdAndProjectId(1, 2);
        });
    }


    @Test
    public void testExistMeshByMeshIdAndProjectId() {
        when(meshRepository.existsMeshByMeshIdAndProjectId(1, 2)).thenReturn(true);

        assertTrue(meshRepositoryService.existMeshByMeshIdAndProjectId(1, 2));
    }

    @Test
    public void testSaveMesh() {
        MeshSnapshot meshSnapshot = new MeshSnapshot(1, 100, 2, null);
        when(meshRepository.existsMeshByMeshIdAndProjectId(1, 2)).thenReturn(false);

        meshRepositoryService.saveMesh(meshSnapshot);

        verify(meshRepository, times(1)).save(meshSnapshot);
    }

    @Test
    public void testSaveMeshAlreadyExists() {
        MeshSnapshot meshSnapshot = new MeshSnapshot(1, 100, 2, null);
        when(meshRepository.existsMeshByMeshIdAndProjectId(1, 2)).thenReturn(true);

        assertThrows(MeshDatabaseException.MeshExistInDatabaseException.class, () -> {
            meshRepositoryService.saveMesh(meshSnapshot);
        });
    }

    @Test
    public void testUpdateMesh() {
        MeshSnapshot meshSnapshot = new MeshSnapshot(1, 100, 2, null);
        when(meshRepository.existsMeshByMeshIdAndProjectId(1, 2)).thenReturn(true);

        meshRepositoryService.updateMesh(meshSnapshot);

        verify(meshRepository, times(1)).save(meshSnapshot);
    }

    @Test
    public void testUpdateMeshDoesNotExist() {
        MeshSnapshot meshSnapshot = new MeshSnapshot(1, 100, 2, null);
        when(meshRepository.existsMeshByMeshIdAndProjectId(1, 2)).thenReturn(false);

        assertThrows(MeshDatabaseException.MeshDoesNotExistInDatabaseException.class, () -> {
            meshRepositoryService.updateMesh(meshSnapshot);
        });
    }


    @Test
    public void testStoreMeshStateNewMesh() {
        MeshSnapshot meshSnapshot = new MeshSnapshot(1, 100, 2, null);
        when(meshRepository.existsMeshByMeshIdAndProjectId(1, 2)).thenReturn(false);

        meshRepositoryService.storeMeshState(meshSnapshot);

        verify(meshRepository, times(1)).save(meshSnapshot);
    }

    @Test
    public void testStoreMeshStateExistingMesh() {
        MeshSnapshot meshSnapshot = new MeshSnapshot(1, 100, 2, null);
        when(meshRepository.existsMeshByMeshIdAndProjectId(1, 2)).thenReturn(true);

        meshRepositoryService.storeMeshState(meshSnapshot);

        verify(meshRepository, times(1)).save(meshSnapshot);
    }

    @Test
    public void testClearDatabase() {
        meshRepositoryService.clearDatabase();

        verify(meshRepository, times(1)).deleteAll();
    }

    @Test
    public void testGetMeshRepository() {
        assertEquals(meshRepository, meshRepositoryService.getMeshRepository());
    }
}