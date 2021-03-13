package ru.andreyhoco.androidacademyproject

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import ru.andreyhoco.androidacademyproject.network.RetrofitModule
import ru.andreyhoco.androidacademyproject.persistence.appDatabase.TheMovieAppDatabase
import ru.andreyhoco.androidacademyproject.repositories.MovieRepository

class AppDi(applicationContext: Context) {
    private val tmdbApiService = RetrofitModule.tmdbApiService
    private val theMovieAppDb = TheMovieAppDatabase.create(
        applicationContext,
        CoroutineScope(SupervisorJob())
    )
    val movieNotifications = MovieNotifications(applicationContext).apply {
        initChannel()
    }

    val movieRepository = MovieRepository(
        tmdbApiService,
        theMovieAppDb
    )

}