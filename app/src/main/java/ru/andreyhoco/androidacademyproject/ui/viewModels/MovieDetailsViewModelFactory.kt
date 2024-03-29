package ru.andreyhoco.androidacademyproject.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.andreyhoco.androidacademyproject.MovieNotifications
import ru.andreyhoco.androidacademyproject.repositories.MovieRepository

class MovieDetailsViewModelFactory(
    private val repository: MovieRepository,
    private val movieId: Long
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = when (modelClass) {
        MovieDetailsViewModel::class.java -> MovieDetailsViewModel(repository, movieId)
        else -> throw IllegalArgumentException("$modelClass is not registered")
    } as T
}