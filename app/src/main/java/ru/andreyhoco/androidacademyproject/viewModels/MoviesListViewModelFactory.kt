package ru.andreyhoco.androidacademyproject.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.andreyhoco.androidacademyproject.data.Movie


class MoviesListViewModelFactory(private val movieList: List<Movie>): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = when (modelClass) {
        MoviesListViewModel::class.java -> MoviesListViewModel(movieList)
        else -> throw IllegalArgumentException("$modelClass is not registered ViewModel")
    } as T
}