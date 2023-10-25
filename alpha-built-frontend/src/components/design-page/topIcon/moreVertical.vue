<template>
    <div class="moreVertical">
        <img class="vector" src="@/assets/more-vertical.svg" alt="moreVertical"/>
    </div>
</template>

<script>
export default {
    name: "moreVertical"
}
</script>

<style scoped>
.moreVertical {
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
}

.moreVertical .vector {
    width: 24px;
    height: 24px;
}
</style>