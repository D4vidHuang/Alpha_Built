<template>
    <div class="trash">
        <img class="vector" src="@/assets/trash-2.svg" alt="trash"/>
    </div>
</template>

<script>
export default {
    name: 'trash',
}
</script>

<style scoped>
.trash {
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
}

.trash .vector {
    width: 24px;
    height: 24px;
}
</style>
