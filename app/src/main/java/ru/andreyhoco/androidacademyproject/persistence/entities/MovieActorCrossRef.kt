package ru.andreyhoco.androidacademyproject.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import ru.andreyhoco.androidacademyproject.persistence.TheMovieAppDbContract

@Entity(primaryKeys = [
    TheMovieAppDbContract.Movies.COLUMN_NAME_ID,
    TheMovieAppDbContract.Actors.COLUMN_NAME_ID
])
data class MovieActorCrossRef(
    @ColumnInfo(name = TheMovieAppDbContract.Movies.COLUMN_NAME_ID)
    val movieId: Long,

    @ColumnInfo(name = TheMovieAppDbContract.Actors.COLUMN_NAME_ID)
    val actorId: Long
)
