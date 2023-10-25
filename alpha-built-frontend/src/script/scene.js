import * as BABYLON from "@babylonjs/core"
import * as GUI from "@babylonjs/gui"
import {GLTF2Export} from '@babylonjs/serializers';
import "@babylonjs/loaders/glTF";
import {constructSocket} from "@/script/socket";
import {constructMessage} from "@/script/message"

// import { CubeMapToSphericalPolynomialTools } from "babylonjs";

const PROJECT_URL = "ws://localhost:8888"


const geoProperties = ["position", "scaling", "rotation"]

// the following variables are for picking up multiple meshes which is the foundation for mesh merging and intersection/union
let allowMultiplePickUp = false
// the list contains all meshes that picked up
let pickedList = []
let positionChanged = false

class BasicScene {
    user_id
    project_id
    canvas
    engine
    socket
    scene
    meshes
    meshToId
    meshIdx
    gui
    gizmoManager
    singlePlayer
    /**
     * Constructor for the BasicScene class.
     *
     * @param user_id The user id of the current user.
     * @param projectId The id of the project the user linked with.
     * @param canvas The canvas the local project relays on.
     */
    constructor(user_id, projectId, canvas, singlePlayer) {
        this.singlePlayer = singlePlayer
        if (!singlePlayer) {
            this.user_id = user_id
            this.project_id = projectId
            this.canvas = canvas
            this.engine = new BABYLON.Engine(canvas, true)
            this.socket = constructSocket(PROJECT_URL, this.user_id, this.project_id)
            this.meshes = {}
            this.meshToId = new Map()
            this.meshIdx = 0
            this.setupSocket()
        } else {
            this.user_id = user_id
            this.project_id = projectId
            this.canvas = canvas
            this.engine = new BABYLON.Engine(canvas, true)
            this.meshes = {}
            this.meshToId = new Map()
            this.meshIdx = 0
            this.createScene()
            this.engine.runRenderLoop(() => {
                this.scene.render()
            })
            window.addEventListener("beforeunload", () => {
                for (let m in this.meshes) {
                    this.meshes[m].dispose();
                }
            });
        }
    }

    /**
     * Set up websocket communication between front-end and back-end.
     * Different event listeners are defined to manage websocket behavior.
     */
    setupSocket() {
        this.socket.addEventListener('open', () => {
            console.log("The socket connection is open.")
            this.sendMessageToServer("HELLO", [])
            this.createScene()
            this.engine.runRenderLoop(() => {
                this.scene.render()
            })
            window.addEventListener("beforeunload", () => {
                const byeMessage = constructMessage("BYE", this.user_id, this.project_id, [])
                this.socket.send(JSON.stringify(byeMessage))
            })
            //socket.send("Websocket connection created by " + userId + " for project " + project_id);
        });
        this.socket.addEventListener('message', this.receiveMessageFromServer.bind(this));
        this.socket.addEventListener('close', () => {
            console.log('WebSocket connection is closed', event.code, event.reason);
        });
    }

    /**
     * Send action messages to server with action type and mesh metadata.
     *
     * @param type The message type.
     * @param meshMetadataList The mesh metadata followed the message.
     */
    sendMessageToServer(type, meshMetadataList) {
        if (!this.singlePlayer) {
            const message = constructMessage(type, this.user_id, this.project_id, meshMetadataList)
            this.socket.send(JSON.stringify(message))
        }
    }

    /**
     * Parsing data received from server.
     *
     * @param event The data from server.
     */
    receiveMessageFromServer(event) {
        let data = event.data;
        const message = JSON.parse(data)
        console.log('Received message:', message);
        this.parseMessage(message)
    }

    /**
     * Parsing message received from server according to the message type.
     * 'Hello_response" and 'GEO' are legal message types with defined reactions.
     *
     * @param message The message received from server.
     */
    parseMessage(message) {
        console.log("The message received is ", message)
        switch (message["type"]) {
            case "HELLO_RESPONSE":
                console.log("The server has responded to the hello message");
                message["meshMetadata"].forEach(metaData => this.parseMeshMetadata(message, metaData))
                return
            case "GEO":
                message["meshMetadata"].forEach(metaData => this.parseMeshMetadata(message, metaData))
                return
        }
    }

    /**
     * Parsing an array to babylon vector.
     *
     * @param arr The arr requires to reformat.
     * @return BABYLON Vector The new generated babylon vector.
     */
    arrayToVector(arr) {
        if (arr.length === 3) {
            return new BABYLON.Vector3(arr[0], arr[1], arr[2])
        }
    }

    /**
     * Parsing and processing data received from server.
     * Different actions will be applied to meshes or scene according to their action type.
     *
     * @param message The message received from server.
     * @param meshMetadata The mesh metadata received from server which is also contained in the message.
     */
    parseMeshMetadata(message, meshMetadata) {
        if (!meshMetadata["properties"]["verdict"]) {
            console.log("The current mesh metadata is invalid.")
            return
        }

        // the REVERT, REDO, MERGE actions should not be processed by the requesting user again.
        if (message["userId"] === this.user_id && message["type"] === "GEO" && meshMetadata["meshAction"] !== "REVERT" && meshMetadata["meshAction"] !== "REDO"
            && meshMetadata["_meshAction"] !== "MERGE" && meshMetadata["_meshAction"] !== "SUBTRACT"
            && meshMetadata["_meshAction"] !== "INTERSECT" && meshMetadata["_meshAction"] !== "UNION") {
            console.log("Received the message sent by myself, discarding ...")
            return
        }
        console.log(meshMetadata["meshAction"])
        switch (meshMetadata["meshAction"]) {
            case "TRANSLATE":
                const targetMesh1 = this.meshes[meshMetadata["meshId"]]
                const positionArray = meshMetadata["properties"]["position"]
                targetMesh1.position = new BABYLON.Vector3(positionArray[0], positionArray[1], positionArray[2])
                return
            case "TRANSLATE_END":
                const targetMesh1End = this.meshes[meshMetadata["meshId"]]
                const positionArrayEnd = meshMetadata["properties"]["position"]
                targetMesh1End.position = new BABYLON.Vector3(positionArrayEnd[0], positionArrayEnd[1], positionArrayEnd[2])
                return
            case "SCALE":
                const targetMesh2 = this.meshes[meshMetadata["meshId"]]
                const scalingArray = meshMetadata["properties"]["scaling"]
                targetMesh2.scaling = new BABYLON.Vector3(scalingArray[0], scalingArray[1], scalingArray[2])
                return
            case "ROTATE":
                const targetMesh3 = this.meshes[meshMetadata["meshId"]]
                const rotationArray = meshMetadata["properties"]["rotation"]
                targetMesh3.rotationQuaternion = new BABYLON.Quaternion(rotationArray[1], rotationArray[2], rotationArray[3], rotationArray[0])
                return
            case "ADD_MESH":
                console.log(this.meshes[meshMetadata["meshId"]])
                if (this.meshes[meshMetadata["meshId"]] === undefined) this.addMesh_(meshMetadata["properties"]["meshType"])
                return
            case "INITIALISE_MESH":
                console.log("Initialising")
                const initialMeshMetadata = {
                    position: meshMetadata["properties"]["position"],
                    scaling: meshMetadata["properties"]["scaling"],
                    rotation: meshMetadata["properties"]["rotation"]
                }
                console.log(initialMeshMetadata)
                this.addMesh_(meshMetadata["properties"]["meshType"], initialMeshMetadata)
                return
            case "MERGE":
                const meshReadyMerged = meshMetadata["properties"]["mergeIndices"]
                // let mergedMeshes = []
                // meshReadyMerged.forEach( function (meshReadyIndex) {
                //     mergedMeshes.push(this.meshes[meshReadyIndex])
                // })
                let mergedMeshes = []
                for(let i = 0;i<meshReadyMerged.length;i++) {
                    mergedMeshes.push(this.meshes[meshReadyMerged[i]])
                }
                const newMesh = BABYLON.Mesh.MergeMeshes(mergedMeshes);
                this.registerMesh(newMesh)
                // this.meshes[meshMetadata["meshId"]] = newMesh
                console.log("merged")
                return
            case "SUBTRACT":
                const meshReadySubtract = meshMetadata["properties"]["subtractIndices"]
                // let mergedMeshes = []
                // meshReadyMerged.forEach( function (meshReadyIndex) {
                //     mergedMeshes.push(this.meshes[meshReadyIndex])
                // })
                let mesh1 = this.meshes[meshReadySubtract[0]]
                let mesh2 = this.meshes[meshReadySubtract[1]]
                let meshCSG1 = BABYLON.CSG.FromMesh(mesh1);
                let meshCSG2 = BABYLON.CSG.FromMesh(mesh2);

                let booleanCSG = meshCSG1.subtract(meshCSG2);

                let newMeshSubtract = booleanCSG.toMesh("newMesh", null, this.scene);
                this.registerMesh(newMeshSubtract)
                // this.meshes[meshMetadata["meshId"]] = newMesh
                console.log("subtracted")
                return
            case "INTERSECT":
                const meshReadyIntersect = meshMetadata["properties"]["intersectIndices"]
                // let mergedMeshes = []
                // meshReadyMerged.forEach( function (meshReadyIndex) {
                //     mergedMeshes.push(this.meshes[meshReadyIndex])
                // })
                let mesh1Intersect = this.meshes[meshReadyIntersect[0]]
                let mesh2Intersect = this.meshes[meshReadyIntersect[1]]
                let meshCSG1Intersect = BABYLON.CSG.FromMesh(mesh1Intersect);
                let meshCSG2Intersect = BABYLON.CSG.FromMesh(mesh2Intersect);

                let booleanCSGIntersect = meshCSG1Intersect.intersect(meshCSG2Intersect);

                let newMeshIntersect = booleanCSGIntersect.toMesh("newMesh", null, this.scene);
                this.registerMesh(newMeshIntersect)
                // this.meshes[meshMetadata["meshId"]] = newMesh
                console.log("Intersected")
                return
            case "UNION":
                const meshReadyUnion = meshMetadata["properties"]["unionIndices"]
                // let mergedMeshes = []
                // meshReadyMerged.forEach( function (meshReadyIndex) {
                //     mergedMeshes.push(this.meshes[meshReadyIndex])
                // })
                let mesh1Union = this.meshes[meshReadyUnion[0]]
                let mesh2Union = this.meshes[meshReadyUnion[1]]
                let meshCSG1Union = BABYLON.CSG.FromMesh(mesh1Union);
                let meshCSG2Union = BABYLON.CSG.FromMesh(mesh2Union);

                let booleanCSGUnion = meshCSG1Union.union(meshCSG2Union);

                let newMeshUnion = booleanCSGUnion.toMesh("newMesh", null, this.scene);
                this.registerMesh(newMeshUnion)
                // this.meshes[meshMetadata["meshId"]] = newMesh
                console.log("Union")
                return
            case "REVERT":
                console.log("REVERT Received")
                const targetMesh4 = this.meshes[meshMetadata["meshId"]]
                console.log(meshMetadata)
                const positionData = meshMetadata["properties"]["position"]
                const scalingData = meshMetadata["properties"]["scaling"]
                const rotationData = meshMetadata["properties"]["rotation"]
                targetMesh4.position = new BABYLON.Vector3(positionData[0],positionData[1],positionData[2])
                targetMesh4.scaling = new BABYLON.Vector3(scalingData[0], scalingData[1], scalingData[2])
                targetMesh4.rotationQuaternion = new BABYLON.Quaternion(rotationData[1], rotationData[2], rotationData[3], rotationData[0])
                return
            case "REDO":
                console.log("REDO received")
                const targetMesh5 = this.meshes[meshMetadata["meshId"]]
                console.log(meshMetadata)
                const positionData2 = meshMetadata["properties"]["position"]
                const scalingData2 = meshMetadata["properties"]["scaling"]
                const rotationData2 = meshMetadata["properties"]["rotation"]
                targetMesh5.position = new BABYLON.Vector3(positionData2[0],positionData2[1],positionData2[2])
                targetMesh5.scaling = new BABYLON.Vector3(scalingData2[0], scalingData2[1], scalingData2[2])
                targetMesh5.rotationQuaternion = new BABYLON.Quaternion(rotationData2[1], rotationData2[2], rotationData2[3], rotationData2[0])
                return
            case "LOCK":
                console.log("Locking")
                if(meshMetadata["userId"] != this.user_id) {
                    const lockMesh = this.meshes[meshMetadata["meshId"]]
                    lockMesh.isPickable = !meshMetadata["properties"]["isLock"]
                    console.log("mesh lOCKED")
                }

                // console.assert(lockMesh["locked"] !== undefined)
                // lockMesh.locked = true
                return
            case "UNLOCK":
                console.log("UnLocking")
                if(meshMetadata["userId"] != this.user_id) {
                    const lockMesh = this.meshes[meshMetadata["meshId"]]
                    lockMesh.isPickable = !meshMetadata["properties"]["isLock"]
                    console.log("mesh UNlOCKED")
                }
                // const unlockMesh = this.meshes[meshMetadata["meshId"]]
                // console.assert(unlockMesh["locked"] !== undefined)
                // geoProperties.forEach((property) => {
                //     console.log(property)
                //     unlockMesh[property] = this.arrayToVector(meshMetadata["properties"][property])
                // })
                // unlockMesh.locked = false
        }
    }

    /**
     * Register the mesh by assigning unique meshId bounding to mesh itself.
     *
     * @param mesh The mesh requires to register.
     */
    registerMesh(mesh) {
        this.meshes[this.meshIdx] = mesh
        this.meshToId.set(mesh, this.meshIdx)
        this.meshIdx++;
    }

    /**
     * Change the state flag of whether enable the multiple picking up mode.
     * Click once for enable, twice for disable.
     * If disabled, the already picked meshes will be cleaned.
     * If disables, the gizmoManger will also dispose all attached meshes.
     */
    allowMultiplePickUp() {
        if(allowMultiplePickUp) {
            console.log("multiple pick up disable")
            allowMultiplePickUp = !allowMultiplePickUp
            pickedList = []
            this.gizmoManager.attachableMeshes = []
        } else {
            allowMultiplePickUp = !allowMultiplePickUp
            console.log("multiple pick up enable")
        }


    }

    /**
     * Generate a new scene for the project with world axes, camera, light, gizmoManager.
     * Mesh pick up checking is set to find the selected mesh once the mouse clicks on a mesh or select nothing if clicking on the background.
     * Once a mesh is selected, it will be locked by the selected user and refuse actions from other users.
     * The mesh is unlocked after the same user clicks on the background.
     * Lock/unlock states will be immediately sent to back-end server to keep consistency.
     */
    createScene() {
        this.scene = new BABYLON.Scene(this.engine)
        this.scene.clearColor = new BABYLON.Color3(0.9, 0.9, 0.9)
        this.createWorldAxes(this.scene)
        const camera = this.createCamera(this.scene)
        const light = this.createLight(this.scene)
        addButtonEventListener(this)
        this.gizmoManager = this.createGizmoManager(this.scene)

        // pick up checking when selecting meshes and lock/unlock meshes
        this.scene.onPointerObservable.add((pointerInfo) => {
            switch (pointerInfo.type) {
                case BABYLON.PointerEventTypes.POINTERDOWN:
                    // have to calculate the PickingInfo manually because the active camera is defined as the last camera in the .activeCameras list by Babylon.js
                    const pickingInfo = this.scene.pick(this.scene.pointerX, this.scene.pointerY, undefined, false, this.scene.cameras[this.checkClickedCamera()]);
                    // check whether enable multiple picking up mode
                    if(!allowMultiplePickUp) {
                        if(pickingInfo.hit) {
                            if(pickingInfo.pickedMesh.isPickable) {

                                console.log("check isPickable");
                                const lockProperty = {isLock: pickingInfo.pickedMesh.isPickable}
                                const lockMeshMetadata = this.constructMeshMetadata("LOCK", this.meshToId.get(pickingInfo.pickedMesh), lockProperty)
                                console.log(lockMeshMetadata)
                                this.sendMessageToServer("GEO", [lockMeshMetadata])
                                // newMesh.locked = true
                                this.gizmoManager.boundingBoxGizmoEnabled=true;
                                this.gizmoManager.attachToMesh(pickingInfo.pickedMesh);
                                this.gizmoManager.attachableMeshes = [pickingInfo.pickedMesh]
                                console.log(pickingInfo.pickedMesh)
                                // remove drag behavior for orthographic cameras since there is a bug for orthographic mode drag behavior.
                                var cur = this.getCurrentCamera()
                                if (cur !== 0 && cur !== 1) {
                                    this.gizmoManager.boundingBoxDragBehavior.detach()
                                }
                            }
                        }
                        else {
                            if(this.gizmoManager.attachableMeshes !== null && this.gizmoManager.attachableMeshes.length !== 0 ) {
                                console.log("unlock")
                                console.log(this.meshToId.get(this.gizmoManager.attachableMeshes[0]));
                                const unLockProperty = {isLock: !this.gizmoManager.attachableMeshes[0].isPickable}
                                const unLockMeshMetadata = this.constructMeshMetadata("UNLOCK", this.meshToId.get(this.gizmoManager.attachableMeshes[0]), unLockProperty)
                                console.log(unLockMeshMetadata)
                                this.sendMessageToServer("GEO", [unLockMeshMetadata])
                                this.gizmoManager.boundingBoxGizmoEnabled=false;
                                this.gizmoManager.attachableMeshes =[];
                            }

                        }
                    }
                    else {
                        if(pickingInfo.hit) {
                            if(pickingInfo.pickedMesh.isPickable) {
                                let pickedId = this.meshToId.get(pickingInfo.pickedMesh)
                                if(!pickedList.includes(pickedId)) {
                                    console.log("check isPickable:", pickedId);
                                    pickedList.push(pickedId)
                                    // const lockProperty = {isLock: pickingInfo.pickedMesh.isPickable}
                                    // const lockMeshMetadata = this.constructMeshMetadata("LOCK", pickedId, lockProperty)
                                    // console.log(lockMeshMetadata)
                                    // this.sendMessageToServer("GEO", [lockMeshMetadata])
                                    // newMesh.locked = true
                                    this.gizmoManager.boundingBoxGizmoEnabled=true;
                                    this.gizmoManager.attachToMesh(pickingInfo.pickedMesh);
                                    // this.gizmoManager.attachableMeshes = [pickingInfo.pickedMesh]
                                    if(this.gizmoManager.attachableMeshes === null) {
                                        this.gizmoManager.attachableMeshes = [pickingInfo.pickedMesh]
                                    } else {
                                        let attachedList = this.gizmoManager.attachableMeshes
                                        attachedList.push(pickingInfo.pickedMesh)
                                        this.gizmoManager.attachableMeshes = attachedList
                                    }

                                    console.log(pickingInfo.pickedMesh)
                                }

                                // remove drag behavior for orthographic cameras since there is a bug for orthographic mode drag behavior.
                                var cur = this.getCurrentCamera()
                                if (cur !== 0 && cur !== 1) {
                                    this.gizmoManager.boundingBoxDragBehavior.detach()
                                }
                            }
                        }
                    }
                    break;
                    // used for solving translation reduction
                // case BABYLON.PointerEventTypes.POINTERUP:
                //     if(positionChanged) {
                //         console.log("Translation action ended!")
                //         console.log("New position is ", gizmoManager.attachableMeshes[0].position)
                //         const updatedPosition = gizmoManager.attachableMeshes[0].position
                //         const properties = {position: [updatedPosition.x, updatedPosition.y, updatedPosition.z]}
                //         console.log(this.meshToId.get(gizmoManager.attachableMeshes[0]))
                //         const currMeshMetaData = this.constructMeshMetadata("TRANSLATE_END", this.meshToId.get(gizmoManager.attachableMeshes[0]), properties)
                //         // Temporary comment for the purpose of proposing a new algorithm
                //         // if (gizmoManager.attachableMeshes[0]["locked"] === false)  {
                //         //     this.sendMessageToServer("GEO", [currMeshMetaData])
                //         // }
                //         this.sendMessageToServer("GEO", [currMeshMetaData])
                //         positionChanged = false
                //         break;
                //     }
                //     break;


            }
        });

        var zoomScale = 1;
        this.scene.onPointerObservable.add(pointerInfo => {
            var camIndex = this.getCurrentCamera();
            if (camIndex === 0) {
                return
            }

            var event = pointerInfo.event;
            let wheelDelta = 0;

            if (event.wheelDelta) {
                wheelDelta = event.wheelDelta;
            } else {
                wheelDelta = -(event.deltaY || event.detail) * 60;
            }

            zoomScale -= wheelDelta / 5000;
            var currentCam = this.scene.cameras[camIndex]
            currentCam.orthoTop = 5 * zoomScale;
            currentCam.orthoBottom = -5 * zoomScale;
            currentCam.orthoLeft = -5 * zoomScale;
            currentCam.orthoRight = 5 * zoomScale;

        }, BABYLON.PointerEventTypes.POINTERWHEEL);

    }

    /**
     * Merge the meshes picked up together to another new mesh.
     * The old meshes will no longer be accessed.
     * The new merged mesh will be registered as a new independent mesh object.
     */
    mergeMesh() {
        if(pickedList.length === 0) {
            return
        }
        let mergedMeshes = []
        for(let i = 0;i<pickedList.length;i++) {
            mergedMeshes.push(this.meshes[pickedList[i]])
        }
        // pickedList.forEach( function (pickedIndex) {
        //     let meshshs = this.mes
        //     mergedMeshes.push(this.meshes[pickedIndex])
        // })
        const newMesh = BABYLON.Mesh.MergeMeshes(mergedMeshes);
        this.registerMesh(newMesh)

        const updatedRotation = newMesh.rotation
        // const properties = {rotation: [updatedRotation._w, updatedRotation._x, updatedRotation._y, updatedRotation._z]}

        const properties = {mergeIndices: pickedList, scaling: [newMesh.scaling.x, newMesh.scaling.y, newMesh.scaling.z],
            rotation: [updatedRotation.x, updatedRotation.y, updatedRotation.z],
            position: [newMesh.position.x, newMesh.position.y, newMesh.position.z]}
        const meshMetadata = this.constructMeshMetadata("MERGE", this.meshToId.get(newMesh), properties)
        console.log("merged sent")
        console.log(meshMetadata)
        this.sendMessageToServer("GEO", [meshMetadata])
    }

    /**
     * Boolean operations that subtract the two meshes picked up together to another new mesh.
     * The subtract action can only be valid for two meshes. The first picked up one will be the base.
     * The second picked up mesh will be the boolean operator.
     * The old meshes can still be accessed.
     * The new subtracted mesh will be registered as a new independent mesh object.
     */
    subtractMesh() {
        if(pickedList.length !== 2) {
            return
        }
        let mesh1 = this.meshes[pickedList[0]]
        let mesh2 = this.meshes[pickedList[1]]
        let meshCSG1 = BABYLON.CSG.FromMesh(mesh1);
        let meshCSG2 = BABYLON.CSG.FromMesh(mesh2);

        let booleanCSG = meshCSG1.subtract(meshCSG2);

        let newMesh = booleanCSG.toMesh("newMesh", null, this.scene);
        this.registerMesh(newMesh)

        const updatedRotation = newMesh.rotation
        // const properties = {rotation: [updatedRotation._w, updatedRotation._x, updatedRotation._y, updatedRotation._z]}

        const properties = {subtractIndices: pickedList, scaling: [newMesh.scaling.x, newMesh.scaling.y, newMesh.scaling.z],
            rotation: [updatedRotation.x, updatedRotation.y, updatedRotation.z],
            position: [newMesh.position.x, newMesh.position.y, newMesh.position.z]}
        const meshMetadata = this.constructMeshMetadata("SUBTRACT", this.meshToId.get(newMesh), properties)
        console.log("subtract sent")
        console.log(meshMetadata)
        this.sendMessageToServer("GEO", [meshMetadata])
    }

    /**
     * Boolean operations that intersect the two meshes picked up together to another new mesh.
     * The intersect action can only be valid for two meshes. The first picked up one will be the base.
     * The second picked up mesh will be the boolean operator.
     * The old meshes can still be accessed.
     * The new intersected mesh will be registered as a new independent mesh object.
     */
    intersectMesh() {
        if(pickedList.length !== 2) {
            return
        }
        let mesh1 = this.meshes[pickedList[0]]
        let mesh2 = this.meshes[pickedList[1]]
        let meshCSG1 = BABYLON.CSG.FromMesh(mesh1);
        let meshCSG2 = BABYLON.CSG.FromMesh(mesh2);

        let booleanCSG = meshCSG1.intersect(meshCSG2);

        let newMesh = booleanCSG.toMesh("newMesh", null, this.scene);
        this.registerMesh(newMesh)

        const updatedRotation = newMesh.rotation
        // const properties = {rotation: [updatedRotation._w, updatedRotation._x, updatedRotation._y, updatedRotation._z]}

        const properties = {intersectIndices: pickedList, scaling: [newMesh.scaling.x, newMesh.scaling.y, newMesh.scaling.z],
            rotation: [updatedRotation.x, updatedRotation.y, updatedRotation.z],
            position: [newMesh.position.x, newMesh.position.y, newMesh.position.z]}
        const meshMetadata = this.constructMeshMetadata("INTERSECT", this.meshToId.get(newMesh), properties)
        console.log("intersect sent")
        console.log(meshMetadata)
        this.sendMessageToServer("GEO", [meshMetadata])
    }

    /**
     * Boolean operations that union the two meshes picked up together to another new mesh.
     * The union action for now can only be valid for two meshes. The first picked up one will be the base.
     * The second picked up mesh will be the boolean operator.
     * The old meshes can still be accessed.
     * The new unioned mesh will be registered as a new independent mesh object.
     */
    unionMesh() {
        if(pickedList.length !== 2) {
            return
        }
        let mesh1 = this.meshes[pickedList[0]]
        let mesh2 = this.meshes[pickedList[1]]
        let meshCSG1 = BABYLON.CSG.FromMesh(mesh1);
        let meshCSG2 = BABYLON.CSG.FromMesh(mesh2);

        let booleanCSG = meshCSG1.union(meshCSG2);

        let newMesh = booleanCSG.toMesh("newMesh", null, this.scene);
        this.registerMesh(newMesh)

        const updatedRotation = newMesh.rotation
        // const properties = {rotation: [updatedRotation._w, updatedRotation._x, updatedRotation._y, updatedRotation._z]}

        const properties = {unionIndices: pickedList, scaling: [newMesh.scaling.x, newMesh.scaling.y, newMesh.scaling.z],
            rotation: [updatedRotation.x, updatedRotation.y, updatedRotation.z],
            position: [newMesh.position.x, newMesh.position.y, newMesh.position.z]}
        const meshMetadata = this.constructMeshMetadata("UNION", this.meshToId.get(newMesh), properties)
        console.log("union sent")
        console.log(meshMetadata)
        this.sendMessageToServer("GEO", [meshMetadata])
    }

    /**
     * create world axes object and make them not selectable.
     *
     * @param scene the current scene.
     */
    createWorldAxes(scene) {
        const worldAxes = new BABYLON.AxesViewer(scene, 2)
        for (const abstractMesh of worldAxes.scene.meshes) {
            if (abstractMesh.parent.id === "arrow")
                abstractMesh.isPickable = false
        }
    }

    /**
     * creates the default main camera and four extra cameras for four-camera mode.
     * default camera = 0, perspective camera = 1, top camera = 2, front camera = 3, right camera = 4.
     * @param scene the default scene
     * @returns {ArcRotateCamera} the default main camera
     */
    createCamera(scene) {
        // const camera = new BABYLON.FreeCamera("camera", new BABYLON.Vector3(1, 1, 1), scene)
        var camera = new BABYLON.ArcRotateCamera("camera", BABYLON.Tools.ToRadians(90), BABYLON.Tools.ToRadians(65), 10, BABYLON.Vector3.Zero(), scene);
        camera.attachControl(this.canvas, true)

        // perspective camera (one of four cameras)
        var perspectiveCam = new BABYLON.ArcRotateCamera("PerspectiveCamera", BABYLON.Tools.ToRadians(90), BABYLON.Tools.ToRadians(65), 10, BABYLON.Vector3.Zero(), scene);
        perspectiveCam.attachControl(this.canvas, true)
        perspectiveCam.inputs.attached.mousewheel.detachControl(this.canvas)

        // top camera
        var topCam = new BABYLON.ArcRotateCamera("TopCamera", 0, 0, 80, BABYLON.Vector3.Zero(), scene);
        topCam.upperAlphaLimit = 0;
        topCam.lowerAlphaLimit = 0;
        topCam.upperBetaLimit = 0;
        topCam.lowerBetaLimit = 0;
        topCam.attachControl(scene.canvas, true);
        // detach default mousewheel control to add custom one
        topCam.inputs.attached.mousewheel.detachControl()
        topCam.mode = BABYLON.Camera.ORTHOGRAPHIC_CAMERA;
        topCam.orthoTop = 5;
        topCam.orthoBottom = -5;
        topCam.orthoLeft = -5;
        topCam.orthoRight = 5;

        // front camera
        var frontCam = new BABYLON.ArcRotateCamera("FrontCamera", 0, Math.PI / 2, 80, BABYLON.Vector3.Zero(), scene);
        frontCam.upperAlphaLimit = 0;
        frontCam.lowerAlphaLimit = 0;
        frontCam.upperBetaLimit = Math.PI / 2;
        frontCam.lowerBetaLimit = Math.PI / 2;
        frontCam.attachControl(scene.canvas, true);
        frontCam.inputs.attached.mousewheel.detachControl()
        frontCam.mode = BABYLON.Camera.ORTHOGRAPHIC_CAMERA;
        frontCam.orthoTop = 5;
        frontCam.orthoBottom = -5;
        frontCam.orthoLeft = -5;
        frontCam.orthoRight = 5;

        // right camera
        var rightCam = new BABYLON.ArcRotateCamera("RightCamera", Math.PI / 2, Math.PI / 2, 80, BABYLON.Vector3.Zero(), scene);
        rightCam.upperAlphaLimit = Math.PI / 2;
        rightCam.lowerAlphaLimit = Math.PI / 2;
        rightCam.upperBetaLimit = Math.PI / 2;
        rightCam.lowerBetaLimit = Math.PI / 2;
        rightCam.attachControl(scene.canvas, true);
        rightCam.inputs.attached.mousewheel.detachControl()
        rightCam.mode = BABYLON.Camera.ORTHOGRAPHIC_CAMERA;
        rightCam.orthoTop = 5;
        rightCam.orthoBottom = -5;
        rightCam.orthoLeft = -5;
        rightCam.orthoRight = 5;

        // set Viewports
        perspectiveCam.viewport = new BABYLON.Viewport(0.5, 0.5, 0.5, 0.5);
        topCam.viewport = new BABYLON.Viewport(0, 0.5, 0.5, 0.5);
        frontCam.viewport = new BABYLON.Viewport(0, 0, 0.5, 0.5);
        rightCam.viewport = new BABYLON.Viewport(0.5, 0, 0.5, 0.5);

        return camera
    }

    /**
     * Generate new hemispheric light to the scene.
     *
     * @param scene The scene the light belongs to.
     * @return light The new generated hemispheric light.
     */
    createLight(scene) {
        const light = new BABYLON.HemisphericLight("light", new BABYLON.Vector3(0, 1, 0), scene)
        light.intensity = 0.7
        return light
    }

    /**
     * Generate Json format for mesh metadata sent to the server.
     *
     * @param actionType The action type of this instruction.
     * @param meshId The id of the mesh the action applied to.
     * @param properties The new properties of the mesh when the action happens.
     * @return Json format for the mesh metadata sent to the back-end server.
     */
    constructMeshMetadata(actionType, meshId, properties) {
        return {
            meshId: meshId,
            meshAction: actionType,
            properties: properties,
            userId: this.user_id
        }
    }

    /**
     * Generate gizmoManager to control the gizmo actions of each mesh.
     * Definitions of translation, scaling and rotation reactions are attached to the manager.
     * The Reaction is sent to server each time a gizmo event is fired.
     *
     * @param scene The scene the gizmoManager belongs to.
     * @return gizmoManager The new generated gizmoManager.
     */
    createGizmoManager(scene) {
        const gizmoManager = new BABYLON.GizmoManager(scene)
        // gizmoManager.attachableMeshes = []
        // gizmoManager.positionGizmoEnabled = true
        // gizmoManager.gizmos.positionGizmo = new BABYLON.PositionGizmo()
        // gizmoManager.scaleGizmoEnabled = true
        // gizmoManager.gizmos.scaleGizmo = new BABYLON.ScaleGizmo()
        // gizmoManager.rotationGizmoEnabled = true
        // gizmoManager.gizmos.rotationGizmo = new BABYLON.RotationGizmo()
        gizmoManager.boundingBoxGizmoEnabled = true
        gizmoManager.usePointerToAttachGizmos = false
        gizmoManager.enableAutoPicking = false
        gizmoManager.gizmos.boundingBoxGizmo.onScaleBoxDragObservable.add(() => {
            console.log("Scale action detected")
            // scaleGizmo.oldScale = gizmoManager.attachedMesh.scaling
            // console.log("Old scale is ", gizmoManager.attachableMeshes[0].scaling)
        })
        gizmoManager.gizmos.boundingBoxGizmo.onScaleBoxDragEndObservable.add(() => {
            console.log("Scale action ended!")
            console.log("New scale is ", gizmoManager.attachableMeshes[0].scaling)
            const updatedScaling = gizmoManager.attachableMeshes[0].scaling
            const properties = {scaling: [updatedScaling.x, updatedScaling.y, updatedScaling.z]}
            const currMeshMetaData = this.constructMeshMetadata("SCALE", this.meshToId.get(gizmoManager.attachableMeshes[0]), properties)
            // Temporary comment for the purpose of proposing a new algorithm
            this.sendMessageToServer("GEO", [currMeshMetaData])
            // if (gizmoManager.attachableMeshes[0]["locked"] === false) {
            //     this.sendMessageToServer("GEO", [currMeshMetaData])
            // }
        })
        gizmoManager.gizmos.boundingBoxGizmo.onRotationSphereDragObservable.add(() => {
            console.log("Rotation action detected")
            // rotationGizmo.oldRotation = gizmoManager.attachedMesh.rotation
            console.log("Old rotation is ", gizmoManager.attachableMeshes[0].rotationQuaternion)
        })
        gizmoManager.gizmos.boundingBoxGizmo.onRotationSphereDragEndObservable.add(() => {
            console.log("Rotation action ended!")
            console.log("New rotation is ", gizmoManager.attachableMeshes[0].rotationQuaternion)
            const updatedRotation = gizmoManager.attachableMeshes[0].rotationQuaternion
            const properties = {rotation: [updatedRotation._w, updatedRotation._x, updatedRotation._y, updatedRotation._z]}
            const currMeshMetaData = this.constructMeshMetadata("ROTATE", this.meshToId.get(gizmoManager.attachableMeshes[0]), properties)
            // Temporary comment for the purpose of proposing a new algorithm
            this.sendMessageToServer("GEO", [currMeshMetaData])
            // if (gizmoManager.attachableMeshes[0]["locked"] === false) {
            //     this.sendMessageToServer("GEO", [currMeshMetaData])
            // }

        })

        //gizmoManager.boundingBoxDragBehavior.dragDeltaRatio = 1.3
        //gizmoManager.boundingBoxDragBehavior.onDragObservable
        gizmoManager.boundingBoxDragBehavior.onDragObservable.add(() => {
            console.log("Translation action detected!")
            // positionGizmo.oldPosition = gizmoManager.attachedMesh.position
            console.log("Old position is ", gizmoManager.attachableMeshes[0].position)
        })
        //used for solving translation reduction
        // gizmoManager.boundingBoxDragBehavior.onDragEndObservable.add(() => {
        //     console.log("Translation action ended!")
        //     console.log("New position is ", gizmoManager.attachableMeshes[0].position)
        //     const updatedPosition = gizmoManager.attachableMeshes[0].position
        //     const properties = {position: [updatedPosition.x, updatedPosition.y, updatedPosition.z]}
        //     console.log(this.meshToId.get(gizmoManager.attachableMeshes[0]))
        //     const currMeshMetaData = this.constructMeshMetadata("TRANSLATE_END", this.meshToId.get(gizmoManager.attachableMeshes[0]), properties)
        //     // Temporary comment for the purpose of proposing a new algorithm
        //     // if (gizmoManager.attachableMeshes[0]["locked"] === false)  {
        //     //     this.sendMessageToServer("GEO", [currMeshMetaData])
        //     // }
        //     this.sendMessageToServer("GEO", [currMeshMetaData])
        // })
        gizmoManager.boundingBoxDragBehavior.onPositionChangedObservable.add(() => {
            console.log("Translation action changing!")
            console.log("New position is ", gizmoManager.attachableMeshes[0].position)
            const updatedPosition = gizmoManager.attachableMeshes[0].position
            const properties = {position: [updatedPosition.x, updatedPosition.y, updatedPosition.z]}
            console.log(this.meshToId.get(gizmoManager.attachableMeshes[0]))
            const currMeshMetaData = this.constructMeshMetadata("TRANSLATE", this.meshToId.get(gizmoManager.attachableMeshes[0]), properties)
            // Temporary comment for the purpose of proposing a new algorithm
            // if (gizmoManager.attachableMeshes[0]["locked"] === false)  {
            //     this.sendMessageToServer("GEO", [currMeshMetaData])
            // }
            this.sendMessageToServer("GEO", [currMeshMetaData])
            positionChanged = true
        })

        return gizmoManager
    }

    /**
     * Generate new ground mesh according default position, scaling and rotation data.
     *
     * @param scene The scene where the mesh belongs to.
     * @return ground The new created ground Mesh object.
     */
    createGround(scene) {
        const ground = BABYLON.MeshBuilder.CreateGround("ground", {width: 10, height: 10}, scene)
        ground.position = new BABYLON.Vector3(0, 0, 0)
        return ground
    }

    /**
     * Generate new meshes according to the mesh type with default position, scaling and rotation data.
     *
     * @param name The mesh type.
     * @param initialMetadata Initial data for the generated mesh receiving from back-end server.
     * @return newMesh The new created Mesh object.
     */
    addMesh_(name, initialMetadata = {}) {
        let newMesh;
        switch (name) {
            case "sphere":
                newMesh = BABYLON.MeshBuilder.CreateSphere("ball", {diameter: 1}, this.scene)
                break
            case "box":
                newMesh = BABYLON.MeshBuilder.CreateBox("box", {width: 1, height: 1, depth: 1}, this.scene)
                break;
            case "ground":
                newMesh = BABYLON.MeshBuilder.CreateGround("ground", {width: 10, height: 10}, this.scene)
                let groundMaterial = new BABYLON.StandardMaterial("Ground Material", this.scene);
                newMesh.material = groundMaterial;
                break;
        }

        // distinguish the position of three different kinds of meshes to have a better view
        newMesh.position = new BABYLON.Vector3(3, 3, 3)
        if (name === "ground") {
            newMesh.position = new BABYLON.Vector3(0, 0, 0)
        }
        if (name === "sphere") {
            newMesh.position = new BABYLON.Vector3(3, 1, 3)
        }
        // newMesh.position = new BABYLON.Vector3(3, 3, 3)

        const {position, scaling, rotation} = initialMetadata
        console.log(position)
        console.log(scaling)
        console.log(rotation)
        if (position !== undefined && scaling !== undefined && rotation !== undefined) {
            newMesh.position = new BABYLON.Vector3(position[0], position[1], position[2])
            console.log(newMesh.position)
            newMesh.scaling = new BABYLON.Vector3(scaling[0], scaling[1], scaling[2])
            console.log(newMesh.scaling)
            newMesh.rotation = new BABYLON.Vector3(rotation[0], rotation[1], rotation[2])
            console.log(newMesh.rotation)
        }
        // newMesh.locked = false
        //
        // newMesh.actionManager = new BABYLON.ActionManager(this.scene);
        // newMesh.actionManager.registerAction(new BABYLON.ExecuteCodeAction(BABYLON.ActionManager.OnRightPickTrigger,
        //     (evt) => {
        //         // Perform your desired action here
        //         console.assert(newMesh.locked !== undefined)
        //         if (newMesh.locked === true) {
        //             console.log("Right-clicked on the mesh from true to false!");
        //             const updatedProperties = {}
        //             geoProperties.forEach((property) => {
        //                 updatedProperties[property] = this._parseGenericProperty(newMesh[property]);
        //             })
        //             const unlockMeshMetadata = this.constructMeshMetadata("UNLOCK", this.meshToId.get(newMesh), updatedProperties)
        //             this.sendMessageToServer("GEO", [unlockMeshMetadata])
        //         } else if (newMesh.locked === false) {
        //             console.log("Right-clicked on the mesh from false to true!");
        //             const lockMeshMetadata = this.constructMeshMetadata("LOCK", this.meshToId.get(newMesh), {})
        //             this.sendMessageToServer("GEO", [lockMeshMetadata])
        //             newMesh.locked = true
        //         }
        //     }))


        // const newGizmoManager = this.createGizmoManager(this.scene)
        // this.gizmoManager.attachToMesh(newMesh)
        // newGizmoManager.gizmos.rotationGizmo.updateGizmoRotationToMatchAttachedMesh = false;
        // const positionGizmo = newGizmoManager.gizmos.positionGizmo
        // const scaleGizmo = newGizmoManager.gizmos.scaleGizmo
        // const rotationGizmo = newGizmoManager.gizmos.rotationGizmo
        // positionGizmo.attachedMesh = newMesh
        // scaleGizmo.attachedMesh = newMesh
        // rotationGizmo.attachedMesh = newMesh
        // positionGizmo.onDragStartObservable.add(() => {
        //     console.log("Translation action detected!")
        //     positionGizmo.oldPosition = positionGizmo.attachedMesh.position
        //     console.log("Old position is ", positionGizmo.attachedMesh.position)
        // })
        // positionGizmo.onDragEndObservable.add(() => {
        //     console.log("Translation action ended!")
        //     console.log("New position is ", positionGizmo.attachedMesh.position)
        //     const updatedPosition = positionGizmo.attachedMesh.position
        //     const properties = {position: [updatedPosition.x, updatedPosition.y, updatedPosition.z]}
        //     console.log(this.meshToId.get(positionGizmo.attachedMesh))
        //     this.sendMessageToServer(this.meshToId.get(positionGizmo.attachedMesh), "translate", properties)
        // })
        // scaleGizmo.onDragStartObservable.add(() => {
        //     console.log("Scale action detected")
        //     scaleGizmo.oldScale = scaleGizmo.attachedMesh.scaling
        //     console.log("Old scale is ", scaleGizmo.attachedMesh.scaling)
        // })
        // scaleGizmo.onDragEndObservable.add(() => {
        //     console.log("Scale action ended!")
        //     console.log("New scale is ", scaleGizmo.attachedMesh.scaling)
        //     const updatedScaling = scaleGizmo.attachedMesh.scaling
        //     const properties = {scaling: [updatedScaling.x, updatedScaling.y, updatedScaling.z]}
        //     this.sendMessageToServer(this.meshToId.get(scaleGizmo.attachedMesh), "scale", properties)
        // })
        // rotationGizmo.onDragStartObservable.add(() => {
        //     console.log("Rotation action detected")
        //     rotationGizmo.oldRotation = rotationGizmo.attachedMesh.rotation
        //     console.log("Old rotation is ", rotationGizmo.attachedMesh.rotation)
        // })
        // rotationGizmo.onDragEndObservable.add(() => {
        //     console.log("Rotation action ended!")
        //     console.log("New rotation is ", rotationGizmo.attachedMesh.rotation)
        //     const updatedRotation = rotationGizmo.attachedMesh.rotation
        //     const properties = {rotation: [updatedRotation.x, updatedRotation.y, updatedRotation.z]}
        //     this.sendMessageToServer(this.meshToId.get(rotationGizmo.attachedMesh), "rotate", properties)
        // })

        this.registerMesh(newMesh)
        return newMesh
    }

    /**
     * Parse object data to the Babylon vector format.
     *
     * @obj the received object from back-end server.
     * @return BABYLON.Vector3 The babylon vector format of the data.
     */
    _parseGenericProperty(obj) {
        if (obj instanceof BABYLON.Vector3) {
            return [obj.x, obj.y, obj.z]
        }
        // @Other Developers: Add more if you want.
    }

    /**
     * Generate new meshes according to the user choice.
     * Send data to backend to keep consistency for all other users.
     *
     * @param name The type of mesh.
     * @return newMesh The new generated Mesh object.
     */
    createMesh(name) {
        const newMesh = this.addMesh_(name)
        const properties = {
            meshType: name,
            position: [newMesh.position.x, newMesh.position.y, newMesh.position.z],
            scaling: [newMesh.scaling.x, newMesh.scaling.y, newMesh.scaling.z],
            rotation: [newMesh.rotation.x, newMesh.rotation.y, newMesh.rotation.z]
        }
        const meshMetadata = this.constructMeshMetadata("ADD_MESH", this.meshToId.get(newMesh), properties)
        this.sendMessageToServer("GEO", [meshMetadata])
        return newMesh
    }

    /**
     * Request a reverted state for the selected mesh.
     */
    revertMesh() {
        const meshMetadata = this.constructMeshMetadata("REVERT", this.meshToId.get(this.gizmoManager.attachableMeshes[0]), {})
        console.log("revert")
        console.log(meshMetadata)
        this.sendMessageToServer("GEO", [meshMetadata])
    }

    /**
     * Request a redo state for the selected mesh for the current reverted state.
     */
    redoMesh() {
        const meshMetadata = this.constructMeshMetadata("REDO", this.meshToId.get(this.gizmoManager.attachableMeshes[0]), {})
        console.log("redo")
        console.log(meshMetadata)
        this.sendMessageToServer("GEO", [meshMetadata])
    }

    /**
     * export the meshes in the current scene as .glb file.
     */
    exportModel() {
        GLTF2Export.GLBAsync(this.scene, "project1234").then((glb) => {
            glb.downloadFiles();
        });
    }

    /**
     * Import model from .glb file to the current scene.
     * @param fileInput file DOM element
     */
    importModel(fileInput) {
        var file = fileInput.files[0];
        if (!file) return;
        // BABYLON.SceneLoader.AppendAsync("", file);
        BABYLON.SceneLoader.ImportMesh(
            null,
            "",
            file,
            this.scene,
            function (meshes) {
                for (const mesh of meshes) {
                    this.registerMesh(mesh)
                    this.sendMessageToServer(this.meshToId.get(mesh), "add_mesh", {meshType: "external"})
                }
            }.bind(this))
    }

    /**
     * method to enable wireframe mode for all meshes.
     */
    enableWireframeMode() {
        var materials = this.scene.materials
        for (const material of materials) {
            material.wireframe = true
        }
    }

    /**
     * method to disable wireframe mode for all meshes.
     */
    disableWireframeMode() {
        var materials = this.scene.materials
        for (const material of materials) {
            material.wireframe = false
        }
    }

    /**
     * enables four camera mode.
     * @param scene the default scene.
     */
    enableFourCameraMode(scene) {
        // switch to four cameras
        scene.activeCameras.push(scene.cameras[1]);
        scene.activeCameras.push(scene.cameras[2]);
        scene.activeCameras.push(scene.cameras[3]);
        scene.activeCameras.push(scene.cameras[4]);
    }

    /**
     * disables four camera mode
     * @param scene the default scene
     */
    disableFourCameraMode(scene) {
        scene.activeCameras.pop()
        scene.activeCameras.pop()
        scene.activeCameras.pop()
        scene.activeCameras.pop()
        // switch to main camera
        scene.activeCamera = scene.cameras[0]
    }

    /**
     * gets the current active camera.
     * @returns {number} camera index. (from 0 to 4)
     */
    getCurrentCamera() {
        var fourCameraToggle = document.getElementById("four-camera-input");
        // when in one camera mode
        if (fourCameraToggle.checked) {
            // last camera in .activeCameras is considered active by gizmo
            var camera = this.scene.activeCameras[3]
            return this.scene.cameras.indexOf(camera)
            // when in four camera mode
        } else {
            return 0
        }
    }

    /**
     * sets the current active camera
     * @param camIndex index of the camera. (from 0 to 4)
     */
    setCurrentCamera(camIndex) {
        // if clicked camera is main camera or is the same as last clicked camera
        if (camIndex === 0 || camIndex === this.getCurrentCamera()) {
            return
        }
        // if clicked camera changed, remove it from the .activeCamera list and append it as the last element of the same list.
        var targetCamera = this.scene.cameras[camIndex]
        // attach mousewheel control if clicked camera is 1. ie the perspective camera
        if (camIndex === 1) {
            targetCamera.inputs.attached.mousewheel.attachControl(this.canvas)
        } else {
            var x = this.scene.activeCameras[3].inputs.attached.mousewheel.detachControl()
        }
        var index = this.scene.activeCameras.indexOf(targetCamera)
        this.scene.activeCameras.splice(index, 1)
        // append the camera as the last element
        this.scene.activeCameras.push(targetCamera)

    }

    /**
     * check the camera that was clicked. In main design page it is the default camera.
     * In four camera mode it is the one of the four cameras.
     * @returns {index} return the camera index in scene.cameras.
     * 0 = default, 1 = perspective, 2 = top, 3 = front, 4 = right
     */
    checkClickedCamera() {
        if (this.getCurrentCamera() === 0) {
            return 0;
        }
        if (this.scene.pointerX < this.canvas.width * 0.5) {
            if (this.scene.pointerY < this.canvas.height * 0.5) {
                this.setCurrentCamera(2)
                return 2
            } else {
                this.setCurrentCamera(3)
                return 3
            }
        } else {
            if (this.scene.pointerY < this.canvas.height * 0.5) {
                this.setCurrentCamera(1)
                return 1
            } else {
                this.setCurrentCamera(4)
                return 4
            }
        }
    }

}

/**
 * Adds event listeners to all buttons in the UI.
 * @param basicScene BasicScene object
 */
function addButtonEventListener(basicScene) {
    document.getElementById("box-icon").addEventListener("click", function () {
        basicScene.createMesh("box")
    });
    document.getElementById("sphere-icon").addEventListener("click", function () {
        basicScene.createMesh("sphere")
    });
    document.getElementById("ground-icon").addEventListener("click", function () {
        basicScene.createMesh("ground")
    });
    document.getElementById("export-icon").addEventListener("click", function () {
        basicScene.exportModel()
    });
    document.getElementById("revert-icon").addEventListener("click", function () {
        basicScene.revertMesh()
    });
    document.getElementById("redo-icon").addEventListener("click", function () {
        basicScene.redoMesh()
    });
    document.getElementById("lock-icon").addEventListener("click", function () {
        basicScene.allowMultiplePickUp()
    });
    document.getElementById("package-icon").addEventListener("click", function () {
        basicScene.mergeMesh()
    });

    document.getElementById("layout-icon").addEventListener("click", function (evt) {
        basicScene.intersectMesh()
    });
    document.getElementById("SearchBig").addEventListener("click", function () {
        basicScene.unionMesh()
    });
    document.getElementById("external-link").addEventListener("click", function () {
        basicScene.subtractMesh()
    });

    // setup upload button for importing.
    var fileInput = document.getElementById("input-file");
    fileInput.addEventListener("change", function () {
        basicScene.importModel(fileInput)
    })
    var wireframeToggle = document.getElementById("wireframe-input");
    wireframeToggle.addEventListener('change', function () {
        if (wireframeToggle.checked) {
            basicScene.enableWireframeMode();
        } else {
            basicScene.disableWireframeMode();
        }
    });
    var fourCameraToggle = document.getElementById("four-camera-input");
    fourCameraToggle.addEventListener('change', function () {
        if (fourCameraToggle.checked) {
            basicScene.enableFourCameraMode(basicScene.scene);
        } else {
            basicScene.disableFourCameraMode(basicScene.scene);
        }
    });
}

export {BasicScene}