import argparse
import asyncio
import os
from common import Command, InterMessage, Measure, MeasureNetwork, MeshMetadata, MeshMetadataCommand, construct_measure, convert_measure_to_measure_network, empty_measure, parse_dict_to_command, parse_dict_to_inter_message, parse_inter_message_to_dict, update_measure_worker
from copy import deepcopy
from intercommunication import construct_messages_from_command
import json
import numpy as np
import time
import threading
from typing import Dict, List, Set
import websocket
import websockets

_LOCAL_ADDRESS, _LOCAL_PORT, _MASTER_ADDRESS, _MASTER_PORT, _TARGET_ADDRESS, _TARGET_PORT = None, None, None, None, None, None
_QUEUE_MASTER_TO_TARGET: asyncio.Queue = asyncio.Queue()
_QUEUE_TARGET_TO_MASTER: asyncio.Queue = asyncio.Queue()
_LOOP = asyncio.get_event_loop()


def _print(server, typ, message):
    print(f"{server} | {typ} : {message}\n")


def _get_args():
    parser = argparse.ArgumentParser()
    parser.add_argument('--local_address', type=str, default="localhost")
    parser.add_argument('--local_port', type=int, default=8080)
    parser.add_argument('--master_address', type=str, default="localhost")
    parser.add_argument('--master_port', type=int, default=8000)
    parser.add_argument('--target_address', type=str, default="localhost")
    parser.add_argument('--target_port', type=int, default=8888)
    args = parser.parse_args()
    return args


def _config(local_address, local_port, master_address, master_port, target_address, target_port):
    global _LOCAL_ADDRESS, _LOCAL_PORT, _MASTER_ADDRESS, _MASTER_PORT, _TARGET_ADDRESS, _TARGET_PORT
    _LOCAL_ADDRESS, _LOCAL_PORT, _MASTER_ADDRESS, _MASTER_PORT, _TARGET_ADDRESS, _TARGET_PORT = local_address, local_port, master_address, master_port, target_address, target_port


def _construct_client_master():

    async def _construct_client_master_():
        async with websockets.connect(f"ws://{_MASTER_ADDRESS}:{_MASTER_PORT}") as ws:

            async def _monitor_command_from_master_coroutine():
                while True:
                    message_ = await ws.recv()
                    _print("MASTER", "ON MESSAGE", message_)
                    command: Command = parse_dict_to_command(json.loads(message_))
                    await _QUEUE_MASTER_TO_TARGET.put(command)

            async def _monitor_measure_from_target_coroutine():
                while True:
                    curr_response: MeasureNetwork = await _QUEUE_TARGET_TO_MASTER.get()
                    _print("MASTER", "RECEIVE MESSAGE FROM TARGET THREAD", curr_response)
                    await ws.send(json.dumps(curr_response._asdict()))
                    _QUEUE_TARGET_TO_MASTER.task_done()

            _print("MASTER", "ON OPEN", "Websocket connection to the master has established.")

            await asyncio.gather(
                _monitor_command_from_master_coroutine(),
                _monitor_measure_from_target_coroutine()
            )

    # Run the main coroutine
    _LOOP.create_task(_construct_client_master_())


def _construct_client_target():
    async def _construct_client_target_():
        ignored_items: Set[str] = {"HELLO_TEST_1", "HELLO_TEST_2"}
        measurements: Dict[str, Measure] = {}

        def _get_inter_message_rtt(inter_message: InterMessage) -> float:
            mesh_metadata: List[MeshMetadata] = inter_message.meshMetaData
            rrts: List[float] = list(map(lambda meta: time.time() - meta.properties["sendTime"], mesh_metadata))
            return np.mean(np.array(rrts))

        def _get_inter_message_test_item(inter_message: InterMessage) -> str:
            return inter_message.meshMetaData[0].properties["testItem"]

        async with websockets.connect(f"ws://{_TARGET_ADDRESS}:{_TARGET_PORT}") as ws:

            async def _perform_action_target(message):
                message_dict: Dict = json.loads(message)
                inter_message: InterMessage = parse_dict_to_inter_message(message_dict)
                if inter_message.type != "HELLO_RESPONSE":
                    curr_test_item: str = _get_inter_message_test_item(inter_message)
                    curr_rtt: float = _get_inter_message_rtt(inter_message)
                    measurements[curr_test_item] = update_measure_worker(measurements[curr_test_item], 1, curr_rtt, curr_rtt ** 2)
                    if measurements[curr_test_item].num_message % 4 == 0:
                        measure_copy_network: MeasureNetwork = convert_measure_to_measure_network(measurements[curr_test_item])
                        await _QUEUE_TARGET_TO_MASTER.put(measure_copy_network)
                        measurements[curr_test_item] = empty_measure(measurements[curr_test_item])

            async def _monitor_response_from_target_coroutine():
                while True:
                    message_ = await ws.recv()
                    _print("TARGET", "ON MESSAGE", message_)
                    await _perform_action_target(message_)

            async def _monitor_command_from_master_coroutine():
                while True:
                    curr_command: Command = await _QUEUE_MASTER_TO_TARGET.get()
                    _print("TARGET", "RECEIVE FROM THREAD MASTER", curr_command)
                    inter_messages: List[InterMessage] = construct_messages_from_command(curr_command)
                    if curr_command.test_item not in ignored_items:
                        measurements[curr_command.test_item] = construct_measure(curr_command.test_item, len(inter_messages) * curr_command.num_users)
                        _print("TARGET", "CONSTRUCT MEASURE", measurements[curr_command.test_item])
                    for inter_message in inter_messages:
                        _print("TARGET", "SEND INTER-MESSAGE", inter_message)
                        await ws.send(json.dumps(parse_inter_message_to_dict(inter_message)))
                    _QUEUE_MASTER_TO_TARGET.task_done()

            _print("TARGET", "ON OPEN", "Websocket connection to the target has established.")
            await asyncio.gather(
                _monitor_command_from_master_coroutine(),
                _monitor_response_from_target_coroutine(),
            )

    _LOOP.create_task(_construct_client_target_())


def main() -> None:
    args = _get_args()
    _config(args.local_address, args.local_port, args.master_address, args.master_port, args.target_address, args.target_port)
    master_thread = threading.Thread(target=_construct_client_master)
    target_thread = threading.Thread(target=_construct_client_target)
    master_thread.start()
    target_thread.start()
    master_thread.join()
    target_thread.join()
    _LOOP.run_forever()


if __name__ == "__main__":
    _print("MAIN", "WORKING DIRECTORY", os.getcwd())
    main()
