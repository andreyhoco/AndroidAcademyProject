package ru.andreyhoco.androidacademyproject.persistence.initialMovies

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ru.andreyhoco.androidacademyproject.persistence.entities.ActorEntity
import ru.andreyhoco.androidacademyproject.persistence.entities.GenreEntity
import ru.andreyhoco.androidacademyproject.persistence.entities.MovieEntity
import ru.andreyhoco.androidacademyproject.ui.uiDataModel.Actor
import ru.andreyhoco.androidacademyproject.ui.uiDataModel.Genre
import ru.andreyhoco.androidacademyproject.ui.uiDataModel.Movie

internal class AssetsRepository(private val context: Context) {
    private val jsonFormat = Json { ignoreUnknownKeys = true }

    suspend fun loadInitialMovies() = withContext(Dispatchers.IO) {
        val actors = loadActorsFromAssets()
        val genres = loadGenresFromAssets()
        loadMoviesFromAssets(genres, actors)
    }

    private suspend fun loadGenresFromAssets(): List<Genre> = withContext(Dispatchers.IO) {
        val data = readAssetFileToString("genres.json")
        val jsonGenres = jsonFormat.decodeFromString<List<JsonGenre>>(data)
        jsonGenres.map { jsonGenre ->
            Genre(
                id = jsonGenre.id,
                name = jsonGenre.name
            )
        }
    }

    private fun readAssetFileToString(fileName: String): String {
        val stream = context.assets.open(fileName)
        return stream.bufferedReader().readText()
    }

    private suspend fun loadActorsFromAssets(): List<Actor> = withContext(Dispatchers.IO) {
        val data = readAssetFileToString("people.json")
        val jsonActors = jsonFormat.decodeFromString<List<JsonActor>>(data)

        jsonActors.map { jsonActor ->
            Actor(
                id = jsonActor.id,
                name = jsonActor.name,
                picture = jsonActor.profilePicture
            )
        }
    }

    private suspend fun loadMoviesFromAssets(
        genres: List<Genre>,
        actors: List<Actor>
    ): List<Movie> = withContext(Dispatchers.IO) {
        val data = readAssetFileToString("data.json")
        val jsonMovies = jsonFormat.decodeFromString<List<JsonMovie>>(data)

        jsonMovies.map { jsonMovie ->
            Movie(
                id = jsonMovie.id.toLong(),
                title = jsonMovie.title,
                overview = jsonMovie.overview,
                poster = jsonMovie.posterPicture,
                backdrop = jsonMovie.backdropPicture,
                ratings = (jsonMovie.ratings / 2),
                numberOfRatings = jsonMovie.votesCount,
                minimumAge = if(jsonMovie.adult) 21 else 13,
                runtime = jsonMovie.runtime,
                genres = jsonMovie.genreIds.map { id ->
                    genres.filter { it.id == id }
                }.flatten(),
                actors = jsonMovie.actors.map { id ->
                    actors.filter { it.id == id }
                }.flatten(),
                releaseYear = jsonMovie
                    .releaseDate
                    .substringBefore('-')
                    .toInt()
            )
        }
    }

    private fun <T : Any> T?.orThrow(createThrowable: () -> Throwable): T {
        return this ?: throw createThrowable()
    }

}
