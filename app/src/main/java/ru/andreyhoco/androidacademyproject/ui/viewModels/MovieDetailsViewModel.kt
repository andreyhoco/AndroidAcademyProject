package ru.andreyhoco.androidacademyproject.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.andreyhoco.androidacademyproject.BuildConfig
import ru.andreyhoco.androidacademyproject.MovieNotifications
import ru.andreyhoco.androidacademyproject.repositories.MovieRepository
import ru.andreyhoco.ru.andreyhoco.androidacademyproject.ui.UiState
import ru.andreyhoco.androidacademyproject.ui.uiDataModel.Movie
import ru.andreyhoco.androidacademyproject.repositories.RequestResult
import timber.log.Timber

class MovieDetailsViewModel(
    private val repository: MovieRepository
) : ViewModel() {
    private val _fragmentState = MutableStateFlow<UiState<Movie>>(UiState.Loading())
    val fragmentState: StateFlow<UiState<Movie>> = _fragmentState.asStateFlow()

    fun loadMovie(movieId: Long) {
        viewModelScope.launch(Dispatchers.Main) {
            val movieDataFlow = repository.getMovieById(movieId)
            movieDataFlow.collect { movieRequestResult ->
                when (movieRequestResult) {
                    is RequestResult.Success -> {
                        val movie = movieRequestResult.value
                        _fragmentState.value = UiState.DataDisplay(movie)
                    }
                    is RequestResult.Failure -> {
                        _fragmentState.value = handleErrorResult(movieRequestResult)
                    }
                }
            }
        }
    }

    private fun handleErrorResult(result: RequestResult.Failure): UiState.DisplayError {
        when (result) {
            is RequestResult.Failure.HttpError -> {
                val statusCode = result.exception.code()
                Timber.w("Http error: $statusCode, ${result.exception.message}")
                when (statusCode) {
                    in 500..599 -> {
                        return UiState.DisplayError.ServerError()
                    }
                    else -> {
                        return UiState.DisplayError.NetworkError()
                    }
                }
            }
            is RequestResult.Failure.Error -> {
                Timber.w("${MovieDetailsViewModel::class.java.name}: ${result.exception}")
                return UiState.DisplayError.UnexpectedError()
            }
        }
    }
}
