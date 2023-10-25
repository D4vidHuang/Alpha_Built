<template>
    <div class="layout" id="layout-icon">
        <img class="layout" src="@/assets/layout.svg" alt="layout"/>
    </div>
</template>

<script>
export default {
    name: "layout"
}
</script>

<style scoped>
.layout {

    width: 42px;
    height: 42px;

    flex: none;
    order: 0;
    flex-grow: 0;
}

.layout > :hover {
  background-color: #D3D3D3;
}

</style>