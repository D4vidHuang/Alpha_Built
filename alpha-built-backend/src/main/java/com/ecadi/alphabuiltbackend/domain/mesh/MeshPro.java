package com.ecadi.alphabuiltbackend.domain.mesh;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class representing a professional Mesh object.
 */
public class MeshPro {

    /**
     * Constructs a new MeshPro object with the specified Mesh and timestamp.
     * Initializes the mesh stack with the given Mesh and timestamp.
     *
     * @param mesh The Mesh object.
     * @param timeStamp The timestamp.
     */
    public MeshPro(Mesh mesh, int timeStamp) {
        this.meshId = mesh.getMeshId();
        meshStack = new ConcurrentLinkedDeque<>();
        lockStruct = new LockStruct();
        currentMeshProPair = new MeshProPair(mesh, timeStamp);
        this.lockUser = new AtomicInteger(-1);
        meshStackRedo = new ConcurrentLinkedDeque<>();
    }

    /**
     * Inner class to handle lock states and logic.
     */
    public static class LockStruct {

        private boolean locked;
        protected int lockedByUserId;
        private Lock lock = new ReentrantLock();

        /**
         * Default constructor for LockStruct. Sets initial locked state to false, and lockedByUserId to -1.
         */
        public LockStruct() {
            locked = false;
            lockedByUserId = -1;
        }

        /**
         * Returns the Lock object used for synchronization.
         *
         * @return The lock.
         */
        public Lock getLock() {
            return lock;
        }

        /**
         * Sets the locked state and the user who locked it.
         *
         * @param locked The new locked state.
         * @param lockedByUserId The ID of the user who locked it.
         */
        public LockStruct(boolean locked, int lockedByUserId) {
            this.locked = locked;
            this.lockedByUserId = lockedByUserId;
        }

        /**
         * Locks the MeshPro object.
         *
         * @param userId The ID of the user who is locking the object.
         */
        public void lock(int userId) {
            checkLockedIsFalse();
            this.locked = true;
            this.lockedByUserId = userId;
        }


        /**
         * Unlocks the MeshPro object.
         */
        public void unlock() {
            checkLockedIsTrue();
            this.locked = false;
            this.lockedByUserId = -1;
        }

        /**
         * Returns the current lock status of the MeshPro object.
         *
         * @return The current lock status.
         */
        public boolean getLockStatus() {
            return this.locked;
        }


        /**
         * Asserts that the lock status is false. Throws an exception if the lock status is true.
         */
        private void checkLockedIsFalse() {
            if (locked || lockedByUserId != -1) {
                throw new MeshException.LockLockedMeshProException();
            }
        }

        /**
         * Asserts that the lock status is true. Throws an exception if the lock status is false.
         */
        private void checkLockedIsTrue() {
            if (!locked || lockedByUserId == -1) {
                throw new MeshException.UnlockUnlockedMeshProException();
            }
        }

        /**
         * Asserts that the lock status is true. Throws an exception if the lock status is false.
         */
        public void assertLockIsTrue() {
            checkLockedIsTrue();
        }

        /**
         * Asserts that the lock status is false. Throws an exception if the lock status is true.
         */
        public void assertLockIsFalse() {
            checkLockedIsFalse();
        }

    }

    /**
     * Inner class to encapsulate a Mesh object and a timestamp.
     */
    public static class MeshProPair {

        private Mesh mesh;
        private Integer timeStamp;

        /**
         * Constructs a MeshProPair with the specified Mesh and timestamp.
         *
         * @param mesh The Mesh object.
         * @param timeStamp The timestamp.
         */
        public MeshProPair(Mesh mesh, Integer timeStamp) {
            this.mesh = mesh;
            this.timeStamp = timeStamp;
        }

        /**
         * Returns the Mesh object in the pair.
         *
         * @return The Mesh object.
         */
        public Mesh getMesh() {
            return mesh;
        }

        /**
         * Returns the timestamp in the pair.
         *
         * @return The timestamp.
         */
        public Integer getTimeStamp() {
            return timeStamp;
        }
    }

    ConcurrentLinkedDeque<MeshProPair> meshStack;
    Integer meshId;
    LockStruct lockStruct;
    AtomicInteger lockUser;
    MeshProPair currentMeshProPair;
    ConcurrentLinkedDeque<MeshProPair> meshStackRedo;

    /**
     * Asserts that the mesh stack is not empty. Throws an exception if it is.
     */
    protected void meshStackNonEmptyCheck() {
        if (meshStack.isEmpty() || meshStack.peek() == null) {
            throw new MeshException.MeshProStackException();
        }
    }

    /**
     * Revert the mesh one step to the previous state.
     */
    public MeshProPair revertMesh() {
        if (meshStack.size() == 1 && currentMeshProPair != null) {
            return currentMeshProPair;
        }
        meshStackRedo.push(currentMeshProPair);
        currentMeshProPair = meshStack.pop();
        return currentMeshProPair;
    }

    /**
     * Redo the mesh one step to the forward state.
     */
    public MeshProPair redoMesh() {
        if (meshStackRedo.isEmpty() && currentMeshProPair != null) {
            return currentMeshProPair;
        }
        meshStack.push(currentMeshProPair);
        currentMeshProPair = meshStackRedo.pop();
        return currentMeshProPair;
    }

    /**
     * Constructs a new MeshPro object with the specified mesh ID. Initializes an empty mesh stack.
     *
     * @param meshId The mesh ID.
     */
    public MeshPro(Integer meshId) {
        meshStack = new ConcurrentLinkedDeque<>();
        this.meshId = meshId;
        lockStruct = new LockStruct();
        currentMeshProPair = null;
        this.lockUser = new AtomicInteger(-1);
        meshStackRedo = new ConcurrentLinkedDeque<>();
    }

    /**
     * Locks the MeshPro object by the specified user ID.
     *
     * @param userId The ID of the user who is locking the object.
     */
    public void lockMeshPro(int userId) {
        lockStruct.getLock().lock();
        try {
            lockStruct.lock(userId);
        } finally {
            lockStruct.getLock().unlock();
        }
    }

    /**
     * Unlocks the MeshPro object.
     */
    public void unlockMeshPro() {
        lockStruct.getLock().lock();
        try {
            lockStruct.unlock();
        } finally {
            lockStruct.getLock().unlock();
        }
    }


    /**
     * Returns the current lock status of the MeshPro object.
     *
     * @return The current lock status.
     */
    public boolean getLockStatus() {
        return lockStruct.getLockStatus();
    }

    /**
     * Returns the latest Mesh object in the mesh stack.
     *
     * @return The latest Mesh object.
     */
    public Mesh getLatestMesh() {
        return currentMeshProPair.getMesh();
    }

    /**
     * Returns the timestamp of the latest Mesh object in the mesh stack.
     *
     * @return The latest timestamp.
     */
    public Integer getLatestTimeStamp() {
        //meshStackNonEmptyCheck();
        return this.currentMeshProPair.getTimeStamp();
    }

    /**
     * Adds a new Mesh object and a timestamp to the mesh stack.
     *
     * @param mesh The new Mesh object.
     * @param timeStamp The new timestamp.
     */
    public void addMeshAndTimeStamp(Mesh mesh, Integer timeStamp) {
        if (!meshStack.isEmpty()) {
            meshStack.push(currentMeshProPair);
            currentMeshProPair = new MeshProPair(mesh, timeStamp);
        } else {
            if (currentMeshProPair == null) {
                currentMeshProPair = new MeshProPair(mesh, timeStamp);
            } else {
                meshStack.push(currentMeshProPair);
                currentMeshProPair = new MeshProPair(mesh, timeStamp);
                meshStackRedo = new ConcurrentLinkedDeque<>();
            }
        }
    }

    /**
     * Pops and returns the latest MeshProPair from the mesh stack.
     *
     * @return The latest MeshProPair.
     */
    public MeshProPair popMeshProPair() {
        meshStackNonEmptyCheck();
        return meshStack.pop();
    }

    /**
     * Returns the latest MeshProPair from the mesh stack without removing it.
     *
     * @return The latest MeshProPair.
     */
    public MeshProPair getMeshProPair() {
        meshStackNonEmptyCheck();
        return meshStack.peek();
    }

    /**
     * Returns whether the mesh stack is empty or not.
     *
     * @return true if the mesh stack is empty, false otherwise.
     */
    public boolean isEmpty() {
        return meshStack.isEmpty();
    }

    /**
     * Asserts the lock status of the MeshPro object. Throws an exception if the assertion is not met.
     *
     * @param tobe The expected lock status.
     */
    public void assertLockStatus(boolean tobe) {
        if (tobe) {
            lockStruct.assertLockIsTrue();
        } else {
            lockStruct.assertLockIsFalse();
        }
    }

    /**
     * Check the lock status of the MeshPro object. Throws an exception if the assertion is not met.
     *
     * @param expected The expected lock status.
     */
    public void checkLockStatus(boolean expected) {
        if (expected) {
            if (lockUser.get() == -1) {
                throw new MeshException.UnlockUnlockedMeshProException();
            }
        } else {
            if (lockUser.get() != -1) {
                throw new MeshException.LockLockedMeshProException();
            }
        }
    }

    /**
     * Set the lockUser to the user that locks this mesh.
     *
     * @param userId The user locks the mesh.
     */
    public void lockMesh(int userId) {
        lockUser.compareAndSet(lockUser.get(), userId);
    }

    /**
     * Unlock the mesh and set the lockUser to -1 if the unlock user is the same as the lock user.
     *
     * @param userId The user wants to unlock the mesh.
     */
    public void unLockMesh(int userId) {
        if (lockUser.get() != userId) {
            throw new MeshException.UnlockUnlockedMeshProException();
        }
        lockUser.compareAndSet(lockUser.get(), -1);
    }

    /**
     * Returns a deep copy of the latest Mesh object in the mesh stack.
     *
     * @return A deep copy of the latest Mesh object.
     */
    public Mesh copyLatestMesh() {
        meshStackNonEmptyCheck();
        return meshStack.peek().getMesh().deepCopy();
    }

}
