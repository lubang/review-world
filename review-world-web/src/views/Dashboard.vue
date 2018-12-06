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
    <section class="section">
      <article class="media" :key="review.reviewId" v-for="review in reviews">
        <figure class="media-left">
          <p class="image is-64x64">
            <img alt="Gerrit logo" src="../assets/gerrit-logo.png">
          </p>
        </figure>
        <div class="media-content">
          <div class="content">
            <strong>{{review.owner}}</strong>
            <small>({{fromNow(review.createdAt)}})</small>
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
      <article class="message is-primary" v-if="reviews.length == 0">
        <div class="message-header"></div>
        <div class="message-body">
          <p>No recent reviews :D</p>
        </div>
      </article>
      <div class="has-text-right">
        <span class="tag is-light">Last fetched</span>
        <small> {{lastFetchedAt}}</small>
        <small class="has-text-grey"> ({{lastFetchedTimeAt}})</small>
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
import { Action, State } from 'vuex-class'
import moment from 'moment'

@Component
export default class Dashboard extends Vue {
  @Action('fetchReviews')
  private fetchReviews: any

  @State('reviews')
  private reviews: any

  private mounted() {
    this.fetchReviews()
  }

  get lastFetchedAt() {
    return moment(this.$store.state.lastFetchedAt).fromNow()
  }

  get lastFetchedTimeAt() {
    return moment(this.$store.state.lastFetchedAt).format('MM/DD HH:mm:ss')
  }

  private fromNow(timestamp: number) {
    return moment(timestamp).fromNow()
  }
}
</script>

