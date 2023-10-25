package com.ecadi.alphabuiltbackend.model;


import com.ecadi.alphabuiltbackend.intercommunication.MeshAction;

/**
 * This class contains a collection of exceptions which may occur while dealing with in-memory data in the application.
 */
public class MemoryException {

    /**
     * This exception is thrown when an operation is attempted on a project that is not loaded into memory.
     */
    public static class NoProjectLoadedException extends RuntimeException {
        public NoProjectLoadedException() {
            super();
        }

        public NoProjectLoadedException(String message) {
            super(message);
        }
    }

    /**
     * This exception is thrown when there is an attempt to add a user to memory that already exists.
     */
    public static class UserAlreadyExistedException extends RuntimeException {
        public UserAlreadyExistedException() {
            super();
        }

        public UserAlreadyExistedException(String message) {
            super(message);
        }

    }

    /**
     * This exception is thrown when there is an attempt to delete a project that has users associated with it.
     */
    public static class DeleteProjectWithUsersException extends RuntimeException {
        public DeleteProjectWithUsersException() {
            super();
        }

        public DeleteProjectWithUsersException(String message) {
            super(message);
        }

    }

    /**
     * Exception thrown when an attempt is made to delete a project that does not exist in memory.
     */
    public static class DeleteProjectNotInMemoryException extends RuntimeException {
        public DeleteProjectNotInMemoryException() {
            super();
        }

        public DeleteProjectNotInMemoryException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when an attempt is made to add a project that already exists in memory.
     */
    public static class ProjectAlreadyInMemoryException extends RuntimeException {
        public ProjectAlreadyInMemoryException() {
            super();
        }

        public ProjectAlreadyInMemoryException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when a project cannot be loaded from the database.
     */
    public static class CannotLoadProjectFromDatabaseException extends RuntimeException {
        public CannotLoadProjectFromDatabaseException() {
            super();
        }

        public CannotLoadProjectFromDatabaseException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when an attempt is made to add a user that already exists in memory.
     */
    public static class UserAlreadyInMemoryException extends RuntimeException {
        public UserAlreadyInMemoryException() {
            super();
        }

        public UserAlreadyInMemoryException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when a user cannot be loaded from the database.
     */
    public static class CannotLoadUserFromDatabaseException extends RuntimeException {
        public CannotLoadUserFromDatabaseException() {
            super();
        }

        public CannotLoadUserFromDatabaseException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when an operation is attempted on a user that does not exist in memory.
     */
    public static class UserDoesNotExistInMemoryException extends RuntimeException {
        public UserDoesNotExistInMemoryException() {
            super();
        }

        public UserDoesNotExistInMemoryException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when an attempt is made to delete an active user.
     */
    public static class DeleteActiveUserException extends RuntimeException {
        public DeleteActiveUserException() {
            super();
        }

        public DeleteActiveUserException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when an attempt is made to create a user that already exists.
     */
    public static class UserAlreadyCreatedException extends RuntimeException {
        public UserAlreadyCreatedException() {
            super();
        }

        public UserAlreadyCreatedException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when an attempt is made to create a project that already exists.
     */
    public static class ProjectAlreadyCreatedException extends RuntimeException {
        public ProjectAlreadyCreatedException() {
            super();
        }

        public ProjectAlreadyCreatedException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when an operation is attempted on a user who does not belong to the specified project.
     */
    public static class UserDoesNotBelongToProjectException extends RuntimeException {
        public UserDoesNotBelongToProjectException() {
            super();
        }

        public UserDoesNotBelongToProjectException(String message) {
            super(message);
        }

    }

    /**
     * Exception thrown when a mesh to be added already exists in the database.
     */
    public static class MeshToAddAlreadyExistInDatabaseException extends RuntimeException {
        public MeshToAddAlreadyExistInDatabaseException() {
            super();
        }

        public MeshToAddAlreadyExistInDatabaseException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when a mesh metadata is missing some basic properties.
     */
    public static class MeshMetadataMissingBasicPropertiesException extends RuntimeException {
        public MeshMetadataMissingBasicPropertiesException() {
            super();
        }

        public MeshMetadataMissingBasicPropertiesException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when a mesh metadata is missing the position property.
     */
    public static class MeshMetadataMissingPositionPropertyException extends RuntimeException {
        public MeshMetadataMissingPositionPropertyException() {
            super();
        }

        public MeshMetadataMissingPositionPropertyException(String message) {
            super(message);
        }
    }

    /**
     * This exception is thrown when there is an attempt to perform an action on a Mesh that does not exist.
     */
    public static class PerformActionOnNonExistingMeshException extends RuntimeException {
        public PerformActionOnNonExistingMeshException() {
            super();
        }

        public PerformActionOnNonExistingMeshException(String message) {
            super(message);
        }

        public PerformActionOnNonExistingMeshException(MeshAction meshAction, String message) {
            super(meshAction.toString() + " | " + message);
        }

    }

    public static class ConvertFailedMeshMetadataToActionLog extends RuntimeException {
        public ConvertFailedMeshMetadataToActionLog() {
            super();
        }

        public ConvertFailedMeshMetadataToActionLog(String msg) {
            super(msg);
        }

    }

    public static class InvalidMeshActionException extends RuntimeException {

        public InvalidMeshActionException() {
            super();
        }

        public InvalidMeshActionException(String s) {
        }
    }
}
