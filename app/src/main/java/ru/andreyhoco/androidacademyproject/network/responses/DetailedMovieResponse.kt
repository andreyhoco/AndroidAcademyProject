package ru.andreyhoco.androidacademyproject.network.responses

import kotlinx.serialization.*

@Serializable
data class DetailedMovieResponse (
    @SerialName("adult")
    val adult: Boolean,

    @SerialName("backdrop_path")
    val backdropPath: String,

    @SerialName("genres")
    val genres: List<GenreResponse>,

    @SerialName("id")
    val movieId: Long,

    @SerialName("title")
    val title: String,

    @SerialName("overview")
    val overview: String,

    @SerialName("poster_path")
    val posterPath: String,

    @SerialName("runtime")
    val runtime: Long,

    @SerialName("vote_average")
    val voteAverage: Double,

    @SerialName("vote_count")
    val voteCount: Long
)

@Serializable
data class GenreResponse (
    @SerialName("id")
    val genreId: Long,

    @SerialName("name")
    val name: String
)
