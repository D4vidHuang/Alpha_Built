<template>
  <div>
    <canvas></canvas>
  </div>
</template>

<script>
import {BasicScene} from "@/script/scene";
import {setupInterface} from "@/script/interface";
import {get} from "@/net";
import {ElMessage} from "element-plus";

export default {
  name: 'CanvasScene',
  mounted() {
    get("api/account/me", (message) => {
      let userInfo = message;
      console.log("Arrive the Scene component")
      const userId = userInfo.id
      const canvas = document.querySelector("canvas")
      // Access the project ID from the route parameters
      const projectId = this.$route.params.id;
      const basicScene = new BasicScene(userId, projectId, canvas, false)
      console.log("Project Id: " + projectId)
    }, () => {
      ElMessage.warning('This is single player mode. Actions are limited.')
      const userId = -1;
      const projectId = -1;
      const canvas = document.querySelector("canvas")
      const basicScene = new BasicScene(userId, projectId, canvas, true)
    })
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
canvas {
  display: block;
  width: 100%;
  height: 95vh;
}
</style>
