<template>
  <v-flex xs12 class="text-xs-center" mt-2>

    <v-layout row wrap>
      <v-flex xs12 align-center>
        <v-toolbar color="white" class="ramin1">

          <v-toolbar-items >
            <v-btn flat :to="'/mydeals/servicesido/'+ $route.params.type" >{{$t('deal.serviceido')}}</v-btn>
            <v-spacer></v-spacer>
            <v-btn flat :to="'/mydeals/servicesiget/'+ $route.params.type" >{{$t('deal.serviceiget')}}</v-btn>
          </v-toolbar-items>
        </v-toolbar>
      </v-flex>
      <v-flex xs12>
        <v-container fluid> 
          <v-radio-group v-model="ram_deals_states" :mandatory="false" row>
      Show <br>
      <router-link :to="'/mydeals/'+ $route.params.get+'/active'"><v-radio label="active" value="active"></v-radio></router-link>
      <router-link :to="'/mydeals/'+ $route.params.get+'/finished'"><v-radio label="finished" value="finished"></v-radio></router-link>
      <router-link :to="'/mydeals/'+ $route.params.get+'/all'"><v-radio label="all" value="all"></v-radio></router-link>
      <!-- <router-link :to="'/mydeals/'+ $route.params.get+'/queue'"><v-radio label="queue" value="queue"></v-radio></router-link> -->
      <!-- <router-link :to="'/mydeals/'+ $route.params.get+'/other'"><v-radio label="other" value="other"></v-radio></router-link> -->

      <router-link :to="'/mydeals/'+ $route.params.get+'/canceled'"><v-radio label="canceled" value="canceled"></v-radio></router-link>
      
      <router-link :to="'/mydeals/'+ $route.params.get+'/conflict'"><v-radio label="conflict" value="conflict"></v-radio></router-link>
      




          </v-radio-group>
        </v-container>

        <v-toolbar v-if="0">
          <v-toolbar-items >
            <v-btn flat :to="'/mydeals/'+ $route.params.get+'/active'" >{{$t('deal.serviceactive')}}</v-btn>
            <v-btn flat :to="'/mydeals/'+ $route.params.get+'/queue'" >{{$t('deal.servicequeue')}}</v-btn>
            <v-btn flat :to="'/mydeals/'+ $route.params.get+'/other'" >{{$t('deal.serviceother')}}</v-btn>
          </v-toolbar-items>
        </v-toolbar>
      </v-flex>
    </v-layout>



    <v-flex xs12>
      <v-card>

        <v-data-table :loading="loading"
                      :headers="headers"
                      :items="data.content"

                      :pagination.sync="pagination"
                      hide-actions

        >
          <v-progress-linear slot="progress" color="blue" indeterminate></v-progress-linear>
          <template slot="headers" slot-scope="props" v-if="0">
            <tr>
              <th
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
            <tr class="clickable" @click="goDeal(props.item.deal.id)" >
            <td >
              <v-avatar class="small-tile"
                        :tile="false"
                        :size="50"
                        color="grey lighten-4"
              >
                <img v-if="props.item.service.logo" :src="props.item.service.logo">
                <img  v-else src="@/assets/nophoto.png">
              </v-avatar>
            </td>
           

            <td class="text-xs-center">{{ i18nTranslate(props.item.service, "title")}}</td>
        
            <td class="text-xs-center">
              {{$t("deal.status." + props.item.deal.status)}} 

              <!-- {{ props.item.deal.cost|formatMoney }} -->
              
              </td>



            </tr>
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
  import {loadValidations} from '@/mixins/validation-rules'
  import CommonMixin from '@/mixins/common';

  import DEAL_COMMON from '@/mixins/dealStatusesMap';
  require('@/i18n/langs');
  export default {

    mixins: [CommonMixin, DEAL_COMMON],
    components: {'my-upload': myUpload},
    props: ['filterService'],
    data: () => ({
      ram_deals_states:"",
      rules: loadValidations(this),
      dialog: false,
      loading: false,
      search: "",

      params: {search: ""},
      filter: {}, filterSellerBuyer: [],
      headers: [
        {text: 'services.logo', sortable: false},
       /* {
          text: 'services.id',
          align: 'left',
          sortable: true,
          value: 'id'
        },*/
        {
          text: 'services.title',
          align: 'center',
          sortable: true,
          localized: true,
          value: 'title'
        },
      /*  {
          text: 'services.category',
          align: 'center',
          sortable: true,
          value: 'category.id'
        },*/
        {
          text: 'services.cost',
          sortable: true,
          value: 'cost'
        }, /*{
          text: 'services.status',
          sortable: true,

          value: 'status'
        }*/
      ],

      filterByStatus:{
        active:[4,5],//active:[4,5,7]
        canceled: [8],
        finished: [6],
        conflict: [7,9,10,11],
        
        all: [4,5,7,8,6,9,10,11],

        queue:[0,1,2,3],
        other:[6,8,9,10,11],
      },
      data: {page: 0, totalPages: 0, content: []},
      showlogo: false,
      editedIndex: -1,
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
    }),

    computed: {

      formTitle () {
        return this.editedIndex === -1 ? this.$t('services.newitem') : this.$t('services.editservice')
      },

    },
    mounted() {
      EventBus.$emit('change-title', "deal.mydeals");
    },
    watch: {



      '$route' (to, from) {

        this.updatePaging();
      },
      dialog (val) {
        val || this.close()
      },

    },

    created () {
      this.ram_deals_states=this.$route.params.type;
      this.loadlist(0);

    },

    methods: {

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
        this.filterSellerBuyer=this.$route.params.get=='servicesiget'?[1]:[0];
        this.filter.dealStatuses=this.filterByStatus[this.$route.params.type];
        this.loading = true;
        let params = {};
        Object.assign(params, {
          filter: this.filter,
          filterSellerBuyer: this.filterSellerBuyer,
          search: this.params.search


        }, this.filterService);
        axios.post('/api/deals/' + (this.$auth.check("ROLE_USER") ? 'list' : 'adminlist')+'?sort=' + this.pagination.sortBy + this.pagination.localized + '&sortDirection=' + (this.pagination.descending ? 'DESC' : 'ASC') + '&page=' + page + '&size=',

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
      pauseItem(item){
        this.$dialog.confirm(this.$t("services.pausedialog"), {
          loader: true, okText: this.$t("base.agree"),
          cancelText: this.$t("base.cancel")
        })
          .then((dialog) => {
            this.loading = true;
            console.log("TESTSERT");
            axios.post('/api/services/pause', item).then(res => {

              if (res.data.success) {
                this.loading = false;
                this.updatePaging();
                dialog.close();
                EventBus.$emit('show-message', {color: "info", text: "services.pausedsucess"});
              }
            })
              .catch(error => {
                dialog.close();
                this.loading = false;
                //this.close()
                EventBus.$emit('error', error);
              })
          })
          .catch((e) => console.log("CATCH", e));
      },
      goDeal(dealId){
        this.$router.push({path: '/deals/'+ dealId});
      },
      playtem(item){
        this.$dialog.confirm(this.$t("services.playdialog"), {
          loader: true, okText: this.$t("base.agree"),
          cancelText: this.$t("base.cancel")
        })
          .then((dialog) => {
            this.loading = true;
            axios.post('/api/services/play', item
            ).then(res => {

              if (res.data.success) {
                this.loading = false;
                this.updatePaging();
                dialog.close();
                EventBus.$emit('show-message', {color: "info", text: "services.playsucess"});
              }
            })
              .catch(error => {
                this.loading = false;
                dialog.close();
                //this.close()
                EventBus.$emit('error', error);
              })
          })
          .catch(function () {

          });
      },
      editItem (item) {
        this.editedIndex = this.data.content.indexOf(item);
        console.log("ITEM", item);
        this.editedItem = Object.assign({}, item);
        this.dialog = true
      },

      deleteItem (item) {
        /* const index = this.items.content.indexOf(item);
         confirm('Are you sure you want to delete this item?') && this.items.splice(index, 1)*/
      },

      close () {
        this.dialog = false;
        setTimeout(() => {
          this.editedItem = Object.assign({}, this.defaultItem);
          this.editedIndex = -1
        }, 300)
      },

      save () {
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
