package ru.mishgan325.chatappsocket.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.mishgan325.chatappsocket.domain.usecases.DisconnectWebSocketUseCase
import ru.mishgan325.chatappsocket.domain.usecases.UnsubscribeWebSocketUseCase
import ru.mishgan325.chatappsocket.utils.SessionManager
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val disconnectWebSocketUseCase: DisconnectWebSocketUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    fun logout() {
        disconnectWebSocketUseCase.invoke()
        sessionManager.logout()
    }
}
