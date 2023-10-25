<template>
  <div id="skeleton">
    <button id="buttonAddSphere" class="addButton"><slot>Create Sphere</slot></button>
    <button id="buttonAddBox" class="addButton"><slot>Create Box</slot></button>
  </div>
</template>

<script>
export default {
  name: 'SideBar',
  props: {
    msg: String
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
#skeleton {
  width: 80vw;
  height: 100vh;
  background-color: #5A5A5A;
}

.addButton {
  width: 50%;
  height: 8%;
  background-color: lightgray;
}

</style>
