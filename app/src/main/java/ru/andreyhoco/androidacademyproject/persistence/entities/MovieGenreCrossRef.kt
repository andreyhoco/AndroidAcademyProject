package ru.andreyhoco.androidacademyproject.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import ru.andreyhoco.androidacademyproject.persistence.TheMovieAppDbContract

@Entity(primaryKeys = [
    TheMovieAppDbContract.Movies.COLUMN_NAME_ID,
    TheMovieAppDbContract.Genres.COLUMN_NAME_ID
])
data class MovieGenreCrossRef(
    @ColumnInfo(name = TheMovieAppDbContract.Movies.COLUMN_NAME_ID)
    val movieId: Long,

    @ColumnInfo(name = TheMovieAppDbContract.Genres.COLUMN_NAME_ID)
    val genreId: Long
)
