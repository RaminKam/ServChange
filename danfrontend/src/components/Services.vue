<template>
  <v-flex xs12>
    <div v-if="$auth.ready() &&! loading">
      <v-container
        fluid
        grid-list-lg
        pa-0
        ma-0
      >
        <v-layout row wrap>
          <v-flex xs12>

            <v-card color="white ">
              <!-- <v-card-title primary-title>
                <div class="headline">Searching services:</div>    
                
              </v-card-title> -->
              <v-card-actions>
                <div class="headline">Searching services:</div> 
                
                <v-spacer></v-spacer>
                <v-btn icon @click="show12 = !show12">
                   <v-icon>{{ show12 ? 'keyboard_arrow_down' : 'keyboard_arrow_up' }}</v-icon>
                </v-btn>
              </v-card-actions>
            </v-card>
            <v-slide-y-transition>
              
              <v-card color="white" v-show="show12" >
                



                <v-layout row wrap align-center justify-center mb-4 pl-3 pr-3>
                  

                  <v-flex xs12>
                    Choice parameters of needed services:
                  </v-flex>


                  <v-flex xs12>
                    <div align="center">
                      <v-card width="270" flat >
                        <v-layout row fill-height justify-center align-center >
                          <v-flex xs8 >
                            <v-text-field
                              v-model="params.search"
                              :label="$t('base.search')"
                              single-line
                              hide-details
                            >   
                            </v-text-field>
                          </v-flex>
                          


                          <v-flex xs4 >
                              <v-btn  flat icon color="indigo" :to="'/services/'+$route.params.category+'/'+params.search" @click="updatePaging()">
                                <v-icon>search</v-icon>

                              </v-btn>

                          </v-flex>
                        </v-layout>
                      </v-card>
                    </div>
              
                  </v-flex>

                  <v-flex xs12>
                    <div align="center">
                    <v-card width="270" flat>
                    <v-layout row fill-height align-center justify-center >
                      
                      <v-flex xs4>
                        <div align="left">Sort by:</div>
                      </v-flex>
                      
                      <v-flex xs8 >
                        <v-select
                  :items="sortByList"
                  v-model="sortBy"
                  label="Select"
                  item-text="title"
                  item-value="sort"
                  max-height="auto"
                  :label="$t('base.sortby')"
                  single-line
                >

                </v-select> 
                      </v-flex>

                    </v-layout>
                  </v-card>
                  </div>
                  </v-flex>

                  <v-flex xs12>
                    <div align="center">
                    <v-card width="270" flat>
                    <v-layout row fill-height justify-center align-center>
                      
                      <v-flex xs4 >
                        <div align="left">Category:</div>
                      </v-flex>
                      
                      <v-flex xs8 >
                        <v-select

                           :items="categories"
                           v-model="filter.categoryId"
                           :label="$t('base.selectcategory')"
                           item-text="title" auto
                           item-value="id"
                           single-line
                           max-height="auto"
                >

                </v-select>
                      </v-flex>

                    </v-layout>
              </v-card>
              </div>
                  </v-flex>
              
              <v-flex xs12>
                <div align="center">
                    <v-card width="270" flat>
                    <v-layout row fill-height justify-center align-center>
                      
                      <v-flex xs4 >
                        <div align="left">University:</div>
                      </v-flex>
                      
                      <v-flex xs8 >
                        <v-select v-model="filter.univerId"
                        

                        item-text="title" item-value="id"
                        :items="univers" 

                        :label="'Select University'"


              ></v-select>
                      </v-flex>

                    </v-layout>
                  </v-card>
                </div>
              </v-flex>

              
               
                                  
              
              
              <v-flex xs8 lg8>

                

              </v-flex>

              <v-flex xs3 lg1>
                
              </v-flex>
            </v-layout>


            </v-card>

            <!-- <v-card-text v-show="show12" v-if="0">
            </v-card-text> -->

            </v-slide-y-transition>
          </v-flex>

          <v-flex xs12 v-for="item in data.content" :key="item.id">
            <v-card>
              <v-layout>
                <v-flex xs5 pl-3>
                  <v-layout column >
                    <v-flex  >
                     
                             
          
        
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
                    <v-flex  >
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
                  <v-layout column wrap >
                    <v-flex xs12>
                      <router-link :to="'/service/'+item.id+'/serviceinfo'">
                      <div style="font-size: 24px;" class="yaFont black--text" align="center">
                        {{i18nTranslate(item, 'title', '')}}
                        
                      </div>
                      </router-link>
                    </v-flex>
                    <v-flex xs12 text-xs-left>
      
                        <span class="yaFont">Time:</span> {{item.calendar}}
                        <br><br>
                          <span class="grey--text">{{categoriesmap[item.categoryId] }} </span>
                          <span class="grey--text">{{univerMap[item.univerId].title }} </span>
 
                        
                    </v-flex>
 
                    <!-- <v-flex d-flex>
                      <div style="font-size: 24px;" class="yaFont" align="left">{{item.cost}} DNC</div>
                      
                    </v-flex> -->
                  </v-layout>
                  
                  <!-- <v-card-title primary-title>
                    <span>
                      <span class="headline"><router-link :to="'/service/'+item.id+'/serviceinfo'">{{i18nTranslate(item, 'title', '')}}</router-link></span>
          
                    </span>
                  </v-card-title> -->
                  <v-layout row >
                    <v-flex xs12 v-if="0">
                    <table>
                    <tr align="left"><td align="left">Time:</td><td align="center">{{item.calendar}}</td></tr>
                    <!-- <tr align="left"><td>University:</td><td align="center">{{univerMap[item.univerId].title }}</td></tr>
                    <tr align="left"><td>Category:</td><td align="center">{{categoriesmap[item.categoryId] }}</td></tr> -->
                    
                    </table>
                     <br>{{categoriesmap[item.categoryId] }} 

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
                <v-btn flat small round color="#01B1C1" outline :to="'/service/'+item.id+'/serviceinfo'">
                <span style="font-size: 12px" class="black--text">
                <b>More info</b>
                </span>
                </v-btn>
                <v-spacer></v-spacer>
                <v-btn @click="newDeal(item.id)" flat small round color="#01B1C1" outline>
                  <span style="font-size: 12px" class="black--text">
                  <b>Buy for {{item.cost}} DNC</b>
                  </span>
                </v-btn>
                
                

              </v-card-actions>

            </v-card>
          </v-flex>
          <v-flex xs12 v-if="data.content.length==0">
              {{$t('base.nodata')}}
          </v-flex>

          
        </v-layout>
                  <v-layout row wrap pt-2 align-center justify-center>

            <v-pagination v-if="data.totalPages>0" @input="updatePaging" v-model="data.page" :total-visible="7"
                          :length="data.totalPages"></v-pagination>

          </v-layout>
      </v-container>
    

      
    </div>
    
    <v-layout v-if="!$auth.ready() || loading" align-center justify-center>
      <v-progress-circular indeterminate color="primary"></v-progress-circular>
    </v-layout>
  </v-flex>


</template>

<script>
import CommonMixin from "@/mixins/common";
import { EventBus } from "@/event-bus";
import router from "@/router";
import axios from "axios";
require("@/i18n/langs");
export default {
  mixins: [CommonMixin],
  data() {
    return {
show12: false,
    contents12:[
      {
      id:1, 
      title:"Ranning1 rrrrr rr rr r rr", 
      src:"https://cdn.vuetifyjs.com/images/cards/halcyon.png", 
      calendar:"one lesseon in 60 minutes",
      category:"Education/Tuitor",
      univer:"Tsinghua University",
      cost:200
       },
      {
      id:2, 
      title:"Ranning2", 
      src:"https://cdn.vuetifyjs.com/images/cards/halcyon.png", 
      calendar:"one lesseon in 60 minutes",
      category:"Education/Tuitor",
      univer:"Tsinghua University",
      cost:200
       },
      {
      id:3, 
      title:"Ranning3", 
      src:"https://cdn.vuetifyjs.com/images/cards/halcyon.png", 
      calendar:"one lesseon in 60 minutes",
      category:"Education/Tuitor",
      univer:"Tsinghua University",
      cost:200
       }
    ],



      filter: {},

      sortBy: { by: "id", dir: "DESC" },
      msg: "",
      loading: false,
      data: { page: 1, totalPages: 0, content: [] },
      params: {
        search: ""
      },

      pagination: {
        descending: false,
        localized: "",
        sortBy: "id"
      }
    };
  },
  watch: {
    filter: {
      handler() {
        this.updatePaging();
      },
      deep: true
    },
    sortBy: {
      handler() {
        this.updatePaging();
      },
      deep: true
    }
  },
  mounted() {
    EventBus.$emit("change-title", "title");
  },
  created: function() {
    this.params.search = this.$route.params.search;
    this.updatePaging();
  },
  methods: {
    updatePaging() {
      this.loadlist(this.data.page - 1);
    },
    newDeal(serviceId) {
      let vm = this;

      this.confirmUrl("services.confirmdeal", "/api/deals/newdeal", {
        service: serviceId,
        t1: this.waitNotMore ? this.waitNotMoreTime : null
      })
        .then(data => {
          this.$router.push({ path: "/deals/" + data.data.id });
        })
        .catch(er => {
          console.log("catch ", er);
        });
    },
    loadlist(page) {
      this.loading = true;
      axios
        .post(
          "/api/services/all?sort=" +
            this.sortBy.by +
            "&sortDirection=" +
            this.sortBy.dir +
            "&page=" +
            page +
            "&size=",
          {
            filter: this.filter,
            search: this.params.search ? this.params.search : null,
            category: this.filter.categoryId
          }
        )
        .then(res => {
          this.loading = false;

          if (res.data.content) {
            //   console.log("data",res.data);
            this.data.content = res.data.content;
            this.data.page = res.data.page + 1;
            this.data.totalPages = res.data.totalPages;
            this.pagination.rowsPerPage = res.data.size;
            this.pagination.totalItems = res.data.totalElements;
          }
        })
        .catch(error => {
          console.log("ERROR!", error);
          this.loading = false;
          EventBus.$emit("error", error);
        });
    }
  }
};
</script>

<style>
.test {
  border: 2px solid red;
}

.rounded-card{
  border-radius:50px;
}
</style>