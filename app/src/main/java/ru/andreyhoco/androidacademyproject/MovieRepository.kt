package ru.andreyhoco.androidacademyproject

import kotlinx.coroutines.*
import retrofit2.HttpException
import ru.andreyhoco.androidacademyproject.data.Actor
import ru.andreyhoco.androidacademyproject.data.Genre
import ru.andreyhoco.androidacademyproject.data.Movie
import ru.andreyhoco.androidacademyproject.network.RequestResult
import ru.andreyhoco.androidacademyproject.network.RetrofitModule
import ru.andreyhoco.androidacademyproject.network.responses.ActorResponse
import ru.andreyhoco.androidacademyproject.network.responses.DetailedMovieResponse
import ru.andreyhoco.androidacademyproject.network.responses.MovieIdResponse
import timber.log.Timber

class MovieRepository {
    private val tmdbService = RetrofitModule.tmdbApiService
    private val baseImageLoadUrl: String = BuildConfig.IMAGE_BASE_URL
    private val posterSize: String = "w185"
    private val backdropSize: String = "w780"
    private val profileSize: String = "w185"

    suspend fun getPopularMovies(pageNum: Int): RequestResult<List<Movie>> {
        return withContext(Dispatchers.IO) {
            try {
                val popularMoviesIds: List<MovieIdResponse> = tmdbService
                    .getPopularMovies(pageNum)
                    .moviesResponseShort

                val detailedMoviesResponse: List<DetailedMovieResponse> = popularMoviesIds.map {
                    tmdbService.getMovieById(it.id)
                }

                val movies = detailedMoviesResponse.map { it.toMovie() }
                RequestResult.Success(movies)
            } catch (e: Exception) {
                handleNetworkException(e)
            }
        }
    }

    suspend fun getMovieById(id: Int): RequestResult<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                val detailedMovieResponse = tmdbService.getMovieById(id.toLong())

                RequestResult.Success(detailedMovieResponse.toMovie())
            } catch (e: Exception) {
                handleNetworkException(e)
            }
        }
    }

    suspend fun getActorsByMovieId(id: Int): RequestResult<List<Actor>> {
        return withContext(Dispatchers.IO) {
            try {
                val actorResponse = tmdbService.getActorsByMovieId(id.toLong())
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

    private fun ActorResponse.toActor(): Actor {
        return Actor(
            id = this.id.toInt(),
            name = this.originalName,
            picture = baseImageLoadUrl + profileSize + this.profilePath
        )
    }

    private fun DetailedMovieResponse.toMovie(): Movie {
        return Movie(
            id = this.movieId.toInt(),
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
                    id = it.genreId.toInt(),
                    name = it.name
                )
            },
            actors = emptyList()
        )
    }
}