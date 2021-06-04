package ru.andreyhoco.androidacademyproject.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import ru.andreyhoco.androidacademyproject.BuildConfig
import ru.andreyhoco.androidacademyproject.repositories.MovieRepository
import ru.andreyhoco.ru.andreyhoco.androidacademyproject.ui.UiState
import ru.andreyhoco.androidacademyproject.ui.uiDataModel.Movie
import ru.andreyhoco.androidacademyproject.repositories.RequestResult
import timber.log.Timber

class MoviesListViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    val fragmentState: StateFlow<UiState<List<Movie>>> = flow {
        repository.getTopRatedMovies(1).collect { requestResult ->
            when(requestResult) {
                is RequestResult.Success -> {
                    emit(UiState.DataDisplay(requestResult.value))
                }
                is RequestResult.Failure -> {
                    emit(handleErrorResult(requestResult))
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, UiState.Loading())

    init {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
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