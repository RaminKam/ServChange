<template xmlns="http://www.w3.org/1999/html">
  <div>

  </div>
</template>

<script type="text/babel">



  export default {
    data: function () {
      return {
        loading: false
      }
    },

    props: {

      params: {},
      suburl: {
        type: String,
        default: function () {
          return "";
        }


      },
      url: {
        type: String,
        default: function () {
          return window.location.pathname
        }

      },
      load_on_start: {

        default: function () {
          return true
        }

      },
      data: {}
    },
    watch: {
      params: function () {
        console.log("new params" + this.params);
        this.update();
      }
    },
    methods: {
      update: function (tries) {
        this.$emit('update:loading', true);
        if(!tries)
          tries=1;
        if(tries>3){
          return;
        }
        let vm = this;
        this.loading = true;
        this.axios.get( this.suburl + (this.params ? ("?" + this.params) : ""))
          .then(function (response) {
            //vm.data = response.data;
            vm.$emit('update:loading', false);
            vm.$emit('input', response.data);
            vm.$emit('loaded', response.data);
            vm.loading = false;
          })
          .catch( (response)=> {
            console.log(response);
            vm.loading = false;
            this.update(tries+1);
          });
      },
    },


    created: function () {
      if (this.load_on_start)
        this.update();

    },

  }
</script>
