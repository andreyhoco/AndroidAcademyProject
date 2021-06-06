package ru.andreyhoco.androidacademyproject.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.andreyhoco.androidacademyproject.repositories.MovieRepository
import ru.andreyhoco.ru.andreyhoco.androidacademyproject.ui.UiState
import ru.andreyhoco.androidacademyproject.ui.uiDataModel.Movie
import ru.andreyhoco.androidacademyproject.repositories.RequestResult
import timber.log.Timber

class MoviesListViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    val moviesFlow: StateFlow<List<Movie>> = repository
        .getTopRatedMovies()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _fragmentState = MutableStateFlow<UiState>(UiState.Loading())
    val fragmentState: StateFlow<UiState> = _fragmentState.asStateFlow()

    init {
        updateMoviesList()
    }

    fun updateMoviesList() {
        viewModelScope.launch(Dispatchers.IO) {
            _fragmentState.value = UiState.Loading()
            val requestResult = repository.loadTopRatedMovies(1)

            when (requestResult) {
                is RequestResult.Success -> {
                    _fragmentState.value = UiState.DisplayData()
                }
                is RequestResult.Failure -> {
                    _fragmentState.value = handleErrorResult(requestResult)
                }
            }
        }
    }

    private fun handleErrorResult(result: RequestResult.Failure): UiState.DisplayError {
        if ((result is RequestResult.Failure.HttpError) && (result.exception.code() in 500..599)) {
            Timber.tag("NETWORK_ERROR")
                .w("Http error: ${result.exception.message}")
            return UiState.DisplayError.ServerError()
        } else {
            return UiState.DisplayError.NetworkError()
        }
    }
}