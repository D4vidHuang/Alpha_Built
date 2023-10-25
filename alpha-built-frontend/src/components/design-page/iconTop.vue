<template>
  <div class="icontop">
    <div class="left-icons">
      <div class="icon-container" id="user-image">
        <UserIcon />
      </div>
      <div class="icon-container">
        <el-button disabled plain>User ID: {{ userId }} - Project ID: {{ projectId }}</el-button>
      </div>
      <div class="icon-container">
        <Share />
      </div>
      <div class="icon-container">
        <el-button @click="showInviteDialog = true" plain>Invite User</el-button>
      </div>
    </div>
    <div class="center-icons">
      <align-justify-icon />
      <edit-icon />
      <left />
      <right />
      <trash />
      <more-vertical />
    </div>
    <div class="exit-button-container">
      <el-button
          @click="onUserLeft"
          type="success"
          id="exit-button"
          :icon="CloseBold"
          circle
      />
    </div>
    <el-dialog v-model="showInviteDialog" title="Invite User" @close="resetInviteForm">
      <el-form :model="inviteForm" :rules="rules" ref="inviteFormRef">
        <el-form-item label="Username" prop="inviteUsername">
          <el-input v-model="inviteForm.inviteUsername"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="inviteUser">Invite</el-button>
          <el-button @click="showInviteDialog = false">Cancel</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import Share from "./topIcon/share.vue";
import AlignJustifyIcon from "./topIcon/alignJustify.vue";
import editIcon from "./topIcon/edit1.vue";
import left from "./topIcon/left.vue";
import Right from "./topIcon/right.vue";
import Trash from "./topIcon/trash.vue";
import MoreVertical from "./topIcon/moreVertical.vue";
import UserIcon from "./topIcon/user.vue";
import { CloseBold } from "@element-plus/icons-vue";
import {useStore} from "@/stores";
import {useRoute} from "vue-router";
import router from "@/router";
import {post} from "@/net";
import {ElMessage} from "element-plus";

export default {
  name: "IconTop",
  methods: {
    router() {
      return router;
    },
    onUserLeft() {
      if (this.store.auth.user == null) {
        router.push("/index");
        return;
      }
      post(
          "/api/project/save-project",
          {
            projectId: this.projectId,
          },
          (message) => {
            ElMessage.success(message);
          },
          (message) => {
            ElMessage.error(message);
          }
      );
      post("api/project/remove-user",
          {
            projectId: this.projectId,
            userId: this.userId,
          },
          (message) => {
            ElMessage.success('Left the project successfully!');
          },
          (message) => {
            ElMessage.error('Failed to leave the project!');
          }
      )
      router.push("/index");
    },
    inviteUser() {
      if (this.store.auth.user == null) {
        ElMessage.error("Can't invite user in single player mode!");
        return;
      }
      post('api/account/add-user-to-project',
          {
            projectId: this.projectId,
            username: this.inviteForm.inviteUsername,
          },
          (message) => {
            ElMessage.success(message);
            this.showInviteDialog = false;
            this.resetInviteForm();
          },
          (message) => {
            ElMessage.error(message);
          }
      )
    },
    validateUsername(rule, value, callback) {
      if (value === "") {
        callback(new Error("Please fill in the username"));
      } else if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
        callback(new Error("Username can't have special characters"));
      } else {
        callback();
      }
    },
    resetInviteForm() {
      this.$refs.inviteFormRef.resetFields();
    },
  },
  data() {
    return {
      store: useStore(),
      showInviteDialog: false,
      inviteForm: {
        inviteUsername: ""
      },
      rules: {
        inviteUsername: [
          { validator: this.validateUsername, trigger: ['blur', 'change'] },
          { min: 5, max: 16, message: "Username's length should be 5 to 16", trigger: ['blur', 'change'] }
        ]
      }
    };
  },
  components: {
    UserIcon,
    MoreVertical,
    Trash,
    Right,
    editIcon,
    AlignJustifyIcon,
    Share,
    left,
  },
  computed: {
    CloseBold() {
      return CloseBold;
    },
    userId() {
      if (this.store.auth.user === null) return -1;
      // Access the user ID from the store
      return this.store.auth.user.id;
    },
    projectId() {
      if (this.store.auth.user === null) return -1;
      const route = useRoute();
      return route.params.id;
    },
  },
};
</script>

<style scoped>
.icontop {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 5vh;
  background: #009156;
  box-shadow: 0px 4px 4px rgba(0, 0, 0, 0.25);
}

.left-icons {
  display: flex;
  gap: 70px;
  align-items: center;
}

.icon-container {
  display: flex;
  align-items: center;
}

.center-icons {
  padding-right: 372px;
  display: flex;
  gap: 30px;
  flex-grow: 1;
  justify-content: center;
  align-items: center;
}

.exit-button-container {
  display: flex;
}

#exit-button {
  margin-right: 25px;
}

#user-image {
  padding-top: 2px;
}
</style>
