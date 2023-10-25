function constructMessage(messageType, userId, projectId, meshMetadataList) {
    return {
        type: messageType,
        userId: userId,
        projectId: projectId,
        meshMetaData: meshMetadataList
    }
}

export {constructMessage}
