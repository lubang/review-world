<template>
  <nav class="navbar" role="navigation" aria-label="main navigation">
    <div class="navbar-brand">
      <router-link to="/" class="navbar-item has-text-weight-bold">#Review World.</router-link>

      <a
        role="button"
        class="navbar-burger burger"
        aria-label="menu"
        aria-expanded="false"
        :class="{'is-active': isActiveNavbar}"
        @click="toggleNavbar()"
      >
        <span aria-hidden="true"></span>
        <span aria-hidden="true"></span>
        <span aria-hidden="true"></span>
      </a>
    </div>

    <div class="navbar-menu" :class="{'is-active': isActiveNavbar}">
      <div class="navbar-start">
        <router-link to="/management" class="navbar-item">Management</router-link>

        <div class="navbar-item has-dropdown is-hoverable">
          <a class="navbar-link">More</a>
          <div class="navbar-dropdown">
            <router-link to="/documentation" class="navbar-item">Documentation</router-link>
            <a href="https://github.com/lubang" class="navbar-item">Contact</a>
            <hr class="navbar-divider">
            <a
              href="https://github.com/lubang/review-world/issues/new?template=bug_report.md"
              class="navbar-item"
            >Report an issue</a>
          </div>
        </div>
      </div>
    </div>
  </nav>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'

@Component
export default class Navbar extends Vue {
  private isActiveNavbar = false

  private toggleNavbar() {
    this.isActiveNavbar = !this.isActiveNavbar
  }
}
</script>
