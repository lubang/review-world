<template>
  <div>
    <section class="hero has-text-centered">
      <div class="hero-body">
        <div class="container">
          <h1 class="title">Review World</h1>
          <h2 class="subtitle">Notify your review for improving your code quality</h2>
        </div>
      </div>
    </section>
    <section class="container">
      <div class="section">
        <article class="media" :key="review.reviewId" v-for="review in $store.state.recentReviews">
          <figure class="media-left">
            <p class="image is-64x64">
              <img alt="Gerrit logo" src="../assets/gerrit-logo.png">
            </p>
          </figure>
          <div class="media-content">
            <div class="content">
              <strong>{{review.owner}}</strong>
              <small> ({{fromNow(review.createdAt)}})</small>
              <span class="description">#{{review.streamlineId}} #{{review.project}}</span>
              <pre>{{review.subject}}</pre>
            </div>
            <nav class="level is-mobile">
              <div class="level-left">
                <a class="level-item">Go to Review</a>
              </div>
            </nav>
          </div>
        </article>
      </div>
    </section>
  </div>
</template>

<style lang="scss" scoped>
.content .description {
  margin-left: 2em;
  font-size: 0.9em;
}
.content pre {
  margin-top: 0.5em;
  padding: 0.8em;
  border-radius: 5px;
}
</style>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'
import moment from 'moment'

@Component
export default class Dashboard extends Vue {
  private fromNow(timestamp: number) {
    return moment(timestamp).fromNow()
  }
}
</script>

