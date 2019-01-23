<template>

  <v-container
    style="min-height: 0;"
    ma-0
    pa-0

  >

    <div v-if="$auth.ready() &&! loading">

      <router-link v-if="$auth.check('ROLE_MAIN_ADMIN')" to="/admin/list/clients">{{$t('base.back')}}</router-link>
      <v-card>


        <v-layout row >
          <UserView :user="user" ></UserView>
        </v-layout>
        <v-flex xs12 v-for="item in data.content" :key="item.id">
          <v-card>
            <v-container
              style="min-height: 0;"
              fluid grid-list-lg
            >

              <v-layout row wrap mb-1>

                <v-flex xs12 lg4  align-left text-xs-left>
                  <router-link :to="'/user/'+item.userId">
                    <v-avatar

                      :size="45"
                      color="grey lighten-4"
                    > <img v-if="item.ava" :src="item.ava">
                      <img  v-else src="@/assets/nophoto.png">

                    </v-avatar>
                  <b>{{$t(item.buyerReview?'deal.recipient.buyer':'deal.recipient.seller')}}</b>: {{item.userName}} </router-link>
                  <router-link   :to="'/user/'+item.userId"> {{$t('wallet.deal')}} #{{item.dealId}}</router-link>
                  
                  <star-rating :increment="0.01" :length="5"
                               :show-rating="false" read-only :star-size="40" v-model="item.rate"/>
                </v-flex>
                <v-flex xs12 lg6>
                  <v-layout row wrap align-left text-xs-left>
                    <v-flex d-flex>

                      <span v-html="item.comments"></span>
                    </v-flex>
                  </v-layout>

                </v-flex>



              </v-layout>


            </v-container>
          </v-card>

        </v-flex>

      </v-card>

    </div>
    <v-layout v-if="!$auth.ready() || loading" align-center justify-center>
      <v-progress-circular indeterminate color="primary"></v-progress-circular>
    </v-layout>
  </v-container>

</template>

<script>
  import CommonMixin from '@/mixins/common';
  import {EventBus} from '@/event-bus'
  import router from "@/router";
  import UserView from "@/components/user/UserView.vue";
  import axios from 'axios'
  require('@/i18n/langs');
  export default {
    mixins: [CommonMixin],
    components: {"UserView": UserView},
    data () {
      return {

        msg: '',
        loading: false,
        user: {},
        sortBy: {by: "id", dir: "ASC"},
        data: {page: 1, totalPages: 0, content: []},
        item: {},
        pagination: {
          descending: false,
          localized: "",
          sortBy: 'id'
        }
      }
    },

    mounted: function () {
      console.log("TEST");
    },
    created: function () {

      this.loadService();
    },
    methods: {
      updatePaging()
      {
        this.loadlist(this.data.page - 1);
      },

      loadlist(page)
      {
        this.loading = true;
        axios.post('/api/user/reviews/' + this.$route.params.id + '?sort=' + this.sortBy.by + '&sortDirection=' + (this.sortBy.dir ) + '&page=' + page + '&size=', {
            filter: this.filter,
            /* search: this.params.search ? this.params.search : null,*/
            /* category: this.$route.params.category == 'all' ? null : this.$route.params.category*/

          }
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

      }
      ,
      loadService(){
        this.loading = true;
        axios.post('/api/' + (this.$auth.check('ROLE_USER') ? 'user' : 'userFull') + '/' + this.$route.params.id, {}
        ).then(res => {
          this.loading = false;

          if (res.data) {
              this. updatePaging();

            this.user = res.data;
          }
        })
          .catch(error => {
            console.log("ERROR!", error);
            this.loading = false;
            EventBus.$emit('error', error);
          })

      },
    }
  }
</script>
