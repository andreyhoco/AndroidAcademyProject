package ru.andreyhoco.androidacademyproject.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.andreyhoco.androidacademyproject.persistence.entities.MovieActorCrossRef

@Dao
interface MovieActorCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(crossRef: MovieActorCrossRef)

    @Query(
        "DELETE FROM MovieActorCrossRef WHERE " +
        "movie_id == :movieId AND actor_id == :actorId"
    )
    suspend fun deleteByIds(movieId: Long, actorId: Long)
}