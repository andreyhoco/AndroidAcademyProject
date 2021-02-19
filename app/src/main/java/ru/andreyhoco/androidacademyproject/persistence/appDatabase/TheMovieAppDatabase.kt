package ru.andreyhoco.androidacademyproject.persistence.appDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.andreyhoco.androidacademyproject.persistence.TheMovieAppDbContract
import ru.andreyhoco.androidacademyproject.persistence.dao.*
import ru.andreyhoco.androidacademyproject.persistence.entities.*

@Database(entities = [MovieEntity::class,
    ActorEntity::class,
    GenreEntity::class,
    MovieActorCrossRef::class,
    MovieGenreCrossRef::class],
    version = 1
    )
abstract class TheMovieAppDatabase : RoomDatabase() {

    abstract val moviesDao: MoviesDao
    abstract val actorsDao: ActorsDao
    abstract val genresDao: GenresDao
    abstract val movieActorCrossRefDao: MovieActorCrossRefDao
    abstract val movieGenreCrossRefDao: MovieGenreCrossRefDao

    companion object {
        fun create(applicationContext: Context): TheMovieAppDatabase {
            return Room.databaseBuilder(
                applicationContext,
                TheMovieAppDatabase::class.java,
                TheMovieAppDbContract.DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}