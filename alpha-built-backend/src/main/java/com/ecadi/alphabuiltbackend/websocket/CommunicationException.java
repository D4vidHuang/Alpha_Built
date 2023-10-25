package com.ecadi.alphabuiltbackend.websocket;

public class CommunicationException {
    /**
     * Exception thrown when a null project is encountered.
     */
    public static class NullProjectException extends RuntimeException {
        /**
         * Constructs a new NullProjectException with no detail message.
         */
        public NullProjectException() {
            super();
        }

        /**
         * Constructs a new NullProjectException with the specified detail message.
         *
         * @param message the detail message.
         */
        public NullProjectException(String message) {
            super(message);
        }


    }

    /**
     * Exception thrown when message is invalid type.
     */
    public static class InvalidMessageTypeException extends RuntimeException {

        /**
         * Constructs a new InvalidMessageTypeException with no detail message.
         */
        public InvalidMessageTypeException() {
            super();
        }

        /**
         * Constructs a new InvalidMessageTypeException with the specified detail message.
         *
         * @param s the detail message.
         */
        public InvalidMessageTypeException(String s) {
            super(s);
        }
    }

}
