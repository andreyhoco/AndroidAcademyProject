package ru.andreyhoco.androidacademyproject.background

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ru.andreyhoco.androidacademyproject.MovieNotifications
import ru.andreyhoco.androidacademyproject.repositories.MovieRepository
import ru.andreyhoco.androidacademyproject.repositories.RequestResult
import timber.log.Timber

class MoviesWorker(
    appContext: Context,
    params: WorkerParameters,
    private val repository: MovieRepository,
    private val movieNotifications: MovieNotifications
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val updateResult = repository.updateMovies()

        return when (updateResult) {
            is RequestResult.Success -> {
                val randomMovie = updateResult.value.random()

                Timber.tag("MOVIES_DB_UPDATE_WORK").d("Update was successful, movie: ${randomMovie.title}")
                movieNotifications.showMovieNotification(randomMovie)
                Result.success()
            }
            is RequestResult.Failure -> {
                Timber.tag("MOVIES_DB_UPDATE_WORK").d("Update failed")
                Result.failure()
            }
        }
    }
}