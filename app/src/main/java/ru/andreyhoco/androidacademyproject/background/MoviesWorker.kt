package ru.andreyhoco.androidacademyproject.background

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ru.andreyhoco.androidacademyproject.MovieNotifications
import ru.andreyhoco.androidacademyproject.repositories.MovieRepository
import ru.andreyhoco.androidacademyproject.repositories.RequestResult
import ru.andreyhoco.androidacademyproject.ui.uiDataModel.Movie
import timber.log.Timber
import java.util.concurrent.CancellationException

class MoviesWorker(
    appContext: Context,
    params: WorkerParameters,
    private val repository: MovieRepository,
    private val movieNotifications: MovieNotifications
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        try {
            val updateResult = repository.loadTopRatedMoviesShortDesc(1)

            when (updateResult) {
                is RequestResult.Success -> {
                    movieNotifications.showMovieNotification(repository.getRandomTopRatedMovie())
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