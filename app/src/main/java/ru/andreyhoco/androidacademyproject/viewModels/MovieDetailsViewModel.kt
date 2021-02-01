package ru.andreyhoco.androidacademyproject.viewModels

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.andreyhoco.androidacademyproject.BuildConfig
import ru.andreyhoco.androidacademyproject.MovieRepository
import ru.andreyhoco.androidacademyproject.UiState
import ru.andreyhoco.androidacademyproject.data.Actor
import ru.andreyhoco.androidacademyproject.data.Movie
import ru.andreyhoco.androidacademyproject.network.RequestResult
import timber.log.Timber

class MovieDetailsViewModel(
    private val repository: MovieRepository,
    private val coroutineScope: LifecycleCoroutineScope
) : ViewModel() {
    private val movieDetailsStateMutableLiveData = MutableLiveData<UiState<Movie>>()
    val fragmentState = movieDetailsStateMutableLiveData

    init {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

    }

    fun loadMovie(movieId: Int) {
        movieDetailsStateMutableLiveData.postValue(UiState.Loading())
        coroutineScope.launch(Dispatchers.Main) {
            val movieRequestResult = repository.getMovieById(movieId)
            when (movieRequestResult) {
                is RequestResult.Success -> {
                    val actorsRequestResult = repository.getActorsByMovieId(movieId)
                    when (actorsRequestResult) {
                        is RequestResult.Success -> {
                            val movie = movieRequestResult.value
                            val actors = actorsRequestResult.value
                            movie.actors = actors

                            movieDetailsStateMutableLiveData.postValue(
                                UiState.DataDisplay(movie)
                            )
                        }
                        is RequestResult.Failure -> {
                            handleErrorResult(
                                actorsRequestResult,
                                movieDetailsStateMutableLiveData
                            )
                        }
                    }
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
