package ru.andreyhoco.androidacademyproject.viewModels

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.andreyhoco.androidacademyproject.BuildConfig
import ru.andreyhoco.androidacademyproject.MovieRepository
import ru.andreyhoco.androidacademyproject.UiState
import ru.andreyhoco.androidacademyproject.data.Movie
import ru.andreyhoco.androidacademyproject.network.RequestResult
import timber.log.Timber

class MoviesListViewModel(
    private val repository: MovieRepository,
    private val coroutineScope: LifecycleCoroutineScope
) : ViewModel() {
    private val moviesListFragmentStateMutableLiveData = MutableLiveData<UiState<List<Movie>>>()
    val fragmentState = moviesListFragmentStateMutableLiveData

    init {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        loadMovies()
    }

    fun loadMovies() {
        moviesListFragmentStateMutableLiveData.value = UiState.Loading()
        coroutineScope.launch(Dispatchers.IO) {
            val moviesResult = repository.getPopularMovies(1)
            delay(1500)
            when (moviesResult) {
                is RequestResult.Success -> {
                    val moviesList = moviesResult.value
                    moviesListFragmentStateMutableLiveData.postValue(
                        UiState.DataDisplay(moviesList)
                    )
                }
                is RequestResult.Failure -> {
                    handleErrorResult(
                        moviesResult,
                        moviesListFragmentStateMutableLiveData
                    )
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
                        uiState.postValue(UiState.DisplayError.ServerError())
                    }
                    else -> {
                        uiState.postValue(UiState.DisplayError.NetworkError())
                    }
                }
                Timber.w(
                    "Http error: $statusCode, ${result.exception.message()}"
                )
            }
            is RequestResult.Failure.Error -> {
                uiState.postValue(UiState.DisplayError.NetworkError())
                Timber.w(
                    "${MovieDetailsViewModel::class.java.name}: ${result.exception}"
                )
            }
        }
    }
}