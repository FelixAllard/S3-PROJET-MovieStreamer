import { createRouter, createWebHistory } from 'vue-router'

import AllMovie from '../views/MovieList.vue'
import HelloWorld from '../components/HelloWorld.vue'


const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/movies',
            component: AllMovie
        },
        {
            path: '/hello',
            component: HelloWorld
        }
    ]
})

export default router