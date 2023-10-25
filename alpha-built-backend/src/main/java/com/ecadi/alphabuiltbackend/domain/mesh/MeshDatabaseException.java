package com.ecadi.alphabuiltbackend.domain.mesh;

/**
 * Container class for exceptions related to the Mesh database.
 */
public class MeshDatabaseException {

    /**
     * Exception indicating that a mesh does not exist in the database.
     */
    public static class MeshDoesNotExistInDatabaseException extends RuntimeException {
        /**
         * Constructs a new exception with null as its detail message.
         */
        public MeshDoesNotExistInDatabaseException() {
            super();
        }

        /**
         * Constructs a new exception with the specified detail message.
         *
         * @param message The detail message.
         *      The detail message is saved for later retrieval by the Throwable.getMessage() method.
         */
        public MeshDoesNotExistInDatabaseException(String message) {
            super(message);
        }
    }

    /**
     * Exception indicating that a mesh already exists in the database.
     */
    public static class MeshExistInDatabaseException extends RuntimeException {
        /**
         * Constructs a new exception with null as its detail message.
         */
        public MeshExistInDatabaseException() {
            super();
        }

        /**
         * Constructs a new exception with the specified detail message.
         *
         * @param message The detail message.
         *      The detail message is saved for later retrieval by the Throwable.getMessage() method.
         */
        public MeshExistInDatabaseException(String message) {
            super(message);
        }
    }
}
