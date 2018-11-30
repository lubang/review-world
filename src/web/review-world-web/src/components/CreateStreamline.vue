<template>
  <div class="form has-top-margin">
    <div class="field">
      <label class="label">Streamline Id</label>
      <div class="control">
        <input class="input" type="text" v-model="streamlineId">
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
                <input class="input" type="number" v-model="fetchInterval">
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
                <input class="input" type="text" v-model="gerritUrl">
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
                <input class="input" type="text" v-model="gerritProject">
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
                <input class="input" type="text" v-model="gerritUsername">
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
                <input class="input" type="password" v-model="gerritPassword">
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
                <input class="input" type="number" v-model="fetchInterval">
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
                <input class="input" type="text" v-model="githubGraphQlUrl">
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
                <input class="input" type="text" v-model="githubOwner">
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
                <input class="input" type="text" v-model="githubRepository">
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
                <input class="input" type="text" v-model="githubUsername">
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
                <input class="input" type="password" v-model="githubPassword">
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
                <input class="input" type="text" v-model="slackWebhookUrl">
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
                <input class="input" type="text" v-model="slackChannel">
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="field is-grouped is-command">
      <div class="control">
        <button class="button is-link" @click="submitToCreateStream">Submit</button>
      </div>
      <div class="control">
        <button class="button is-text" @click="cancelToCreateStream">Cancel</button>
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
import { Action } from 'vuex-class'
import { IdUtil } from '@/utils/IdUtil'

@Component
export default class CreateStreamline extends Vue {
  private streamlineId = IdUtil.generateUuid()

  private fetcherType = 'Gerrit'
  private notifierType = 'Slack'

  private fetchInterval = 10000

  private gerritUrl = 'https://gerrit.url'
  private gerritProject = ''
  private gerritUsername = ''
  private gerritPassword = ''

  private githubGraphQlUrl = 'https://api.github.com/graphql'
  private githubOwner = ''
  private githubRepository = ''
  private githubUsername = ''
  private githubPassword = ''

  private slackWebhookUrl = ''
  private slackChannel = ''

  @Action('createStreamline')
  private createStreamline: any

  private getFetcherConfig() {
    if (this.fetcherType === 'Gerrit') {
      return {
        fetchInterval: this.fetchInterval,
        url: this.gerritUrl,
        project: this.gerritProject,
        username: this.gerritUsername,
        password: this.gerritPassword,
      }
    } else {
      return {
        fetchInterval: this.fetchInterval,
        githubGraphQlUrl: this.githubGraphQlUrl,
        githubOwner: this.githubOwner,
        githubRepository: this.githubRepository,
        githubUsername: this.githubUsername,
        githubPassword: this.githubPassword,
      }
    }
  }

  private getNotifierConfig() {
    return {
      webhookUrl: this.slackWebhookUrl,
      channel: this.slackChannel,
    }
  }

  private submitToCreateStream() {
    const payload = {
      streamlineId: this.streamlineId,
      data: {
        fetcherType: this.fetcherType,
        fetcherConfig: this.getFetcherConfig(),
        notifierType: this.notifierType,
        notifierConfig: this.getNotifierConfig(),
      },
    }
    this.createStreamline(payload)
  }

  private cancelToCreateStream() {
    this.streamlineId = IdUtil.generateUuid()
  }
}
</script>
