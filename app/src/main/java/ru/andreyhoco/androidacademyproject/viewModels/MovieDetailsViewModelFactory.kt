package ru.andreyhoco.androidacademyproject.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.andreyhoco.androidacademyproject.data.Movie

class MovieDetailsViewModelFactory(private val movie: Movie) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = when (modelClass) {
        MovieDetailsViewModel::class.java -> MovieDetailsViewModel(movie)
        else -> throw IllegalArgumentException("$modelClass is not registered")
    } as T
}