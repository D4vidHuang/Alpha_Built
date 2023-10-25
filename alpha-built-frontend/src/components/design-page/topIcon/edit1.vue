<template>
    <div class="edit-icon">
        <img class="vector" src="@/assets/edit.svg" alt="editIcon"/>
    </div>
</template>

<script>
export default {
    name: "editIcon"
}
</script>

<style scoped>
.edit-icon {
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
}

.edit-icon .vector {
    width: 24px;
    height: 24px;
}
</style>