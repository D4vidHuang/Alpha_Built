package com.ecadi.alphabuiltbackend.domain.project;

/**
 * A wrapper class containing various exceptions related to Project operations.
 */
public class ProjectDatabaseException {

    /**
     * Exception thrown when an operation tries to access a Project that does not exist in the database.
     */
    public static class ProjectNotExistInDatabaseException extends RuntimeException {

        /**
         * Exception thrown when an operation tries to access a Project that does not exist in the database.
         */
        public ProjectNotExistInDatabaseException() {
            super();
        }

        /**
         * Constructor with custom message.
         *
         * @param message the detail message
         */
        public ProjectNotExistInDatabaseException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when an operation tries to add a Project that already exists in the database.
     */
    public static class ProjectExistInDatabaseException extends RuntimeException {

        /**
         * Default constructor.
         */
        public ProjectExistInDatabaseException() {
            super();
        }

        /**
         * Constructor with custom message.
         *
         * @param message the detail message
         */
        public ProjectExistInDatabaseException(String message) {
            super(message);
        }

    }

    /**
     * Exception thrown when a Project contains multiple Users with the same user ID.
     */
    public static class ProjectContainsMultipleUsersWithSameUserIdException extends RuntimeException {

        /**
         * Default constructor.
         */
        public ProjectContainsMultipleUsersWithSameUserIdException() {
            super();
        }

        /**
         * Constructor with custom message.
         *
         * @param message the detail message
         */
        public ProjectContainsMultipleUsersWithSameUserIdException(String message) {
            super(message);
        }
    }

    public static class ProjectAlreadyInitialisedException extends RuntimeException {
        public ProjectAlreadyInitialisedException() {
            super();
        }

        public ProjectAlreadyInitialisedException(String msg) {
            super(msg);
        }
    }

}
