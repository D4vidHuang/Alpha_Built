package com.ecadi.alphabuiltbackend.intercommunication;

/**
 * Collection of exceptions related to parsing InterMessage.
 */
public class InterMessageParsingException {

    /**
     * Exception thrown when the message type is undefined.
     */
    public static class UndefinedMessageTypeException extends RuntimeException {
        public UndefinedMessageTypeException() {
            super();
        }

        /**
         * Constructs a new UndefinedMessageTypeException with the specified detail message.
         *
         * @param message The detail message.
         */
        public UndefinedMessageTypeException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when the user ID is undefined.
     */
    public static class UndefinedUserIdException extends RuntimeException {
        public UndefinedUserIdException() {
            super();
        }

        /**
         * Constructs a new UndefinedUserIdException with the specified detail message.
         *
         * @param message The detail message.
         */
        public UndefinedUserIdException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when the project ID is undefined.
     */
    public static class UndefinedProjectIdException extends RuntimeException {
        public UndefinedProjectIdException() {
            super();
        }

        /**
         * Constructs a new UndefinedProjectIdException with the specified detail message.
         *
         * @param message The detail message.
         */
        public UndefinedProjectIdException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when the mesh metadata is undefined.
     */
    public static class UndefinedMeshMetadataException extends RuntimeException {
        public UndefinedMeshMetadataException() {
            super();
        }

        /**
         * Constructs a new UndefinedMeshMetadataException with the specified detail message.
         *
         * @param message The detail message.
         */
        public UndefinedMeshMetadataException(String message) {
            super(message);
        }
    }
}
