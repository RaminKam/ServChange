// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from "vue";
import App from "./App";
import {i18n, loadLanguageAsync} from "./setup/i18n-setup";
import router from "./router";
import VueAxios from "vue-axios";
import Vuetify from "vuetify";

import "vuetify/dist/vuetify.min.css";
import "./css/general.css";
/*import "./stylus/main.styl";*/
import store from "./store";
import * as axios from "axios";
import colors from "vuetify/es5/util/colors";
import VuejsDialog from "vuejs-dialog";
import VueQuillEditor from "vue-quill-editor";
// require styles
import "quill/dist/quill.core.css";
import "quill/dist/quill.snow.css";
import "quill/dist/quill.bubble.css";
import StarRating from "vue-star-rating";

import flatPickr from "vue-flatpickr-component";
import "flatpickr/dist/flatpickr.css";

import VueApexCharts from "vue-apexcharts";

Vue.use(VueApexCharts);

Vue.use(flatPickr);

Vue.component('star-rating', StarRating);
Vue.use(VueQuillEditor, /* { default global options } */);

Vue.use(VueAxios, axios);
axios.defaults.withCredentials = true;

Vue.prototype.$axios = axios;
Vue.config.productionTip = false;

Vue.use(Vuetify, {
  theme: {
    info: colors.cyan.base,
  }
});

Vue.axios = axios;
Vue.router = router;
Vue.use(require('@websanova/vue-upload'));
Vue.use(require('@websanova/vue-auth'), {
  auth: require('./http/jwt'),
  http: require('@websanova/vue-auth/drivers/http/axios.1.x.js'),
  router: require('@websanova/vue-auth/drivers/router/vue-router.2.x.js')
});

router.beforeEach((to, from, next) => {
  let saved = localStorage.getItem("locale");
  //console.log("saved", saved);
  const lang = to.params.lang ? to.params.lang : ((saved && saved != "undefined" ) ? saved : "en");


  loadLanguageAsync(lang).then(() => {
    const requiresAuth = !to.matched.some(record => record.meta.nonRequiresAuth);
    const isLoginPage = to.matched.some(record => record.meta.loginPage);
    const isLanding = to.matched.some(record => record.meta.landing);
    const isRestricted = to.matched.some(record => record.meta.role && !Vue.auth.check(record.meta.role));

    const isAuthenticated = Vue.auth.check();

    if (isRestricted)
      next('/restricted');
    if (requiresAuth && !isAuthenticated) {
      next('/login')
    } else if (isLanding&& isAuthenticated|| isLoginPage) {
      if (Vue.auth.check('ROLE_USER')) {
        if (Vue.auth.user().status=='APPROVED')
          next();//router.push('/myservices');
        else router.push('/profile');
      }

      if (Vue.auth.check('ROLE_MAIN_ADMIN'))
        router.push('/admin/list/admins');
      if (Vue.auth.check('ROLE_ADMIN'))
        router.push('/admin/list/clients');
      if (Vue.auth.check('ROLE_MODERATOR'))
        router.push('/moderator_area/list/requests_for_registrations');
      if (Vue.auth.check('ROLE_CURATOR'))
        router.push('/curator_area/list');
      if (Vue.auth.check('ROLE_MAIN_CURATOR'))
        router.push('/main_curator_area/list/curators')
    }
    next()
  })


});

// Tell Vue to install the plugin.
Vue.use(VuejsDialog, {
  html: true,

  animation: 'fade',
});
let locale = localStorage.getItem("locale");
loadLanguageAsync('en');
//loadLanguageAsync(locale === null ? 'en' : locale); TODO change when langs
/* eslint-disable no-new */
new Vue({
 /* sockets: {
    connect: function () {
      console.log('socket connected')
    },
    customEmit: function (val) {
      console.log('this method was fired by the socket server. eg: io.emit("customEmit", data)')
    }
  },*/

  el: '#app',
  router,
  store,
  i18n,
  components: {App},
  template: '<App/>'
});



