package com.ecadi.alphabuiltbackend.intercommunication;

/**
 * Enumeration of the types of inter-communication messages.
 */
public enum InterMessageType {
    // Client to server
    HELLO,
    BYE,

    GEO,
    // Server to client,
    HELLO_RESPONSE,
    BYE_RESPONSE,
    CTRLZ
}