package ru.andreyhoco

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import ru.andreyhoco.androidacademyproject.AppDi
import ru.andreyhoco.androidacademyproject.background.MoviesWorkerFactory

class TheMovieApp : Application(), Configuration.Provider {
    val appDi: AppDi by lazy { AppDi(applicationContext) }

    override fun getWorkManagerConfiguration(): Configuration {
        val workerFactory = DelegatingWorkerFactory()
        workerFactory.addFactory(
            MoviesWorkerFactory(
                appDi.movieRepository,
                appDi.movieNotifications
            )
        )

        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()
    }
}