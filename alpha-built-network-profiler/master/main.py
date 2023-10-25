import argparse
import asyncio
import threading
from collections import namedtuple
from common import Command, MeshMetadataCommand, TaskName, convert_namedtuple_to_dict, divide_items_into_buckets, \
    set_mesh_metadata_ids
from data import data_analysis_process
import json
from multiprocessing import Process, Pipe
from multiprocessing.connection import PipeConnection
import numpy as np
import os
import socket
from task import create_mesh_task
from typing import Dict, List, NamedTuple, Tuple
import websockets

TEST_ITEM = str

Worker = namedtuple("Worker", ["address", "socket"])

_ADDRESS, _PORT = None, None
_MIN_NUM_USER = 2
_TASKS: Dict[TEST_ITEM, TaskName] = {}


def _print(typ, metadata, message):
    print(f"Main Process || {typ} | {metadata} | {message}\n")


def _get_args():
    parser = argparse.ArgumentParser()
    parser.add_argument('--address', type=str, default="localhost")
    parser.add_argument('--port', type=int, default=8000)
    args = parser.parse_args()
    return args


def _config(address, port):
    global _ADDRESS, _PORT
    _ADDRESS, _PORT = address, port


def _setup_tasks() -> None:
    for i in range(1, 2):
        _TASKS.update(create_mesh_task(i, 8, 64))


def _generate_commands_from_task(workers: List[Worker], task: TaskName) -> List[Tuple[Worker, Command]]:
    def _get_second_item(tuple_list: List[Tuple[int, List[int]]]) -> List[List[int]]:
        return list(map(lambda tuple_: tuple_[1], tuple_list))

    num_worker: int = len(workers)
    num_user: int = task.num_users
    worker_numbers: List[int] = list(range(num_worker))
    user_numbers: List[int] = list(range(num_user))
    mesh_ids: List[int] = task.mesh_metadata_command.mesh_ids
    worker_user_division: List[Tuple[int, List[int]]] = divide_items_into_buckets(worker_numbers, user_numbers)
    worker_mesh_division: List[Tuple[int, List[int]]] = divide_items_into_buckets(worker_numbers, mesh_ids)
    user_mesh_pair: List[Tuple[List[int], List[int]]] = list(
        zip(_get_second_item(worker_user_division), _get_second_item(worker_mesh_division)))
    command_list: List[Command] = list(
        map(
            lambda tuple_: Command(task.project_id, tuple_[0], task.message_type,
                                   set_mesh_metadata_ids(task.mesh_metadata_command, tuple_[1]), task.test_item,
                                   task.num_users),
            user_mesh_pair
        )
    )
    assert len(command_list) == len(workers)
    return list(zip(workers, command_list))


def _construct_server(data_analysis_process_, parent_conn):
    workers: List[Worker] = []
    server_lock: threading.RLock = threading.RLock()

    async def _send_command_to_worker_(worker: Worker, command: Command):
        _print("SEND COMMAND", f"COMMAND IS {str(command)}", f"The current worker is {str(worker)}.")
        await worker.socket.send(json.dumps(convert_namedtuple_to_dict(command)))

    async def _send_commands_to_workers(curr_workers: List[Worker]):
        _print("BEGIN SENDING COMMANDS", f"NUMBER OF WORKERS {len(workers)}", "Sending commands to workers.")
        for task_name, task in _TASKS.items():
            _print("PROCESS TASK", f"TASK NAME IS {task}", str(task))
            curr_worker_commands: List[Tuple[Worker, Command]] = _generate_commands_from_task(curr_workers, task)
            await asyncio.gather(
                *[_send_command_to_worker_(curr_worker_command[0], curr_worker_command[1]) for curr_worker_command in
                  curr_worker_commands])

    async def _perform_action(websocket, message):
        parent_conn.send(message)
        pass

    async def _handle_message(websocket, message):
        _print("RECEIVED MESSAGE", websocket.remote_address, message)
        await _perform_action(websocket, message)

    async def _handle_client(websocket, path):
        _print("NEW CONNECTION", websocket.remote_address, "New connection has established.")
        with server_lock:
            workers.append(Worker(websocket.remote_address, websocket))
            if len(workers) >= _MIN_NUM_USER:
                await _send_commands_to_workers(workers)

        try:
            async for message in websocket:
                await _handle_message(websocket, message)
        finally:
            _print("CONNECTION CLOSED", websocket.remote_address, "Connection has closed.")

    async def _command_worker(worker: Worker, command: Command):
        _print("COMMAND WORKER", worker.address, command)
        worker.socket.send(command)

    async def _setup_server():
        _print("INFRASTRUCTURE", _ADDRESS, "The master Websocket server is setting up.")
        start_server = await websockets.serve(_handle_client, _ADDRESS, _PORT, family=socket.AF_INET)
        # tasks = [asyncio.gather(*[_command_worker(worker, curr_command) for worker in workers]) for curr_command in _COMMANDS]
        # await asyncio.gather(*tasks)
        await start_server.wait_closed()

    try:
        asyncio.run(_setup_server())
    except KeyboardInterrupt:
        pass


def _construct_data_analysis_process() -> (Process, PipeConnection):
    parent_conn, child_conn = Pipe()
    data_analysis_process_ = Process(target=data_analysis_process, args=(child_conn,))
    data_analysis_process_.start()
    return data_analysis_process_, parent_conn


def main() -> None:
    args = _get_args()
    _config(args.address, args.port)
    _setup_tasks()
    data_analysis_process_, parent_conn = _construct_data_analysis_process()
    _construct_server(data_analysis_process_, parent_conn)


if __name__ == "__main__":
    _print("CONFIG", "MASTER", os.getcwd())
    main()
