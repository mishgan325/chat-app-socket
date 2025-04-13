package ru.mishgan325.chatappsocket.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.mishgan325.chatappsocket.Result
import ru.mishgan325.chatappsocket.data.api.model.AuthResponse
import ru.mishgan325.chatappsocket.domain.usecases.LoginUseCase
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {
    private val _authResponse = MutableLiveData<NetworkResult<AuthResponse>>()

    val authResponse: LiveData<NetworkResult<AuthResponse>>
        get() = _authResponse
    private val _authState = MutableStateFlow<Result<Unit>>(Result.Loading)

    val authState: StateFlow<Result<Unit>> = _authState

    fun login(username: String, password: String) {
        viewModelScope.launch {
            loginUseCase.invoke(username, password).let {
                _authResponse.value = it
            }
//            _authState.value = Result.Loading
//            try {
//                // 3. Предполагаем, что repository возвращает кастомный Result
//                when (val result = api.login(username, password)) {
//                    is Result.Success<*> -> {
//                        _authState.value = Result.Success(Unit)
//                    }
//                    is Result.Error -> {
//                        _authState.value = Result.Error(result.exception)
//                    }
//                    else -> {}
//                }
//            } catch (e: Exception) {
//                // 4. Обрабатываем непредвиденные исключения
//                _authState.value = Result.Error(e)
//            }
        }
    }
}