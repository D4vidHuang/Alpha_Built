package com.ecadi.alphabuiltbackend.domain.user;

/**
 * This package-level exception class handles exceptions related to User operations in the database.
 */
public class UserDatabaseException {

    /**
     * This exception is thrown when a User that does not exist in the database is being accessed.
     */
    public static class UserNotExistInDatabaseException extends RuntimeException {

        /**
         * Constructs a new exception with null as its detail message.
         */
        public UserNotExistInDatabaseException() {
            super();
        }

        /**
         * Constructs a new exception with the specified detail message.
         *
         * @param message The detail message, saved for later retrieval by the getMessage() method.
         */
        public UserNotExistInDatabaseException(String message) {
            super(message);
        }
    }
}
