package ru.andreyhoco.androidacademyproject.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import ru.andreyhoco.androidacademyproject.repositories.MovieRepository
import ru.andreyhoco.ru.andreyhoco.androidacademyproject.ui.UiState
import ru.andreyhoco.androidacademyproject.ui.uiDataModel.Movie
import ru.andreyhoco.androidacademyproject.repositories.RequestResult
import timber.log.Timber

class MovieDetailsViewModel(
    private val repository: MovieRepository,
    private var movieId: Long
) : ViewModel() {
    private val _fragmentState = MutableStateFlow<UiState>(UiState.Loading())
    val fragmentState: StateFlow<UiState> = _fragmentState.asStateFlow()

    private val _movieFlow: MutableSharedFlow<Movie> = MutableSharedFlow(
        replay = 1,
        extraBufferCapacity = 0,
        BufferOverflow.DROP_OLDEST
    )
    val movieFlow: SharedFlow<Movie> = _movieFlow.asSharedFlow()
    private var redirectMovieJob: Job? = null

    init {
        viewModelScope.launch(SupervisorJob()) {
            updateMovie()
            redirectMovieJob = redirectMovieFlowTo(this, movieId)
        }
    }

    private suspend fun redirectMovieFlowTo(scope: CoroutineScope, movieId: Long): Job {
        return repository.getMovieById(movieId).onEach { movie ->
                _movieFlow.emit(movie)
            }.launchIn(scope)
    }

    suspend fun updateMovie() {
        withContext(Dispatchers.IO) {
            _fragmentState.value = UiState.Loading()

            val requestResult = repository.loadMovieById(movieId)
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

    suspend fun changeMovieId(newMovieId: Long) {
        withContext(Dispatchers.IO) {
            _fragmentState.value = UiState.Loading()

            val requestResult = repository.loadMovieById(movieId)
            when (requestResult) {
                is RequestResult.Success -> {
                    movieId = newMovieId
                    redirectMovieJob?.cancel()
                    redirectMovieJob = redirectMovieFlowTo(this, newMovieId)
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
            Timber.tag("NETWORK_ERROR")
                .w("${MovieDetailsViewModel::class.java.name}: $result")
            return UiState.DisplayError.NetworkError()
        }
    }
}
