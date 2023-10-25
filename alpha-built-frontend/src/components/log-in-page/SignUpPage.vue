<template>
      <div style="text-align: center;padding: 0 20px">
        <div style="margin-top: 60%">
          <div style="font-size: 25px; color: black; font-weight: bold">Sign Up</div>
          <div style="font-size: 14px; color: gray">
            Welcome to sign up for our platform. Please fill in the information.
          </div>
        </div>

        <!--Input Fields-->
        <div>

          <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">

            <el-form-item prop="username">
              <!--Username-->
              <el-input v-model="form.username" type="text" placeholder="Username" style="margin-top: 30px">
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item prop="password">
              <!--Password-->
              <el-input v-model="form.password" show-password type="password" placeholder="Password">
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item prop="password_confirm">
              <!--Password confirm-->
              <el-input v-model="form.password_confirm" show-password type="password" placeholder="Confirm Password">
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item prop="email">
              <!--Email-->
              <el-input v-model="form.email" type="email" placeholder="E-mail">
                <template #prefix>
                  <el-icon><Message /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item>
              <el-row :gutter="10" style="width: 100%;">
                <el-col :span="17">
                  <!--code-->
                  <el-input v-model="form.code" disabled type="text" placeholder="E-mail Verification code">
                    <template #prefix>
                      <el-icon><EditPen /></el-icon>
                    </template>
                  </el-input>
                </el-col>
                <el-col :span="7">
                  <el-button style="width: 100%" disabled type="success" :disabled="!isEmailValid">Get Code</el-button>
                </el-col>
              </el-row>
            </el-form-item>

          </el-form>
        </div>
        <div style="margin-top: 80px">
          <el-button @click="signUp" style="width: 200px" type="warning" plain>Sign up</el-button>
        </div>
        <div style="margin-top: 10px">
          <span style="font-size: 14px;color: grey">Already has an account? </span>
          <el-link type="primary" style="translate: 0 -2px" @click="router.push('/auth/log-in')">Log in here</el-link>
        </div>

      </div>
</template>

<script setup>

import router from "@/router";
import {Lock, User, Message, EditPen} from "@element-plus/icons-vue";
import {post} from "@/net";
import {reactive, ref} from "vue";
import {ElMessage} from "element-plus";

// A `reactive` form object holding the registration form fields
// This includes fields for `username`, `password`, `password_confirm`, `email`, and `code`
const form = reactive({
  username: '',
  password: '',
  password_confirm: '',
  email: '',
  code: ''
})

// `validateUsername` is a custom validation method that checks if the username
// isn't empty and doesn't contain special characters.
const validateUsername = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('Please fill in the username'))
  } else if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
    callback(new Error('Username can\'t have special characters'))
  } else {
    callback()
  }
}

// `validatePassword` is a custom validation method that checks if the password
// confirmation matches with the original password.
const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('Please enter the password again'))
  } else if (value !== form.password) {
    callback(new Error('The passwords don\'t match.'))
  } else {
    callback()
  }
}

// `isEmailValid` is a reactive property that holds the validation state of the email field
const isEmailValid = ref(false);

// `rules` is an object that contains validation rules for form fields
const rules = {
  username: [
    { validator: validateUsername, trigger: ['blur', 'change']},
    { min: 5, max: 16, message: 'Username\'s length should be 5 to 16', trigger: ['blur', 'change'] },
  ],
  password: [
    { required: true, message: 'Please enter the password', trigger: ['blur', 'change']},
    { min: 6, max: 16, message: 'Password\'s length should be 6 to 16', trigger: ['blur', 'change'] }
  ],
  password_confirm: [
    { validator: validatePassword, trigger: ['blur', 'change']}
  ],
  email: [
    {
      required: true,
      message: 'Please enter email address',
      trigger: ['blur', 'change']
    },
    {
      type: 'email',
      message: 'Please input valid email address',
      trigger: ['blur', 'change'],
    }
  ]
}

// `onValidate` is a method that updates `isEmailValid` based on the validation status of the email field
const onValidate = (prop, isValid) => {
  if (prop === 'email') {
    isEmailValid.value = isValid
  }
}

// `formRef` holds the reference to the form component
const formRef = ref()

// `signUp` is a method that validates form entries, and if they are valid, sends them to the server for registration
// Upon successful registration, a success message is displayed and the user is redirected to the login page
const signUp = () => {
  formRef.value.validate((isValid) => {
  //   If the form entries are all valid, send to backend
    if (isValid) {
      post('api/auth/sign-up', {
        username: form.username,
        password: form.password,
        email: form.email
      }, (message) => {
        // console.log("I am here!")
        ElMessage.success(message)
        router.push('/auth/log-in')
        // this.$forceUpdate();
      })
    } else {
      ElMessage.warning('Please complete the form!')
    }
  })
}

</script>

<style scoped>

</style>