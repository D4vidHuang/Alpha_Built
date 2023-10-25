import json

import numpy as np

from common import Measure, MeasureNetwork, construct_measure_network, empty_measure_network, update_measure_master
from multiprocessing.connection import PipeConnection
from typing import Dict
import os


_MEASURES_DIR = "measures"


def _print(typ, metadata, message):
    print(f"Data Process || {typ} | {metadata} | {message}")


def data_analysis_process(conn: PipeConnection):
    measures: Dict[str, MeasureNetwork] = {
        "ADD_MESH_TEST_1": construct_measure_network("ADD_MESH_TEST_1", 16),
        "ADD_MESH_TEST_2": construct_measure_network("ADD_MESH_TEST_2", 16),
        "ADD_MESH_TEST_3": construct_measure_network("ADD_MESH_TEST_3", 16)
    }

    def _create_file(file_name: str) -> str:
        if not os.path.exists(_MEASURES_DIR):
            os.mkdir(_MEASURES_DIR)
        return os.path.join(_MEASURES_DIR, file_name)

    def _save_measure_to_file(file_path: str, test_item: str, num_message: int, mean: float, std: float) -> None:
        with open(file_path, "a") as curr_file:
            _print("WRITING RESULT TO FILE", file_path, f"Writing to file {file_path} with mean {mean} and std {std}")
            curr_file.write(f"""
            TEST {test_item} | Number of messages {num_message}:
                - The mean round trip time is {mean}.
                - The std round trip time is {std}.
            """)

    def _final_processing(measure: MeasureNetwork) -> None:
        def _mean() -> float:
            return measure.sum_rtt / measure.num_message

        def _std() -> float:
            return np.sqrt(measure.sum_sqr_rtt / measure.num_message - (measure.sum_rtt / measure.num_message) ** 2)

        file_path: str = _create_file(measure.test_item)
        curr_mean: float = _mean()
        curr_std: float = _std()
        _save_measure_to_file(file_path, measure.test_item, measure.num_message, curr_mean, curr_std)

    def _compute(add_measure: MeasureNetwork) -> None:
        measures[add_measure.test_item] = update_measure_master(measures[add_measure.test_item], add_measure)
        _print("AFTER COMPUTATION", f"Add measure is {add_measure}", f"Result measure is {measures[add_measure.test_item]}")
        if measures[add_measure.test_item].num_message % 8 == 0:
            _print("FINAL PROCESSING", f"The test item is {measures[add_measure.test_item].test_item}", str(measures[add_measure.test_item]))
            _final_processing(measures[add_measure.test_item])
            measures[add_measure.test_item] = empty_measure_network(measures[add_measure.test_item])

    while True:
        message = conn.recv()
        _print("RECEIVE MESSAGE", None, message)
        curr_measure: MeasureNetwork = MeasureNetwork(**json.loads(message))
        _compute(curr_measure)
