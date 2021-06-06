package ru.andreyhoco.androidacademyproject.network.responses

import kotlinx.serialization.*

@Serializable
data class TopRatedMoviesResponse (
    @SerialName("page")
    val page: Long,

    @SerialName("results")
    val moviesResponseShort: List<MovieIdResponse>,
)

@Serializable
data class MovieIdResponse (
    @SerialName("id")
    val id: Long,

    @SerialName("poster_path")
    val poster: String,

    @SerialName("release_date")
    val releaseDate: String,

    @SerialName("genre_ids")
    val genreIds: List<Long>,

    @SerialName("title")
    val title: String,

    @SerialName("vote_count")
    val numberOfRatings: Int,

    @SerialName("vote_average")
    val ratings: Float
)