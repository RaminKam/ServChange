<template>
<v-layout column >
<v-card>
              <v-layout   pt-1 >
                <v-flex xs5 >    
                  <v-layout column  >
                    <v-flex pl-1 pr-1>
                     
                      <v-img v-if="user.logo" 
                    :src="user.logo"
                    height="125px"
                    contain
                  ></v-img>

                  <v-img v-else
                    :src="require('@/assets/nophoto.png')"
                    height="125px"
                    contain
                  ></v-img>
                      
                    </v-flex>
                    <v-flex  align-self-center v-if="!user.removed">
                      <div align="center">
<star-rating :show-rating="false" inline :length="5" read-only v-model="user.rate" :star-size="20" :increment="0.01" />
                        <!-- <v-rating
                      v-model="user.rate"
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
                      <div style="font-size: 24px;" class="yaFont">{{user.username}} {{user.secondname}}</div>
                    </v-flex>
                  </v-layout>
                  
      <v-layout row wrap>
        <v-flex xs12  v-if="user.removed">
          <b>{{$t('user.removed')}}</b>
        </v-flex>
        <v-flex xs12  v-if="!user.removed">
                  <table>
                    <tr v-if="user.about">
                      <td colspan="2" align="left">{{user.about }}<br> <br></td>
                      <td> </td>
                    </tr>
                    <tr><td> </td><td> </td></tr>
                    <tr align="left">
                      <td align="left"><span class="yaFont"> {{$t('register.university')}}:</span></td>
                      <td align="left">{{user.universitetId ? univerMap[user.universitetId].title : ""}}</td>
                    </tr>
                    <tr align="left">
                      <td align="left"><span class="yaFont">{{$t('register.lang')}}:</span></td>
                      <td align="left">{{$t('langs.' + user.lang)}}</td>
                      </tr> 
                    <tr align="left" v-if="user.city">
                      <td align="left"><span class="yaFont">{{$t('register.city')}}:</span></td>
                      <td align="left">{{user.city }}</td>
                    </tr>
                    <tr align="left" v-if="user.wasAt">
                      <td align="left"><span class="yaFont">{{$t('base.wasat')}}:</span></td>
                      <td align="left">{{user.wasAt | formatDateTime}}</td>
                    </tr>
                    
                  </table>
        </v-flex>



        <!-- <v-flex xs12  v-if="user.removed"><b>{{$t('user.removed')}}</b></v-flex>
        <v-flex xs12><b>{{user.username}}</b></v-flex>
        <v-flex xs12><b>{{user.name}} {{user.secondname}}</b></v-flex>
        <v-flex xs12 v-if="user.email"><b>{{$t('register.email')}}</b>: {{user.email}}</v-flex>
        <v-flex xs12  v-if="!user.removed" test>
          {{$t('register.university')}}:<b>{{user.universitetId ? univerMap[user.universitetId].title : ""}}</b>
        </v-flex>
        <v-flex xs12 v-if="!user.removed">{{$t('register.lang')}}: <b>{{$t('langs.' + user.lang)}}</b></v-flex>

        <v-flex xs12 v-if="user.studentid">{{$t('register.studentid')}}: <b>{{user.studentid }}</b></v-flex>
        <v-flex xs12 v-if="user.city">{{$t('register.city')}}: <b>{{user.city }}</b></v-flex>
        <v-flex xs12 v-if="user.about">{{$t('register.about')}}: <b>{{user.about }}</b></v-flex>
        <v-flex xs12 v-if="user.wasAt">{{$t('base.wasat')}}: {{user.wasAt | formatDateTime}}</v-flex>
            <v-flex xs12>
      {{user.about}}
    </v-flex> -->
      </v-layout>


 



 


      
                </v-flex>
              </v-layout>


              <v-divider light></v-divider>
              <v-card-actions class="pa-3">
                
     

              </v-card-actions>
              <v-layout column>
                <v-flex xs12 v-if="$auth.check('ROLE_USER')&&user.services&&user.services.length>0">
      <span class="yaFont"><B>Providing Services</B>
      </span>
      <div v-if="0">{{$t('user.providedserivces')}}</div>
      <br>


      <v-layout body-1 row wrap v-for="service in user.services" :key="service.id" mb-1>
        <template>
          <v-flex xs7 body-1 align-self-center>
            <span class="yaFont">{{i18nTranslate(service, "title")}}
            </span>
          </v-flex>
          
          <v-flex xs5>
            <span class="yaFont">{{service.cost | formatMoney}}</span>

            <v-btn @click="openService(service.id)"
            small round color="#01B1C1" outline
            >
              <span style="font-size: 12px" class="black--text">
              <b>
              {{$t('services.buy')}}
              </b>
              </span>
            </v-btn>
          </v-flex>

        </template>

      </v-layout>


    </v-flex>
    <v-flex xs12 v-if="$auth.check('ROLE_USER')&&user.feedbacks&&user.feedbacks.length>0">
      <B>{{$t('user.doerfeed')}}</B>
      <br>
      <v-layout row wrap v-for="service in user.services" :key="service.id">
        {{i18nTranslate(service, "title")}}:
      </v-layout>


    </v-flex>

    <template v-if="$auth.check() && !$auth.check('ROLE_USER')">
      <v-flex xs2><b>{{$t('user.roleName')}}</b>:</v-flex>
      <v-flex xs10>{{$t('user.role.' + user.role)}}</v-flex>
      <template v-if="user.role=='ROLE_USER'">
        <v-flex xs2><b>{{$t('user.verificated')}}</b>:</v-flex>
        <v-flex xs10>{{user.verificated ? $t('user.verificatedTrue') : $t('user.verificatedFalse')}}</v-flex>
        <v-flex xs2><b>{{$t('user.feedbackCount')}}</b>:</v-flex>
        <v-flex xs10>{{user.feedbackCount}}</v-flex>
        <v-flex xs2><b>{{$t('user.servicesCount')}}</b>:</v-flex>
        <v-flex xs10>{{user.servicesCount}}</v-flex>
        <v-flex xs2><b>{{$t('user.dealsBuyCount')}}</b>:</v-flex>
        <v-flex xs10>{{user.dealsBuyCount}}</v-flex>
        <v-flex xs2><b>{{$t('user.dealsSellCount')}}</b>:</v-flex>
        <v-flex xs10>{{user.dealsSellCount}}</v-flex>
      </template>
    </template>
    <v-flex mt-5 v-if="user.resources && user.resources.length>0" caption>
      <b>{{$t('admin.resources')}}</b>:<br>
      <v-flex mt-2 color="info" dark v-for="res in user.resources" :key="res.id">

        <a :href="'/api/resources/download/'+res.id">
          {{res.original}}
          <v-icon color="blue" small>cloud_download</v-icon>
        </a>
      </v-flex>
    </v-flex>


              </v-layout>

            </v-card>
</v-layout>




  <!-- <v-layout row wrap align-left text-xs-left v-if="0">
    <v-flex xs12 lg4 mt-1 mb-1 >
      <v-avatar
        :tile="false"
        :size="200"
        color="grey lighten-4"
      ><img height="100" style="min-height: 100px" v-if="user.logo" :src="user.logo">
        <img v-else src="@/assets/nophoto.png"></v-avatar>
    </v-flex>

    <v-flex xs8 mt-1 mb-1>
      <v-layout row wrap>
        <v-flex xs12  v-if="user.removed"><b>{{$t('user.removed')}}</b></v-flex>
        <v-flex xs12><b>{{user.username}}</b></v-flex>
        <v-flex xs12><b>{{user.name}} {{user.secondname}}</b></v-flex>
        <v-flex xs12 v-if="user.email"><b>{{$t('register.email')}}</b>: {{user.email}}</v-flex>
        <v-flex xs12  v-if="!user.removed">
          {{$t('register.university')}}:<b>{{user.universitetId ? univerMap[user.universitetId].title : ""}}</b>
        </v-flex>
        <v-flex xs12 v-if="!user.removed">{{$t('register.lang')}}: <b>{{$t('langs.' + user.lang)}}</b></v-flex>

        <v-flex xs12 v-if="user.studentid">{{$t('register.studentid')}}: <b>{{user.studentid }}</b></v-flex>
        <v-flex xs12 v-if="user.city">{{$t('register.city')}}: <b>{{user.city }}</b></v-flex>
        <v-flex xs12 v-if="user.about">{{$t('register.about')}}: <b>{{user.about }}</b></v-flex>
        <v-flex xs12 v-if="user.wasAt">{{$t('base.wasat')}}: {{user.wasAt | formatDateTime}}</v-flex>
      </v-layout>
    </v-flex>
    <v-flex xs12>
      {{user.about}}
    </v-flex>
    <v-flex xs12 v-if="!user.removed">
      <star-rating :increment="0.01" :length="5" read-only inline :star-size="20" v-model="user.rate"/>
    </v-flex>
    <v-flex xs12 v-if="$auth.check('ROLE_USER')&&user.services&&user.services.length>0">
      <B>{{$t('user.providedserivces')}}</B>
      <br>


      <v-layout body-1 row wrap v-for="service in user.services" :key="service.id" mb-1 >
        <template>
          <v-flex xs7 body-1>{{i18nTranslate(service, "title")}}<br><b>{{service.cost | formatMoney}}</b></v-flex>
          <v-flex xs5>
            <v-btn @click="openService(service.id)" color="info">
              <v-icon>play_circle_outline</v-icon>
              {{$t('services.buy')}}
            </v-btn>
          </v-flex>

        </template>

      </v-layout>


    </v-flex>
    <v-flex xs12 v-if="$auth.check('ROLE_USER')&&user.feedbacks&&user.feedbacks.length>0">
      <B>{{$t('user.doerfeed')}}</B>
      <br>
      <v-layout row wrap v-for="service in user.services" :key="service.id">
        {{i18nTranslate(service, "title")}}:
      </v-layout>


    </v-flex>

    <template v-if="$auth.check() && !$auth.check('ROLE_USER')">
      <v-flex xs2><b>{{$t('user.roleName')}}</b>:</v-flex>
      <v-flex xs10>{{$t('user.role.' + user.role)}}</v-flex>
      <template v-if="user.role=='ROLE_USER'">
        <v-flex xs2><b>{{$t('user.verificated')}}</b>:</v-flex>
        <v-flex xs10>{{user.verificated ? $t('user.verificatedTrue') : $t('user.verificatedFalse')}}</v-flex>
        <v-flex xs2><b>{{$t('user.feedbackCount')}}</b>:</v-flex>
        <v-flex xs10>{{user.feedbackCount}}</v-flex>
        <v-flex xs2><b>{{$t('user.servicesCount')}}</b>:</v-flex>
        <v-flex xs10>{{user.servicesCount}}</v-flex>
        <v-flex xs2><b>{{$t('user.dealsBuyCount')}}</b>:</v-flex>
        <v-flex xs10>{{user.dealsBuyCount}}</v-flex>
        <v-flex xs2><b>{{$t('user.dealsSellCount')}}</b>:</v-flex>
        <v-flex xs10>{{user.dealsSellCount}}</v-flex>
      </template>
    </template>
    <v-flex mt-5 v-if="user.resources && user.resources.length>0" caption>
      <b>{{$t('admin.resources')}}</b>:<br>
      <v-flex mt-2 color="info" dark v-for="res in user.resources" :key="res.id">

        <a :href="'/api/resources/download/'+res.id">
          {{res.original}}
          <v-icon color="blue" small>cloud_download</v-icon>
        </a>
      </v-flex>
    </v-flex>
  </v-layout> -->


</template>

<script>
  import CommonMixin from '@/mixins/common';
  require('@/i18n/langs');
  export default {
    mixins: [CommonMixin],
    props: ["user"],
    methods: {

      openService(serviceId){
        this.$router.push({path: '/service/' + serviceId+"/serviceinfo"});
      }
    }
  }
</script>
<style scoped>
.test {
  border: 2px solid red;
}


</style>