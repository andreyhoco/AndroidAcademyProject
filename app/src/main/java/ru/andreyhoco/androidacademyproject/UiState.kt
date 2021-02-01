package ru.andreyhoco.androidacademyproject

sealed class UiState<out T> {

    class Loading : UiState<Nothing>()

    class DataDisplay<out T>(
        val value: T
    ) : UiState<T>()

    sealed class DisplayError : UiState<Nothing>() {

        class NetworkError: DisplayError()

        class ServerError: DisplayError()
    }
}
