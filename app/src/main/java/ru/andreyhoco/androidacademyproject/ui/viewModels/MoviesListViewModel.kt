package ru.andreyhoco.androidacademyproject.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.andreyhoco.androidacademyproject.repositories.MovieRepository
import ru.andreyhoco.ru.andreyhoco.androidacademyproject.ui.UiState
import ru.andreyhoco.androidacademyproject.ui.uiDataModel.Movie
import ru.andreyhoco.androidacademyproject.repositories.RequestResult
import timber.log.Timber

class MoviesListViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val moviesListFragmentStateMutableLiveData = MutableLiveData<UiState<List<Movie>>>()

    val fragmentState = moviesListFragmentStateMutableLiveData

    init {
        loadMovies()
    }

    fun loadMovies() {
        moviesListFragmentStateMutableLiveData.value = UiState.Loading()

        viewModelScope.launch(Dispatchers.Main) {
            val moviesFlow = repository.getTopRatedMovies(1)
            moviesFlow.collect {
                when (it) {
                    is RequestResult.Success -> {
                        val moviesList = it.value
                        moviesListFragmentStateMutableLiveData.value =
                            UiState.DataDisplay(moviesList)
                    }
                    is RequestResult.Failure -> {
                        handleErrorResult(
                            it,
                            moviesListFragmentStateMutableLiveData
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