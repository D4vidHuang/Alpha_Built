<template>
  <div id="design-page-container">
    <IconTop/>
    <MainScene />
    <LeftBar />
    <WireframeToggle/>
    <version />
    <botBar />
  </div>
</template>

<script>
import MainScene from "@/components/design-page/Scene.vue";
import IconTop from "@/components/design-page/iconTop.vue";
import LeftBar from "@/components/design-page/leftBar.vue";
import BotBar from "@/components/design-page/botBar.vue";
import RightBar from "@/components/design-page/rightBar.vue";
import Version from "@/components/design-page/rightBar/version.vue";
import {CloseBold} from "@element-plus/icons-vue";
import WireframeToggle from "@/components/design-page/Toggles.vue";

export default {
  name: 'design-page',
  components: {
    WireframeToggle,
    BotBar, LeftBar, Version,
    IconTop,
    MainScene,
    // eslint-disable-next-line
    RightBar
    // SideBar,
  }
}
</script>

<style scoped>
@import '../../assets/test.css';
#design-page-container {
  display: flex;
  flex-direction: column;
/*  width: 100vw;
  height: 100vh;*/
  background-color:  var(--color-white);
  flex: none;
  order: 1;
  flex-grow: 0;
  position: relative;
}

</style>

<style>
body {
  margin: 0;
}
</style>
