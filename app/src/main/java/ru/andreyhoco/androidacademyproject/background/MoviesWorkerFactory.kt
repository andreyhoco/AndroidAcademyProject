package ru.andreyhoco.androidacademyproject.background

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import ru.andreyhoco.androidacademyproject.MovieNotifications
import ru.andreyhoco.androidacademyproject.repositories.MovieRepository

class MoviesWorkerFactory(
    private val repository: MovieRepository,
    private val moviesNotifications: MovieNotifications
    ) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {

        return when(workerClassName) {
            MoviesWorker::class.java.name -> {
                MoviesWorker(appContext, workerParameters, repository, moviesNotifications)
            }
            else -> {
                null
            }
        }

    }
}