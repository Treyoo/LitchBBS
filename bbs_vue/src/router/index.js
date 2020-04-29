import Vue from 'vue'
import VueRouter from 'vue-router'
import UserManage from '../views/UserManage'
import AddBook from '../views/AddBook'
import Index from '../views/Index'
import BookUpdate from '../views/BookUpdate'

Vue.use(VueRouter)

const routes = [
  {
    path:"/",
    name:"用户管理",
    component:Index,
    show:true,
    redirect:"/UserManage",
    children:[
      {
        path:"/UserManage",
        name:"查询用户",
        component:UserManage
      },
      {
        path:"/AddBook",
        name:"添加用户",
        component:AddBook
      }
    ]
  },
  {
    path:'/update',
    component:BookUpdate,
    show:false
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
