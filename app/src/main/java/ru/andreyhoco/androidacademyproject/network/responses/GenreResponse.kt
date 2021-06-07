package ru.andreyhoco.androidacademyproject.network.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenreResponse (
    @SerialName("id")
    val genreId: Long,

    @SerialName("name")
    val name: String
)