import Vue from "vue";
import Router from "vue-router";
import NotFound from "@/components/NotFound";
import Restricted from "@/components/Restricted";
import Login from "@/components/LoginPage";
import Test from "@/components/Test";
import {clientRouter} from "./client";
import {adminRouter} from "./admin";
Vue.use(Router);
const router = new Router({
  routes: clientRouter.concat(adminRouter).concat([

    {path: '/login', component: Login, meta: {loginPage: true, nonRequiresAuth: true}},
    {path: '/login/:verificationCode', component: Login, meta: {loginPage: true, nonRequiresAuth: true}},
    {path: '/testhello', component: Test, meta: {}},
    {path: '/restricted', component: Restricted},

    {path: '*', component: NotFound}
    ])
});


export default router
