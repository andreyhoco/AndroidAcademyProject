package ru.andreyhoco.androidacademyproject.repositories

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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
import ru.andreyhoco.androidacademyproject.persistence.dao.MoviesDao
import ru.andreyhoco.androidacademyproject.persistence.entities.*
import timber.log.Timber

class MovieRepository(
    private val tmdbService: TmdbApiService,
    appDatabase: TheMovieAppDatabase
) {

    private val baseImageLoadUrl: String = BuildConfig.IMAGE_BASE_URL
    private val posterSize: String = "w185"
    private val backdropSize: String = "w780"
    private val profileSize: String = "w185"

    private val moviesDao = appDatabase.moviesDao
    private val actorsDao = appDatabase.actorsDao
    private val genresDao = appDatabase.genresDao
    private val movieActorCrossRefDao = appDatabase.movieActorCrossRefDao
    private val movieGenreCrossRefDao = appDatabase.movieGenreCrossRefDao

    private suspend fun loadTopRatedMovies(pageNum: Int): RequestResult<List<Movie>> {
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

                RequestResult.Success(remoteMovies)
            } catch (e: Exception) {
                handleNetworkException(e)
            }

        }
    }

    suspend fun getTopRatedMovies(pageNum: Int): Flow<RequestResult<List<Movie>>> {
        return flow {
            val localMovies = moviesDao.getAllMoviesWithActorsAndGenres().map {
                it.toMovie()
            }
            emit(RequestResult.Success(localMovies))

            val moviesRequestResult = loadTopRatedMovies(pageNum)
            if ((moviesRequestResult is RequestResult.Success)) {

                val remoteMovies = moviesRequestResult.value

                if (remoteMovies.toSet() != localMovies.toSet()) {
                    remoteMovies.forEach {
                        insertMovieInDb(it)
                    }

                    val updatedMoviesList = moviesDao.getAllMoviesWithActorsAndGenres().map {
                        it.toMovie()
                    }
                    emit(RequestResult.Success(updatedMoviesList))
                }
            } else {
                emit(moviesRequestResult)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getMovieById(id: Long): Flow<RequestResult<Movie>> {
        return flow {
            val localMovie = moviesDao.getMovieWithActorsAndGenresById(id).toMovie()
            emit(RequestResult.Success(localMovie))

            val movieRequestResult = loadMovieById(id)
            if (movieRequestResult is RequestResult.Success) {

                val remoteMovie = movieRequestResult.value
                if (remoteMovie != localMovie) {

                    insertMovieInDb(remoteMovie)
                    val updatedMovie = moviesDao.getMovieWithActorsAndGenresById(id).toMovie()
                    emit(RequestResult.Success(updatedMovie))
                }
            } else {
                emit(movieRequestResult)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun updateMoviesDb(): Boolean {
        return withContext(Dispatchers.IO) {
            val moviesRequestResult = loadTopRatedMovies(1)

            if (moviesRequestResult is RequestResult.Success) {
                moviesDao.deleteAll()

                moviesRequestResult.value.forEach { movie ->
                    insertMovieInDb(movie)
                }

                true
            } else {
                false
            }
        }
    }

    private suspend fun loadMovieById(id: Long): RequestResult<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                val detailedMovieResponse = tmdbService.getMovieById(id)
                val actorsResponse = tmdbService.getActorsByMovieId(id).actors

                RequestResult.Success(detailedMovieResponse.toMovie(actorsResponse))
            } catch (e: Exception) {
                handleNetworkException(e)
            }
        }
    }

    suspend fun loadActorsByMovieId(id: Long): RequestResult<List<Actor>> {
        return withContext(Dispatchers.IO) {
            try {
                val actorResponse = tmdbService.getActorsByMovieId(id)
                val actors = actorResponse.actors.map { it.toActor() }

                RequestResult.Success(actors)
            } catch (e: Exception) {
                handleNetworkException(e)
            }
        }
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

        movie.actors.forEach{ actor ->
            movieActorCrossRefDao.insert(MovieActorCrossRef(movie.id, actor.id))
        }

        movie.genres.forEach{ genre ->
            movieGenreCrossRefDao.insert(MovieGenreCrossRef(movie.id, genre.id))
        }
    }

    private fun ActorResponse.toActor(): Actor {
        return Actor(
            id = this.id,
            name = this.originalName,
            picture = baseImageLoadUrl + profileSize + this.profilePath
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