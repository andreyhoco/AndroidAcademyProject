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

    @SerialName("release_date")
    val releaseDate: String,

    @SerialName("title")
    val title: String,

    @SerialName("overview")
    val overview: String,

    @SerialName("poster_path")
    val posterPath: String,

    @SerialName("runtime")
    val runtime: Int,

    @SerialName("vote_count")
    val numberOfRatings: Int,

    @SerialName("vote_average")
    val ratings: Float
)
