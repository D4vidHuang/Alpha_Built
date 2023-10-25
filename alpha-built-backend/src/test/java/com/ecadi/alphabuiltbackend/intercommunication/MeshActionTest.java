package com.ecadi.alphabuiltbackend.intercommunication;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MeshActionTest {
    @Test
    void testMeshAction() {
        assertEquals(MeshAction.CREATE, MeshAction.valueOf("CREATE"));
        assertEquals(MeshAction.TRANSLATE, MeshAction.valueOf("TRANSLATE"));
        assertEquals(MeshAction.SCALE, MeshAction.valueOf("SCALE"));
        assertEquals(MeshAction.ROTATE, MeshAction.valueOf("ROTATE"));
        assertEquals(MeshAction.ADD_MESH, MeshAction.valueOf("ADD_MESH"));
        assertEquals(MeshAction.INITIALISE_MESH, MeshAction.valueOf("INITIALISE_MESH"));
        assertEquals(MeshAction.REMOVE, MeshAction.valueOf("REMOVE"));
        assertEquals(MeshAction.LOCK, MeshAction.valueOf("LOCK"));
        assertEquals(MeshAction.UNLOCK, MeshAction.valueOf("UNLOCK"));
    }
}