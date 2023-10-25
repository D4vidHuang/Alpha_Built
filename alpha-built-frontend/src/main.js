import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router';

import 'element-plus/dist/index.css'
import axios from "axios";

let baseURL;
if (navigator.userAgent.indexOf("Mac") !== -1) {
    baseURL = 'http://127.0.0.1:8080';
} else if (navigator.userAgent.indexOf("Win") !== -1) {
    baseURL = 'http://localhost:8080';
} else {
    baseURL = 'http://localhost:8080'; // Default to localhost for other platforms
}

axios.defaults.baseURL = baseURL;

// // axios.defaults.baseURL = 'http://localhost:8080'
// axios.defaults.baseURL = 'http://127.0.0.1:8080'
// // axios.defaults.baseURL = 'http://192.168.178.53:8080'

const app = createApp(App)
app.use(router)
app.use(createPinia())

app.mount('#app')
