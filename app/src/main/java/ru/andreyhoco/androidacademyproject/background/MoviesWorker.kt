package ru.andreyhoco.androidacademyproject.background

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import ru.andreyhoco.androidacademyproject.MovieNotifications
import ru.andreyhoco.androidacademyproject.repositories.MovieRepository
import ru.andreyhoco.androidacademyproject.repositories.RequestResult
import timber.log.Timber
import java.util.concurrent.CancellationException

class MoviesWorker(
    appContext: Context,
    params: WorkerParameters,
    private val repository: MovieRepository,
    private val movieNotifications: MovieNotifications
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        Timber.plant(Timber.DebugTree())
        try {
            val updateResult = repository.updateMovies()

            when (updateResult) {
                is RequestResult.Success -> {
                    val randomMovie = updateResult.value.random()

                    Timber.tag("MOVIES_DB_UPDATE_WORK").d("Update was successful, movie: ${randomMovie.title}")
                    movieNotifications.showMovieNotification(randomMovie)
                    return Result.success()
                }
                is RequestResult.Failure -> {
                    Timber.tag("MOVIES_DB_UPDATE_WORK").d("Update failed")
                    return Result.failure()
                }
            }
        } catch (e: CancellationException) {
            Timber.tag("WORK_BUG_FIX").d("Catch CancellationException")
        }

        if (isStopped) {
            Timber.tag("WORK_BUG_FIX").d("Is stopped")
            return Result.success()
        }

        return Result.success()
    }
}