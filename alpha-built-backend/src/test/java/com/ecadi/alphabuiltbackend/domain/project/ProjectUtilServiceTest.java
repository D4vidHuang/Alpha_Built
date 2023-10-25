package com.ecadi.alphabuiltbackend.domain.project;

import com.ecadi.alphabuiltbackend.domain.mesh.Mesh;
import com.ecadi.alphabuiltbackend.domain.mesh.MeshSnapshot;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ProjectUtilServiceTest {

    @Mock
    private ProjectSnapshot projectSnapshot;

    @Mock
    private MeshSnapshot meshSnapshot;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testConstructProjectFromProjectSnapshot() {
        ObjectNode on = new ObjectMapper().createObjectNode();
        on.put("key", "value");
        when(projectSnapshot.getProjectId()).thenReturn(Integer.valueOf("1"));
        when(projectSnapshot.getTimeStamp()).thenReturn(1000);
        when(projectSnapshot.getMeshList()).thenReturn(Arrays.asList(meshSnapshot, meshSnapshot));

        when(meshSnapshot.getMeshId()).thenReturn(Integer.valueOf("1"));
        when(meshSnapshot.getProperties()).thenReturn(on);

        Project result = ProjectUtilService.constructProjectFromProjectSnapshot(projectSnapshot);

        assertEquals(1, result.getProjectId());
        assertEquals(1000, result.getLatestTimeStamp());
        assertEquals(2, result.getMeshList().size());

        // further assertion checks can be done for the Mesh objects created
        List<Mesh> meshes = result.getMeshList();
        for (Mesh mesh : meshes) {
            assertEquals(1, mesh.getMeshId());
            assertEquals("{\"key\":\"value\"}", mesh.getProperties().toString());
        }
    }
}
