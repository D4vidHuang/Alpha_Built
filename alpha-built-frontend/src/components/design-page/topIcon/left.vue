<template>
    <div class="left" id="revert-icon">
        <img class="vector" src="@/assets/corner-up-left.svg" alt="left"/>
    </div>
</template>

<script>
export default {
    name: "left"
}
</script>

<style scoped>
.left {
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
}

.left .vector {
    width: 24px;
    /*height: 24px;*/
    //width: 48px;
    //height: 48px;
}
</style>