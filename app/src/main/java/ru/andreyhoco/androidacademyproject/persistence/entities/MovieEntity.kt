package ru.andreyhoco.androidacademyproject.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.andreyhoco.androidacademyproject.persistence.TheMovieAppDbContract

@Entity(tableName = TheMovieAppDbContract.Movies.TABLE_NAME)
data class MovieEntity(
    @PrimaryKey
    @ColumnInfo(name = TheMovieAppDbContract.Movies.COLUMN_NAME_ID)
    val movieId: Long,

    @ColumnInfo(name = TheMovieAppDbContract.Movies.COLUMN_NAME_TITLE)
    val title: String,

    @ColumnInfo(name = TheMovieAppDbContract.Movies.COLUMN_NAME_OVERVIEW)
    val overview: String = "",

    @ColumnInfo(name = TheMovieAppDbContract.Movies.COLUMN_NAME_POSTER)
    val poster: String,

    @ColumnInfo(name = TheMovieAppDbContract.Movies.COLUMN_NAME_BACKDROP)
    val backdrop: String = "",

    @ColumnInfo(name = TheMovieAppDbContract.Movies.COLUMN_NAME_RATINGS)
    val ratings: Float,

    @ColumnInfo(name = TheMovieAppDbContract.Movies.COLUMN_NAME_NUM_OF_RATINGS)
    val numberOfRatings: Int,

    @ColumnInfo(name = TheMovieAppDbContract.Movies.COLUMN_NAME_IS_ADULT)
    val isAdult: Boolean = false,

    @ColumnInfo(name = TheMovieAppDbContract.Movies.COLUMN_NAME_RUNTIME)
    val runtime: Int = 0,

    @ColumnInfo(name = TheMovieAppDbContract.Movies.COLUMN_NAME_RELEASE_DATE)
    val releaseYear: Int
)
