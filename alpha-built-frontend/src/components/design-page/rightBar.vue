<template>
    <div id="rightBar">
        <version />
    </div>
</template>

<script>


import Version from "./rightBar/version.vue";

export default {
    name: "rightBar",
    components: {Version},

}
</script>

<style scoped>
.rightBar {
    position: absolute;
    left: 0%;
    right: 0%;
    top: 0%;
    bottom: 85.85%;

}
</style>