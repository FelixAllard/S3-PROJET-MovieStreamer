// entities/User.js
import { WatchMovieUser } from './WatchMovieUser'

export class User {
    constructor({ id, username, email, keycloakId, watchedMovieUsers = [] } = {}) {
        this.id = id
        this.username = username
        this.email = email
        this.keycloakId = keycloakId
        this.watchedMovieUsers = watchedMovieUsers.map(w => new WatchMovieUser(w))
    }
}