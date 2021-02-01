package ru.andreyhoco.androidacademyproject.viewModels

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.andreyhoco.androidacademyproject.MovieRepository
import ru.andreyhoco.androidacademyproject.data.Movie


class MoviesListViewModelFactory(
    private val repository: MovieRepository,
    private val coroutineScope: LifecycleCoroutineScope
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = when (modelClass) {
        MoviesListViewModel::class.java -> MoviesListViewModel(repository, coroutineScope)
        else -> throw IllegalArgumentException("$modelClass is not registered ViewModel")
    } as T
}