package ru.andreyhoco.androidacademyproject.repositories

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import ru.andreyhoco.androidacademyproject.BuildConfig
import ru.andreyhoco.androidacademyproject.persistence.appDatabase.TheMovieAppDatabase
import ru.andreyhoco.androidacademyproject.ui.uiDataModel.Actor
import ru.andreyhoco.androidacademyproject.ui.uiDataModel.Genre
import ru.andreyhoco.androidacademyproject.ui.uiDataModel.Movie
import ru.andreyhoco.androidacademyproject.network.TmdbApiService
import ru.andreyhoco.androidacademyproject.network.responses.ActorResponse
import ru.andreyhoco.androidacademyproject.network.responses.DetailedMovieResponse
import ru.andreyhoco.androidacademyproject.network.responses.MovieIdResponse
import ru.andreyhoco.androidacademyproject.persistence.entities.*
import timber.log.Timber

class MovieRepository(
    private val tmdbService: TmdbApiService,
    appDatabase: TheMovieAppDatabase
) {
    private val baseImageLoadUrl: String = BuildConfig.IMAGE_BASE_URL
    private val posterSize: String = "w154"
    private val backdropSize: String = "w780"
    private val profileSize: String = "w185"

    private val moviesDao = appDatabase.moviesDao
    private val actorsDao = appDatabase.actorsDao
    private val genresDao = appDatabase.genresDao
    private val movieActorCrossRefDao = appDatabase.movieActorCrossRefDao
    private val movieGenreCrossRefDao = appDatabase.movieGenreCrossRefDao

    suspend fun loadTopRatedMovies(pageNum: Int): RequestResult {
        return withContext(Dispatchers.IO) {
            try {
                val popularMoviesIds: List<MovieIdResponse> = tmdbService
                    .getTopRatedMovies(pageNum)
                    .moviesResponseShort

                val remoteMovies: List<Movie> = popularMoviesIds.map {
                    val movieResponse = tmdbService.getMovieById(it.id)
                    val actorsResponse = tmdbService.getActorsByMovieId(it.id).actors
                    movieResponse.toMovie(actorsResponse)
                }

                remoteMovies.forEach { movie ->
                    insertMovieInDb(movie)
                }

                RequestResult.Success()
            } catch (e: Exception) {
                handleNetworkException(e)
            }
        }
    }

     fun getTopRatedMovies(): Flow<List<Movie>> {
         return moviesDao.getAllMoviesWithActorsAndGenresFlow()
             .map { moviesWithActorsAndGenres ->
                 moviesWithActorsAndGenres.map { movieWithActorsAndGenres ->
                     movieWithActorsAndGenres.toMovie()
                 }
             }.flowOn(Dispatchers.IO)
    }

    fun getMovieById(id: Long): Flow<Movie> {
        return moviesDao.getMovieWithActorsAndGenresFlowByMovieId(id)
            .map { movieWithActorsAndGenres ->
                movieWithActorsAndGenres.toMovie()
            }.flowOn(Dispatchers.IO)
    }

     suspend fun loadMovieById(id: Long): RequestResult {
        return withContext(Dispatchers.IO) {
            try {
                val detailedMovieResponse = tmdbService.getMovieById(id)
                val actorsResponse = tmdbService.getActorsByMovieId(id).actors

                insertMovieInDb(detailedMovieResponse.toMovie(actorsResponse))
                RequestResult.Success()
            } catch (e: Exception) {
                handleNetworkException(e)
            }
        }
    }

    suspend fun loadActorsByMovieId(id: Long): RequestResult {
        return withContext(Dispatchers.IO) {
            try {
                val actorResponse = tmdbService.getActorsByMovieId(id)
                val actors = actorResponse.actors.map { it.toActor() }

                //insertActors

                RequestResult.Success()
            } catch (e: Exception) {
                handleNetworkException(e)
            }
        }
    }

    suspend fun getRandomTopRatedMovie(): Movie {
        return moviesDao.getAllMoviesWithActorsAndGenres().random().toMovie()
    }

    private fun handleNetworkException(e: Exception): RequestResult.Failure {
        return if (e is HttpException) {
            RequestResult.Failure.HttpError(e)
        } else {
            RequestResult.Failure.Error(e)
        }
    }

    private suspend fun insertMovieInDb(movie: Movie) {
        moviesDao.insert(movie.getMovieEntity())
        actorsDao.insertActors(movie.getActorEntities())
        genresDao.insertGenres(movie.getGenreEntities())

        movie.actors.forEach { actor ->
            movieActorCrossRefDao.insert(MovieActorCrossRef(movie.id, actor.id))
        }

        movie.genres.forEach { genre ->
            movieGenreCrossRefDao.insert(MovieGenreCrossRef(movie.id, genre.id))
        }
    }

    private fun ActorResponse.toActor(): Actor {
        return Actor(
            id = this.id,
            name = this.originalName,
            picture = if (this.profilePath == null) {
                ""
            } else {
                baseImageLoadUrl + profileSize + this.profilePath
            }
        )
    }

    private fun DetailedMovieResponse.toMovie(actorsResponse: List<ActorResponse>): Movie {
        return Movie(
            id = this.movieId,
            title = this.title,
            overview = this.overview,
            poster = baseImageLoadUrl + posterSize + this.posterPath,
            backdrop = baseImageLoadUrl + backdropSize + this.backdropPath,
            ratings = this.voteAverage.toFloat(),
            numberOfRatings = this.voteCount.toInt(),
            minimumAge = if (this.adult) 16 else 13,
            runtime = this.runtime.toInt(),
            genres = this.genres.map {
                Genre(
                    id = it.genreId,
                    name = it.name
                )
            },
            actors = if (actorsResponse.isNotEmpty()) {
                actorsResponse.map { actorResponse ->
                    actorResponse.toActor()
                }
            } else {
                emptyList()
            }
        )
    }

    private fun Movie.getMovieEntity(): MovieEntity {
        return MovieEntity(
            movieId = this.id,
            title = this.title,
            overview = this.overview,
            poster = this.poster,
            backdrop = this.backdrop,
            ratings = this.ratings,
            numberOfRatings = this.numberOfRatings,
            isAdult = (this.minimumAge > 16),
            runtime = this.runtime
        )
    }

    private fun Movie.getGenreEntities(): List<GenreEntity> {
        return this.genres.map {
            GenreEntity(
                genreId = it.id,
                name = it.name
            )
        }
    }

    private fun Movie.getActorEntities(): List<ActorEntity> {
        return this.actors.map {
            ActorEntity(
                actorId = it.id,
                name = it.name,
                picture = it.picture
            )
        }
    }

    private fun MovieWithActorsAndGenres.toMovie(): Movie {
        val movie = this.movie

        return Movie(
            id = movie.movieId,
            title = movie.title,
            overview = movie.overview,
            poster = movie.poster,
            backdrop = movie.backdrop,
            ratings = movie.ratings,
            numberOfRatings = movie.numberOfRatings,
            minimumAge = if (movie.isAdult) 16 else 13,
            runtime = movie.runtime,
            genres = this.genres.map {
                Genre(
                    id = it.genreId,
                    name = it.name
                )
            },
            actors = this.actors.map {
                Actor(
                    id = it.actorId,
                    name = it.name,
                    picture = it.picture
                )
            }
        )
    }
}