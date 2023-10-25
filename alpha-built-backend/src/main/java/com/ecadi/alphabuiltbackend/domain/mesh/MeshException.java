package com.ecadi.alphabuiltbackend.domain.mesh;

public class MeshException {
    public static class LockLockedMeshProException extends RuntimeException {
        public LockLockedMeshProException() {
            super();
        }

        public LockLockedMeshProException(String msg) {
            super(msg);
        }
    }

    public static class UnlockUnlockedMeshProException extends RuntimeException {
        public UnlockUnlockedMeshProException() {
            super();
        }

        public UnlockUnlockedMeshProException(String msg) {
            super(msg);
        }
    }

    public static class MeshProStackException extends RuntimeException {
        public MeshProStackException() {
            super();
        }

        public MeshProStackException(String msg) {
            super(msg);
        }
    }
}
