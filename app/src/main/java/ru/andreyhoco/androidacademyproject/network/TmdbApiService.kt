package ru.andreyhoco.androidacademyproject.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.andreyhoco.androidacademyproject.network.responses.ActorsByMovieIdResponse
import ru.andreyhoco.androidacademyproject.network.responses.ConfigurationResponse
import ru.andreyhoco.androidacademyproject.network.responses.DetailedMovieResponse
import ru.andreyhoco.androidacademyproject.network.responses.TopRatedMoviesResponse

interface TmdbApiService {
    @GET("configuration")
    suspend fun getConfiguration(): ConfigurationResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(@Query("page") pageNum: Int): TopRatedMoviesResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieById(@Path("movie_id") movieId: Long): DetailedMovieResponse

    @GET("movie/{movie_id}/credits")
    suspend fun getActorsByMovieId(@Path("movie_id") personId: Long): ActorsByMovieIdResponse
}