<template>
    <div id="left-bar">
        <MousePointer />
        <edit-logo />
        <anchor />
        <group4 />
        <external-link />
        <codepen />
        <move />
        <rotate-c-c-w />
        <Camera />
        <Search />
        <zoom-in-icon />
        <BoxIcon />
        <SphereIcon />
        <GroundIcon />
        <ExportIcon />
        <ImportIcon />
    </div>
</template>

<script>
import MousePointer from "./icons/pointer.vue";
import EditLogo from "./icons/edit.vue";
import Anchor from "./icons/anchor.vue";
import Group4 from "./icons/group4.vue";
import ExternalLink from "./icons/externalLink.vue";
import Codepen from "./icons/codePen.vue";
import Move from "./icons/move.vue";
import RotateCCW from "./icons/rotateCCW.vue";
import Camera from "./icons/camera.vue";
import Search from "./icons/search.vue";
import ZoomInIcon from "./icons/searchBig.vue";
import BoxIcon from "./icons/box.vue";
import SphereIcon from "./icons/sphere.vue";
import GroundIcon from "./icons/ground.vue";
import ExportIcon from "./icons/export.vue";
import ImportIcon from "./icons/import.vue";
export default {
    name: 'LeftBar',
    components: {
      ImportIcon,
      ExportIcon,
      GroundIcon,
      SphereIcon,
      BoxIcon,
        ZoomInIcon,
        Search, Camera, RotateCCW, Move, Codepen, ExternalLink, Group4, Anchor, EditLogo, MousePointer},
}
</script>

<style scoped>
#left-bar {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    width: 3%;
    height: 80%;
    background: #FFFFFF;
    box-shadow: 0px 4px 4px rgba(0, 0, 0, 0.25);
    border-radius: 0px 10px 10px 0px;
    position: absolute;
    left: 0%;
    right: 0%;
    top: 12%;
    bottom: 85.85%;
}

#left-bar > * {
  width: 30px;
  height: 30px;
  flex-grow: 1;
  flex-basis: 0;
  display: flex;
  justify-content: center;
  align-items: center;
}

#left-bar > *:hover {
  background-color: #D3D3D3;
}

</style>

