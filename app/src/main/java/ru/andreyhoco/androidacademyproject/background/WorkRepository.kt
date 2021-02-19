package ru.andreyhoco.androidacademyproject.background

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import java.util.concurrent.TimeUnit

class WorkRepository {
    private val updateConstraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.UNMETERED)
        .setRequiresCharging(true)
        .build()

    val periodicMoviesUpdateRequest = PeriodicWorkRequest.Builder(
        MoviesWorker::class.java,
        8,
        TimeUnit.HOURS
    )
        .setConstraints(updateConstraints)
        .build()
}