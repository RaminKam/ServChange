<template>
  <v-flex xs12 class="text-xs-center" mt-3>

    <v-flex xs12>
      <v-layout align-left mb-3 pl-2 pr-2>
        <v-flex xs12 align-start justify-start text-xs-left>
          <b>{{$t('wallet.balance')}}: </b>
          {{$auth.user().balance.balance}} DNC<br>
          <template v-if="$auth.user().balance.credit &&$auth.user().balance.credit>0"> 
            <b>{{$t('wallet.credit')}}: 
            </b>{{$auth.user().balance.credit|formatMoney}}   
            <v-btn v-if="$auth.user().balance.credit<=$auth.user().balance.balance" color="info" active-class="red-text" @click="solveCredit" :loading="loading">
            {{$t('wallet.solvecredit')}}
          </v-btn><br> 
          </template>
          <template v-if="$auth.user().balance.blocked &&$auth.user().balance.blocked>0">  <b>{{$t('wallet.blocked')}}: </b>{{$auth.user().balance.blocked|formatMoney}}<br>
          </template>
          <template v-if="demerge">  <b>{{$t('wallet.demerge')}}: </b>{{demerge|formatMoney}}<br>
          </template>
        </v-flex>
      </v-layout>



      <v-layout row wrap mb-2>
        <v-flex xs12>
        
        <v-layout row wrap mb-3>
      
      <v-layout v-if="!$auth.user().balance.credit>0&&maxCredit&&maxCredit>0" wrap >
        <v-flex xs12><b>{{$t('wallet.ranout')}}</b></v-flex>
     <!--   <v-flex xs12>{{$t('wallet.youcantake')}}: {{maxCredit}}</v-flex>-->
        <v-flex xs12>
        <!--  <v-text-field v-model="creditCost" type="number" :label="$t('services.cost')"></v-text-field>-->


          <v-dialog v-model="creditDialog" max-width="500px">

            <v-card>
              <v-card-title>
                <span class="headline">{{ $t('wallet.takecredit') }}</span>
              </v-card-title>

              <v-card-text>
                {{$t('wallet.youcantake')}}: {{maxCredit|formatMoney}}

                <v-text-field type="number" v-model="creditCost"
                            :label="$t('wallet.creditsum')"></v-text-field>
              </v-card-text>
              <v-card-actions>
                <v-spacer></v-spacer>

                <v-btn flat @click.native="creditDialog=false"
                round color="#01B1C1" outline
                ><span style="font-size: 12px" class="black--text">
                  {{$t('base.cancel')}}
                  </span>
                </v-btn>

                <v-btn :loading="loading" flat @click.native="takeCredit"
                round color="#01B1C1" outline
                ><span style="font-size: 12px" class="black--text">
                  {{$t('wallet.take')}}
                  </span>
                </v-btn>
              </v-card-actions>
            </v-card>
 
<v-btn slot='activator'  active-class="red-text" :loading="loading" flat round color="#01B1C1" outline>
              <span style="font-size: 12px" class="black--text">
              {{$t('wallet.takecredit')}}
              </span>
            </v-btn>


          </v-dialog>
        </v-flex>
      </v-layout>


    </v-layout>
    <span style="font-size: 24px">
   
<b>{{$t('wallet.transactionhistory')}}</b>
    </span>
        </v-flex>
           
      </v-layout>
      <v-card >
        <v-card-title v-if="0">

          <!--   <v-select :items="operationKinds" multiple v-model="filter.statuses"
                       chips :label="$t('deal.selectstatues')">

             </v-select>-->

        </v-card-title>

        <v-data-table :loading="loading"
                      :headers="headers"
                      :items="data.content"

                      :pagination.sync="pagination"
                      hide-actions

        >
          <v-progress-linear slot="progress" color="blue" indeterminate></v-progress-linear>
          <template slot="headers" slot-scope="props" >
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

            </tr>
          </template>
          <template slot="items" slot-scope="props">

      <!--      <td>{{props.item.createdAt | formatDateTime}}</td>-->
            <td class="text-xs-center">
              {{props.item.fromTo}}
            </td>
            <td class="text-xs-center"> <router-link v-if="props.item.service"
              :to="'services/'+props.item.service.id">{{i18nTranslate(props.item.service, 'title', '')}}</router-link>
              <span v-if="props.item.dealId">
              <br><router-link
              :to="'deals/'+props.item.dealId">{{$t('wallet.deal')}} #{{props.item.dealId}} </router-link></span></td>
            <td class="text-xs-center">
              {{ $t('wallet.kinds.' + props.item.kind) }}<br>
              <!--INITIAL, SYSTEM_CORRECTION,BLOCK, INCOME, CREDIT, RESET_BLOCK, CHARGE, REPAY_CREDIT, DEMERS-->
              <template v-if="props.item.kind=='INCOME' || props.item.kind=='INCOME'|| props.item.kind=='CREDIT'|| props.item.kind=='RESET_BLOCK'">+</template>
              <template v-if="props.item.kind=='CHARGE' || props.item.kind=='DEMERS'|| props.item.kind=='REPAY_CREDIT'|| props.item.kind=='BLOCK'">-</template>
              {{props.item.delta | formatMoney }}</td>


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

  import walletStatusesMap from '@/mixins/walletStatusesMap';
  require('@/i18n/langs');
  export default {

    mixins: [CommonMixin, walletStatusesMap],
    components: {'my-upload': myUpload},
    data: () => ({
      maxCredit: 0,
      demerge:0,
      creditCost: 0,
      rules: loadValidations(this),
      dialog: false,
      loading: false,
      search: "",
      creditDialog:false,
      params: {search: ""},
      filter: {statuses: []},
      headers: [

       /* {
          text: 'createdAt',
          align: 'center',
          sortable: true,
          value: 'createdAt'
        },*/
        {
          text: 'wallet.kind',
          align: 'center',
          sortable: false,

          value: 'kind'
        },  {
          text: 'wallet.service',
          align: 'center',
          sortable: false
        },
        {
          text: 'wallet.delta',
          align: 'center',
          sortable: false,

          value: 'delta'
        },


      ],
      data: {page: 0, totalPages: 0, content: []},
      showlogo: false,
      editedIndex: -1,

      defaultItem: {
        title: '',
        cost: 0,
        contacts: '',
        calendar: '',
        categoryId: null,
        description: ''
      },
      pagination: {
        descending: false,
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
      EventBus.$emit('change-title', "wallet.title");
    },
    watch: {
      dialog (val) {
        val || this.close()
      },
      filter () {
        this.updatePaging();
      }
    },

    created () {
      this.loadlist(0);




    },

    methods: {
      showMaxCredit(){
        axios.post('/api/wallet/maxCredit',
        ).then(res => {
          if (res.data.demerge)
            this.demerge = res.data.demerge;
          if (res.data.maxCredit) {
            this.maxCredit = res.data.maxCredit;this.creditCost=this.maxCredit;



          }
        })
          .catch(error => {
            EventBus.$emit('error', error);
          })
      },
      solveCredit(){

          axios.post('/api/wallet/solveCredit'          ).then(res => {
            if (res.data.success) {
              EventBus.$emit('show-message', {color: "info", text: "messages.creditSolved"});

              this.$auth.fetch({

                success: function () {
                  this.updatePaging();
                },


              });
            }
          })
            .catch(error => {
              EventBus.$emit('error', error);
            })
      },
      takeCredit(){
        if (this.maxCredit < this.creditCost) {
          EventBus.$emit('show-message', {color: "error", text: "messages.cannotTakeCredit"});
        } else if(this.creditCost>0) {
          this.loading = true;
          axios.post('/api/wallet/takeCredit', {credit: this.creditCost}
          ).then(res => {
            if (res.data.success) {
              EventBus.$emit('show-message', {color: "info", text: "messages.creditTaken"});

              this.$auth.fetch({

                success: function () {
                  this.updatePaging();
                  this.loading = false;
                  this.creditDialog=false;
                },


              });
            }
          })
            .catch(error => { this.loading = false;
              EventBus.$emit('error', error);
            })
        }
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
        axios.post('/api/wallet/list?sort=' + this.pagination.sortBy + this.pagination.localized + '&sortDirection=' + (this.pagination.descending ? 'DESC' : 'ASC') + '&page=' + page + '&size=', {
            filter: this.filter,
            search: this.params.search

          }
        ).then(res => {
          this.loading = false;

          this.showMaxCredit();
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
