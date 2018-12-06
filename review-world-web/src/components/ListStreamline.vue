<template>
  <b-table class="has-top-margin" :data="$store.state.streamlines" :columns="columns"></b-table>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'

@Component
export default class ListStreamline extends Vue {
  private columns = [
    {
      field: 'id',
      label: 'Streamline Id',
    },
    {
      field: 'fetcher',
      label: 'Fetcher',
    },
    {
      field: 'notifier',
      label: 'Notifiers',
    },
    {
      field: 'lastFetchedAt',
      label: 'Last Fechted At',
    },
    {
      field: 'register',
      label: 'Register',
    },
  ]
}
</script>
