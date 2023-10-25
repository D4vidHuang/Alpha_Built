import * as BABYLON from "@babylonjs/core"
import * as GUI from "@babylonjs/gui"
// import {AdvancedDynamicTexture, StackPanel, Control } from "@babylonjs/gui"
function setupInterface(scene) {
    // initialiseAddSphereButton(scene)
    // initialiseAddBoxButton(scene)
    initialiseBabyLonGUI(scene)
}


function initialiseAddSphereButton(scene) {
    const addSphereButton = document.getElementById("buttonAddSphere")
    addSphereButton.addEventListener("click", () => {
        scene.createMesh("sphere");
    })
}

function initialiseAddBoxButton(scene) {
    const addBoxButton = document.getElementById("buttonAddBox")
    addBoxButton.addEventListener("click", () => {
        scene.createMesh("box")
    })
}

function initialiseBabyLonGUI(scene) {
    const adt = GUI.AdvancedDynamicTexture.CreateFullscreenUI("UI", true, scene  );

    const panel = new GUI.StackPanel();
    panel.width = "220px";
    panel.horizontalAlignment = GUI.Control.HORIZONTAL_ALIGNMENT_CENTER;
    panel.verticalAlignment = GUI.Control.VERTICAL_ALIGNMENT_TOP;
    panel.paddingTopInPixels = "20px";
    adt.addControl(panel);

    const header = new GUI.TextBlock();
    header.text = "X distance: 0";
    header.height = "30px";
    header.color = "white";
    panel.addControl(header); 

    var slider = new GUI.Slider();
    slider.minimum = -2;
    slider.maximum = 2;
    slider.value = 0;
    slider.height = "20px";
    slider.width = "200px";
    slider.onValueChangedObservable.add(function(value) {
        header.text = "X distance: " + Math.round(value * 10) / 10;
        scene.getMeshByName("sphere").position.x = value;
    });
    panel.addControl(slider); 

    const ButtonPanel = new GUI.StackPanel();
    ButtonPanel.horizontalAlignment = GUI.Control.HORIZONTAL_ALIGNMENT_RIGHT;

    ButtonPanel.verticalAlignment = GUI.Control.VERTICAL_ALIGNMENT_BOTTOM;
    ButtonPanel.width = "100%";


    ButtonPanel.color = "white";

    // Aligns buttons next to each other
    const buttonsPanel = new GUI.StackPanel();
    buttonsPanel.horizontalAlignment = GUI.Control.HORIZONTAL_ALIGNMENT_RIGHT;
    buttonsPanel.verticalAlignment = GUI.Control.VERTICAL_ALIGNMENT_CENTER;
    buttonsPanel.height = "100px";
    buttonsPanel.isVertical = false;

    // Buttons
    const buttonBox = GUI.Button.CreateSimpleButton("btnBox", "Box");
    buttonBox.width = "80px";
    buttonBox.height = "40px";
    buttonBox.color = "white";
    buttonBox.cornerRadius = 20;
    buttonBox.background = "green";
    buttonBox.onPointerUpObservable.add(function() {
        const box = BABYLON.MeshBuilder.CreateBox("box", {height: 1, width: 0.75, depth: 0.25});
    });  
    const buttonSphere = GUI.Button.CreateSimpleButton("btnSphere", "Sphere");
    buttonSphere.width = "80px";
    buttonSphere.height = "40px";
    buttonSphere.color = "white";
    buttonSphere.cornerRadius = 20;
    buttonSphere.background = "green";
    buttonSphere.onPointerUpObservable.add(function() {
        const sphere = BABYLON.MeshBuilder.CreateSphere("sphere", {diameter: 2, segments: 32}, scene);
    });  
    const buttonGround = GUI.Button.CreateSimpleButton("btnGround", "Ground");
    buttonGround.width = "80px";
    buttonGround.height = "40px";
    buttonGround.color = "white";
    buttonGround.cornerRadius = 20;
    buttonGround.background = "green";
    buttonGround.onPointerUpObservable.add(function() {
        const ground = BABYLON.MeshBuilder.CreateGround("ground", {width: 6, height: 6}, scene);
        let groundMaterial = new BABYLON.StandardMaterial("Ground Material", scene);
        ground.material = groundMaterial;
    });  

    buttonsPanel.addControl(buttonBox);
    buttonsPanel.addControl(buttonSphere);
    buttonsPanel.addControl(buttonGround);
    ButtonPanel.addControl(buttonsPanel);
    adt.addControl(ButtonPanel);    
}


export {setupInterface}