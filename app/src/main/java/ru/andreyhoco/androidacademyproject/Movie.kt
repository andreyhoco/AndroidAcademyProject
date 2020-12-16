package ru.andreyhoco.androidacademyproject

data class Movie(
    val posterImageId: Int,
    val microPosterImageId: Int,
    val title: String,
    val pg: Int,
    val genre: List<String>,
    var rating: Double,
    var numberOfReviews: Int,
    var isFavorite: Boolean,
    val durationInMinutes: Int,
    val storyline: String,
    val cast: List<Actor>
)

fun getMovies() = listOf(
    Movie(
        R.drawable.avengers_poster,
        R.drawable.avengers_list_poster,
        "Avengers: End Game",
        13,
        listOf("Action", "Adventure", "Fantasy"),
        4.35,
        125,
        false,
        126,
        "After the devastating events of Avengers:" +
        "Infinity War, the universe is in ruins. With the help of remaining allies," +
        "the Avengers assemble once more in order to reverse Thanos'" +
        "actions and restore balance to the universe.",
        getActors()
    ),
    Movie(
        R.drawable.avengers_poster,
        R.drawable.avengers_list_poster,
        "Avengers: End Game",
        21,
        listOf("Action", "Adventure", "Fantasy"),
        3.75,
        111,
        true,
        117,
        "After the devastating events of Avengers:" +
                "Infinity War, the universe is in ruins. With the help of remaining allies," +
                "the Avengers assemble once more in order to reverse Thanos'" +
                "actions and restore balance to the universe.",
        getActors()
    ),
    Movie(
        R.drawable.avengers_poster,
        R.drawable.avengers_list_poster,
        "Avengers: End Game",
        18,
        listOf("Action", "Adventure", "Fantasy"),
        4.95,
        331,
        false,
        129,
        "After the devastating events of Avengers:" +
                "Infinity War, the universe is in ruins. With the help of remaining allies," +
                "the Avengers assemble once more in order to reverse Thanos'" +
                "actions and restore balance to the universe.",
        getActors()
    ),Movie(
        R.drawable.avengers_poster,
        R.drawable.avengers_list_poster,
        "Avengers: End Game",
        18,
        listOf("Action", "Adventure", "Fantasy"),
        4.95,
        331,
        false,
        129,
        "After the devastating events of Avengers:" +
                "Infinity War, the universe is in ruins. With the help of remaining allies," +
                "the Avengers assemble once more in order to reverse Thanos'" +
                "actions and restore balance to the universe.",
        getActors()
    ),Movie(
        R.drawable.avengers_poster,
        R.drawable.avengers_list_poster,
        "Avengers: End Game",
        18,
        listOf("Action", "Adventure", "Fantasy"),
        4.95,
        331,
        false,
        129,
        "After the devastating events of Avengers:" +
                "Infinity War, the universe is in ruins. With the help of remaining allies," +
                "the Avengers assemble once more in order to reverse Thanos'" +
                "actions and restore balance to the universe.",
        getActors()
    )
)