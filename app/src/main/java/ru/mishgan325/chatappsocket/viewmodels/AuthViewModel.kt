package ru.mishgan325.chatappsocket.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.mishgan325.chatappsocket.api.ApiRepository
import javax.inject.Inject
import ru.mishgan325.chatappsocket.Result

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: ApiRepository
) : ViewModel() {

    // 2. Упрощаем состояния, используя только Result
    private val _authState = MutableStateFlow<Result<Unit>>(Result.Loading)
    val authState: StateFlow<Result<Unit>> = _authState

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _authState.value = Result.Loading
            try {
                // 3. Предполагаем, что repository возвращает кастомный Result
                when (val result = repository.login(username, password)) {
                    is Result.Success<*> -> {
                        _authState.value = Result.Success(Unit)
                    }
                    is Result.Error -> {
                        _authState.value = Result.Error(result.exception)
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                // 4. Обрабатываем непредвиденные исключения
                _authState.value = Result.Error(e)
            }
        }
    }

    // Аналогичная логика для регистрации
}