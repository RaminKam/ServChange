export default  {
  data(){
    return {
      DEAL_STATUSES: ['WAITING_SELLER', 'GENERATING_RULES', 'RULES_GENERATED', 'RULES_REJECTED',
        'WAITING_DEAL_RESULT', 'DEAL_COMPLETED', 'FINISHED', 'MODERATING', 'CLOSED'
      ],
      STATUSES_MAP: {
        SELLER:{
          WAITING_SELLER:['GENERATING_RULES','CLOSED'],
          GENERATING_RULES:['CLOSED'],
          RULES_GENERATED:['WAITING_DEAL_RESULT','RULES_REJECTED', 'CLOSED'],
          RULES_REJECTED:['CLOSED'],
          WAITING_DEAL_RESULT:['DEAL_COMPLETED','CURATOR_REVIEW',  'CLOSED'],
          DEAL_COMPLETED:['CURATOR_REVIEW','CLOSED'],
          CURATOR_APPROVE_DEAL:['APPROVE_CURATOR_SOLUTION','MAIN_CURATOR_REVIEW'],
          CURATOR_REJECT_DEAL:['APPROVE_CURATOR_SOLUTION','MAIN_CURATOR_REVIEW'],
          FINISHED:[],
          MODERATING:[],
          CLOSED:[]
        },
        BUYER:{
          WAITING_SELLER:['CLOSED'],
          GENERATING_RULES:['RULES_GENERATED','CLOSED'],
          RULES_GENERATED:['CLOSED'],
          RULES_REJECTED:['RULES_GENERATED','CLOSED'],
          WAITING_DEAL_RESULT:['CURATOR_REVIEW','FINISHED'],
          DEAL_COMPLETED:['CURATOR_REVIEW','FINISHED'],
          CURATOR_APPROVE_DEAL:['APPROVE_CURATOR_SOLUTION','MAIN_CURATOR_REVIEW'],
          CURATOR_REJECT_DEAL:['APPROVE_CURATOR_SOLUTION','MAIN_CURATOR_REVIEW'],
          FINISHED:[],
          MODERATING:[],
          CLOSED:[]
        },
        CURATOR:{
          CURATOR_REVIEW:['CURATOR_APPROVE_DEAL','CURATOR_REJECT_DEAL']
        },
        MAIN_CURATOR:{
          MAIN_CURATOR_REVIEW:['CURATOR_APPROVE_DEAL','CURATOR_REJECT_DEAL']
        }
      }
    }
  },
  computed:{
    dealStatusSelect(){
           return this.DEAL_STATUSES.map((status,index)=>{return {text:this.$t('deal.status.'+status),value:index}})
    },
    sellerOrBuyer(){
      return [{text:this.$t('deal.SELLER_ROLE'),value:0},{text:this.$t('deal.BUYER_ROLE'),value:1}];
    }
  }
}
