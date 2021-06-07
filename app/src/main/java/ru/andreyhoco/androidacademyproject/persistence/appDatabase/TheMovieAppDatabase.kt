package ru.andreyhoco.androidacademyproject.persistence.appDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.andreyhoco.androidacademyproject.persistence.TheMovieAppDbContract
import ru.andreyhoco.androidacademyproject.persistence.dao.*
import ru.andreyhoco.androidacademyproject.persistence.entities.*
import ru.andreyhoco.androidacademyproject.persistence.initialMovies.getActorEntities
import ru.andreyhoco.androidacademyproject.persistence.initialMovies.getGenreEntities
import ru.andreyhoco.androidacademyproject.persistence.initialMovies.getMovieEntity
import ru.andreyhoco.androidacademyproject.persistence.initialMovies.AssetsRepository

@Database(entities = [MovieEntity::class,
    ActorEntity::class,
    GenreEntity::class,
    MovieActorCrossRef::class,
    MovieGenreCrossRef::class],
    version = 2
    )
abstract class TheMovieAppDatabase : RoomDatabase() {

    abstract val moviesDao: MoviesDao
    abstract val actorsDao: ActorsDao
    abstract val genresDao: GenresDao
    abstract val movieActorCrossRefDao: MovieActorCrossRefDao
    abstract val movieGenreCrossRefDao: MovieGenreCrossRefDao

    companion object {
        private var DATABASE: TheMovieAppDatabase? = null

        fun create(
            applicationContext: Context,
            coroutineScope: CoroutineScope
        ): TheMovieAppDatabase {
            val databaseInstance =  Room.databaseBuilder(
                applicationContext,
                TheMovieAppDatabase::class.java,
                TheMovieAppDbContract.DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .addCallback(MovieAssetsDatabaseCallback(applicationContext, coroutineScope))
                .addMigrations(MigrationFrom1To2())
                .build()

            DATABASE = databaseInstance
            return  databaseInstance
        }

        private class MovieAssetsDatabaseCallback(
            private val context: Context,
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback(){
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                scope.launch {
                    populateFromAssets(
                        context,
                        DATABASE?.moviesDao,
                        DATABASE?.actorsDao,
                        DATABASE?.genresDao,
                        DATABASE?.movieActorCrossRefDao,
                        DATABASE?.movieGenreCrossRefDao
                    )
                }
            }
        }

        private class MigrationFrom1To2 : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Movies ADD COLUMN " +
                        "${TheMovieAppDbContract.Movies.COLUMN_NAME_RELEASE_DATE} INTEGER " +
                        "NOT NULL DEFAULT 0")
            }
        }

        suspend fun populateFromAssets(
            context: Context,
            moviesDao: MoviesDao?,
            actorsDao: ActorsDao?,
            genresDao: GenresDao?,
            movieActorCrossRefDao: MovieActorCrossRefDao?,
            movieGenreCrossRefDao: MovieGenreCrossRefDao?
        ) = withContext(Dispatchers.IO) {
            val assetsRepository = AssetsRepository(context)

            val movies = assetsRepository.loadInitialMovies()

            movies.forEach { movie ->
                moviesDao?.insert(movie.getMovieEntity())
                actorsDao?.insertActors(movie.getActorEntities())
                genresDao?.insertGenres(movie.getGenreEntities())

                movie.actors.forEach { actor ->
                    movieActorCrossRefDao?.insert(MovieActorCrossRef(movie.id, actor.id))
                }

                movie.genres.forEach { genre ->
                    movieGenreCrossRefDao?.insert(MovieGenreCrossRef(movie.id, genre.id))
                }
            }
        }
    }
}