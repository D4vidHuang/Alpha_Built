package com.ecadi.alphabuiltbackend.intercommunication;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;

@Data
public class MeshMetadata {
    private int meshId;
    private MeshAction meshAction;
    private ObjectNode properties;

    /**
     * Constructs a new instance of the MeshMetadata class.
     *
     * @param meshId     The ID of the mesh.
     * @param meshAction The action associated with the mesh.
     * @param properties The properties associated with the mesh.
     */
    public MeshMetadata(int meshId, MeshAction meshAction, ObjectNode properties) {
        this.meshId = meshId;
        this.meshAction = meshAction;
        this.properties = properties;
    }


    /**
     * Adds a verdict to the properties.
     *
     * @param verdict The verdict to add.
     */
    public void addVerdict(Boolean verdict) {
        properties.put("verdict", verdict);
    }


    /**
     * Checks if the properties contain a position property.
     *
     * @return true if the position property exists, false otherwise.
     */
    public boolean containsPropertyPosition() {
        return properties.get("position") != null;
    }

    /**
     * Checks if the properties contain a scaling property.
     *
     * @return true if the scaling property exists, false otherwise.
     */
    public boolean containsPropertyScaling() {
        return properties.get("scaling") != null;
    }

    /**
     * Checks if the properties contain a rotation property.
     *
     * @return true if the rotation property exists, false otherwise.
     */
    public boolean containsPropertyRotation() {
        return properties.get("rotation") != null;
    }


    /**
     * Checks if the properties contain the basic properties required (position, scaling, and rotation).
     *
     * @return true if all basic properties exist, false otherwise.
     */
    public boolean containsBasicProperties() {
        return containsPropertyPosition() && containsPropertyRotation() && containsPropertyScaling();
    }

    /**
     * Checks if the properties contain the mergeIndices properties required for mesh mergeing.
     *
     * @return true if the mergeIndices property exists, false otherwise.
     */
    public boolean containsMergedListProperty() {
        return properties.get("mergeIndices") != null;
    }

    /**
     * Checks if the properties contain the subtractIndices properties required for mesh subtracting.
     *
     * @return true if the subtractIndices property exists, false otherwise.
     */
    public boolean containsSubtractListProperty() {
        return properties.get("subtractIndices") != null;
    }

    /**
     * Checks if the properties contain the intersectIndices properties required for mesh intersection.
     *
     * @return true if the intersectIndices property exists, false otherwise.
     */
    public boolean containsIntersectListProperty() {
        return properties.get("intersectIndices") != null;
    }

    /**
     * Checks if the properties contain the unionIndices properties required for mesh subtracting.
     *
     * @return true if the unionIndices property exists, false otherwise.
     */
    public boolean containsUnionListProperty() {
        return properties.get("unionIndices") != null;
    }
}
