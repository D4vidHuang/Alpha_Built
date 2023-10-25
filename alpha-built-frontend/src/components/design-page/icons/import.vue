<template>
  <div id="import-icon" style="position: relative; width: 30px; height: 30px;">
    <img src="@/assets/import.svg" alt="Import Logo">
    <input id="input-file" type="file" style="opacity: 0.0; position: absolute; top:0; left: 0; bottom: 0; right:0; width: 100%; height:100%;">
  </div>
</template>

<script>
export default {
  name: 'ImportIcon',
}
</script>

<style scoped>

#import-icon {
  order: 15;
}

</style>
