import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', name: 'home', component: () => import('../views/HomeView.vue') },
  { path: '/diy', name: 'diy-lab', component: () => import('../views/DiyLabView.vue') },
  { path: '/classic', name: 'classic-coffee', component: () => import('../views/ClassicCoffeeView.vue') },
  { path: '/my-recipes', name: 'my-recipes', component: () => import('../views/MyRecipesView.vue') },
  { path: '/share', name: 'share-square', component: () => import('../views/ShareSquareView.vue') },
  { path: '/top', name: 'top-recipes', component: () => import('../views/TopRecipesView.vue') },
  { path: '/login', name: 'login', component: () => import('../views/LoginView.vue') }
]

export default createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})
