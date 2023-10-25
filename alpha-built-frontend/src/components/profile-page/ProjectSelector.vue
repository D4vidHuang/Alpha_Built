<template>
    <div class="main-container">
        <div class="top-bar">
            <div class="user-info">
                <img :src="userProfileImage" alt="User profile" class="user-profile-image"/>
                <span>Welcome {{ store.auth.user.username }}!
                <el-button @click="router.push('/')" type="primary" :icon="House"  circle/></span>
            </div>

            <el-button @click="logout" type="danger" plain class="logout-button">Log out</el-button>
        </div>
        <div class="content-area">
            <div class="project-container">
                <div style="display: flex; align-items: center; justify-content: space-between;">
                  <h1>Select a Project</h1>
                  <el-button @click="updateUserProjects" type="primary"
                             :icon="RefreshRight" size="large" circle>
                  </el-button>
                </div>
                <ul class="project-list">
                    <li v-for="project in projects" :key="project.id">
                      <el-button type="success" plain
                                 style="margin-top: 5px;margin-bottom: 5px"
                                 size="large"
                                 :icon="School"
                                 tag="router-link" :to="`/project/${project.id}`">Project - {{ project.id }}
                      </el-button>
                    </li>
                </ul>
            </div>
            <div class="create-project-container">
                <h2>No project available? Create your own!</h2>
                <el-button @click="createNewProject" :icon="Plus">Create Project</el-button>
            </div>
        </div>
    </div>
</template>


<script setup>

import {get} from '@/net'
import {ElMessage} from "element-plus";
import router from "@/router";
import { ref } from 'vue';
import {useStore} from "@/stores";
import { onMounted } from 'vue';
import {RefreshRight, Plus, School, House} from "@element-plus/icons-vue";


const store = useStore()

const testClickOnId = (id) => {
  console.log(id)
}

// Same logout function as before
const logout = () => {
  get('/api/auth/logout',(message) => {
    ElMessage.success(message)
    store.auth.user = null
    router.push('/')
  })
}

// User profile image
import md5 from 'blueimp-md5';
let userProfileImage = `https://gravatar.com/avatar/${md5(store.auth.user.username)}?d=identicon`;

const updateUserProjects = async () => {
  await getAndUpdateUserInfo();
  placeUserProjects();
  console.log(store.auth.user.userRolesInProjects)
}

const placeUserProjects = () => {
  if (store.auth.user && store.auth.user.userRolesInProjects) {
    projects.value = store.auth.user.userRolesInProjects.map(p => ({id: p.projectId}));
  }
}

// Fetch projects from API
const getAndUpdateUserInfo = () => {
  return new Promise((resolve, reject) => {
    get('api/account/me', (message) => {
      store.auth.user = message;
      resolve();
    }, () => {
      store.auth.user = null;
      ElMessage.warning('未登录');
      reject();
    })
  });
}

const createNewProject = () => {
  // 1. step 1, get the account_id and use this as user_id in the project
  let userId = store.auth.user.id;
  // 2. step 2.1, get the next_available project_id.
  get('/api/account/create-new-project', (message) => {
    let projectId = message;
    // 3. step 2.2, when get the project_id, also create a project with that id on the backend.
    // 4. step 2.3, use the project_id returned from backend to create the button for that project.
    // 5. step 2.4, add the project_id to the userRolesInProjects for that account.
    // 6. step 3, add a button for the newest project.
    updateUserProjects();
    ElMessage.success('创建Project - ' + projectId + ' 成功')
  },() => {
    ElMessage.warning('创建失败')
  })
  console.log('createNewProject')
}

// placeholder for projects
let projects = ref();

onMounted(updateUserProjects);

</script>

<style scoped>
.main-container {
    display: flex;
    flex-direction: column;
    height: 100vh; /* viewport height */
}

.top-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px;
    border-bottom: 1px solid #ccc;
}

.user-info {
    display: flex;
    align-items: center;
}

.user-profile-image {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    margin-right: 10px;
}

.logout-button {
    position: absolute;
    top: 20px;
    right: 20px;
}

.content-area {
    display: flex;
    justify-content: center;
    align-items: center;
    height: calc(100vh - 60px);
    padding: 20px;
}

.project-container, .create-project-container {
    flex: 1;
    border: 1px solid #ccc;
    margin: 20px;
    padding: 20px;
    max-height: 500px;
    overflow: auto; /* Add a scrollbar if necessary */
}

.project-list {
  list-style-type: none;
  padding-left: 0px;
}

</style>
