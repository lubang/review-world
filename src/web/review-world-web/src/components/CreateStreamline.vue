<template>
  <div class="form has-top-margin">
    <div class="field">
      <label class="label">Streamline Id</label>
      <div class="control">
        <input class="input" type="text" placeholder="Unique Streamline Id">
      </div>
    </div>

    <div class="field">
      <label class="label">Review Fetcher</label>
      <div class="field">
        <div class="control">
          <div class="select">
            <select v-model="fetcherType">
              <option>Gerrit</option>
              <option>Github</option>
            </select>
          </div>
        </div>
      </div>
      <div v-if="fetcherType == 'Gerrit'" class="is-configuration">
        <div class="field is-horizontal">
          <div class="field-label is-normal">
            <label class="label">Fetching Interval (Millis)</label>
          </div>
          <div class="field-body">
            <div class="field is-narrow">
              <div class="control">
                <input class="input" type="number" placeholder="fetchInterval">
              </div>
            </div>
          </div>
        </div>
        <div class="field is-horizontal">
          <div class="field-label is-normal">
            <label class="label">Gerrit REST API URL</label>
          </div>
          <div class="field-body">
            <div class="field">
              <div class="control">
                <input class="input" type="text" placeholder="url">
              </div>
            </div>
          </div>
        </div>
        <div class="field is-horizontal">
          <div class="field-label is-normal">
            <label class="label">Project</label>
          </div>
          <div class="field-body">
            <div class="field is-narrow">
              <div class="control">
                <input class="input" type="text" placeholder="project">
              </div>
            </div>
          </div>
        </div>
        <div class="field is-horizontal">
          <div class="field-label is-normal">
            <label class="label">Gerrit HTTP Username</label>
          </div>
          <div class="field-body">
            <div class="field is-narrow">
              <div class="control">
                <input class="input" type="text" placeholder="username">
              </div>
            </div>
          </div>
        </div>
        <div class="field is-horizontal">
          <div class="field-label is-normal">
            <label class="label">Gerrit HTTP Password</label>
          </div>
          <div class="field-body">
            <div class="field is-narrow">
              <div class="control">
                <input class="input" type="text" placeholder="password">
              </div>
            </div>
          </div>
        </div>
      </div>
      <div v-if="fetcherType == 'Github'" class="is-configuration">
        <div class="field is-horizontal">
          <div class="field-label is-normal">
            <label class="label">Fetching Interval (Millis)</label>
          </div>
          <div class="field-body">
            <div class="field is-narrow">
              <div class="control">
                <input class="input" type="number" placeholder="fetchInterval">
              </div>
            </div>
          </div>
        </div>
        <div class="field is-horizontal">
          <div class="field-label is-normal">
            <label class="label">Github Graphql URL</label>
          </div>
          <div class="field-body">
            <div class="field">
              <div class="control">
                <input class="input" type="text" placeholder="githubGraphQlUrl">
              </div>
            </div>
          </div>
        </div>
        <div class="field is-horizontal">
          <div class="field-label is-normal">
            <label class="label">Repository Owner</label>
          </div>
          <div class="field-body">
            <div class="field is-narrow">
              <div class="control">
                <input class="input" type="text" placeholder="owner">
              </div>
            </div>
          </div>
        </div>
        <div class="field is-horizontal">
          <div class="field-label is-normal">
            <label class="label">Repository Name</label>
          </div>
          <div class="field-body">
            <div class="field is-narrow">
              <div class="control">
                <input class="input" type="text" placeholder="repository">
              </div>
            </div>
          </div>
        </div>
        <div class="field is-horizontal">
          <div class="field-label is-normal">
            <label class="label">Github Username (for API)</label>
          </div>
          <div class="field-body">
            <div class="field is-narrow">
              <div class="control">
                <input class="input" type="text" placeholder="username">
              </div>
            </div>
          </div>
        </div>
        <div class="field is-horizontal">
          <div class="field-label is-normal">
            <label class="label">Github Password (for API)</label>
          </div>
          <div class="field-body">
            <div class="field is-narrow">
              <div class="control">
                <input class="input" type="text" placeholder="password">
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="field">
      <label class="label">Review Notifier</label>
      <div class="field is-grouped">
        <div class="control">
          <div class="select">
            <select v-model="notifierType">
              <option>Slack</option>
            </select>
          </div>
        </div>
      </div>
      <div v-if="notifierType == 'Slack'" class="is-configuration">
        <div class="field is-horizontal">
          <div class="field-label is-normal">
            <label class="label">Slack Webhook URL</label>
          </div>
          <div class="field-body">
            <div class="field">
              <div class="control">
                <input class="input" type="text" placeholder="webhookUrl">
              </div>
            </div>
          </div>
        </div>
        <div class="field is-horizontal">
          <div class="field-label is-normal">
            <label class="label">Slack Channel</label>
          </div>
          <div class="field-body">
            <div class="field is-narrow">
              <div class="control">
                <input class="input" type="text" placeholder="channel">
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="field is-grouped is-command">
      <div class="control">
        <button class="button is-link">Submit</button>
      </div>
      <div class="control">
        <button class="button is-text">Cancel</button>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.is-command {
  margin-top: 2.4em;
}
.is-configuration {
  background-color: rgba(0, 0, 0, 0.025);
  border: 1px dashed #eeeeee;
  border-radius: 4px;
  padding: 1em;
}
</style>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator'

@Component
export default class CreateStreamline extends Vue {
  private fetcherType = 'Gerrit'
  private notifierType = 'Slack'
}
</script>
