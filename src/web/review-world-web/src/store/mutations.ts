import { MutationTree } from 'vuex'
import { State, MutationTypes } from './types'

export const mutations: MutationTree<State> = {
  [MutationTypes.ERROR_THROWN](state, { error, thrownAt }) {
    state.error = {
      error,
      thrownAt,
    }
  },

  [MutationTypes.REVIEWS_FETCHED](state, { reviews }) {
    state.reviews = reviews
    state.reviewLastFetchedAt = new Date()
  },

  [MutationTypes.STREAMLINE_FETCHED](state, { streamlines }) {
    state.streamlines = streamlines
    state.streamlineLastFetchedAt = new Date()
  },
}
