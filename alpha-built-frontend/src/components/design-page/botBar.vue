<template>
    <div id="botBar">
        <layout />
        <package />
        <eye />
        <lock />
    </div>
</template>

<script>
import Layout from "./botIcon/layout.vue";
import Package from "./botIcon/package.vue";
import Eye from "./botIcon/eye.vue";
import Lock from "./botIcon/lock.vue";

export default {
    name: "botBar",
    components: { Lock, Package, Layout, Eye },
};
</script>

<style scoped>
#botBar {
    display: flex;
    justify-content: space-between; /* Added */
    align-items: center;
    padding: 15px 20px;
    position: fixed; /* changed from absolute to fixed */
    bottom: 0;
    left: 50%;
    transform: translateX(-50%); /* Added */

    width: 260px;
    height: 67px;
    background: #ffffff;
    box-shadow: 0px 4px 4px rgba(0, 0, 0, 0.25);
    border-radius: 10px 10px 0px 0px;
}


#botBar > * {
    flex-grow: 1; /* Added */
    flex-basis: 0; /* Added */
    margin-left: 10px; /* Adjust as desired */
    margin-right: 10px; /* Adjust as desired */
}
</style>
