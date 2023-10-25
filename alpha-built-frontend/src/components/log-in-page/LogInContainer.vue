<template>
  <div id="login-page-container">
    <div id="login-page-image-container">
      <el-image
          src="https://www.ecadi.com/public/20180926/images/guodu-b.jpg"
          style="width: 100%; height: 100%"
          fit="cover"/>
    </div>
    <div id="login-page-feature-container">
      <router-view/>
    </div>
  </div>
</template>

<script set-up>
import router from "@/router";
</script>

<style scoped>
#login-page-container {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  display: flex;
}

#login-page-image-container {
  display: flex;
  flex: 1;
  background-color: dodgerblue;
  align-items: center;
  flex-direction: column;
  justify-content: center;
}

#login-page-feature-container {
  width: 33%;
  background-color: white;
}
</style>