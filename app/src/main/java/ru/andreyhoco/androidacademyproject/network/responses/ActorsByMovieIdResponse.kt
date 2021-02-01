package ru.andreyhoco.androidacademyproject.network.responses

import kotlinx.serialization.*

@Serializable
data class ActorsByMovieIdResponse (
    @SerialName("id")
    val movieId: Long,
    @SerialName("cast")
    val actors: List<ActorResponse>,
)

@Serializable
data class ActorResponse (
    @SerialName("id")
    val id: Long,

    @SerialName("original_name")
    val originalName: String,

    @SerialName("profile_path")
    val profilePath: String? = null
)