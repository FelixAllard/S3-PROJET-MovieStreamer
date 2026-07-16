// entities/Movie.js
import { Tag } from './Tag'

export class Movie {
    constructor({
                    id,
                    title,
                    description,
                    tags = [],
                    year,
                    movieLength,
                    thumbnail,
                    director,
                    writer,
                    studio,
                    language,
                    streamId
                } = {}) {
        this.id = id
        this.title = title
        this.description = description
        this.tags = tags.map(t => new Tag(t))
        this.year = year
        this.movieLength = movieLength
        this.thumbnail = thumbnail
        this.director = director
        this.writer = writer
        this.studio = studio
        this.language = language
        this.streamId = streamId || '';
    }
}