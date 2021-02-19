package ru.andreyhoco.androidacademyproject.persistence.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.andreyhoco.androidacademyproject.persistence.entities.MovieWithActorsAndGenres
import ru.andreyhoco.androidacademyproject.persistence.entities.MovieEntity

@Dao
interface MoviesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: MovieEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Transaction
    @Query("SELECT * FROM Movies ORDER BY ratings DESC, title ASC")
    suspend fun getAllMoviesWithActorsAndGenres(): List<MovieWithActorsAndGenres>

    @Transaction
    @Query("SELECT * FROM Movies WHERE movie_id == :id")
    suspend fun getMovieWithActorsAndGenresById(id: Long): MovieWithActorsAndGenres

    @Query("DELETE FROM Movies WHERE movie_id IN (:movieIds)")
    suspend fun deleteMoviesByIds(movieIds: List<Long>)

    @Query("DELETE FROM Movies")
    suspend fun deleteAll()
}