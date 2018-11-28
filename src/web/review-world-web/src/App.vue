<template>
  <div id="app" class="container">
    <Navbar/>
    <router-view/>
  </div>
</template>

<style lang="scss">
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

.has-top-margin {
  margin-top: 1em;
}
</style>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import Navbar from '@/components/Navbar.vue'

@Component({
  components: {
    Navbar,
  },
})
export default class App extends Vue {}
</script>
