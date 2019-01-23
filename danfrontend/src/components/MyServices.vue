<template>
  <v-flex xs12 class="text-xs-center" mt-3>
    <v-flex xs12 >
               <!-- <v-btn flat small round color="#01B1C1" outline :to="'/service/'+item.id+'/serviceinfo'">
                <span style="font-size: 12px" class="black--text">
                <b>More info</b>
                </span>
                </v-btn> -->
      <v-btn slot="activator" v-if="$auth.check('ROLE_USER')" @click="newItem();dialog=true"  
      flat round color="#01B1C1" outline
      >
      <span style="font-size: 12px" class="black--text">
        {{$t('services.newitem')}}
      </span>
      </v-btn>



      <!-- <v-toolbar v-if="0">
        <v-toolbar-items >


          <v-btn ml-2 flat :to="'/myservices/0'" >
            <v-icon v-if="$vuetify.breakpoint.xs" color="indigo">how_to_reg</v-icon>
            <template v-if="!$vuetify.breakpoint.xs" color="indigo">
            {{$t('services.statuses.MODERATE')}}</template></v-btn>
          <v-btn flat  :to="'/myservices/1'" > <v-icon v-if="$vuetify.breakpoint.xs">cancel</v-icon>
            <template v-if="!$vuetify.breakpoint.xs">{{$t('services.statuses.REJECTED')}}</template></v-btn>
          <v-btn flat :to="'/myservices/2'" > <v-icon v-if="$vuetify.breakpoint.xs" color="red">pause</v-icon>
            <template v-if="!$vuetify.breakpoint.xs">{{$t('services.statuses.PAUSED')}}</template></v-btn>
          <v-btn flat :to="'/myservices/3'" > <v-icon v-if="$vuetify.breakpoint.xs" color="green">check</v-icon>
            <template v-if="!$vuetify.breakpoint.xs">{{$t('services.statuses.APPROVED')}}</template></v-btn>
        </v-toolbar-items>
      </v-toolbar> -->
    </v-flex>
    <v-flex xs12 >


      <v-flex xs12 ml-3 mr-3 pl-3 pr-3>
        <v-layout column align-center>
          <v-flex pl-3 pr-3>
            <v-radio-group v-model="ram_states" :mandatory="false" row>
      Show <br>
      <router-link :to="'/myservices/0'"><v-radio label="MODERATE" value="0"></v-radio></router-link>
      <router-link :to="'/myservices/1'"><v-radio label="REJECTED" value="1"></v-radio></router-link>
      <router-link :to="'/myservices/2'"><v-radio label="PAUSED" value="2"></v-radio></router-link>
      <router-link :to="'/myservices/3'"><v-radio label="APPROVED" value="3"></v-radio></router-link>
    </v-radio-group>
          </v-flex>

          <v-flex>
            <v-select
            :items="sortByList"
            v-model="sortBy"
            label="Select"
            item-text="title"
            item-value="sort"
            max-height="auto"
            :label="$t('base.sortby')"
          >
          </v-select>
          </v-flex>
        </v-layout>
        
          <!-- <v-container fluid test> 
           
    
          </v-container> -->
          


                 
   


      </v-flex>


      <v-card>
        <v-card-title> 
          <v-flex xs12 lg2>

 

        </v-flex>
          <v-dialog v-model="dialog" max-width="900px">

            <v-card>
              <v-card-title>
                <span class="headline">{{ formTitle }}</span>

              </v-card-title>

              <v-card-text>
                <v-container grid-list-md>
                  <my-upload field="img"
                             @crop-success="cropSuccess"
                             v-model="showlogo"
                             :width="300"
                             :height="300"
                             :langType="$i18n.locale"


                             img-format="png"></my-upload>

                  <v-avatar class="small-tile"
                            :tile="false"
                            :size="150"
                            color="grey lighten-4"
                  > <img v-if="editedItem.logo" :src="editedItem.logo">
                    <img  v-else src="@/assets/nophoto.png">

                  </v-avatar>



                  <v-btn
                  v-if="$auth.check('ROLE_USER')"  class="btn" @click="toggleShow"
                  flat round color="#01B1C1" outline
                  >
                  <span style="font-size: 12px" class="black--text">
                  {{$t('services.logo')}}
                  </span>
                  </v-btn>

                  <v-form v-model="valid" ref="form" lazy-validation>
                  <template v-if="$auth.check('ROLE_MODERATOR')">
                    <v-layout wrap>

                      <v-flex xs12 sm6 md4 v-for="subItem in langs" :key="subItem.lang">
                        <v-text-field v-model="editedItem['title'+subItem.lang]"
                                      :label="$t('services.title')+'('+$t('langs_short.'+subItem.lang)+')'"></v-text-field>
                      </v-flex>


                    </v-layout>
                    <v-layout wrap>

                      <v-flex xs12 sm6 md4 v-for="subItem in langs" :key="subItem.lang">
                        <v-textarea  v-model="editedItem['description'+subItem.lang]"
                                    :label="$t('services.description')+'('+$t('langs_short.'+subItem.lang)+')'"></v-textarea>
                      </v-flex>


                    </v-layout>
                  </template>
                  <template v-if="$auth.check('ROLE_USER')">
                    <v-layout wrap>

                      <v-flex xs12>
                        <v-text-field    
                        :rules="validation.name" 
                        v-model="editedItem['title'+$i18n.locale]"
                                      :label="$t('services.title')"></v-text-field>
                      </v-flex>


                    </v-layout>
                    <v-layout wrap>

                      <v-flex xs12>
                        <v-textarea 
                        v-model="editedItem['description'+$i18n.locale]"
                                    :label="$t('services.description')"></v-textarea>
                      </v-flex>


                    </v-layout>
                  </template>
                  <v-flex xs12 v-if="$auth.check('ROLE_USER')">
                    <v-text-field v-model="editedItem.cost" required :rules="validation.moreThan0" type="number" :label="$t('services.cost')"></v-text-field>
                  </v-flex>
                  <v-flex xs12 v-if="$auth.check('ROLE_USER')">
                    <v-select  
                      :rules="validation.categorySelect"
                      :items="categories"
                      v-model="editedItem.categoryId"
                               :label="$t('base.selectcategory')"
                      item-text="title" auto
                      item-value="id"
                               single-line
                      max-height="auto"
                    >

                    </v-select>
                  </v-flex>


                  <v-layout wrap v-if="$auth.check('ROLE_USER')">
                   <!-- <v-flex xs12 sm6 md4>
                      <v-textarea v-model="editedItem.contacts"
                                  :label="$t('services.contacts')"></v-textarea>
                    </v-flex>-->
                    <v-flex xs12>
                      <v-textarea v-model="editedItem.calendar"  
                                  :label="$t('services.calendar')"></v-textarea>
                    </v-flex>

                  </v-layout>

                  </v-form>
                </v-container>
              </v-card-text>
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn flat @click.native="close"
                round color="#01B1C1" outline
                >
                <span style="font-size: 12px" class="black--text">
                {{$t('base.cancel')}}
                </span>
                </v-btn>
                <v-btn flat @click.native="save"
                round color="#01B1C1" outline
                >
                <span style="font-size: 12px" class="black--text">
                {{$t('base.save')}}
                </span>
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-dialog>
<v-dialog
      v-model="dialogForModerAgr"
      width="500"
    >
      

      <v-card>
        <v-card-title
          class="headline grey lighten-2"
          primary-title
        >
          Moderating
        </v-card-title>

        <v-card-text>
          You have changed moderating fields. Do you want to send service for remoderating?

        </v-card-text>

        <v-divider></v-divider>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            @click="dialogForModerAgr = false;dialog=true"
            flat round color="#01B1C1" outline
          >
            Cancel
          </v-btn>

          <v-btn
            @click="dialogForModerAgr = false;saveWithModerating();"
            flat round color="#01B1C1" outline
          >
            Yes
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>




        </v-card-title>

        <v-progress-linear v-if="loading" color="blue" indeterminate></v-progress-linear>
        <template v-if="!loading"></template>
        
        
        <v-flex xs12 v-for="item in data.content" :key="item.id">
          <v-card>
            <v-container
              style="min-height: 0;"
              fluid grid-list-lg
              pa-0
              ma-0
            >
            <!-- !!! -->
            


 <v-card>
              <v-layout>
                <v-flex xs5 >
                  <v-layout column  pl-2 pr-2>
                    <v-flex>
                  
                      <v-img v-if="item.logo" 
                    :src="item.logo"
                    height="125px"
                    contain
                  ></v-img>

                  <v-img v-else
                    :src="require('@/assets/nophoto.png')"
                    height="125px"
                    contain
                  ></v-img>
                      
                    </v-flex>
                    <v-flex>
                      <div aligin="center" class="grey--text">

{{item.status}}
                        <!-- <v-rating
                      v-model="item.rate"
            background-color="pink lighten-3"
      color="pink" small dense readonly
    ></v-rating> -->
    
    </div>
                    </v-flex>

                  </v-layout>
   
                  
                </v-flex>
                <v-flex xs7>
                  <v-layout row wrap>
                    <v-flex d-flex>
                        
                        <router-link :to="'/service/'+item.id+'/serviceinfo'">
                        <div style="font-size: 24px;" class="yaFont black--text" align="center">
                        {{i18nTranslate(item, 'title', '')}}
                        </div>
                        </router-link>
                      
                    </v-flex>
                    <!-- <v-flex d-flex>
                      <div style="font-size: 24px;"></div>
                      
                    </v-flex> -->
                  </v-layout>
                  
                  <!-- <v-card-title primary-title>
                    <span>
                      <span class="headline"><router-link :to="'/service/'+item.id+'/serviceinfo'">{{i18nTranslate(item, 'title', '')}}</router-link></span>
          
                    </span>
                  </v-card-title> -->
                  <v-layout row>
                    <v-flex>
                    <table>
                    
                    <!-- <tr align="left"><td>University:</td><td align="center">{{univerMap[item.univerId].title }}</td></tr>
                    <tr align="left"><td>Category:</td><td align="center">{{categoriesmap[item.categoryId] }}</td></tr> -->
                    
                     </table>
                    </v-flex>
                  </v-layout>


 
                  <v-layout align-start justify-end column>
                    <v-flex>
                      <v-layout row justify-space-between>
                        <v-flex >
                          <!-- Status: -->
                      </v-flex>
                      <v-spacer>
                      </v-spacer>
                        
                      <v-flex >
                          <!-- {{item.status}} -->
                        </v-flex>

                      </v-layout>
                                                                    
                        
                                                                   
                    </v-flex>
                    
                  </v-layout>


 


                      <!-- <div>Time: {{item.calendar}}</div>
                      <div>University: {{item.univer}}</div>
                      <div>Category: {{item.category}}</div>
                      <div>Deeder: Ramin Kamalov</div> -->
                </v-flex>
              </v-layout>
              <v-divider light></v-divider>
              <v-card-actions class="pa-3">
                      <v-btn
                        v-if="(item.status=='APPROVED'||item.status=='APPROVED_MODERATED')&&$auth.check('ROLE_USER')"
                        :loading="loading" icon class="mx-0"
                        @click="pauseItem(item)">
                        <v-icon color="teal">pause</v-icon>
                      </v-btn>
                      <v-spacer></v-spacer>
                      <v-btn v-if="item.status=='PAUSED'&&$auth.check('ROLE_USER')" :loading="loading" icon class="mx-0"
                             @click="playtem(item)">
                        <v-icon color="teal">play_arrow</v-icon>
                      </v-btn>
                      
                       <v-spacer></v-spacer>
                       <!-- (item.status!='APPROVED_MODERATED')&&(item.status!='APPROVED')&&(item.status!='MODERATE')&&(item.status!='PAUSED')&&$auth.check('ROLE_USER')
                        -->
                       <v-btn v-if="(item.status=='REJECTED')&&$auth.check('ROLE_USER')" 
                      :loading="loading" icon class="mx-0"
                             @click="sendToModerate(item)">
                        <v-icon color="indigo">how_to_reg</v-icon>
                      </v-btn>
                       <v-spacer></v-spacer>
                      <v-btn :loading="loading" icon class="mx-0" @click="editItem(item)">
                        <v-icon color="teal">edit</v-icon>
                      </v-btn>
                       <v-spacer></v-spacer>
                      <v-btn icon class="mx-0" @click="deleteItem(item)" v-if="$auth.check('ROLE_USER')">
                        <v-icon color="red">delete</v-icon>
                      </v-btn>

                <!-- <v-btn flat color="blue" pa-0 ma-0>
                <v-icon>settings</v-icon>
                Manage
               </v-btn>
                
                <v-spacer></v-spacer>
                <v-btn flat small :to="'/service/'+item.id+'/serviceinfo'"><b>More info</b></v-btn> -->
                
                

              </v-card-actions>

            </v-card>




































            <!-- old in core version:-->
            <v-layout align-start justify-space-between row fill-height wrap v-if="0">
              <v-flex >
                                      <!-- <v-img v-if="item.logo" 
                    :src="item.logo"
                    height="125px"
                    contain
                  ></v-img>

                  <v-img v-else
                    :src="require('@/assets/nophoto.png')"
                    height="125px"
                    contain
                  ></v-img> -->
                <v-avatar class="small-tile"
                              :tile="false"
                              :size="75"
                              color="grey lighten-4"
                    >
                      <img v-if="item.logo" :src="item.logo">
                      <img  v-else src="@/assets/nophoto.png">
                    </v-avatar>
              </v-flex>
              

              
              <v-flex >
                {{i18nTranslate(item, 'title', '')}}
              </v-flex>



              <v-flex    >
               <v-btn flat color="blue" pa-0 ma-0>
                <v-icon>settings</v-icon>
               </v-btn>
              </v-flex >


              <v-flex  >
                {{item.status}}

              </v-flex>


            </v-layout>


              <v-layout row wrap mb-1 v-if="0">

                <v-flex lg2 sm2 xs12
                        md2>
                  <v-flex xs12>
                    <v-avatar class="small-tile"
                              :tile="false"
                              :size="75"
                              color="grey lighten-4"
                    >
                      <img v-if="item.logo" :src="item.logo">
                      <img  v-else src="@/assets/nophoto.png">
                    </v-avatar>
                  </v-flex>

                    <v-flex xs12>
                      <v-btn
                        v-if="(item.status=='APPROVED'||item.status=='APPROVED_MODERATED')&&$auth.check('ROLE_USER')"
                        :loading="loading" icon class="mx-0"
                        @click="pauseItem(item)">
                        <v-icon color="teal">pause</v-icon>
                      </v-btn>
                      <v-btn v-if="item.status=='PAUSED'&&$auth.check('ROLE_USER')" :loading="loading" icon class="mx-0"
                             @click="playtem(item)">
                        <v-icon color="teal">play_arrow</v-icon>
                      </v-btn>
                      <v-btn v-if="(item.status!='MODERATE')&&(item.status!='PAUSED')&&$auth.check('ROLE_USER')" :loading="loading" icon class="mx-0"
                             @click="sendToModerate(item)">
                        <v-icon color="indigo">how_to_reg</v-icon>
                      </v-btn>
                      <v-btn :loading="loading" icon class="mx-0" @click="editItem(item)">
                        <v-icon color="teal">edit</v-icon>
                      </v-btn>
                      <v-btn icon class="mx-0" @click="deleteItem(item)" v-if="$auth.check('ROLE_USER')">
                        <v-icon color="red">delete</v-icon>
                      </v-btn>
                    </v-flex>

                </v-flex>
                <v-flex lg5 sm5 xs10 md5>
                  <v-layout row wrap align-left text-xs-left>
                    <v-flex d-flex>
                      <router-link :to="'/service/'+item.id+'/serviceinfo'">{{i18nTranslate(item, 'title', '')}}</router-link>

                    </v-flex>
                  </v-layout>
                  <v-layout row wrap align-left text-xs-left>
                    <v-flex d-flex crop>
                      <div class="crop">
                        {{i18nTranslate(item, 'description', '')}}
                      </div>
                      <div class="crop">

                        {{item.calendar}}
                      </div>
                    </v-flex>
                  </v-layout>

                </v-flex>
                <v-flex lg5 xs12 sm5 md5>
                  <v-layout row wrap align-left text-xs-left>
                    <v-flex xs9 lg6 md6>

                      <b><nobr>{{item.cost|formatMoney}}</nobr></b>
                      <br>
                      <star-rating :increment="0.01" :length="5" read-only inline :star-size="20"
                                   v-model="item.rate"/>
                      <br>
                      <router-link :to="'/service/'+item.id+'/reviews'" >{{$t('services.reviews')}}({{item.reviewCount}})</router-link>

                    </v-flex>

                  </v-layout>

                </v-flex>


              </v-layout>


            </v-container>
          </v-card>

        </v-flex>
        <div class="text-xs-center pt-2">


          <v-pagination v-if="data.totalPages>0" @input="updatePaging" v-model="data.page" :total-visible="7"
                        :length="data.totalPages"></v-pagination>

        </div>
      </v-card>
    </v-flex>


  </v-flex>
</template>

<script>
  import {EventBus} from '@/event-bus'
  import axios from 'axios'
  import myUpload from 'vue-image-crop-upload';

  import CommonMixin from '@/mixins/common';
  import ServicesStatus from '@/mixins/servivesStatusesMap';

  require('@/i18n/langs');
  export default {

    mixins: [CommonMixin, ServicesStatus],
    components: {'my-upload': myUpload},
    data(){
      return {


        ram_states:"",

        $_veeValidate: {
          validator: 'new'
        },
        dialogForModerAgr:false,
        dialog: false,
        loading: false,
        moderatorDialog: false,
        search: "",
        valid: false,
        params: {search: ""},
        filter: {},
        headers: [
          {
            text: 'services.logo', sortable: false,
            hide: !this.$auth.check("ROLE_USER")
          },
          {
            text: 'services.date',
            align: 'left',
            sortable: true,
            value: 'id'
          },
          {
            text: 'services.title',
            align: 'center',
            sortable: true,
            localized: true,
            value: 'title'
          },
          {
            text: 'services.rate',
            align: 'center',
            sortable: true,

            value: 'rate'
          },
          {
            text: 'services.category',
            align: 'center',
            sortable: true,
            value: 'category.id'
          },
          {
            text: 'services.univer',
            align: 'center',
            sortable: true,
            value: 'user.univer.id'
          },
          {
            text: 'services.cost',
            sortable: true,
            value: 'cost'
          }, {
            text: 'services.status',
            sortable: true,

            value: 'status'
          },

          {
            text: 'services.statistic',
            hide: !this.$auth.check(['ROLE_MODERATOR', 'ROLE_ADMIN']),

          }
        ],
        data: {
          page: 0, totalPages: 0, content: []
          
          },
        showlogo: false,
        editedIndex: -1,
        moderateIndex: -1,
        serviceToModerate: {},
        user: {},
        actionMode:"",// "NEW_SERVICE" "CHANGE_SERVICE_INFO"
        //itemForCompare:{},
        editedItem: {
          id: -1,
          title: '',
          cost: 0,
          logo: "",
          contacts: '',
          calendar: '',
          categoryId: -1,
          description: ''
        },
        defaultItem: {
          title: '',
          cost: 0,
          contacts: '',
          calendar: '',
          categoryId: null,
          description: ''
        },
        sortBy: {by: "id", dir: "ASC"},
        pagination: {
          descending: true,
          localized: "",
          sortBy: 'id'
        }
      }
    },
    props: ['filterService'],
    computed: {
      formTitle () {
        return this.editedIndex === -1 ? this.$t('services.newitem') : this.$t('services.editservice')
      },

    },

    watch: {

      '$route' (to, from) {
        this.updatePaging();
      },
      dialog (val) {
        val || this.close()
      },
      filter: {
        deep: true,
        handler(){
          this.updatePaging();
        }
      },
      filterService () {
        this.updatePaging();
      },
      sortBy: {
        handler(){
          this.updatePaging();
        },
        deep: true
      }
    },
    mounted() {
      if (this.$auth.check('ROLE_USER'))
        EventBus.$emit('change-title', "services.myservice");
    },
    created () {
      this.ram_states=this.$route.params.status;
      console.log("hello ",this.$route.params.status);

      if (this.$route.params.moderatorId) {
        this.loadUserData();
      } else
        this.loadlist(0);

    },

    methods: {

      loadUserData(){
        this.loading = true;
        axios.post('/api/userFull/' + this.$route.params.moderatorId, {}
        ).then(res => {
          this.loading = false;

          if (res.data) {

            this.user = res.data;
            this.loadlist(0);
          }
        })
          .catch(error => {
            console.log("ERROR!", error);
            this.loading = false;
            EventBus.$emit('error', error);
          })

      },
      changeSort (header) {


        if (this.pagination.sortBy == header.value) {
          this.pagination.descending = !this.pagination.descending
        } else {
          this.pagination.sortBy = header.value;
          this.pagination.localized = header.localized ? this.$i18n.locale : "";
          this.pagination.descending = false
        }
        this.updatePaging();
      },

      toggleShow() {
        this.showlogo = !this.showlogo;
      },
      cropSuccess(imgDataUrl, field){

        this.editedItem.logo = imgDataUrl;
      },
      updatePaging(){
        this.loadlist(this.data.page - 1);
      },

      loadlist(page){

        this.filter.statuses=(this.$route.params.status==3)?[3,4]:[this.$route.params.status];
        this.loading = true;
        let params = {};
        this.filter.moderatorId = this.$route.params.moderatorId;
        Object.assign(params, {
          filter: this.filter,
          search: this.params.search,


        }, this.filterService);
        axios.post('/api/services/' + (this.$auth.check("ROLE_USER") ? 'list' : 'adminlist') + '?sort=' + this.sortBy.by  + '&sortDirection=' +this.sortBy.dir + '&page=' + page + '&size=',
          params
        ).then(res => {
          this.loading = false;

          if (res.data.content) {
            //   console.log("data",res.data);
            this.data.content = res.data.content;
            this.data.page = res.data.page + 1;
            this.data.totalPages = res.data.totalPages;
            this.pagination.rowsPerPage=res.data.size;
            this.pagination.totalItems=res.data.totalElements;
          }
        })
          .catch(error => {
            console.log("ERROR!", error);
            this.loading = false;
            EventBus.$emit('error', error);
          })

      },
      moderate (item) {
        this.moderateIndex = this.data.content.indexOf(item);

        this.serviceToModerate = Object.assign({}, item);
        this.moderatorDialog = true
      },
      closeModerate () {
        this.moderatorDialog = false;
        setTimeout(() => {
          this.serviceToModerate = Object.assign({}, this.defaultItem);
          this.moderateIndex = -1
        }, 300)
      },

      deleteItem(item){
        this.confirmUrl("services.deletedialog", '/api/services/remove', item).then((data) => {
          this.loading = false;
          this.updatePaging();
        }).catch((er) => {
          this.loading = false;
        })
      },
      sendToModerate(item){
        this.confirmUrl("services.sendtomoderate", '/api/services/sendToModerate', item).then((data) => {
          this.loading = false;
          this.updatePaging();
        }).catch((er) => {
          this.loading = false;
        })
      },
      pauseItem(item){

        this.confirmUrl("services.pausedialog", '/api/services/pause', item).then((data) => {
          this.loading = false;
          this.updatePaging();
        }).catch((er) => {
          this.loading = false;
        })

      },
      playtem(item){
        this.confirmUrl("services.playdialog", '/api/services/play', item).then((data) => {
          this.loading = false;
          this.updatePaging();
        }).catch((er) => {
          this.loading = false;
        })
      },
      editItem (item) {
        this.$refs.form.reset();
        this.editedIndex = this.data.content.indexOf(item);
        console.log("ITEM", item);
        this.editedItem = Object.assign({}, item);
        this.dialog = true
        this.actionMode="CHANGE_SERVICE_INFO";
      },


      close () {
        this.dialog = false;
        setTimeout(() => {
          this.editedItem = Object.assign({}, this.defaultItem);
          this.editedIndex = -1
        }, 300)
      },
      newItem(){
        this.$refs.form.reset();
        this.actionMode="NEW_SERVICE";
      },
      saveWithModerating(){
        //this.sendToModerate(this.data.content[this.editedIndex]);
        this.loading = true;

        axios.post('/api/services/sendToModerate', this.editedItem
        ).then(res => {
          if (res.data.success) {
            this.loading = false;
   
          }
        })
          .catch(error => {
            this.loading = false;
            
            EventBus.$emit('error', error);
          })
        
        
        this.save2();

      },

      save2(){
        this.loading = true;

        axios.post('/api/services/crud', this.editedItem
        ).then(res => {

          console.log("Services Load", res);
          if (res.data.success) {
            this.loading = false;
            this.updatePaging();
            this.close();
            //this.items.push(this.editedItem)
          }
        })
          .catch(error => {
            this.loading = false;
            //this.close()
            EventBus.$emit('error', error);
          })
      },
      save () {
        if (!this.$refs.form.validate())
          return;

        if(this.actionMode=="NEW_SERVICE"){

        }else{
          if(this.ram_states!="0" && this.ram_states!="1"){
            if(this.actionMode=="CHANGE_SERVICE_INFO"){
            var itemForComp=this.data.content[this.editedIndex];
            //this.editedIndex = this.data.content.indexOf(item);
            console.log("itemForComp=",itemForComp);
            console.log("usedItem=",this.editedItem);

            
              var isNeed="no";
              if(this.editedItem['title'+this.$i18n.locale]!=itemForComp['title'+this.$i18n.locale] ||
              this.editedItem['description'+this.$i18n.locale]!=itemForComp['description'+this.$i18n.locale] ||
              this.editedItem.categoryId!=itemForComp.categoryId ||
              this.editedItem.calendar!=itemForComp.calendar      ||
              this.editedItem.logo!=itemForComp.logo
              ) 
              isNeed="yes";
              
              console.log("need to moderate=",isNeed);
              
              if(isNeed=="yes"){
                //this.dialog=false;
                this.dialogForModerAgr=true;
                return;

              }else{

              }

              

            

          }
          }else{

          }
          
        }
        this.save2();


      //   this.loading = true;

      //   axios.post('/api/services/crud', this.editedItem
      //   ).then(res => {

      //     console.log("Services Load", res);
      //     if (res.data.success) {
      //       this.loading = false;
      //       this.updatePaging();
      //       this.close();
      //       //this.items.push(this.editedItem)
      //     }
      //   })
      //     .catch(error => {
      //       this.loading = false;
      //       //this.close()
      //       EventBus.$emit('error', error);
      //     })
       }


    }
  }
</script>
<style scoped>
.test {
  border: 2px solid red;
}

.rounded-card{
  border-radius:50px;
}

.v-btn {
  min-width: 0;
}
</style>