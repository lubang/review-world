export class MutationTypes {
  public static readonly ERROR_THROWN = 'ERROR_THROWN'
  // Streamlines
  public static readonly STREAMLINE_FETCHED = 'STREAMLINE_FETCHED'
  // Reviews
  public static readonly REVIEWS_FETCHED = 'REVIEWS_FETCHED'
}

export interface ErrorState {
  error: string
  thrownAt: Date
}

export interface State {
  error: ErrorState
  reviews: any[]
  reviewLastFetchedAt: Date
  streamlines: any[]
  streamlineLastFetchedAt: Date
}

