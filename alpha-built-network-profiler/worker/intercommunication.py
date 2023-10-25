from common import Command, InterMessage, Measure, MeshMetadata, MeshMetadataCommand, construct_measure, divide_items_into_buckets
import time
from typing import List, Tuple


def _additional_process(mesh_metadata: MeshMetadata, command: Command) -> None:
    mesh_metadata.properties["testItem"] = command.test_item
    mesh_metadata.properties["sendTime"] = time.time()


def construct_messages_from_command(command: Command) -> List[InterMessage]:
    inter_messages: List[InterMessage] = []
    user_meshes_division: List[Tuple[int, List[int]]] = divide_items_into_buckets(command.user_ids, command.mesh_metadata_command.mesh_ids)
    for user_mesh in user_meshes_division:
        curr_user_message: InterMessage = InterMessage(command.message_type, user_mesh[0], command.project_id, [])
        for mesh_id in user_mesh[1]:
            curr_mesh_metadata = MeshMetadata(mesh_id, command.mesh_metadata_command.action_type, command.mesh_metadata_command.properties)
            _additional_process(curr_mesh_metadata, command)
            curr_user_message.meshMetaData.append(curr_mesh_metadata)
        inter_messages.append(curr_user_message)
    return inter_messages


def construct_measure_from_command(command: Command) -> Measure:
    """
    Each command received from master would create a local measure object, representing a local statistics.
    Note: The number of measures, for the sake of simplicity, is equal to the number of users.
    """
    return construct_measure(command.test_item, len(command.user_ids))
