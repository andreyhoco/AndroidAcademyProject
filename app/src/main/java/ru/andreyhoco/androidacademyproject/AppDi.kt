package ru.andreyhoco.androidacademyproject

import android.content.Context
import android.util.Log
import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import ru.andreyhoco.androidacademyproject.background.MoviesWorkerFactory
import ru.andreyhoco.androidacademyproject.network.RetrofitModule
import ru.andreyhoco.androidacademyproject.persistence.appDatabase.TheMovieAppDatabase
import ru.andreyhoco.androidacademyproject.repositories.MovieRepository

class AppDi(applicationContext: Context) {
    private val tmdbApiService = RetrofitModule.tmdbApiService
    private val theMovieAppDb = TheMovieAppDatabase.create(applicationContext)

    val movieRepository = MovieRepository(
        tmdbApiService,
        theMovieAppDb
    )

}