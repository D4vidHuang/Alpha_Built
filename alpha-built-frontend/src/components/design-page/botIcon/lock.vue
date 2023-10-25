<template>
    <div class="lock" id="lock-icon">
        <img class="vector" src="@/assets/lock.svg" alt="lock"/>
    </div>
</template>

<script>
export default {
    name: "lock"
}
</script>

<style scoped>
.lock {

    width: 37px;
    height: 37px;

    flex: none;
    order: 3;
    flex-grow: 0;
}

.lock > :hover {
  background-color: #D3D3D3;
}
</style>