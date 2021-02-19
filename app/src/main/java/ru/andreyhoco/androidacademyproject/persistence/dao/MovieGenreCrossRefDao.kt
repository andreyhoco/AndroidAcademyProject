package ru.andreyhoco.androidacademyproject.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.andreyhoco.androidacademyproject.persistence.entities.MovieGenreCrossRef

@Dao
interface MovieGenreCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(crossRef: MovieGenreCrossRef)

    @Query(
        "DELETE FROM MovieGenreCrossRef WHERE movie_id == :movieId AND genre_id == :genreId"
    )
    suspend fun deleteByIds(movieId: Long, genreId: Long)
}