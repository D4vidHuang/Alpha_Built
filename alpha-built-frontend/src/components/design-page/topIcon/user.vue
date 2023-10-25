<template>
  <div className="userIcon">
    <img class="user" :src="userProfileImage" alt="userIcon"/>
  </div>
</template>

<script>
import md5 from "blueimp-md5";
import {useStore} from "@/stores";

export default {
  name: "userIcon",
  setup() {

    const store = useStore();
    if (store.auth.user == null) {
      const userProfileImage = `https://gravatar.com/avatar/${md5('SinglePlayer')}?d=identicon`;

      return {
        userProfileImage
      };
    } else {
      // User profile image
      const userProfileImage = `https://gravatar.com/avatar/${md5(store.auth.user.username)}?d=identicon`;

      return {
        userProfileImage
      };
    }
  }
}
</script>

<style scoped>

.userIcon {
  left: 20px;
  /* user */
  position: absolute;
  /* Inside auto layout */
  flex: none;
  order: 7;
  flex-grow: 0;
  z-index: 7;
}

.user {
  width: 30px;
  height: 30px;
}
</style>
