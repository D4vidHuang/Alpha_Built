import {createRouter, createWebHistory} from 'vue-router'
import {useStore} from "@/stores";
import {ElMessage} from "element-plus";
import {get} from "@/net";

const router = createRouter(
    {
        history: createWebHistory(),
        routes: [
            {   // main-page
                path: '/',
                name: 'welcome-home-page',
                component: () => import('../components/welcome-page/WelcomePage.vue')
            },
            {
                path: '/auth/',
                name: 'login-or-signup',
                component: () => import('../components/log-in-page/LogInContainer.vue'),
                children: [
                    {
                        // No path specified for default child page
                        path: 'log-in',
                        name: 'auth-log-in',
                        component: () => import('../components/log-in-page/LogInPage.vue')
                    },
                    {
                        path: 'sign-up',
                        name: 'auth-sign-up',
                        component: () => import('../components/log-in-page/SignUpPage.vue')
                    }
                ]
            },
            {
                path: '/index',
                name: 'temporal-profile-page',
                component: () => import('../components/profile-page/ProjectSelector.vue')
            },
            {
                path: '/design-page',
                name: 'design-page',
                component: () => import('../components/design-page/DesignPage.vue')
            },
            {
                path: '/project/:id',
                name: 'project-page',
                component: () => import('../components/design-page/DesignPage.vue')
            }
        ]
    }
)

router.beforeEach(async (to, from, next) => {

    const store = useStore()
    await fetchUserData(store)

    if (store.auth.user && to.name && to.name.startsWith('auth-')) {
        // If the user is logged-in and tries to access the welcome page, redirect to profile page
        next('/index')
    } else if (store.auth.user && to.name && to.name.startsWith('design-')) {
        ElMessage.warning('Quick Start is for un-logged in users only')
        next('/index')
    } else if (!store.auth.user && to.fullPath.startsWith('/index')) {
        // If the user is not logged-in and tries to access profile page, redirect to welcome page
        next('/')
    } else if (to.matched.length === 0) {
        // If the user tries to access a non-existing page, redirect to welcome page
        next('/index')
    } else if (to.name === 'project-page' && store.auth.user) {
        const projectId = to.params.id
        if (!hasAccessToProject(store.auth.user, projectId)) {
            ElMessage.warning('You do not have access to this project')
            next('/index')
        } else if (to.fullPath === from.fullPath) {
            next(false)  // Cancel the navigation
        } else {
            next()  // Continue with the navigation
        }
    } else {
        next()
    }
})

async function fetchUserData(store) {
    if (store.auth.user == null) {
        // Wait for the fetch to complete
        return new Promise((resolve) => {
            get('api/account/me', (message) => {
                store.auth.user = message
                // ElMessage.success('已登录')
                resolve()
            }, () => {
                store.auth.user = null
                // ElMessage.warning('未登录')
                resolve()
            })
        })
    } else {
        // If user data is already available, return immediately
        return Promise.resolve()
    }
}

function hasAccessToProject(user, projectId) {
    let result = user.userRolesInProjects.some(
        (project) => {
            return project.projectId == projectId;
        }
    );
    return result
}
export default router