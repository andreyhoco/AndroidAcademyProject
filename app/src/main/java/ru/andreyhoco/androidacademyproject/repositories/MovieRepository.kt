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
import ru.andreyhoco.androidacademyproject.network.responses.MovieShortResponse
import ru.andreyhoco.androidacademyproject.persistence.entities.*
import ru.andreyhoco.androidacademyproject.ui.uiDataModel.MovieShortDesc
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

    fun getTopRatedMoviesWithShortDesc(): Flow<List<MovieShortDesc>> {
        return moviesDao.getAllMoviesWithActorsAndGenresFlow()
            .map { moviesWithActorsAndGenres ->
                moviesWithActorsAndGenres.map { movieWithActorsAndGenres ->
                    movieWithActorsAndGenres.toMovieShortDesc()
                }
            }.flowOn(Dispatchers.IO)
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

    suspend fun loadTopRatedMoviesShortDesc(pageNum: Int): RequestResult {
        return withContext(Dispatchers.IO) {
            try {
                val popularMoviesShortResponse: List<MovieShortResponse> = tmdbService
                    .getTopRatedMovies(pageNum)
                    .movieShortResponses

                popularMoviesShortResponse.forEach { movieShortResponse ->
                    insertShortResponse(movieShortResponse)
                }

                RequestResult.Success()
            } catch (e: Exception) {
                Timber.tag("CAUGHT_ERROR")
                    .w("${this@MovieRepository::class.java.name}: $e")

                handleNetworkException(e)
            }
        }
    }

     suspend fun loadMovieById(id: Long): RequestResult {
        return withContext(Dispatchers.IO) {
            try {
                val detailedMovieResponse = tmdbService.getMovieById(id)
                val actorsResponse = tmdbService.getActorsByMovieId(id).actors
                insertMovieWithActors(detailedMovieResponse, actorsResponse)

                RequestResult.Success()
            } catch (e: Exception) {
                handleNetworkException(e)
            }
        }
    }

    suspend fun loadActorsByMovieId(id: Long): RequestResult {
        return withContext(Dispatchers.IO) {
            try {
                val actorsByMovieIdResponse = tmdbService.getActorsByMovieId(id)
                val actors = actorsByMovieIdResponse.actors

                actorsDao.insertActors(
                    actors.map { actorResponse ->
                        actorResponse.toActorEntity()
                    }
                )

                RequestResult.Success()
            } catch (e: Exception) {
                handleNetworkException(e)
            }
        }
    }

    private suspend fun insertShortResponse(movieShortResponse: MovieShortResponse) {
        val movieEntity = movieShortResponse.toMovieEntity()

        val insertResult = moviesDao
            .insertMovieShortDesc(movieEntity)

        if (insertResult == -1L) {
            moviesDao.updateMovieByShortDesc(
                id = movieEntity.movieId,
                newNumOfRatings = movieEntity.numberOfRatings,
                newRating = movieEntity.ratings
            )
        }

        movieShortResponse.genreIds.forEach { genreId ->
            movieGenreCrossRefDao.insert(MovieGenreCrossRef(
                movieShortResponse.id,
                genreId
            ))
        }
    }

    private suspend fun insertMovieWithActors(
        movieResponse: DetailedMovieResponse,
        actorsResponse: List<ActorResponse>
    ) {
        moviesDao.insert(movieResponse.toMovieEntity())
        actorsDao.insertActors(
            actorsResponse.map { actorResponse ->
                actorResponse.toActorEntity()
            }
        )

        actorsResponse.map { actorResponse ->
            actorResponse.id
        }.forEach { actorId ->
            movieActorCrossRefDao.insert(MovieActorCrossRef(
                movieResponse.movieId,
                actorId
            ))
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

    private fun ActorResponse.toActorEntity(): ActorEntity {
        return ActorEntity(
            actorId = this.id,
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
            ratings = this.ratings,
            numberOfRatings = this.numberOfRatings,
            minimumAge = if (this.adult) 16 else 13,
            runtime = this.runtime,
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
            },
            releaseYear = releaseDate.substringBefore('-').toInt()
        )
    }

    private fun MovieShortResponse.toMovieEntity(): MovieEntity {
        return MovieEntity(
            movieId = this.id,
            title = this.title,
            poster = baseImageLoadUrl + posterSize + this.poster,
            ratings = this.ratings,
            numberOfRatings = this.numberOfRatings,
            isAdult = false,
            releaseYear = this.releaseDate.substringBefore('-').toInt()
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
            runtime = this.runtime,
            releaseYear = this.releaseYear
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

    private fun DetailedMovieResponse.toMovieEntity(): MovieEntity {
        return MovieEntity(
            movieId = this.movieId,
            title = this.title,
            overview = this.overview,
            poster = baseImageLoadUrl + posterSize + this.posterPath,
            backdrop = baseImageLoadUrl + backdropSize + this.backdropPath,
            ratings = this.ratings,
            numberOfRatings = this.numberOfRatings,
            isAdult = this.adult,
            runtime = this.runtime,
            releaseYear = this.releaseDate.substringBefore('-').toInt()
        )
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
            },
            releaseYear = this.movie.releaseYear
        )
    }

    private fun MovieWithActorsAndGenres.toMovieShortDesc(): MovieShortDesc {
        val movie = this.movie

        return MovieShortDesc(
            id = movie.movieId,
            title = movie.title,
            poster = movie.poster,
            ratings = movie.ratings,
            numberOfRatings = movie.numberOfRatings,
            genres = this.genres.map {
                Genre(
                    id = it.genreId,
                    name = it.name
                )
            },
            releaseYear = this.movie.releaseYear
        )
    }
}