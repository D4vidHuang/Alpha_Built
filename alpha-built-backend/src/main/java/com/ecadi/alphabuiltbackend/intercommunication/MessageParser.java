package com.ecadi.alphabuiltbackend.intercommunication;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class MessageParser {
    private static final Logger logger = LoggerFactory.getLogger("Message Parser");

    /**
     * Parses a raw intercommunication message and constructs an InterMessage object.
     *
     * @param rawMessage The raw message to parse.
     * @return The parsed InterMessage object.
     */
    public static InterMessage parseInterMessage(JsonNode rawMessage) {
        Optional<JsonNode> rawMessageType = Optional.ofNullable(rawMessage.get("type"));
        if (rawMessageType.isEmpty()) {
            String errMessage = String.format("Message does not have type in message %s.", rawMessage);
            logger.error(errMessage);
            throw new InterMessageParsingException.UndefinedMessageTypeException(errMessage);
        }

        Optional<JsonNode> rawUserId = Optional.ofNullable(rawMessage.get("userId"));
        if (rawUserId.isEmpty()) {
            String errMessage = String.format("Message does not have user id in message %s.", rawMessage);
            logger.error(errMessage);
            throw new InterMessageParsingException.UndefinedUserIdException(errMessage);
        }

        Optional<JsonNode> rawProjectId = Optional.ofNullable(rawMessage.get("projectId"));
        if (rawProjectId.isEmpty()) {
            String errMessage = String.format("Message does have project id in message %s.", rawMessage);
            logger.error(errMessage);
            throw new InterMessageParsingException.UndefinedProjectIdException(errMessage);
        }

        Optional<JsonNode> rawMeshMetadata = Optional.ofNullable(rawMessage.get("meshMetaData"));
        if (rawMeshMetadata.isEmpty()) {
            String errMessage = String.format("Message does not have mesh metadata in message %s.", rawMessage);
            logger.error(errMessage);
            throw new InterMessageParsingException.UndefinedMeshMetadataException(errMessage);
        }
        JsonNode rawMeshMetadataList = rawMeshMetadata.get();
        if (!(rawMeshMetadataList instanceof ArrayNode)) {
            String errMessage = String.format("Message mesh metadatas are not of array type in message %s.", rawMessage);
            logger.error(errMessage);
            throw new InterMessageParsingException.UndefinedMeshMetadataException(errMessage);
        }

        InterMessageType interMessageType = getMessageType(rawMessageType.get().asText());

        int userId = rawUserId.get().asInt();
        int projectId = rawProjectId.get().asInt();

        ArrayNode meshMetadataList = (ArrayNode) rawMeshMetadataList;
        List<MeshMetadata> parsedMeshMetaDataList = parseMeshMetadataList(meshMetadataList);

        return new InterMessage(interMessageType, userId, projectId, parsedMeshMetaDataList);
    }


    /**
     * Parses a list of raw mesh metadata and constructs a list of MeshMetadata objects.
     *
     * @param rawMeshActionList The raw mesh action list to parse.
     * @return A list of parsed MeshMetadata objects.
     */
    public static List<MeshMetadata> parseMeshMetadataList(ArrayNode rawMeshActionList) {
        Function<JsonNode, MeshMetadata> parseMeshMeta = (JsonNode element) -> {
            Optional<JsonNode> rawMeshAction = Optional.ofNullable(element.get("meshAction"));
            if (rawMeshAction.isEmpty()) {
                String errMessage = String.format("Mesh metadata %s does not have action.", element);
                logger.error(errMessage);
                throw new InterMessageParsingException.UndefinedMeshMetadataException(errMessage);
            }
            MeshAction meshAction = getMeshAction(rawMeshAction.get().asText());

            Optional<JsonNode> rawMeshId = Optional.ofNullable(element.get("meshId"));
            if (rawMeshId.isEmpty()) {
                String errMessage = String.format("Mesh metadata %s does not have mesh id.", element);
                logger.error(errMessage);
                throw new InterMessageParsingException.UndefinedMeshMetadataException(errMessage);
            }
            int meshId = rawMeshId.get().asInt();

            Optional<JsonNode> rawMeshProperties = Optional.ofNullable(element.get("properties"));
            if (rawMeshProperties.isEmpty()) {
                String errMessage = String.format("Mesh metadata %s does not have mesh properties", element);
                logger.error(errMessage);
                throw new InterMessageParsingException.UndefinedMeshMetadataException(errMessage);
            }
            JsonNode meshProperties = rawMeshProperties.get();
            return new MeshMetadata(meshId, meshAction, (ObjectNode) meshProperties);
        };

        Stream<JsonNode> stream = StreamSupport
                .stream(
                        Spliterators
                                .spliteratorUnknownSize(rawMeshActionList.elements(), Spliterator.ORDERED),
                        false
                );
        return stream.map(parseMeshMeta).collect(Collectors.toList());
    }

    /**
     * Converts the name of a message type to its corresponding InterMessageType enum value.
     *
     * @param name The name of the message type.
     * @return The corresponding InterMessageType enum value.
     */
    private static InterMessageType getMessageType(String name) {
        switch (name) {
            case "HELLO":
                return InterMessageType.HELLO;
            case "BYE":
                return InterMessageType.BYE;
            case "GEO":
                return InterMessageType.GEO;
            default:
                String errMessage = String.format("Message type %s is undefined.", name);
                logger.error(errMessage);
                throw new InterMessageParsingException.UndefinedMessageTypeException(errMessage);
        }
    }

    /**
     * Converts the name of a mesh action to its corresponding MeshAction enum value.
     *
     * @param name The name of the mesh action.
     * @return The corresponding MeshAction enum value.
     */
    public static MeshAction getMeshAction(String name) {
        switch (name) {
            case "CREATE":
                return MeshAction.CREATE;
            case "TRANSLATE":
                return MeshAction.TRANSLATE;
            case "SCALE":
                return MeshAction.SCALE;
            case "ROTATE":
                return MeshAction.ROTATE;
            case "REMOVE":
                return MeshAction.REMOVE;
            case "ADD_MESH":
                return MeshAction.ADD_MESH;
            case "REVERT":
                return MeshAction.REVERT;
            case "REDO":
                return MeshAction.REDO;
            case "LOCK":
                return MeshAction.LOCK;
            case "UNLOCK":
                return MeshAction.UNLOCK;
            case "MERGE":
                return MeshAction.MERGE;
            case "SUBTRACT":
                return MeshAction.SUBTRACT;
            case "TRANSLATE_END":
                return MeshAction.TRANSLATE_END;
            case "INTERSECT":
                return MeshAction.INTERSECT;
            case "UNION":
                return MeshAction.UNION;
            default:
                String errMessage = String.format("Mesh action %s is undefined.", name);
                logger.error(errMessage);
                throw new InterMessageParsingException.UndefinedMeshMetadataException(errMessage);
        }
    }
}
