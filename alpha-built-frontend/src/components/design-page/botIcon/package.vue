<template>
    <div class="package" id="package-icon">
        <img class="vector" src="@/assets/package.svg" alt="package"/>
    </div>
</template>

<script>
export default {
    name: "package"
}
</script>

<style scoped>
.package {

    width: 42px;
    height: 42px;
    flex: none;
    order: 1;
    flex-grow: 0;
}

.package > :hover {
  background-color: #D3D3D3;
}
</style>