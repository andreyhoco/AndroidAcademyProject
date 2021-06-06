package ru.andreyhoco.androidacademyproject.repositories

import retrofit2.HttpException

sealed class RequestResult {

    class Success : RequestResult()

    sealed class Failure : RequestResult() {

        class HttpError(val exception: HttpException) : Failure()

        class Error(val exception: Exception) : Failure()
    }
}
