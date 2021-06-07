package ru.andreyhoco.androidacademyproject.network.responses

import kotlinx.serialization.*

@Serializable
data class TopRatedMoviesResponse (
    @SerialName("page")
    val page: Long,

    @SerialName("results")
    val movieShortResponses: List<MovieShortResponse>,
)

