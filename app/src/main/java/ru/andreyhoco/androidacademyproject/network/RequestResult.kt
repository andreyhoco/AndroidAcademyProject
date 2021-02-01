package ru.andreyhoco.androidacademyproject.network

import retrofit2.HttpException

sealed class RequestResult<out T> {

    class Success<out T> (val value: T) : RequestResult<T>()

    sealed class Failure : RequestResult<Nothing>() {

        class HttpError(val exception: HttpException) : Failure()

        class Error(val exception: Exception) : Failure()
    }
}
