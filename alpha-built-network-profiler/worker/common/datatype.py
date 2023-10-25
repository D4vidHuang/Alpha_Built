import threading
from typing import Dict, List, NamedTuple

"""
Class MeshMetadataCommand:
* Fields
    - Mesh Ids: range
    - Action Type: string
    - Properties: Dict
"""
MeshMetadataCommand = NamedTuple(
    "MeshMetadataCommand", [
        ("mesh_ids", List[int]),
        ("action_type", str),
        ("properties", Dict)
    ]
)


"""
Class Task:
* Fields:
    - Project Id: int
    - Number of users: int
    - Message Type: string
    - Mesh Metadata Command: MeshMetadataCommand
* Generate commands.
"""
TaskName = NamedTuple(
    "Task", [
        ("project_id", int),
        ("message_type", str),
        ("mesh_metadata_command", MeshMetadataCommand),
        ("test_item", str),
        ("num_users", int)
    ]
)


"""
Class Command: 
* Fields:
    - Project Id: int
    - User Ids: List[int]
    - Message Type: string
    - Mesh Metadata Command: MeshMetadataCommand
* From master to worker: Indicate a command
"""
Command = NamedTuple(
    "Command", [
        ("project_id", int),
        ("user_ids", List[int]),
        ("message_type", str),
        ("mesh_metadata_command", MeshMetadataCommand),
        ("test_item", str),
        ("num_users", int)
    ]
)


"""
Class MeshMetadata
"""
MeshMetadata = NamedTuple(
    "MeshMetadata", [
        ("meshId", int),
        ("meshAction", str),
        ("properties", Dict)
    ]
)


"""
Class InterMessage: 
* From worker to server
"""
InterMessage = NamedTuple(
    "InterMessage", [
        ("type", str),
        ("userId", int),
        ("projectId", int),
        ("meshMetaData", List[MeshMetadata])
    ]
)


"""
Class Measure:
* Fields:
    - Test Item: string
    - Number of messages: int
    - Sum of RTT (Round Trip Time): int
    - Sum of square of RTT: int
    - Total messages: int 
    - Lock: Lock (For thread safe purpose)
* From worker to master: Represents a measurement of a test item
* For the sake of simplicity: for one worker, one command is corresponding to a measure 
"""
Measure = NamedTuple(
    "Measure", [
        ("test_item", str),
        ("num_message", int),
        ("sum_rtt", float),
        ("sum_sqr_rtt", float),
        ("total_message", int),
        ("lock", threading.Lock)
    ]
)


MeasureNetwork = NamedTuple(
    "MeasureNetwork", [
        ("test_item", str),
        ("num_message", int),
        ("sum_rtt", float),
        ("sum_sqr_rtt", float),
        ("total_message", int),
    ]
)


def construct_measure(test_item: str, total_message: int) -> Measure:
    return Measure(test_item, 0, 0, 0, total_message, threading.Lock())


def construct_measure_network(test_item: str, total_message: int) -> MeasureNetwork:
    return MeasureNetwork(test_item, 0, 0, 0, total_message)


def _update_measure_worker_threadsafe(measure: Measure, add_num_message: int, add_sum_rtt: float, add_sum_sqr_rtt: float) -> Measure:
    with measure.lock:
        return Measure(
            measure.test_item,
            measure.num_message + add_num_message,
            measure.sum_rtt + add_sum_rtt,
            measure.sum_sqr_rtt + add_sum_sqr_rtt,
            measure.total_message,
            measure.lock
        )


def update_measure_worker(measure: Measure, add_num_message: int, add_sum_rtt: float, add_sum_sqr_rtt: float) -> Measure:
    return _update_measure_worker_threadsafe(measure, add_num_message, add_sum_rtt, add_sum_sqr_rtt)


def update_measure_master(measure: MeasureNetwork, add_measure: MeasureNetwork) -> MeasureNetwork:
    """
    Update the measure object in master data analysis process (always thread safe)
    :param measure: The measure object at data analysis process
    :param add_measure: The measure object received from worker, representing a partial statistics.
    :return: None
    """
    return MeasureNetwork(
        measure.test_item,
        measure.num_message + add_measure.num_message,
        measure.sum_rtt + add_measure.sum_rtt,
        measure.sum_sqr_rtt + add_measure.sum_sqr_rtt,
        measure.total_message,
    )


def convert_measure_to_measure_network(measure: Measure) -> MeasureNetwork:
    return MeasureNetwork(
        measure.test_item,
        measure.num_message,
        measure.sum_rtt,
        measure.sum_sqr_rtt,
        measure.total_message
    )


def empty_measure(measure: Measure) -> Measure:
    return Measure(
        measure.test_item,
        0,
        0,
        0,
        measure.total_message,
        threading.Lock()
    )


def empty_measure_network(measure_network: MeasureNetwork) -> MeasureNetwork:
    return MeasureNetwork(
        measure_network.test_item,
        0,
        0,
        0,
        measure_network.total_message
    )
