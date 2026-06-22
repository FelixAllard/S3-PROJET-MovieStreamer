import { createRouter, createWebHistory } from 'vue-router'

import AllMovie from '../views/MovieList.vue'
import SingleMovie from '../views/SingleMovie.vue'
import HomePage from "../views/HomePage.vue";


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
        }

        /*,
        {
            path: '/hello',
            component: HelloWorld
        }*/
    ]
})

export default router