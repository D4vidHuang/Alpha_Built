<template>
    <div class="right" id="redo-icon">
        <img class="vector" src="@/assets/corner-up-right.svg" alt="right"/>
    </div>
</template>

<script>
export default {
    name: "right"
}
</script>

<style scoped>
.right {
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
}

.right .vector {
    width: 24px;
    height: 24px;
}
</style>