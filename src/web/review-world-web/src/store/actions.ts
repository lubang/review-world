import { ActionTree, ActionContext } from 'vuex'
import axios from 'axios'

import { State, MutationTypes } from './types'

export const actions: ActionTree<State, State> = {
  async fetchReviews(store: ActionContext<State, State>) {
    try {
      const { data } = await axios.get('/api/reviews')
      store.commit(MutationTypes.REVIEWS_FETCHED, { reviews: data })
    } catch (err) {
      store.commit(MutationTypes.ERROR_THROWN, {
        error: err,
        thrownAt: Date.now,
      })
    }
  },

  async fetchStreamlines(store: ActionContext<State, State>) {
    try {
      const { data } = await axios.get('/api/streamlines')
      store.commit(MutationTypes.STREAMLINE_FETCHED, { streamlines: data })
    } catch (err) {
      store.commit(MutationTypes.ERROR_THROWN, {
        error: err,
        thrownAt: Date.now,
      })
    }
  },

  async createStreamline(store: ActionContext<State, State>, payload) {
    try {
      await axios.post('/api/streamlines/' + payload.streamlineId, payload.data)
      store.dispatch('fetchStreamlines')
    } catch (err) {
      store.commit(MutationTypes.ERROR_THROWN, {
        error: err,
        thrownAt: Date.now,
      })
    }
  },
}
