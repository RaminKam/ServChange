<template>
  <v-flex xs12 class="text-xs-center">
    <v-flex xs12>

      <v-card>
        <v-card-title>

          <v-dialog v-model="moderatorDialog" max-width="500px" v-if="$auth.check('ROLE_MODERATOR')">

            <v-card>
              <v-card-title>
                <span class="headline">{{ $t('admin.moderateservice') }}</span>
              </v-card-title>

              <v-card-text>
                <template v-if=" serviceToModerate.notsolvedclaims&& serviceToModerate.notsolvedclaims.length>0">
                  <v-flex xs12>
                    <b>{{$t('services.pleasesolveclaims')}}:</b>
                  </v-flex>
                  <v-flex xs12 v-for="claim in serviceToModerate.notsolvedclaims "
                          :key="claim.id">

                    <v-flex xs7 caption
                            mt-2>
                      <v-card color="red lighten-5">
                        <v-flex xs12>
                          <b>#{{claim.id}}</b>: {{claim.comments}}<br>
                        </v-flex>
                        <v-flex caption xs12>
                          <v-btn color="green" caption :loading="loading"
                                 @click.native="solveClaim(claim.id,'RESOLVED')">
                            {{$t('admin.approveclaim')}}
                          </v-btn>
                          <v-btn color="red" caption :loading="loading" @click.native="solveClaim(claim.id,'REJECTED')">
                            {{$t('admin.rejectclaim')}}
                          </v-btn>
                        </v-flex>
                        <v-flex offset-xs9 xs3 caption text-xs-rigth color="">
                          {{claim.createdAt | formatDateTime}}
                        </v-flex>
                      </v-card>
                    </v-flex>

                  </v-flex>
                </template>
                <v-textarea v-if="!serviceToModerate.notsolvedclaims|| serviceToModerate.notsolvedclaims.length==0"
                            v-model="serviceToModerate.comments"
                            :label="$t('admin.comments')"></v-textarea>
              </v-card-text>
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="info" flat @click.native="closeModerate">{{$t('base.cancel')}}</v-btn>
                <v-btn v-if="!serviceToModerate.notsolvedclaims|| serviceToModerate.notsolvedclaims.length==0"
                       color="blue darken-1" flat @click.native="approveService(true)">
                  {{$t('admin.approve')}}
                </v-btn>
                <v-btn v-if="!serviceToModerate.notsolvedclaims|| serviceToModerate.notsolvedclaims.length==0"
                       color="red darken-1" flat @click.native="approveService(false)">
                  {{$t('admin.reject')}}
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-dialog>

          <v-dialog v-model="dialog" max-width="500px">
            <v-btn slot="activator" v-if="$auth.check('ROLE_USER')" @click="newItem" color="info" dark class="mb-2">
              {{$t('services.newitem')}}
            </v-btn>
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

                  <v-avatar class="small-tile" :tile="false" :size="150" color="grey lighten-4"
                  >

                    <img v-if="editedItem.logo" :src="editedItem.logo">
                    <img v-else src="@/assets/nophoto.png">
                  </v-avatar>
                  <v-btn v-if="$auth.check('ROLE_USER')" color="info" class="btn" @click="toggleShow">{{$t('services.logo')}}</v-btn>
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
                        <v-textarea v-model="editedItem['description'+subItem.lang]"
                                    :label="$t('services.description')+'('+$t('langs_short.'+subItem.lang)+')'"></v-textarea>
                      </v-flex>


                    </v-layout>
                  </template>
                  <template v-if="$auth.check('ROLE_USER')">
                    <v-layout wrap>

                      <v-flex xs12>
                        <v-text-field     :rules="validation.name" v-model="editedItem['title'+$i18n.locale]"
                                      :label="$t('services.title')"></v-text-field>
                      </v-flex>


                    </v-layout>
                    <v-layout wrap>

                      <v-flex xs12>
                        <v-textarea v-model="editedItem['description'+$i18n.locale]"
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
                      label="Select"
                      item-text="title"
                      item-value="id"
                      max-height="auto"
                    >

                    </v-select>
                  </v-flex>


                  <v-layout wrap v-if="$auth.check('ROLE_USER')">
                    <v-flex xs12 sm6 md4>
                      <v-textarea v-model="editedItem.contacts"
                                  :label="$t('services.contacts')"></v-textarea>
                    </v-flex>
                    <v-flex xs12 sm6 md4>
                      <v-textarea v-model="editedItem.calendar"
                                  :label="$t('services.calendar')"></v-textarea>
                    </v-flex>

                  </v-layout>

                  </v-form>
                </v-container>
              </v-card-text>
              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="info" flat @click.native="close">{{$t('base.cancel')}}</v-btn>
                <v-btn color="blue darken-1" flat @click.native="save">{{$t('base.save')}}</v-btn>
              </v-card-actions>
            </v-card>
          </v-dialog>
          <v-flex xs12  v-if="$auth.check('ROLE_ADMIN')">
            <a @click="$router.go(-1)">{{$t('base.back')}}</a> <br> {{$t('services.formoderator')}} {{user.username}}

          </v-flex>
          <v-select :items="servicesStatuses" multiple v-model="filter.statuses"
                    chips :label="$t('services.selectstatues')">

          </v-select>
          <v-flex v-if="$auth.check(['ROLE_ADMIN','ROLE_MAIN_ADMIN','ROLE_MODERATOR'])">
            <v-select v-model="filter.univerId"
                      

                      item-text="title" item-value="id"
                      :items="univers" prepend-icon="work"

                      :label="$t('register.university')"


            ></v-select>
          </v-flex>


          <!-- <v-spacer></v-spacer>
           <v-text-field
             v-model="params.search"
             :label="$t('base.search')"
             single-line
             hide-details
           ></v-text-field>
           <v-btn @click="updatePaging()">
             <v-icon>search</v-icon>

           </v-btn>-->
        </v-card-title>

        <v-data-table :loading="loading"
                      :headers="headers"
                      :items="data.content"

                      :pagination.sync="pagination"
                      hide-actions

        >
          <v-progress-linear slot="progress" color="blue" indeterminate></v-progress-linear>
          <template slot="headers" slot-scope="props">
            <tr>
              <th v-if="!header.hide"
                  v-for="header in props.headers"
                  :key="header.text"
                  :class="['column sortable', pagination.descending ? 'desc' : 'asc', header.value === pagination.sortBy ? 'active' : '']"
                  @click="changeSort(header)"
              >
                <v-icon v-if="header.sortable" small>arrow_upward</v-icon>
                {{$t(header.text) }}
              </th>
              <th></th>
            </tr>
          </template>
          <template slot="items" slot-scope="props">
            <td v-if="$auth.check('ROLE_USER')">
              <v-avatar class="small-tile"
                        :tile="false"
                        :size="50"
                        color="grey lighten-4"
              > <img v-if="props.item.logo" :src="props.item.logo">
                <img  v-else src="@/assets/nophoto.png">
              </v-avatar>
            </td>
            <td>{{ props.item.id }}<br>{{props.item.createdAt | formatDateTime}}</td>

            <td class="text-xs-center">
              <router-link :to="'/service/'+props.item.id+'/serviceinfo'">{{i18nTranslate(props.item, "title") }}</router-link>
              <br>     <spen class="crop">{{ props.item["description" + $i18n.locale] }}</spen>
            </td>
            <td class="text-xs-center">
              <star-rating :increment="0.01" :length="5" read-only inline :star-size="20" v-model="props.item.rate"/>
              <template v-if="$auth.check('ROLE_MODERATOR')">
                <br>{{$t('admin.notsolvedclaims')}}({{props.item.notsolvedclaims.length}});
              </template>
            </td>
            <td class="text-xs-center">{{categoriesmap[props.item.categoryId] }}</td>
            <td class="text-xs-center">{{univerMap[props.item.univerId].title }}</td>
            <td class="text-xs-center">{{ props.item.cost }}</td>
            <td class="text-xs-center">{{ $t('services.statuses.' + props.item.status) }}<br>
              <template v-if="props.item.status=='REJECTED'">
              {{props.item.moderateComments}}
              </template>
            </td>


            <td class="text-xs-center" v-if="$auth.check(['ROLE_MODERATOR','ROLE_ADMIN'])">
              <b>{{$t('services.claimsCount')}}</b>:{{props.item.claimsCount}}<br>
              <b>{{$t('services.claimsRejectedCount')}}</b>:{{props.item.claimsRejectedCount}}<br>
              <b>{{$t('services.claimsApprovedCount')}}</b>:{{props.item.claimsApprovedCount}}<br>
              <b>{{$t('services.freezeCount')}}</b>:{{props.item.freezeCount}}

            </td>
            <td class="justify-center layout px-0">
              <v-btn
                v-if="(props.item.status=='APPROVED'||props.item.status=='APPROVED_MODERATED')&&$auth.check('ROLE_USER')"
                :loading="loading" icon class="mx-0"
                @click="pauseItem(props.item)">
                <v-icon color="teal">pause</v-icon>
              </v-btn>
              <v-btn v-if="props.item.status=='PAUSED'&&$auth.check('ROLE_USER')" :loading="loading" icon class="mx-0"
                     @click="playtem(props.item)">
                <v-icon color="teal">play_arrow</v-icon>
              </v-btn>
              <v-btn v-if="props.item.status=='REJECTED'&&$auth.check('ROLE_USER')" :loading="loading" icon class="mx-0"
                     @click="sendToModerate(props.item)">
                <v-icon color="indigo">how_to_reg</v-icon>
              </v-btn>
              <v-btn :loading="loading" icon class="mx-0" @click="editItem(props.item)">
                <v-icon color="teal">edit</v-icon>
              </v-btn>
              <v-btn icon class="mx-0" @click="deleteItem(props.item)" v-if="$auth.check('ROLE_USER')">
                <v-icon color="red">delete</v-icon>
              </v-btn>
              <v-btn v-if="$auth.check(['ROLE_MODERATOR'])&&(props.item.status=='MODERATE'||props.item.status=='APPROVED_MODERATED')" icon class="mx-0"
                     @click="moderate(props.item)">
                <v-icon color="indigo">how_to_reg</v-icon>
              </v-btn>
              <br>

            </td>
          </template>
          <template slot="no-data">
            {{$t('base.nodata')}}
          </template>

        </v-data-table>

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
        $_veeValidate: {
          validator: 'new'
        },
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
        data: {page: 0, totalPages: 0, content: []},
        showlogo: false,
        editedIndex: -1,
        moderateIndex: -1,
        serviceToModerate: {},
        user: {},
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
    },
    mounted() {
      if (this.$auth.check('ROLE_USER'))
        EventBus.$emit('change-title', "services.myservice");
    },
    created () {

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
        this.loading = true;
        let params = {};
        this.filter.moderatorId = this.$route.params.moderatorId;
        Object.assign(params, {
          filter: this.filter,
          search: this.params.search,


        }, this.filterService);
        axios.post('/api/services/' + (this.$auth.check("ROLE_USER") ? 'list' : 'adminlist') + '?sort=' + this.pagination.sortBy + this.pagination.localized + '&sortDirection=' + (this.pagination.descending ? 'DESC' : 'ASC') + '&page=' + page + '&size=',
          params
        ).then(res => {
          this.loading = false;

          if (res.data.content) {
            //   console.log("data",res.data);
            this.data.content = res.data.content;
            this.data.page = res.data.page + 1;
            this.data.totalPages = res.data.totalPages;

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
      solveClaim(claimId, status){
        this.loading = true;
        axios.post('/api/services/solveClaim', {
            id: claimId,
            status: status
          }
        ).then(res => {
          this.loading = false;
          if (res.data) {
            this.serviceToModerate = res.data;
          }


        })
          .catch(error => {
            this.loading = false;

            EventBus.$emit('error', error);
          })
      },
      approveService(result){
        axios.post('/api/moderator/moderateService', {
            id: this.serviceToModerate.id,
            comments: this.serviceToModerate.comments,
            moderateStatus: result ? "APPROVED" : "REJECTED"
          }
        ).then(res => {

          console.log("Services Load", res);
          if (res.data.success) {
            this.loading = false;
            this.updatePaging();
            this.closeModerate();
            //this.items.push(this.editedItem)
          }
        })
          .catch(error => {
            this.loading = false;
            //this.close()
            EventBus.$emit('error', error);
          })
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
      },
      save () {
        if (!this.$refs.form.validate())
          return;
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
      }


    }
  }
</script>
