from common import MeshMetadataCommand, TaskName
from typing import Dict, List, Union

TEST_ITEM = str


def create_mesh_task(idx: int, num_user: int, mesh_list: Union[int, List[int]]) -> Dict[TEST_ITEM, TaskName]:
    if type(mesh_list) == int:
        mesh_list = list(range(mesh_list))
    test_name_hello: TEST_ITEM = f"HELLO_TEST_{idx}"
    test_name_add_mesh: TEST_ITEM = f"ADD_MESH_TEST_{idx}"

    return {
        test_name_hello: TaskName(idx, "HELLO", MeshMetadataCommand(mesh_list, "ADD_MESH", {"position": [1, 1, 1], "rotation": [0, 0, 0], "scaling": [1, 1, 1]}), test_name_hello, num_user),
        test_name_add_mesh: TaskName(idx, "GEO", MeshMetadataCommand(mesh_list, "ADD_MESH", {"position": [1, 1, 1], "rotation": [0, 0, 0], "scaling": [1, 1, 1]}), test_name_add_mesh, num_user)
    }
