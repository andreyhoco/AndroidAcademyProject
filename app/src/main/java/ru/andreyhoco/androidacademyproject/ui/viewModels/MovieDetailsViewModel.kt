package ru.andreyhoco.androidacademyproject.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
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
    private val movieDetailsStateMutableLiveData = MutableLiveData<UiState<Movie>>()
    val fragmentState = movieDetailsStateMutableLiveData

    fun loadMovie(movieId: Long) {
        movieDetailsStateMutableLiveData.postValue(UiState.Loading())
        viewModelScope.launch(Dispatchers.Main) {
            val movieDataFlow = repository.getMovieById(movieId)
            movieDataFlow.collect { movieRequestResult ->
                when (movieRequestResult) {
                    is RequestResult.Success -> {
                        val movie = movieRequestResult.value
                        movieDetailsStateMutableLiveData.value = UiState.DataDisplay(movie)
                    }
                    is RequestResult.Failure -> {
                        handleErrorResult(
                            movieRequestResult,
                            movieDetailsStateMutableLiveData
                        )
                    }
                }
            }
        }
    }

    private fun <T> handleErrorResult(
        result: RequestResult.Failure,
        uiState: MutableLiveData<UiState<T>>
    ) {
        when (result) {
            is RequestResult.Failure.HttpError -> {
                val statusCode = result.exception.code()
                when (statusCode) {
                    in 500..599 -> {
                        uiState.value = UiState.DisplayError.ServerError()
                    }
                    else -> {
                        uiState.value = UiState.DisplayError.NetworkError()
                    }
                }
                Timber.w(
                    "Http error: $statusCode, ${result.exception.message()}"
                )
            }
            is RequestResult.Failure.Error -> {
                uiState.value = UiState.DisplayError.NetworkError()
                Timber.w(
                    "${MovieDetailsViewModel::class.java.name}: ${result.exception}"
                )
            }
        }
    }
}
