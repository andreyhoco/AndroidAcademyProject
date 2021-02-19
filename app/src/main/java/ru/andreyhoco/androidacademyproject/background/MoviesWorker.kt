package ru.andreyhoco.androidacademyproject.background

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ru.andreyhoco.androidacademyproject.AppDi
import ru.andreyhoco.androidacademyproject.repositories.MovieRepository
import timber.log.Timber

class MoviesWorker(
    appContext: Context,
    params: WorkerParameters,
    private val repository: MovieRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return if (repository.updateMoviesDb()) {
            Timber.tag("MOVIES_DB_UPDATE_WORK").d("Update was successful")
            Result.success()
        } else {
            Timber.tag("MOVIES_DB_UPDATE_WORK").d("Update failed")
            Result.failure()
        }
    }
}