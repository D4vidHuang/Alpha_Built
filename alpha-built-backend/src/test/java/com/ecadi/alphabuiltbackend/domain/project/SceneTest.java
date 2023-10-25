package com.ecadi.alphabuiltbackend.domain.project;

import com.ecadi.alphabuiltbackend.domain.mesh.Mesh;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SceneTest {

    @Test
    public void testGetList() {
        List<Mesh> meshes = new ArrayList<>();
        meshes.add(new Mesh());
        meshes.add(new Mesh());

        Scene scene = new Scene(meshes, 1);

        assertEquals(meshes, scene.getList());
    }

}
