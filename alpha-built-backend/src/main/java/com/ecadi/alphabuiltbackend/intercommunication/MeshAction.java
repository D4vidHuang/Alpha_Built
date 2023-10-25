package com.ecadi.alphabuiltbackend.intercommunication;

/**
 * Enumeration of the types of actions that can be performed on a Mesh.
 */
public enum MeshAction {
    CREATE,           // Create a new Mesh
    TRANSLATE,        // Translate the Mesh
    SCALE,            // Scale the Mesh
    ROTATE,         // Rotate the Mesh
    ADD_MESH,         // Add an existing Mesh
    INITIALISE_MESH,  // Initialise a new Mesh
    REVERT,
    REDO,
    REMOVE,           // Remove an existing Mesh
    LOCK,
    UNLOCK,
    MERGE,
    SUBTRACT,
    TRANSLATE_END,
    INTERSECT,
    UNION,


}
