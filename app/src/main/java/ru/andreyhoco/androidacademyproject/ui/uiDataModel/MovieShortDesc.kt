package ru.andreyhoco.androidacademyproject.ui.uiDataModel

data class MovieShortDesc(
    val id: Long,
    val poster: String,
    val releaseYear: Int,
    val genres: List<Genre>,
    val title: String,
    val numberOfRatings: Int,
    val ratings: Float
)
