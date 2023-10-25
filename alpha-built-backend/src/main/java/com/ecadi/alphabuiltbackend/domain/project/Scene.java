package com.ecadi.alphabuiltbackend.domain.project;

import com.ecadi.alphabuiltbackend.domain.mesh.Mesh;

import java.util.List;

public class Scene {
    private List<Mesh> current;
    private int timestamp;

    public Scene(List<Mesh> current, int timestamp) {
        this.current = current;
        this.timestamp = timestamp;
    }

    public List<Mesh> getList() {
        return current;
    }
}
