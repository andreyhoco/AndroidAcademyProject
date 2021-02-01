package ru.andreyhoco.androidacademyproject.network.responses

import kotlinx.serialization.*

@Serializable
data class PopularMoviesResponse (
    @SerialName("page")
    val page: Long,

    @SerialName("results")
    val moviesResponseShort: List<MovieIdResponse>,
)

@Serializable
data class MovieIdResponse (
    @SerialName("id")
    val id: Long,
)