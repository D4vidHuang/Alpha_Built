<template>
    <div class="align-justify-icon">
        <img class="vector" src="@/assets/align-justify.svg" alt="alignJustifyIcon"/>
    </div>
</template>

<script>
export default {
    name: 'AlignJustifyIcon',
}
</script>

<style scoped>
.align-justify-icon {
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
}

.align-justify-icon .vector {
    width: 24px;
    height: 24px;
}
</style>
