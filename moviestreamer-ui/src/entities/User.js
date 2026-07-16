export class User {
    constructor({
        id,
        username,
        email,
        keycloakId,
        enabled,
        watchedMovieUsers = []
    } = {}) {
        this.id = id
        this.username = username
        this.email = email
        this.keycloakId = keycloakId
        this.enabled = enabled
        this.watchedMovieUsers = watchedMovieUsers.map(w => new WatchMovieUser(w))
    }
}