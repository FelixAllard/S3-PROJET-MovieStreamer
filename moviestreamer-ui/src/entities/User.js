// entities/User.js
import { WatchMovieUser } from './WatchMovieUser'

export class User {
    constructor({ id, name, surname, email, keycloakId, watchedMovieUsers = [] } = {}) {
        this.id = id
        this.name = name
        this.surname = surname
        this.email = email
        this.keycloakId = keycloakId
        this.watchedMovieUsers = watchedMovieUsers.map(w => new WatchMovieUser(w))
    }
}