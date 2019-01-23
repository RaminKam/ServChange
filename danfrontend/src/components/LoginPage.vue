<template>
<v-layout justify-center>
  <v-flex xs8 sm5 lg4 md5 mt-5>
    <p class="text-centerX">
      <img width="200px" src="/static/logo.png">
    </p>
    <v-dialog v-model="forgetDialog" max-width="500px">
    <v-card>
      <v-toolbar>
        <v-toolbar-title>{{$t("forgetpassword.form")}}</v-toolbar-title>
      </v-toolbar>

      <v-card-text>
        <v-form v-model="valid" ref="fgForm" lazy-validation>

          <v-text-field
            :counter="100" required :rules="rules.email"
            v-model="forgetForm.mail" prepend-icon="email" :label="$t('register.email')"
            type="text"></v-text-field>
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn  active-class="red-text" @click="forgetPassword" :loading="loading"
        flat  round color="#01B1C1" outline
        ><span style="font-size: 12px" class="black--text">
          {{$t('right.reset')}}
          </span>
        </v-btn>
        <v-btn   @click.native="forgetDialog=false;"
        flat  round color="#01B1C1" outline>
          <span style="font-size: 12px" class="black--text">
          {{$t('base.cancel')}}
          </span>
        </v-btn>
      </v-card-actions>


    </v-card>
  </v-dialog>
    <v-dialog v-model="resetPasswordDialog" max-width="500px">
      <v-card>
        <v-toolbar>
          <v-toolbar-title>{{$t("resetpassword.form")}}</v-toolbar-title>
        </v-toolbar>

        <v-card-text>
          <v-form v-model="valid" ref="resForm" lazy-validation>

            <v-text-field
              :counter="100" required :rules="rules.email"
              v-model="resetPasswordForm.email" prepend-icon="email" :label="$t('register.email')"
              type="text"></v-text-field>

            <v-text-field
              :counter="100" required :rules="rules.password"
              min="8"
              counter
              :append-icon="e1 ? 'visibility' : 'visibility_off'"
              :type="e1 ? 'password' : 'text'"
              :append-icon-cb="() => (e1 = !e1)"
              v-model="resetPasswordForm.password" prepend-icon="security" name="login" :label="$t('register.password')"
            ></v-text-field>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn   @click.native="resetPasswordDialog=false;"
          flat  round color="#01B1C1" outline
          ><span style="font-size: 12px" class="black--text">
            {{$t('base.cancel')}}
            </span>
          </v-btn>
          <v-btn active-class="red-text" @click="resetPassword" :loading="loading"
          flat  round color="#01B1C1" outline
          ><span style="font-size: 12px" class="black--text">
            {{$t('right.resetPass')}}
            </span>
          </v-btn>

        </v-card-actions>


      </v-card>
    </v-dialog>
    <v-form>
      <v-text-field v-model="username" prepend-icon="person" name="login" :label="$t('login.login')"
                    type="text"></v-text-field>
      <v-text-field v-model="password" id="password" prepend-icon="lock" name="password" :label="$t('login.password')"
                    type="password"></v-text-field>
    </v-form>

    <v-spacer></v-spacer>
    <v-btn  active-class="red-text" @click="userSignIn"
    flat  round color="#01B1C1" outline
    >
    <span style="font-size: 12px" class="black--text">
      {{$t('right.login')}}
      </span>
      </v-btn>
    <v-btn  active-class="red-text" to="/register"
    flat  round color="#01B1C1" outline
    >
    <span style="font-size: 12px" class="black--text">
    {{$t('right.register')}}
    </span>
    </v-btn>
    <v-btn  active-class="red-text" @click="forgetDialog=true"
    flat  round color="#01B1C1" outline
    >
    <span style="font-size: 12px" class="black--text">
      {{$t('right.forget')}}
      </span>
    </v-btn>

  </v-flex>
</v-layout>
</template>

<script>
  import {EventBus} from '@/event-bus'
  import router from "@/router";
  import axios from 'axios'
  import {loadValidations} from '@/mixins/validation-rules'
  export default {
    data () {
      return {
        $_veeValidate: {
          validator: 'new'
        },
        valid: false,
        resetPasswordDialog: false,
        username: '',
        password: '',
        forgetForm: {
          mail: ""
        }, resetPasswordForm: {
          mail: "",
          password: ""
        },
        rules: loadValidations(this),
        alert: false,
        e1:true,
        forgetDialog: false,
        loading: false
      }
    },
    props: ['verificationCode'],
    mounted(){



    },
    created(){
      if (this.$route.params.verificationCode)
        this.resetPasswordDialog = true;
    },
    computed: {
      error () {
        return this.$store.getters.getError
      }

    },
    watch: {
      error (value) {
        if (value) {
          this.alert = true
        }
      },
      alert (value) {
        if (!value) {
          this.$store.dispatch('setError', false)
        }
      }
    },
    mounted() {
      EventBus.$emit('change-title', "login.form");
    },
    methods: {
      resetPassword(){
        if (!this.$refs.resForm.validate())
          return;
        this.loading = true;
        axios.post('/api/register/resetPassword',
          {
            "mail": this.resetPasswordForm.email,
            "password": this.resetPasswordForm.password,
            "verify": this.$route.params.verificationCode
          }
        ).then(res => {

          if (res.data.success) {
            EventBus.$emit('show-message', {color: "info", text: "reset.success"});
            router.push('/login');
            this.resetPasswordDialog = false;
            this.loading = false; router.push('/login');
          }
        })
          .catch(error => {
            this.loading = false;
            EventBus.$emit('error', error);
          })
      },
      forgetPassword(){
        if (!this.$refs.fgForm.validate())
          return;
        this.loading = true;
        axios.post('/api/register/forgetPassword',
          this.forgetForm
        ).then(res => {

          if (res.data.success) {
            EventBus.$emit('show-message', {color: "info", text: "restore.success"});

            this.forgetDialog = false;
            this.loading = false; router.push('/login');
          }
        })
          .catch(error => {
            this.loading = false;
            EventBus.$emit('error', error);
          })
      },
      userSignIn () {
        this.$auth.login({
          data: {usernameOrEmail: this.username, password: this.password},
          rememberMe: true
        }).then(() => {
          router.push('/');
        }).catch((error) => {
          //console.log('error ',res);
          EventBus.$emit('error', error);
        });
      }
    }
  }
</script>


<style scoped>
  p.text-centerX {
    text-align: center !important;
    text-indent: 0px !important;
  }
</style>
