import { createRouter, createWebHistory } from 'vue-router'

import AllMovie from '../views/MovieList.vue';
import SingleMovie from '../views/SingleMovie.vue';
import HomePage from '../views/HomePage.vue';
import Login from '../views/Login.vue';
import AdminAllUsers from '../views/AdminAllUsers.vue';
import SignUp from '../views/SignUp.vue';
import SingleUser from '../views/SingleUser.vue';


const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/',
            component: HomePage
        },
        {
            path: '/movies',
            component: AllMovie
        },
        {
            path: '/movies/:id',
            component: SingleMovie,
            props: true
        },
        {
            path: '/login',
            component: Login
        },
        {
            path: '/signup',
            component: SignUp
        },
        {
            path: '/admin/users',
            component: AdminAllUsers,
            meta: { requiresAdmin: true }
        },
        {
            path: '/user/:id',
            component: SingleUser,
            meta: { requiresAuth: true }
        }

        /*,
        {
            path: '/hello',
            component: HelloWorld
        }*/
    ]
})

router.beforeEach((to, from, next) => {
    if (to.meta.requiresAdmin) {
        const token = localStorage.getItem('token')
        
        if (!token) {
            return next('/login')
        }

        try {
            const base64Url = token.split('.')[1]
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
            const jsonPayload = decodeURIComponent(
                atob(base64)
                    .split('')
                    .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
                    .join('')
            )
            const payload = JSON.parse(jsonPayload)
            const roles = payload.realm_access?.roles || []

            if (roles.includes('admin')) {
                next()
            } else {
                console.warn('Access denied: Missing admin role.')
                next('/movies')
            }
        } catch (err) {
            console.error('Failed to parse token in router guard:', err)
            next('/login')
        }
    } else {
        next()
    }
})

export default router