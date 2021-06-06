package ru.andreyhoco.ru.andreyhoco.androidacademyproject.ui

sealed class UiState {

    class Loading : UiState()

    class DisplayData : UiState()

    sealed class DisplayError : UiState() {

        class NetworkError: DisplayError()

        class ServerError: DisplayError()
    }
}
