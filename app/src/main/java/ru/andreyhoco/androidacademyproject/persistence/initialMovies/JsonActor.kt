package ru.andreyhoco.androidacademyproject.persistence.initialMovies

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class JsonActor(
    val id: Long,
    val name: String,
    @SerialName("profile_path")
    val profilePicture: String
)