import Vue from 'vue'
import Router from 'vue-router'
import Dashboard from './views/Dashboard.vue'
import Management from './views/Management.vue'

Vue.use(Router)

export default new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [
    {
      path: '/',
      name: 'Dashboard',
      component: Dashboard,
    },
    {
      path: '/management',
      name: 'Management',
      component: Management,
    },
    {
      path: '/documentation',
      name: 'Documentation',
      component: () =>
        import(/* webpackChunkName: "documentation" */ './views/Documentation.vue'),
    },
  ],
})
