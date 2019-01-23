<template>

  <v-flex xs12 class="text-xs-center">
    <v-card>

      <v-toolbar>
        <v-toolbar-title>{{user.name}} {{user.secondname}} {{$t("profile.title")}}</v-toolbar-title>
      </v-toolbar>
      <v-card-text>


        <my-upload field="img"
                   @crop-success="cropSuccess"
                   v-model="show"
                   :width="300"
                   :height="300"
                   :langType="$i18n.locale"
                   img-format="png"></my-upload>

        <v-flex xs12 headline v-if="$auth.check('ROLE_USER') &&!$auth.user().status!='APPROVED'">

          {{$t('profile.moderatestatus')}}: <B>{{$t('admin.moderate.result.' + $auth.user().status)}}</B>
          <p v-if="$auth.user().moderateComments">{{$auth.user().moderateComments}}</p>
        </v-flex>
        <v-flex v-if="!$auth.user().active">
          <B class="red--text"> {{$t('profile.usernotactive')}} </B>
        </v-flex>
        <v-form v-if="$auth.user().active" v-model="valid" ref="form" lazy-validation>
          <v-container >
            <v-layout row wrap>
              <v-flex xs12>

                <v-avatar
                  :tile="false"
                  :size="100"
                  color="grey lighten-4"
                >

                  <img height="100" style="min-height: 100px" v-if="user.avatar" :src="user.avatar">
                  <img v-else src="@/assets/nophoto.png"></v-avatar>

                <v-btn class="btn" @click="toggleShow"
                flat round color="#01B1C1" outline
                >
                  <span style="font-size: 12px" class="black--text">
                  {{$t('profile.updateava')}}
                  </span>

                  </v-btn>
              </v-flex>


              <v-flex xs12>

                <v-text-field :disabled="user.status=='APPROVED'"
                              :counter="100" required :rules="validation.name"

                              v-model="user.username" prepend-icon="person" :label="$t('register.username')"
                              type="text"></v-text-field>
                <v-text-field
                  :disabled="user.status=='APPROVED'"
                  :counter="100" required :rules="validation.email"
                  v-model="user.email" prepend-icon="email" :label="$t('register.email')"
                  type="text"></v-text-field>
              </v-flex>

              <v-flex xs6 v-if="!$auth.user().verificated" >
                <v-text-field
                  :counter="6"
                  v-model="verification_code" prepend-icon="security" :label="$t('profile.verifycode')"
                  type="text"></v-text-field>
              </v-flex>

              <v-flex xs6 v-if="!$auth.user().verificated" >
                <v-btn active-class="red-text" @click="verify"
                flat round color="#01B1C1" outline
                >
                <span style="font-size: 12px" class="black--text">
                  {{$t('base.verify')}}
                </span>
                </v-btn>
                <v-btn active-class="red-text" @click="resend"
                flat round color="#01B1C1" outline
                >
                <span style="font-size: 12px" class="black--text">
                  {{$t('base.resend')}}
                </span>
                </v-btn>
              </v-flex>
              <v-flex xs12>
                <v-text-field :disabled="user.status=='APPROVED'"
                              :counter="100" required :rules="validation.name"
                              v-model="user.name" prepend-icon="person" :label="$t('register.name')"
                              type="text"></v-text-field>

                <v-text-field :disabled="user.status=='APPROVED'"
                              :counter="100" required :rules="validation.name"
                              v-model="user.secondname" prepend-icon="person" :label="$t('register.secondname')"
                              type="text"></v-text-field>
                <v-textarea

                  v-model="user.about" prepend-icon="comment" :label="$t('register.about')"
                  type="text"></v-textarea>
                <v-text-field

                  v-model="user.city" prepend-icon="domain" :label="$t('register.city')"
                  type="text"></v-text-field>
                <v-select v-if="$auth.check('ROLE_USER')||$auth.check('ROLE_CURATOR')||$auth.check('ROLE_MAIN_CURATOR')"
                          v-model="user.univerId"
                          value="id" :disabled="user.status=='APPROVED'"
                          item-text="title" item-value="id"
                          :items="univers" prepend-icon="work"
                          :rules="validation.requiredSelect"
                          :label="$t('register.university')"
                          required

                ></v-select>
                <v-select
                  v-model="user.lang" prepend-icon="language"
                  :items="langItems"
                  :rules="validation.requiredSelect"
                  :label="$t('register.lang')"
                  required

                ></v-select>
                <v-text-field v-if="$auth.check('ROLE_USER')" :disabled="user.status=='APPROVED'"
                              :counter="100" required :rules="validation.name"
                              v-model="user.studentid" prepend-icon="credit_card" name="login"
                              :label="$t('register.studentid')"
                              type="text"></v-text-field>
                <v-text-field
                  required  :rules="validation.name"
                  v-model="user.phone" prepend-icon="chat" name="phone" :label="$t('register.phone')"
                  type="text"></v-text-field>             <v-text-field
                required  :rules="validation.name"
                v-model="user.wechatid" prepend-icon="chat" name="wechatid" :label="$t('register.wechatid')"
                type="text"></v-text-field>
                <v-text-field
                  :counter="100" :rules="validation.passwordNotReq"
                  min="6"
                  counter
                  :append-icon="e1 ? 'visibility' : 'visibility_off'"
                  :type="e1 ? 'password' : 'text'"
                  :append-icon-cb="() => (e1 = !e1)"
                  v-model="user.password" prepend-icon="security" name="login" :label="$t('profile.password')"
                ></v-text-field>
                <v-layout row wrap v-if="$auth.check('ROLE_USER')">
                  <v-flex xs12>
                    <vue-dropzone @vdropzone-file-added="addFile" @vdropzone-removed-file="removeFile" ref="uploader"
                                  id="dropzone"
                                  :options="dropzoneOptions"></vue-dropzone>
                  </v-flex>
                </v-layout>
              </v-flex>
            </v-layout>
          </v-container>
        </v-form>

      </v-card-text>
      <v-card-actions v-if="$auth.user().active">

        <!-- <v-spacer></v-spacer> -->
        <data-load ref="loader" suburl="/api/register/common" :loading.sync="loading" v-model="data"
                   :params="params"></data-load>
                   

<v-layout v-if="$auth.check('ROLE_USER') &&$auth.user().active &&$auth.user().status!='READY' &&$auth.user().status!='APPROVED' ">
            <v-flex>
            <v-btn
              
               @click="save(true)" :loading="loading"
              flat round color="#01B1C1" outline
              
              >
              <span style="font-size: 12px" class="black--text">
              {{$t('base.saveAndSend')}}
              </span>
            </v-btn>
            </v-flex>
</v-layout>


          <template v-else>
            <v-layout row wrap> 
              <v-flex   xs12>
              <v-btn  @click="save(false)" :loading="loading"
              flat round color="#01B1C1" outline
              >
                <span style="font-size: 12px" class="black--text">
                {{$t('base.save')}}
                </span>
              </v-btn>
              </v-flex>
              <v-flex   xs12>
              <template v-if="$auth.check('ROLE_USER')&& $auth.user().status=='APPROVED' ">

                {{$t('profile.readystatus')}}
                <v-btn @click="resetState()" :loading="loading"
                flat round color="#01B1C1" outline
                >
                <span style="font-size: 12px" class="black--text">
                  {{$t('profile.switchtonotmodarated')}}
                  </span>
                </v-btn>
              </template>
              </v-flex>

            </v-layout>
          </template>


        <!-- <v-btn color="info"
                v-if="$auth.check('ROLE_USER') &&$auth.user().active &&$auth.user().status!='READY' &&$auth.user().moderate!='APPROVED' "
                active-class="red-text" @click="sendToModerate" :loading="loading">
           {{$t('base.sendToModerate')}}
         </v-btn>-->


      </v-card-actions>


    </v-card>
  </v-flex>

</template>

<script>
  import axios from 'axios'
  import vue2Dropzone from 'vue2-dropzone'
  import 'vue2-dropzone/dist/vue2Dropzone.min.css'
  import router from "@/router";

  import DataLoad from '@/common/data-loader'
  import {EventBus} from '@/event-bus'

  import myUpload from 'vue-image-crop-upload';
  import CommonMixin from '@/mixins/common';

  require("@/i18n/langs");
  export default {
    mixins: [CommonMixin],
    components: {
      'my-upload': myUpload,
      'data-load': DataLoad,
      'vue-dropzone': vue2Dropzone
    },
    $_veeValidate: {
      validator: 'new'
    },
    data () {
      return {
        show: false,


        imgDataUrl: '',
        userAvatar: undefined,
        e1: true,
        verification_code: "",


        valid: false,
        data: {},

        user: {
          name: "",
          password: "",
          univer: null,
          lang: null,
          studentId: null,
          email: null,
          removedFiles: [],
          avatar: ""
        },
        removedFiles: [],
        params: "lang=" + this.$i18n.locale,
        loading: true,
        msg: ''
      }
    },

    mounted () {
      this.updateUser();
      EventBus.$emit('change-title', "profile.title");
    },

    created: function () {
      this.user = this.$auth.user();
      EventBus.$on('update-locale', lang => {
        this.params = "lang=" + lang;
      });


    },
    methods: {
      updateUser(){
        this.user = this.$auth.user();
        if (this.user.status == 'APPROVED') {
          if (this.$refs.uploader) this.$refs.uploader.disable();
        }
        else
          this.$refs.uploader.enable();
        if (this.user.resources) {
          this.$refs.uploader.removeAllFiles();
          this.user.resources.forEach((f) => this.$refs.uploader.manuallyAddFile(f, "/api/resources/download/" + f.id));
          this.removedFiles = [];
        }
      },
      addFile(file, error, xhr){
        console.log("addFile", file, error, xhr);
        if (file.size > 5 * 1024 * 1024) {
          this.$refs.uploader.removeFile(file);
          this.$dialog.alert(this.$t('profile.cannotaddbigfile'), {
            cancelText: this.$t("base.cancel")
          })


        }
      },
      removeFile(file, error, xhr){
        if (file.id) {
          this.removedFiles.push(file.id);
          console.log("remove file", file, error, xhr);
        }

      },
      toggleShow() {
        this.show = !this.show;
      },
      cropSuccess(imgDataUrl, field){

        this.user.avatar = imgDataUrl;
      },

      resetState(){
        axios.post('/api/profile/resetModerated'
        ).then(res => {

          if (res.data.success) {
            this.$auth.fetch({

              success: () => {

                this.updateUser();
                this.loading = false;
              },


            });
          }
        })
          .catch(error => {
            this.loading = false;
            EventBus.$emit('error', error);
          })

      },

      save(toModerate){

        if (!this.$refs.form.validate())
          return;
        this.loading = true;
        let formData = new FormData();
        for (var i in this.user) {
          if (this.user[i])
            formData.append(i, this.user[i]);
        }
        formData.append("toModerate", toModerate);
        if (this.$refs.uploader) {


          this.$refs.uploader.getQueuedFiles().forEach((f, i) => formData.append("file", f));
        }
        formData.append("removeResources", new Blob([JSON.stringify(this.removedFiles)], {type: 'application/json'}));

        axios.post('/api/profile/update',
          formData,
          {
            headers: {'Content-Type': 'multipart/mixed'}
          }
        ).then(res => {

          if (res.data.success) {

            EventBus.$emit('show-message', {color: "info", text: "messages.saved_success"});
            this.$auth.fetch({

              success: () => {

                this.updateUser();
                this.loading = false;
              },


            });
          }
        })
          .catch(error => {
            this.loading = false;
            EventBus.$emit('error', error);
          })

      },
      verify(){
        axios.post('/api/profile/verify?code=' + this.verification_code
        ).then(res => {
          if (res.data.success) {
            EventBus.$emit('show-message', {color: "info", text: "messages.code_update_successfully"});

            this.$auth.fetch({

              success: () => {
                this.updateUser()
              },


            });
          }
        })
          .catch(error => {
            EventBus.$emit('error', error);
          })

      },
      sendToModerate(){
        axios.post('/api/profile/readyToModerate'
        ).then(res => {
          if (res.data.success) {
            EventBus.$emit('show-message', {color: "info", text: "messages.ready_to_moderate_s"});

            this.$auth.fetch({

              success: () => {
                this.updateUser()
              },


            });
          }
        })
          .catch(error => {
            EventBus.$emit('error', error);
          })

      },
      resend(){
        axios.post('/api/profile/resend',
        ).then(res => {
          if (res.data.success) {
            EventBus.$emit('show-message', {color: "info", text: "messages.code_resended"});
            // router.push('/profile');
          }
        })
          .catch(error => {
            EventBus.$emit('error', error);
          })

      },
      clear () {
        this.name = '';
        this.email = '';
        this.select = null;
        this.checkbox = null
      }
    }
  }
</script>
