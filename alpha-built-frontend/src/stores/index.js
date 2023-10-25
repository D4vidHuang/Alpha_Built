import {reactive} from 'vue'
import { defineStore } from 'pinia'
import {get} from "@/net";

export const useStore = defineStore('store', () => {
    const auth = reactive({
        user: null
    })
    return { auth }
})
// export const useStore = defineStore('store', {
//     state: () => ({
//         auth: {
//             user: null,
//             isFetching: false,
//             fetchComplete: false,
//         }
//     }),
//     actions: {
//         fetchUser() {
//             if (this.auth.isFetching) {
//                 return this.auth.fetchUserPromise;
//             }
//
//             this.auth.isFetching = true;
//             this.auth.fetchUserPromise = new Promise((resolve, reject) => {
//                 get('api/account/me', (message) => {
//                     this.auth.user = message;
//                     this.auth.isFetching = false;
//                     this.auth.fetchComplete = true;
//                     resolve();
//                 }, () => {
//                     this.auth.user = null;
//                     this.auth.isFetching = false;
//                     this.auth.fetchComplete = true;
//                     reject();
//                 });
//             });
//
//             return this.auth.fetchUserPromise;
//         }
//     }
// });