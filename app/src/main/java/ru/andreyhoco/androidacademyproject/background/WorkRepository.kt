package ru.andreyhoco.androidacademyproject.background

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import java.util.concurrent.TimeUnit

class WorkRepository {
    private val updateConstraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.METERED)
        .setRequiresCharging(true)
        .build()
//        .setRequiredNetworkType(NetworkType.UNMETERED)

    val periodicMoviesUpdateRequest = PeriodicWorkRequest.Builder(
        MoviesWorker::class.java,
        17,
        TimeUnit.MINUTES
    )
        .setConstraints(updateConstraints)
        .addTag(UNIQUE_UPDATE_TAG)
        .build()

    companion object {
        const val UNIQUE_UPDATE_TAG = "update_tag"
        const val PERIODIC_MOVIES_UPDATE = "movies_update"
    }
}