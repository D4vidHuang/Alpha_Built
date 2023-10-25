<template>
    <div class="eye">
        <img class="vector" src="@/assets/eye.svg" alt="eye"/>
    </div>
</template>

<script>
export default {
    name: "eye"
}
</script>

<style scoped>
.eye {

    width: 39px;
    height: 40px;


    flex: none;
    order: 2;
    flex-grow: 0;
}
.eye > :hover {
  background-color: #D3D3D3;
}
</style>