<script setup>
import {get} from "@/net";
import {ElMessage} from "element-plus";
import {useStore} from "@/stores";
import router from "@/router";

const store = useStore()
if(store.auth.user == null) {
  get('api/account/me', (message) => {
    store.auth.user = message
    ElMessage.success('已登录')
  }, () => {
    store.auth.user = null
    ElMessage.warning('未登录')
  })
}
// const store = useStore();
//
// store.fetchUser().then(() => {
//   ElMessage.success('已登录')
//   router.push('/index')
// }).catch(() => {
//   ElMessage.warning('未登录')
// });

</script>

<template>
  <router-view></router-view>
</template>

<style scoped>

</style>
