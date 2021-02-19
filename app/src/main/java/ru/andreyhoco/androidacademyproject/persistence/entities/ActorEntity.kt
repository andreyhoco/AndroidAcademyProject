package ru.andreyhoco.androidacademyproject.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.andreyhoco.androidacademyproject.persistence.TheMovieAppDbContract

@Entity(tableName = TheMovieAppDbContract.Actors.TABLE_NAME)
data class ActorEntity(
    @PrimaryKey
    @ColumnInfo(name = TheMovieAppDbContract.Actors.COLUMN_NAME_ID)
    val actorId: Long,

    @ColumnInfo(name = TheMovieAppDbContract.Actors.COLUMN_NAME_ACTOR_NAME)
    val name: String,

    @ColumnInfo(name = TheMovieAppDbContract.Actors.COLUMN_NAME_PICTURE)
    val picture: String
)
