import { State } from './types'

export const state: State = {
  error: {
    error: '',
    thrownAt: new Date(),
  },
  reviews: [
    {
      streamlineId: 'String',
      reviewId: 'GERRIT-1',
      project: 'String',
      branch: 'String',
      subject:
        'Separate Command and Query in Streamline\n\n- Command: Mutate action\n- Query: Immutate action\n',
      owner: 'Donggeun Bang',
      createdAt: Date.now(),
      updatedAt: Date.now(),
    },
    {
      streamlineId: 'String',
      reviewId: 'RW-2',
      project: 'RW-2',
      branch: 'String',
      subject: 'String',
      owner: 'Donggeun Bang',
      createdAt: Date.now(),
      updatedAt: Date.now(),
    },
    {
      streamlineId: 'String',
      reviewId: 'RW-3',
      project: 'RW-3',
      branch: 'String',
      subject: 'String',
      owner: 'Donggeun Bang',
      createdAt: Date.now(),
      updatedAt: Date.now(),
    },
  ],
  reviewLastFetchedAt: new Date(),
  streamlines: [
    {
      id: 'RW-0000001',
      fetcher: 'Gerrit (Interval: 1000ms, Project: review-world)',
      notifier: 'Slack #channel & Lineworks #channel',
      lastFetchedAt: '2016-10-15 13:43:27',
      register: 'Donggeun Bang',
    },
    {
      id: 'RW-0000002',
      fetcher: 'Gerrit (Interval: 1000ms, Project: review-world)',
      notifier: 'Slack #channel & Lineworks #channel',
      lastFetchedAt: '2016-10-15 13:43:27',
      register: 'Donggeun Bang',
    },
    {
      id: 'RW-0000003',
      fetcher: 'Gerrit (Interval: 1000ms, Project: review-world)',
      notifier: 'Slack #channel & Lineworks #channel',
      lastFetchedAt: '2016-10-15 13:43:27',
      register: 'Donggeun Bang',
    },
    {
      id: 'RW-0000004',
      fetcher: 'Gerrit (Interval: 1000ms, Project: review-world)',
      notifier: 'Slack #channel & Lineworks #channel',
      lastFetchedAt: '2016-10-15 13:43:27',
      register: 'Donggeun Bang',
    },
    {
      id: 'RW-0000005',
      fetcher: 'Gerrit (Interval: 1000ms, Project: review-world)',
      notifier: 'Slack #channel & Lineworks #channel',
      lastFetchedAt: '2016-10-15 13:43:27',
      register: 'Donggeun Bang',
    },
  ],
  streamlineLastFetchedAt: new Date(),
}
