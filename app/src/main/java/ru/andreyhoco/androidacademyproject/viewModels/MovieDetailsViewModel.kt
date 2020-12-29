package ru.andreyhoco.androidacademyproject.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.andreyhoco.androidacademyproject.data.Movie

class MovieDetailsViewModel(movie: Movie) : ViewModel() {
    private val movieMutableLiveData = MutableLiveData<Movie>()
    val movieLiveData = movieMutableLiveData

    init {
        movieMutableLiveData.value = movie
    }
}