package ru.andreyhoco.androidacademyproject

data class Actor(
    val firstName: String,
    val lastName: String,
    var imageId: Int
)

fun getActors(): List<Actor> = listOf(
    Actor("Robert", "Dawney Jr.", R.drawable.dawney_jr),
    Actor("Chris", "Hemsworth", R.drawable.hemsworth),
    Actor("Chris", "Evans", R.drawable.evans),
    Actor("Mark", "Ruffalo", R.drawable.ruffalo),
    Actor("Scarlett", "Johansson", R.drawable.johansson),
)