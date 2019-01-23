<template>


  <v-layout row wrap mb-1 pa-3>
    <v-flex xs4 lg2>
      <v-avatar class="small-tile"
                :tile="false"
                :size="$vuetify.breakpoint.xs?80:250"
                color="grey lighten-4"
      > <img v-if="item.logo" :src="item.logo">
        <img  v-else src="@/assets/nophoto.png">

      </v-avatar>
    </v-flex>
    <v-flex xs8 lg10>
      <v-flex xs12>
        <b>{{$t('base.cost')}}</b>{{item.cost|formatMoney}}
      </v-flex>
      <v-flex xs12 v-if="0">
        <b>{{$t('deal.service')}}</b>:
        <router-link :to="'/service/'+item.id+'/serviceinfo'">{{item['title' + $i18n.locale]}} </router-link>
      </v-flex>
 

      <v-flex xs12>
        <b>{{$t('deal.SELLER_ROLE')}}</b>:
        <router-link :to="'/user/'+user.id">{{user.username}} {{user.secondname}} </router-link>
      </v-flex>
      <v-flex xs12 v-if="buyer">
        <b>{{$t('deal.BUYER_ROLE')}}</b>:
        <router-link :to="'/user/'+buyer.id">{{buyer.username}} {{buyer.secondname}} </router-link>
      </v-flex>
      <v-flex xs12>
        <b>{{$t('services.status')}}:</b>:
        {{$t("deal.status." + deal1.status)}} 
      </v-flex>

    </v-flex>
    <v-flex xs12 mt-4 headline class="text-xs-center">

    </v-flex>

  </v-layout>


</template>

<script>
  import CommonMixin from '@/mixins/common';
  import {EventBus} from '@/event-bus'
  import router from "@/router";
  import axios from 'axios'

  require('@/i18n/langs');
  export default {
    mixins: [CommonMixin],

    props: ["item", "user","buyer","deal1"]
  }
</script>
