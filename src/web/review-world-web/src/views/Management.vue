<template>
  <section class="has-top-margin">
    <b-tabs>
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
import CreateStreamline from '@/components/CreateStreamline.vue'
import ListStreamline from '@/components/ListStreamline.vue'

@Component({
  components: {
    CreateStreamline,
    ListStreamline,
  },
})
export default class Management extends Vue {}
</script>
