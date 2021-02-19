package ru.andreyhoco.androidacademyproject.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.andreyhoco.androidacademyproject.persistence.entities.ActorEntity

@Dao
interface ActorsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActors(actors: List<ActorEntity>)

    @Query("SELECT * FROM Actors")
    suspend fun getAll(): List<ActorEntity>

    @Query("DELETE FROM Actors WHERE actor_id == :id")
    suspend fun deleteActorById(id: Long)

    @Query("DELETE FROM Actors WHERE actor_id IN (:actorsIds)")
    suspend fun deleteActorsByIds(actorsIds: List<Long>)

    @Query("DELETE FROM Actors")
    suspend fun deleteAll()
}