package com.ecadi.alphabuiltbackend.domain.mesh;

import com.ecadi.alphabuiltbackend.domain.project.Project;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


class MeshProTest {

    private MeshPro meshPro;

    @BeforeEach
    public void setUp() {
        meshPro = new MeshPro(1);
    }

    @Test
    public void testMeshProConstructor() {
        Mesh mesh = new Mesh(1, new Project(), null);
        int timeStamp = 123;

        MeshPro meshPro = new MeshPro(mesh, timeStamp);

        assertEquals(mesh.getMeshId(), meshPro.getLatestMesh().getMeshId());
        assertEquals(timeStamp, meshPro.getLatestTimeStamp());
    }

    @Test
    public void testLockStructConstructor() {
        MeshPro.LockStruct lockStruct = new MeshPro.LockStruct();

        assertFalse(lockStruct.getLockStatus());
        assertEquals(-1, lockStruct.lockedByUserId);
        assertNotNull(lockStruct.getLock());
    }

    @Test
    public void testLockStructConstructorWithParameters() {
        boolean locked = true;
        int lockedByUserId = 1;
        MeshPro.LockStruct lockStruct = new MeshPro.LockStruct(locked, lockedByUserId);

        assertEquals(locked, lockStruct.getLockStatus());
        assertEquals(lockedByUserId, lockStruct.lockedByUserId);
        assertNotNull(lockStruct.getLock());
    }

    @Test
    public void testLockStructLock() {
        MeshPro.LockStruct lockStruct = new MeshPro.LockStruct();

        lockStruct.lock(1);

        assertTrue(lockStruct.getLockStatus());
        assertEquals(1, lockStruct.lockedByUserId);
    }

    @Test
    public void testLockStructLockWhenAlreadyLocked() {
        boolean locked = true;
        int lockedByUserId = 1;
        MeshPro.LockStruct lockStruct = new MeshPro.LockStruct(locked, lockedByUserId);

        assertThrows(MeshException.LockLockedMeshProException.class, () -> lockStruct.lock(2));
    }

    @Test
    public void testLockStructUnlock() {
        boolean locked = true;
        int lockedByUserId = 1;
        MeshPro.LockStruct lockStruct = new MeshPro.LockStruct(locked, lockedByUserId);

        lockStruct.unlock();

        assertFalse(lockStruct.getLockStatus());
        assertEquals(-1, lockStruct.lockedByUserId);
    }

    @Test
    public void testLockStructUnlockWhenAlreadyUnlocked() {
        MeshPro.LockStruct lockStruct = new MeshPro.LockStruct();

        assertThrows(MeshException.UnlockUnlockedMeshProException.class, lockStruct::unlock);
    }

    @Test
    public void testLockStructAssertLockIsTrue() {
        boolean locked = true;
        int lockedByUserId = 1;
        MeshPro.LockStruct lockStruct = new MeshPro.LockStruct(locked, lockedByUserId);

        lockStruct.assertLockIsTrue();
    }

    @Test
    public void testLockStructAssertLockIsTrueWhenFalse() {
        MeshPro.LockStruct lockStruct = new MeshPro.LockStruct();

        assertThrows(MeshException.UnlockUnlockedMeshProException.class, lockStruct::assertLockIsTrue);
    }

    @Test
    public void testLockStructAssertLockIsFalse() {
        MeshPro.LockStruct lockStruct = new MeshPro.LockStruct();

        lockStruct.assertLockIsFalse();
    }

    @Test
    public void testLockStructAssertLockIsFalseWhenTrue() {
        boolean locked = true;
        int lockedByUserId = 1;
        MeshPro.LockStruct lockStruct = new MeshPro.LockStruct(locked, lockedByUserId);

        assertThrows(MeshException.LockLockedMeshProException.class, lockStruct::assertLockIsFalse);
    }

    @Test
    public void testMeshProConstructorWithMeshId() {
        assertEquals(1, meshPro.meshId);
        assertNotNull(meshPro.meshStack);
        assertNotNull(meshPro.lockStruct);
    }

    @Test
    public void testLockMeshPro() {
        meshPro.lockMeshPro(1);
        assertTrue(meshPro.getLockStatus());
    }

    @Test
    public void testUnlockMeshPro() {
        boolean locked = true;
        int lockedByUserId = 1;

        meshPro.lockStruct = new MeshPro.LockStruct(locked, lockedByUserId);

        meshPro.unlockMeshPro();

        assertFalse(meshPro.getLockStatus());
    }

    //    @Test
    //    public void testGetLatestMesh() {
    //        Mesh mesh = new Mesh(1, new Project(1), null);
    //        int timeStamp = 123;
    //        MeshPro.MeshProPair pair = new MeshPro.MeshProPair(mesh, timeStamp);
    //        meshPro.meshStack.push(pair);
    //
    //        Mesh latestMesh = meshPro.getLatestMesh();
    //
    //        assertEquals(mesh, latestMesh);
    //    }

    //    @Test
    //    public void testGetLatestTimeStamp() {
    //        Mesh mesh = new Mesh(1, new Project(1), null);
    //        int timeStamp = 123;
    //        MeshPro.MeshProPair pair = new MeshPro.MeshProPair(mesh, timeStamp);
    //        meshPro.meshStack.push(pair);
    //
    //        int latestTimeStamp = meshPro.getLatestTimeStamp();
    //
    //        assertEquals(timeStamp, latestTimeStamp);
    //    }

    //    @Test
    //    public void testAddMeshAndTimeStamp() {
    //        Mesh mesh = new Mesh(1, new Project(1), null);
    //        int timeStamp = 123;
    //
    //        meshPro.addMeshAndTimeStamp(mesh, timeStamp);
    //        assertFalse(meshPro.isEmpty());
    //        assertEquals(mesh, meshPro.getLatestMesh());
    //        assertEquals(timeStamp, meshPro.getLatestTimeStamp());
    //    }

    @Test
    public void testPopMeshProPair() {
        Mesh mesh = new Mesh(1, new Project(1), null);
        int timeStamp = 123;
        MeshPro.MeshProPair pair = new MeshPro.MeshProPair(mesh, timeStamp);

        meshPro.meshStack.push(pair);

        MeshPro.MeshProPair poppedPair = meshPro.popMeshProPair();

        assertEquals(pair, poppedPair);
        assertTrue(meshPro.isEmpty());
    }

    @Test
    public void testGetMeshProPair() {
        Mesh mesh = new Mesh(1, new Project(1), null);
        int timeStamp = 123;
        MeshPro.MeshProPair pair = new MeshPro.MeshProPair(mesh, timeStamp);

        meshPro.meshStack.push(pair);

        MeshPro.MeshProPair retrievedPair = meshPro.getMeshProPair();

        assertEquals(pair, retrievedPair);
        assertFalse(meshPro.isEmpty());
    }

    @Test
    public void testIsEmpty() {
        MeshPro meshPro = new MeshPro(1);

        assertTrue(meshPro.isEmpty());

        Mesh mesh = new Mesh(1, new Project(1), null);
        int timeStamp = 123;
        MeshPro.MeshProPair pair = new MeshPro.MeshProPair(mesh, timeStamp);
        meshPro.meshStack.push(pair);

        assertFalse(meshPro.isEmpty());
    }

    @Test
    public void testAssertLockStatusWhenTrue() {
        MeshPro.LockStruct lockStruct = new MeshPro.LockStruct(true, 1);
        MeshPro meshPro = new MeshPro(1);
        meshPro.lockStruct = lockStruct;

        assertDoesNotThrow(() -> meshPro.assertLockStatus(true));
    }

    @Test
    public void testAssertLockStatusWhenFalse() {
        meshPro.lockStruct = new MeshPro.LockStruct(false, -1);
        assertDoesNotThrow(() -> meshPro.assertLockStatus(false));
    }

    @Test
    public void testAssertLockStatusWhenTrueButLockStructIsFalse() {
        meshPro.lockStruct = new MeshPro.LockStruct(false, -1);

        assertThrows(MeshException.UnlockUnlockedMeshProException.class, () -> meshPro.assertLockStatus(true));
    }

    @Test
    public void testAssertLockStatusWhenFalseButLockStructIsTrue() {
        meshPro.lockStruct = new MeshPro.LockStruct(true, 1);
        assertThrows(MeshException.LockLockedMeshProException.class, () -> meshPro.assertLockStatus(false));
    }

    @Test
    public void testCopyLatestMesh() {
        ObjectNode properties = new ObjectMapper().createObjectNode();
        properties.put("key", "value");

        Mesh mesh = new Mesh(1, new Project(1), properties);
        int timeStamp = 123;
        MeshPro.MeshProPair pair = new MeshPro.MeshProPair(mesh, timeStamp);

        meshPro.meshStack.push(pair);

        Mesh copiedMesh = meshPro.copyLatestMesh();

        assertNotSame(mesh, copiedMesh);
        assertEquals(mesh.getMeshId(), copiedMesh.getMeshId());
    }



    @Test
    public void testMeshStackNonEmptyCheckWhenEmpty() {
        assertThrows(MeshException.MeshProStackException.class, meshPro::meshStackNonEmptyCheck);
    }

    @Test
    public void testMeshStackNonEmptyCheckWhenPeekIsNull() {
        meshPro.meshStack.push(new MeshPro.MeshProPair(null, 1));
        assertDoesNotThrow(meshPro::meshStackNonEmptyCheck);
    }

}