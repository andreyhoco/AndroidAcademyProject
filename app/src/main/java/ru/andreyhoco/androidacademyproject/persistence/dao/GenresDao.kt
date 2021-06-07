package ru.andreyhoco.androidacademyproject.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.andreyhoco.androidacademyproject.persistence.entities.GenreEntity

@Dao
interface GenresDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenres(genres: List<GenreEntity>)

    @Query("SELECT * FROM Genres")
    suspend fun getAll(): List<GenreEntity>

    @Query("SELECT * FROM Genres WHERE genre_id IN (:genreIds)")
    suspend fun getGenresByIds(genreIds: List<Long>): List<GenreEntity>

    @Query("DELETE FROM Genres WHERE genre_id == :id")
    suspend fun deleteGenreById(id: Long)

    @Query("DELETE FROM Genres WHERE genre_id IN (:genresIds)")
    suspend fun deleteGenresByIds(genresIds: List<Long>)

    @Query("DELETE FROM Genres")
    suspend fun deleteAll()
}