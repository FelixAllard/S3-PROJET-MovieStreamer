
export const MovieStatus = Object.freeze({
    WATCHED: 'WATCHED',
    WATCHING: 'WATCHING',
    PLAN_TO_WATCH: 'PLAN_TO_WATCH'
})

export class WatchMovieUser {
    constructor({ id, status, saved, rating } = {}) {
        this.id = id
        this.status = status   // one of MovieStatus values
        this.saved = saved
        this.rating = rating
        // user and movie are @JsonIgnore on the backend, so they won't appear here
    }
}