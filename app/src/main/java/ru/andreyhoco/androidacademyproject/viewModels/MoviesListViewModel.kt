package ru.andreyhoco.androidacademyproject.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.andreyhoco.androidacademyproject.BuildConfig
import ru.andreyhoco.androidacademyproject.data.Movie
import timber.log.Timber

class MoviesListViewModel(movieList: List<Movie>) : ViewModel() {

    private val moviesListMutableLiveData = MutableLiveData(emptyList<Movie>())
    val moviesListLiveData = moviesListMutableLiveData

    init {
        moviesListMutableLiveData.value = movieList
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("MoviesListViewModel destroyed")
    }
}