<template>
  <section class="has-top-margin">
    <b-tabs @change="changeTab">
      <b-tab-item label="Streamlines">
        <ListStreamline/>
      </b-tab-item>
      <b-tab-item label="Create Streamline">
        <CreateStreamline/>
      </b-tab-item>
    </b-tabs>
  </section>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import { Action } from 'vuex-class'
import CreateStreamline from '@/components/CreateStreamline.vue'
import ListStreamline from '@/components/ListStreamline.vue'

@Component({
  components: {
    CreateStreamline,
    ListStreamline,
  },
})
export default class Management extends Vue {
  private static readonly STREAMLINE_TAB = 0

  @Action('fetchStreamlines')
  private fetchStreamlines: any

  private mounted() {
    this.fetchStreamlines()
  }

  private changeTab(index: number) {
    if (index === Management.STREAMLINE_TAB) {
      this.fetchStreamlines()
    }
  }
}
</script>
