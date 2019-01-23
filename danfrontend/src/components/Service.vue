<template>

  <v-container
    style="min-height: 0;"
    pa-0
    ma-0

    mt-2

  >

    <div v-if="$auth.ready() &&! loading">

      <v-card>
     <!--   <router-link v-if="$auth.check(['ROLE_ADMIN'])" to="/moderator_area/list/services">{{$t('base.back')}}</router-link>-->

        <v-layout row wrap>
          <v-flex>
            <v-card flat>
              <v-layout mt-2>
                <v-flex xs5 pl-2 pr-2>
                  <v-layout column  >
                    <v-flex >
                             
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
                    <v-flex  align-self-center>
                      <div align="center">
                        <!-- <v-rating
                      v-model="item.rate"
            background-color="pink lighten-3"
      color="pink" small dense readonly
    ></v-rating> -->
    
    <star-rating :show-rating="false" inline :length="5" read-only v-model="item.rate" :star-size="20" :increment="0.01" />
    </div>
                    </v-flex>

                  </v-layout>
   
                  
                </v-flex>
                <v-flex xs7>
                  <v-layout row wrap>
                    <v-flex d-flex>
                      <div style="font-size: 24px;" class="yaFont" align="center">{{i18nTranslate(item, 'title', '')}}</div>
                    </v-flex>
                    <!-- <v-flex d-flex>
                      <div style="font-size: 24px;">{{item.cost}} DNC</div>
                      
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
                    <tr>
                      <td colspan="2" align="left">{{item['description' + $i18n.locale]}}<br> <br></td>
                      <td> </td>
                    </tr>
                    <tr><td> </td><td> </td></tr>
                    <tr align="left">
                      <td align="left"><span class="yaFont">Time:</span></td>
                      <td align="left">{{item.calendar}}</td>
                    </tr>
                    <tr align="left">
                      <td align="left"><span class="yaFont">Category:</span></td>
                      <td align="left">{{categoriesmap[item.categoryId] }}</td>
                      </tr> 
                    <tr align="left">
                      <td align="left"><span class="yaFont">University:</span></td>
                      <td align="left">{{univerMap[item.univerId].title }}</td>
                    </tr>
                    
                    
                     </table>
                    </v-flex>
                  </v-layout>


 
                  <v-layout align-start justify-end column v-if="0">
                    <v-flex>
                      <v-layout row justify-space-between>
                        <v-flex >
                          {{categoriesmap[item.categoryId] }} 
                      </v-flex>
                      <v-spacer>
                      </v-spacer>
                        
                      <v-flex >
                          {{univerMap[item.univerId].title }}
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
                 <v-btn slot="activator" @click="postServiceClaim" :disabled="hasClaim" :dark="!hasClaim" color="red"
                 flat small round outline
                 >
                   
                          <v-icon>bug_report</v-icon>
                          <span style="font-size: 12px">
                          <b>
                          {{$t('services.complaint')}}
                          </b>
                          </span>
                </v-btn>
                <v-spacer></v-spacer>
                <v-btn @click="newDeal(item.id)" flat small round color="#01B1C1" outline>
                  <span style="font-size: 12px" class="black--text">
                  <b>Buy for {{item.cost}} DNC</b>
                  </span>
                </v-btn>
                
                

              </v-card-actions>
            <v-dialog v-model="claimDialog" max-width="500px">

              <v-card>
                <v-card-title>
                  <span class="headline">{{ $t('services.claimdialog') }}</span>
                </v-card-title>

                <v-card-text>

                  <v-textarea v-model="claim.comments"
                              :label="$t('admin.comments')"></v-textarea>
                </v-card-text>
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn flat @click.native="closeClaim"
                  small round color="#01B1C1" outline
                  ><span style="font-size: 12px" class="black--text">
                    {{$t('base.cancel')}}
                    </span>
                    </v-btn>
                    

                  <v-btn :loading="loading" flat @click.native="sendClaim"
                  small round color="#01B1C1" outline
                  ><span style="font-size: 12px" class="black--text">
                    {{$t('services.createclaim')}}
                    </span>
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-dialog>
            </v-card>
          </v-flex>





          <!-- <v-flex xs6 lg4 headline>
            <b>{{item['title' + $i18n.locale]}}</b>

          </v-flex>
          <v-flex xs6 lg4 headline>
            <b>{{$t('base.cost')}}{{item.cost|formatMoney}}</b>
          </v-flex>
          <v-flex xs2 lg4>
            <v-btn @click="newDeal(item.id)" color="info">
              <v-icon>play_circle_outline</v-icon>
              {{$t('services.buy')}}
            </v-btn>

            <v-dialog v-model="claimDialog" max-width="500px">

              <v-card>
                <v-card-title>
                  <span class="headline">{{ $t('services.claimdialog') }}</span>
                </v-card-title>

                <v-card-text>

                  <v-textarea v-model="claim.comments"
                              :label="$t('admin.comments')"></v-textarea>
                </v-card-text>
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn color="info" flat @click.native="closeClaim">{{$t('base.cancel')}}</v-btn>

                  <v-btn :loading="loading" color="red darken-1" flat @click.native="sendClaim">
                    {{$t('services.createclaim')}}
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-dialog>

          </v-flex> -->



          <v-flex xs12 lg12 mt-5 >

            <v-tabs color="white"
                    fixed-tabs
                    v-model="activeTab"
                    slider-color="yellow">
              
              <v-tab ripple>
                <span style="font-size: 12px" class="black--text yaFont">
                  <b>
                {{$t('services.userinfo')}}
                </b>
                </span>
              </v-tab>
              <v-tab ripple>
                <span style="font-size: 12px" class="black--text yaFont">
                  <b>
                Feedbacks
                ({{item.reviewCount}})
                </b>
                </span>
              </v-tab>
              <v-tab v-if="$auth.check() && !$auth.check('ROLE_USER')" ripple>{{$t('services.statistic')}}</v-tab>

              <v-tab-item  >
                <!-- <v-card flat > -->
                  <!-- <v-card-text > -->
                    <v-layout row wrap mt-2>
                      <UserView :user="user"></UserView>




                    </v-layout>
                  <!-- </v-card-text> -->
                <!-- </v-card> -->
              </v-tab-item>

              <v-tab-item>

                <v-flex xs12 v-for="item in data.content" :key="item.id">
                  <v-card>
                    <v-container
                      style="min-height: 0;"
                      fluid grid-list-lg
                    
                    >

                      <v-layout row wrap mb-1>

                        <v-flex xs12 lg4  align-left text-xs-left>
                          <v-layout row align-start>
                            <v-flex xs5 sm2 md2 lg4>
                              <v-card flat>
                              <v-avatar

                              :size="80"
                              color="grey lighten-4"
                            > <img v-if="item.ava" :src="item.ava">
                              <img  v-else src="@/assets/nophoto.png">

                            </v-avatar>
                            </v-card >
                            </v-flex >

                            <v-flex xs6 sm8 md8 lg8>
                              <v-card flat>
                              <v-layout column wrap>
                            <v-flex>
                              <router-link :to="'/user/'+item.userId">{{item.userName}} </router-link>
                            </v-flex>
                            <v-flex>
                              <star-rating :increment="0.01" :length="5"  :show-rating="false" read-only :star-size="20" v-model="item.rate"/>
                            </v-flex>
                            <v-flex>
                              <span v-html="item.comments"></span>
                            </v-flex>
                          </v-layout>
                              </v-card>
                            </v-flex>
                          </v-layout>
                            



                          
                          
                        </v-flex>
                        <!-- <v-flex xs12 lg6 >
                          <v-layout row wrap align-left text-xs-left>
                            <v-flex d-flex>

                              <span v-html="item.comments"></span>
                            </v-flex>
                          </v-layout>

                        </v-flex> -->
                        <v-flex xs12 lg2>
                          <v-btn slot="activator" @click="postReviewClaim(item.id)" :disabled="item.hasClaim"
                                 :dark="!item.hasClaim" color="red"
                                 flat small round outline 
                                 >
                            <v-icon>bug_report</v-icon>
                          </v-btn>
                        </v-flex>


                      </v-layout>


                    </v-container>
                  </v-card>

                </v-flex>
                <v-layout row wrap align-center justify-center>

                  <v-pagination v-if="data.totalPages>0" @input="updatePaging" v-model="data.page" :total-visible="7"
                                :length="data.totalPages"></v-pagination>

                </v-layout>

              </v-tab-item>
              <v-tab-item v-if="$auth.check() && !$auth.check('ROLE_USER')">


                <v-flex xs2><b>{{$t('services.claimsCount')}}</b>:</v-flex>
                <v-flex xs10>{{item.claimsCount}}</v-flex>
                <v-flex xs2><b>{{$t('services.claimsRejectedCount')}}</b>:</v-flex>
                <v-flex xs10>{{item.claimsRejectedCount}}</v-flex>
                <v-flex xs2><b>{{$t('services.claimsApprovedCount')}}</b>:</v-flex>
                <v-flex xs10>{{item.claimsApprovedCount}}</v-flex>
                <v-flex xs2><b>{{$t('services.freezeCount')}}</b>:</v-flex>
                <v-flex xs10>{{item.freezeCount}}</v-flex>

              </v-tab-item>
            </v-tabs>
          </v-flex>


        </v-layout>

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
  import axios from 'axios'
  import UserView from "@/components/user/UserView.vue";
  require('@/i18n/langs');
  export default {
    mixins: [CommonMixin],
    data () {
      return {
        hasClaim: {},
        claim: {},
        claimUrl: "",
        claimId: 0,
        claimDialog: false,
        activeTab: 0,
        msg: '',
        loading: false,
        user: {},
        item: {}, sortBy: {by: "id", dir: "ASC"},
        data: {page: 1, totalPages: 0, content: []},
        pagination: {
          descending: false,
          localized: "",
          sortBy: 'id'
        }

      }
    },
    props:['activeTabParam'],
    created: function () {
        this.activeTab=this.activeTabParam;
      this.loadService();
    },
    components: {"UserView": UserView},
    watch: {
      activeTab(value){
        if (value == 1) {
          this.updatePaging();
        }

      },
    '$route' (to, from) {
      this.activeTab = 0;
      this.loadService();

    }
    },
    methods: {
      newDeal(serviceId){

        this.confirmUrl("services.confirmdeal", '/api/deals/newdeal', {
          service: serviceId,
          t1: this.waitNotMore ? this.waitNotMoreTime : null
        }).then((data) => {
          this.$router.push({path: '/deals/' + data.data.id});
        }).catch((er) => {
          console.log("catch ", er);
        })
      },
      postServiceClaim(){
        this.claim.comments = "";
        this.claimDialog = true;
        this.claimId = this.$route.params.id;
        this.claimUrl = '/api/services/postClaim';
      },
      postReviewClaim(id){
        this.claim.comments = "";
        this.claimDialog = true;
        this.claimId = id;
        this.claimUrl = '/api/services/postReviewClaim';
      },
      sendClaim(){


        if (this.claim.comments) {
          this.loading = true;
          axios.post(this.claimUrl, {
              id: this.claimId,
              comments: this.claim.comments
            }
          ).then(res => {
            this.loading = false;
            this.claim.comments = "";


            if (this.activeTab == 2) {
              this.updatePaging();
            } else {
              this.loadService();
            }
            this.closeClaim();


          })
            .catch((error) => {
              this.loading = false;
              console.log(error);

              EventBus.$emit('error', error);
            })
        }


      },
      closeClaim()
      {
        this.claimDialog = false;
      },
      updatePaging()
      {
        this.loadlist(this.data.page - 1);
      },

      loadlist(page)
      {
        this.loading = true;
        axios.post('/api/services/reviews/' + this.$route.params.id + '?sort=' + this.sortBy.by + '&sortDirection=' + (this.sortBy.dir ) + '&page=' + page + '&size=', {
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
      loadService()
      {
        this.loading = true;
        axios.post('/api/services/' + this.$route.params.id, {}
        ).then(res => {
          this.loading = false;

          if (res.data.service) {
            this.item = res.data.service;
            this.hasClaim = res.data.hasClaim;
            this.user = res.data.user;
          }
        })
          .catch(error => {
            console.log("ERROR!", error);
            this.loading = false;
            EventBus.$emit('error', error);
          })

      }
      ,
    }
  }
</script>
<style scoped>
.test {
  border: 2px solid red;
}


</style>