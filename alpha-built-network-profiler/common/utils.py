from .datatype import Command, InterMessage, MeshMetadata, MeshMetadataCommand
from typing import Dict, List, NamedTuple, Tuple


def convert_namedtuple_to_dict(obj: NamedTuple):
    if isinstance(obj, tuple):
        return {field: convert_namedtuple_to_dict(value) for field, value in obj._asdict().items()}
    return obj


def _parse_dict_mesh_metadata_command(metadata_dict: Dict) -> MeshMetadataCommand:
    return MeshMetadataCommand(
        metadata_dict["mesh_ids"],
        metadata_dict["action_type"],
        metadata_dict["properties"]
    )


def parse_dict_to_command(command_dict: Dict) -> Command:
    return Command(
        command_dict["project_id"],
        command_dict["user_ids"],
        command_dict["message_type"],
        _parse_dict_mesh_metadata_command(command_dict["mesh_metadata_command"]),
        command_dict["test_item"],
        command_dict["num_users"]
    )


def _parse_dict_to_mesh_metadata(mesh_metadata_dict: Dict) -> MeshMetadata:
    return MeshMetadata(
        mesh_metadata_dict["meshId"],
        mesh_metadata_dict["meshAction"],
        mesh_metadata_dict["properties"]
    )


def parse_dict_to_inter_message(inter_message_dict: Dict) -> InterMessage:
    return InterMessage(
        inter_message_dict["type"],
        inter_message_dict["userId"],
        inter_message_dict["projectId"],
        list(map(_parse_dict_to_mesh_metadata, inter_message_dict["meshMetadata"]))
    )


def _pase_mesh_metadata_to_dict(mesh_meta: MeshMetadata) -> Dict:
    return {
        "meshId": mesh_meta.meshId,
        "meshAction": mesh_meta.meshAction,
        "properties": mesh_meta.properties
    }


def _parse_mesh_metadata_list_to_list(mesh_metadata_list: List[MeshMetadata]) -> List[Dict]:
    return list(map(_pase_mesh_metadata_to_dict, mesh_metadata_list))


def parse_inter_message_to_dict(inter_message: InterMessage) -> Dict:
    return {
        "type": inter_message.type,
        "userId": inter_message.userId,
        "projectId": inter_message.projectId,
        "meshMetaData": _parse_mesh_metadata_list_to_list(inter_message.meshMetaData)
    }


def divide_items_into_buckets(buckets: List[int], items: List[int]) -> List[Tuple[int, List[int]]]:
    bucket_len: int = len(buckets)
    item_len: int = len(items)
    assert bucket_len <= item_len
    step: int = item_len // bucket_len
    ranges: List[Tuple[int, int]] = [(idx * step, min((idx + 1) * step, item_len)) for idx in range(bucket_len)]
    item_sliced: List[List[int]] = list(map(lambda slice_: items[slice_[0]:slice_[1]], ranges))
    return list(zip(buckets, item_sliced))


def set_mesh_metadata_ids(mesh_metadata_command: MeshMetadataCommand, updated_mesh_ids: List[int]) -> MeshMetadataCommand:
    return MeshMetadataCommand(updated_mesh_ids, mesh_metadata_command.action_type, mesh_metadata_command.properties)