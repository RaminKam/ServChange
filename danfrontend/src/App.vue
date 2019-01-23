<template>
  <div id="app">

    <v-app id="danApplication">

<template v-if="ram_is_show_menu">

      <v-dialog v-model="askSupportDialog" max-width="500px">
        <v-card>
          <v-toolbar>
            <v-toolbar-title>{{$t("asksupport.form")}}</v-toolbar-title>
          </v-toolbar>

          <v-card-text>
            <v-form v-model="valid" ref="fgForm" lazy-validation>

              <v-text-field multi-line
                            required
                            v-model="askSupport.message" prepend-icon="contact_support" :label="$t('support.question')"
                            type="text"></v-text-field>
            </v-form>
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn  active-class="red-text" @click="askQuestion" :loading="loading"
            flat round color="#01B1C1" outline
            ><span style="font-size: 12px" class="black--text">
              {{$t('right.ask')}}
              </span>

            </v-btn>
            <v-btn   @click.native="askSupportDialog=false;"
            flat round color="#01B1C1" outline
            >
              <span style="font-size: 12px" class="black--text">
              {{$t('base.cancel')}}
              </span>
            </v-btn>
          </v-card-actions>


        </v-card>
      </v-dialog>
</template>

<template v-if="ram_is_show_menu"> 
      <v-navigation-drawer v-if="loaded" v-model="drawer" fixed right app>

        <v-list dense light>
          <v-flex ml-3 mt-3 mb-3 xs12 align-start justify-start text-xs-left
                  v-if="$auth.check('ROLE_USER')&&$auth.user()&&$auth.user().balance">
            <b>{{$t('wallet.balance')}}: </b>{{$auth.user().balance.balance}} DNC<br>
            <template v-if="$auth.user().balance.credit &&$auth.user().balance.credit>0">
              <b>{{$t('wallet.credit')}}: </b>{{$auth.user().balance.credit | formatMoney}}<br></template>
            <b>{{$t('wallet.blocked')}}: </b>{{$auth.user().balance.blocked | formatMoney}}<br>

            <b>{{$t('user.feedbackCount')}}</b>:{{$auth.user().feedbackCount}}<br>
            <b>{{$t('user.servicesCount')}}</b>:{{$auth.user().servicesCount}}<br>
            <b>{{$t('user.dealsBuyCount')}}</b>:{{$auth.user().dealsBuyCount}}<br>
            <b>{{$t('user.dealsSellCount')}}</b>:{{$auth.user().dealsSellCount}}<br>

            <template v-if="$auth.check('ROLE_CURATOR')">
              <b>{{$t('user.rate')}}</b>:
              {{$auth.user().rate}}<br>
              <b>{{$t('user.curatorClaims')}}</b>:
              {{$auth.user().curatorClaims}}<br>
              <b>{{$t('user.curatorAppoveClaims')}}</b>:
              {{$auth.user().curatorAppoveClaims}}<br>
              <b>{{$t('user.curatorRejectClaims')}}</b>:
              {{$auth.user().curatorRejectClaims}}<br>
              <b>{{$t('user.claimToCuratorWork')}}</b>:
              {{$auth.user().claimToCuratorWork}}<br>

            </template>
            <v-flex v-if="$auth.check('ROLE_USER') && $auth.user().status!='APPROVED'">
              <B> {{$t('profile.moderatestatus')}}: </B>{{$t('admin.moderate.result.' + $auth.user().status)}}
            </v-flex>
            <v-flex v-if="!$auth.user().active">
              <B class="red--text"> {{$t('profile.usernotactive')}} </B>
            </v-flex>
          </v-flex>


          <v-list-tile v-show="$auth.check()&&!$auth.check('ROLE_USER')" :to="adminZoneUrl" @click="drawer=false;">
            <v-list-tile-action>
              <v-icon>face</v-icon>
            </v-list-tile-action>
            <v-list-tile-content>
              <v-list-tile-title>{{$t('admin.adminzone')}}</v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>
          <v-list-tile v-show="$auth.check()" to="/profile" @click="drawer=false;">
            <v-list-tile-action>
              <v-icon>person</v-icon>
            </v-list-tile-action>
            <v-list-tile-content>
              <v-list-tile-title>{{$t('right.profile')}}</v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>
          <v-list-tile v-show="$auth.check()&&!$auth.check('ROLE_USER')" to="/admin/chat" @click="drawer=false;">
            <v-list-tile-action>
              <v-icon>chat</v-icon>
            </v-list-tile-action>
            <v-list-tile-content>
              <v-list-tile-title>{{$t('right.chat')}}</v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>

          <v-list-tile v-show="!$auth.check()" to="/register" @click="drawer=false;">
            <v-list-tile-action>
              <v-icon>assignment_turned_in</v-icon>
            </v-list-tile-action>
            <v-list-tile-content>
              <v-list-tile-title>{{$t('right.register')}}</v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>
          <v-list-tile v-show="!$auth.check()" to="/login" @click="drawer=false;">
            <v-list-tile-action>
              <v-icon>person</v-icon>
            </v-list-tile-action>
            <v-list-tile-content>
              <v-list-tile-title>{{$t('right.login')}}</v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>
          <v-list-tile @click="drawer=false;askSupportDialog=true">
            <v-list-tile-action>
              <v-icon>contact_support</v-icon>
            </v-list-tile-action>
            <v-list-tile-content>
              <v-list-tile-title>{{$t('right.support')}}</v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>
          <v-list-group v-if="langs.length>1"

                        no-action
          >
            <v-list-tile slot="activator">
              <v-list-tile-action>
                <v-icon>language</v-icon>
              </v-list-tile-action>
              <v-list-tile-content>
                <v-list-tile-title>{{$t('langs.lang')}}: {{$t('langs.' + $i18n.locale)}} </v-list-tile-title>
              </v-list-tile-content>
            </v-list-tile>
            <v-list-tile v-for="subItem in langs" :key="subItem.title" @click="switchLang(subItem);">
              <v-list-tile-action>
                <v-icon v-if="subItem.lang==$i18n.locale">label</v-icon>
              </v-list-tile-action>
              <v-list-tile-content>
                <v-list-tile-title>{{$t('langs.' + subItem.lang)}} </v-list-tile-title>
              </v-list-tile-content>

            </v-list-tile>
          </v-list-group>
        </v-list>
        <v-list-tile v-show="$auth.check()" @click="drawer=false;logout()">
          <v-list-tile-action>
            <v-icon>exit_to_app</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>{{$t('right.logout')}}</v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>
        <v-list-tile v-show="$auth.check()" @click="drawer=false;">
          <v-list-tile-action>
            <v-icon>close</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>{{$t('right.close')}}</v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>

      </v-navigation-drawer>


      <v-toolbar color="white" height="64" fixed app class="ramin1">


        <router-link :to="'/'">
<!-- <img class="logoim" height="50px" src="@/assets/logo2.png"> -->

          <!-- <img class="logoim" height="50px" src="@/assets/main_page/dan_logo.png"> -->

          <v-img :src="require('@/assets/main_page/dan_logo.png')"
             aspect-ratio="0.69" width="40" ></v-img>

        </router-link>

        <v-toolbar-title>
          <v-layout column>
            <v-flex pb-1>
              <router-link :to="'/'">
              <div style="font-size: 20px;" class="yaFont black--text" align="left">Deeds & Needs</div>
              </router-link>
               <!-- <div class="title ">Deeds & Needs</div> -->
            </v-flex>
            <v-flex>
              <div style="font-size: 10px;">{{$t(title)}}</div>
            </v-flex>
          </v-layout>
          
          
        </v-toolbar-title>
        <v-spacer></v-spacer>
        
        <v-toolbar-side-icon @click.stop="updateUser();drawer = !drawer" >

        </v-toolbar-side-icon>
                
      </v-toolbar>

      
</template>

  
      <v-content>


        <div v-if="$auth.ready() && loaded">
          <v-flex xs12 class="text-xs-center">
            <!-- <v-layout justify-center> -->
              <!-- <v-flex xs12 md10 lg10 align-center align-content-center> -->
                <!-- <v-content> -->
                  <!-- <v-container fluid> -->
                    <!-- <v-layout row wrap justify-center> -->
                      <router-view></router-view>
                    <!-- </v-layout> -->
                  <!-- </v-container> -->
                <!-- </v-content> -->
              <!-- </v-flex> -->

            <!-- </v-layout> -->
          </v-flex>


        </div>
        <div v-if="!$auth.ready() || !loaded">
          <v-container fluid fill-height>
            <v-layout align-center justify-center>
              <v-progress-circular indeterminate color="primary"></v-progress-circular>
            </v-layout>
          </v-container>
        </div>
      </v-content> 


<template v-if="ram_is_show_menu"> 
      <v-bottom-nav z-index="50"
                    v-if="$auth.check('ROLE_USER')&&$auth.user().status=='APPROVED'&&$auth.user().active && loaded"
                    :value="true" color="white" :app="true" fixed>
        <v-btn flat color="teal" to="/services">
          <!--   <span>SD</span>-->
          <!--<v-icon>shopping_cart</v-icon>-->
          <img width="24" src="@/assets/recruitment.png">
        </v-btn>
        <v-btn flat color="teal" to="/myservices">
          <!--  <span>MS</span>-->
          <!--  <v-icon>list</v-icon>-->
          <img width="24" src="@/assets/noun_services_1546238.png">
        </v-btn>
        <v-btn flat color="teal" to="/mydeals">
          <!-- <span>MD</span>-->
          <!--     <v-icon>pan_tool</v-icon>-->
          <img width="24" src="@/assets/shake-hands.png">
        </v-btn>
        <v-btn flat color="teal" to="/wallet">
          <!--  <span>W</span>-->
          <!--    <v-icon>attach_money</v-icon>-->
          <img width="24" src="@/assets/noun_Coins_823469.png">
        </v-btn>
        <v-btn flat color="teal" to="/about">
          <!--<span>A</span>-->
          <!--<v-icon>help_outline</v-icon>-->
          <img width="24" src="@/assets/noun_questions_551302.png">
        </v-btn>
      </v-bottom-nav>
      <v-snackbar
        :timeout="5000"
        :color="message.color"
        :multi-line="'multi-line'"
        :vertical="'vertical'"
        v-model="snackbar"
      >
        {{ $t(message.text) }}
        <v-btn flat @click.native="snackbar = false">Close</v-btn>
      </v-snackbar>
</template>
    </v-app>
    <data-load ref="loader" suburl="/api/register/common" v-model="data" @loaded="loadedData"
    ></data-load>
        
        
  </div>
</template>

<script>
  import SockJS from "sockjs-client";
  import Stomp from "webstomp-client";
  import CommonMixin from '@/mixins/common';
  import router from "@/router";
  import axios from 'axios'
  import {EventBus} from './event-bus.js'
  import {loadLanguageAsync} from './setup/i18n-setup'
  import DataLoad from '@/common/data-loader'
  export default {

    name: 'App',
    components: {
      'data-load': DataLoad,
    },
    mixins: [CommonMixin],
    data() {
      return {
        title: "title",
        drawer: false,
        snackbar: false,
        askSupportDialog: false,
        data: [],
        message: {text: "", color: "info"},
        askSupport: {
          message: ""
        },
        stompSubscribe: [],
        loaded: false,
        loading: false,
        connected: false,
        listeners: [],
      }
    },
    watch: {

      '$route' (to, from) {
        this.connectSock();
        this.$auth.fetch();
      },
    },
    mounted() {
      // console.log('rammmmmmmmm2323233',this.$route.path);
        
        // if(this.$route.path=='/' )
        // {
        //   //router.push('/login');
        //   console.log('It s roor=',this.$route.path);
          
        // } else {
        //   console.log('It s not roor=',this.$route.path);

        // }

    },
    created: function () {
      this.connectSock();
      EventBus.$on('listen-socket', soketId => {
        this.subSocket(soketId);
      });
      EventBus.$on('remove-listen-socket', soketId => {
        this.unsubSocket(soketId);
      });
      EventBus.$on('show-message', message => {
        this.message = message;
        this.snackbar = true;
      });
      EventBus.$on('change-title', title => {
        this.title = title;
      });

      EventBus.$on('loading', () => {
        this.loaded = false;
        
      });
      EventBus.$on('loaded', () => {
        this.loaded = true;
        
      });
      EventBus.$on('error', error => {

        console.log(error.response);
        this.message = {
          color: "error",
          text: "messages." + error.response.data.message
        };
        this.snackbar = true;
      });
    },
    computed: {


      ramin_to:function(){
        
        return 1;
      },
ram_is_show_menu:function(){
      //console.log('path=',this.$route.path);
      //console.log('res=',this.$route.path!='/');
      return (
        this.$route.path!='/' 
        && this.$route.path!='/ourteampage'
        && this.$route.path!='/aboutuspage'
        && this.$route.path!='/rulespolicypage'
        && this.$route.path!='/faqpage'
        && this.$route.path!='/contactspage'
      );
    }

    },
    methods: {
      askQuestion(){
        this.loading = true;
        axios.post('/api/user/askQuestion',
          this.askSupport
        ).then(res => {

          if (res.data.success) {
            EventBus.$emit('show-message', {color: "info", text: "messages.asksuccess"});

            this.askSupportDialog = false;
            this.loading = false;
          }
        })
          .catch(error => {
            this.loading = false;
            EventBus.$emit('error', error);
          })
      },
      updateUser(){
        if (this.$auth.check())
          this.$auth.fetch();
      },
      loadedData(data){
        this.$store.dispatch('setCommonData', data);
        this.loaded = true;
      },
      switchLang(lang){
        loadLanguageAsync(lang.lang);
      },
      logout() {
        this.$auth.logout({makeRequest: false, redirect: '/login',});
      },

      subSocket(sId){
        if (this.stompClient) {
          console.log("subscribe to " + sId);
          this.stompSubscribe.push(sId);
          this.resubscribe();

        }

      },
      resubscribe(){
        console.log("resubscribe", this.stompSubscribe);
        this.stompSubscribe.forEach((sId) => {
          this.stompClient.unsubscribe(sId);
          this.stompClient.subscribe(sId, tick => {
            console.log("emit to " + sId, tick);
            EventBus.$emit(sId, JSON.parse(tick.body));
          });
        })
      },
      unsubSocket(sId){
        if (this.stompClient)
          this.stompClient.unsubscribe(sId);
      },
      connectSock() {

        if (!this.connected) {

          let token = this.$auth.token();
          console.log("Connect to socket" + token);

          if (token) {
            let url =  "/api/wssock";
            this.socket = new SockJS(url + "?token=" + token);
            this.stompClient = Stomp.over(this.socket);
            this.stompClient.connect(
              {'X-Authorization': 'token'},
              frame => {
                this.connected = true;
                console.log("FRAME:", frame);
                console.log("SocketOpened! ");
                this.resubscribe();

              },
              error => {
                console.log(error);
                this.connected = false;
              }
            );
          }
        }
      },

    }
  }
</script>

<style scoped>
  .dg-content-footer .dg-btn--ok {
    border-color: green;
  }

  .logoim {
    padding: 5px;
  }

  .ramin1{
    box-shadow: 0 0 5px #01B1C1;
    /* box-shadow: 0 0 10px rgba(0,0,0,0); */
    /* box-shadow: 4px #01B1C1; */
  }
</style>
