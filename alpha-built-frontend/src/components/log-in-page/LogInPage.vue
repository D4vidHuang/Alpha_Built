<template>
      <div style="text-align: center;padding: 0 20px">
        <div style="margin-top: 60%">
          <div style="font-size: 25px; color: black; font-weight: bold">Login</div>
          <div style="font-size: 14px; color: gray">
            Please enter your username and password for login before accessing the system.
          </div>
        </div>

        <div style="margin-top: 30px">
          <!--账号 / 邮箱-->
          <el-input v-model="form.username" type="text" placeholder="Username / E-mail">
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
          <!--密码-->
          <el-input v-model="form.password" show-password type="password" placeholder="Password" style="margin-top: 10px">
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </div>

        <el-row>
          <el-col :span="12" style="text-align: left">
            <el-checkbox v-model="form.remember" label="Remember me"/>
          </el-col>
          <el-col :span="12" style="text-align: right; margin-top: 5.5px; font-size: 12px">
            <el-link>Forgot your password?</el-link>
          </el-col>
        </el-row>

        <!--登录按钮-->
        <div style="margin-top: 20px">
          <el-button @click="login" style="width: 200px" type="success" plain>Login</el-button>
        </div>
        <!--分割线-->
        <el-divider>
          <span style="color: gray;font-size: 12px">Haven't signed up yet?</span>
        </el-divider>
        <!--注册按钮-->
        <div>
          <el-button @click="router.push('/auth/sign-up')" style="width: 200px" type="warning" plain>
            Sign up
          </el-button>
        </div>
      </div>
</template>

<script setup>

import {User, Lock} from '@element-plus/icons-vue'
import {get, post} from '@/net'
import {reactive} from "vue";
import {ElMessage} from "element-plus";
import router from "@/router";
import {useStore} from "@/stores";

// This `reactive` object stores the state of the form that is used for login
// `username` and `password` will contain the login credentials entered by the user
// `remember` is a boolean that will be `true` if the user wants their login information to be remembered
const form = reactive({
  username: '',
  password: '',
  remember: false
})

const store = useStore()

// `login` is a method that is called when the user attempts to log in
// It first checks if both `username` and `password` fields are filled
// If either field is empty, it will display a warning message using `ElMessage.warning`
// If both fields are filled, it makes a POST request to '/api/auth/login' with the form data
// Upon successful login, a success message is shown using `ElMessage.success` and the user is redirected to '/index'
// If the login fails, the corresponding error message will be displayed
const login = () => {
  // Check if the `username` and `password` fields are filled
  if (!form.username || !form.password) {
    // Display warning message if either `username` or `password` field is empty
    ElMessage.warning("Please fill in both the username and the password!")
  } else {
    // If both fields are filled, make a POST request to the login API
    post('/api/auth/login', {
      username: form.username,
      password: form.password,
      remember: form.remember
    }, (message) => {
      ElMessage.success(message)
      // Upon successful login, display a success message and redirect the user to '/index'
      get('api/account/me', (message) => {
        console.log("here too")
        store.auth.user = message
        router.push('/index')
      }, () => {
        console.log("here")
        console.log(store)
        store.auth.user = null
      })
    })
  }
}

</script>

<style scoped>

</style>