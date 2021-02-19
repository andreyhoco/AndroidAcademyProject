package ru.andreyhoco.androidacademyproject.persistence.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import ru.andreyhoco.androidacademyproject.persistence.TheMovieAppDbContract
import ru.andreyhoco.androidacademyproject.persistence.entities.*

data class MovieWithActorsAndGenres(
    @Embedded
    val movie: MovieEntity,

    @Relation(
        parentColumn = TheMovieAppDbContract.Movies.COLUMN_NAME_ID,
        entityColumn = TheMovieAppDbContract.Actors.COLUMN_NAME_ID,
        associateBy = Junction(MovieActorCrossRef::class)
    )
    val actors: List<ActorEntity>,

    @Relation(
        parentColumn = TheMovieAppDbContract.Movies.COLUMN_NAME_ID,
        entityColumn = TheMovieAppDbContract.Genres.COLUMN_NAME_ID,
        associateBy = Junction(MovieGenreCrossRef::class)
    )
    val genres: List<GenreEntity>
)
