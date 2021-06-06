package ru.andreyhoco.ru.andreyhoco.androidacademyproject.ui

sealed class UiState {

    class Loading : UiState()

    class DataDisplay : UiState()

    sealed class DisplayError : UiState() {

        class NetworkError: DisplayError()

        class ServerError: DisplayError()

        class UnexpectedError: DisplayError()
    }
}
