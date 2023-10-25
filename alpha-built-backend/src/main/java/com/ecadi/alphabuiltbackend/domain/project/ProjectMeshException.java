package com.ecadi.alphabuiltbackend.domain.project;

/**
 * A wrapper class containing various exceptions related to Mesh operations within a Project.
 */
public class ProjectMeshException {

    /**
     * Exception thrown when an operation tries to access or modify a Mesh using an invalid index.
     */
    public static class MeshIndexErrorException extends RuntimeException {

        /**
         * Default constructor.
         */
        public MeshIndexErrorException() {
            super();
        }

        /**
         * Constructor with custom message.
         *
         * @param message the detail message
         */
        public MeshIndexErrorException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when an operation tries to access a property that does not exist in the Mesh.
     */
    public static class MeshDoesNotContainInPropertyException extends RuntimeException {

        /**
         * Default constructor.
         */
        public MeshDoesNotContainInPropertyException() {
            super();
        }

        /**
         * Constructor with custom message.
         *
         * @param message the detail message
         */
        public MeshDoesNotContainInPropertyException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when an operation tries to add a property to a Mesh that already contains that property.
     */
    public static class MeshAlreadyContainInPropertyException extends RuntimeException {

        /**
         * Default constructor.
         */
        public MeshAlreadyContainInPropertyException() {
            super();
        }

        /**
         * Constructor with custom message.
         *
         * @param message the detail message
         */
        public MeshAlreadyContainInPropertyException(String message) {
            super(message);
        }

    }

}
