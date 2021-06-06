package ru.andreyhoco.androidacademyproject.persistence

import ru.andreyhoco.androidacademyproject.persistence.entities.ActorEntity
import ru.andreyhoco.androidacademyproject.persistence.entities.GenreEntity
import ru.andreyhoco.androidacademyproject.persistence.entities.MovieEntity

import ru.andreyhoco.androidacademyproject.ui.uiDataModel.Movie



fun Movie.getMovieEntity(): MovieEntity {
    return MovieEntity(
        movieId = this.id,
        title = this.title,
        overview = this.overview,
        poster = this.poster,
        backdrop = this.backdrop,
        ratings = this.ratings,
        numberOfRatings = this.numberOfRatings,
        isAdult = (this.minimumAge > 16),
        runtime = this.runtime,

        releaseDate = 0
    )
}

fun Movie.getGenreEntities(): List<GenreEntity> {
    return this.genres.map {
        GenreEntity(
            genreId = it.id,
            name = it.name
        )
    }
}

fun Movie.getActorEntities(): List<ActorEntity> {
    return this.actors.map {
        ActorEntity(
            actorId = it.id,
            name = it.name,
            picture = it.picture
        )
    }
}

