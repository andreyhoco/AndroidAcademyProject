package ru.andreyhoco.androidacademyproject.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.andreyhoco.androidacademyproject.persistence.TheMovieAppDbContract

@Entity(tableName = TheMovieAppDbContract.Genres.TABLE_NAME)
data class GenreEntity(
    @PrimaryKey
    @ColumnInfo(name = TheMovieAppDbContract.Genres.COLUMN_NAME_ID)
    val genreId: Long,

    @ColumnInfo(name = TheMovieAppDbContract.Genres.COLUMN_NAME_GENRE_NAME)
    val name: String
)
