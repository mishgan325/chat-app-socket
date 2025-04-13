package ru.mishgan325.chatappsocket.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.mishgan325.chatappsocket.data.api.model.AuthResponse
import ru.mishgan325.chatappsocket.domain.usecases.LoginUseCase
import ru.mishgan325.chatappsocket.utils.NetworkResult
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _authResponse = MutableLiveData<NetworkResult<AuthResponse>>()
    val authResponse: LiveData<NetworkResult<AuthResponse>> get() = _authResponse

    private val _authState = MutableStateFlow<NetworkResult<Unit>>(NetworkResult.Loading())
    val authState: StateFlow<NetworkResult<Unit>> = _authState

    private val TAG = "LoginViewModel"

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _authState.value = NetworkResult.Loading()

            loginUseCase.invoke(username, password).let { result ->
                _authResponse.value = result

                when (result) {
                    is NetworkResult.Error -> {
                        Log.d(TAG, "Error: ${result.message}")
                        _authState.value = NetworkResult.Error(null, result.message)
                    }
                    is NetworkResult.Loading -> {
                        Log.d(TAG, "Auth is loading")
                        _authState.value = NetworkResult.Loading()
                    }
                    is NetworkResult.Success -> {
                        Log.d(TAG, "SUCCESS: ${result.data?.token}")
                        _authState.value = NetworkResult.Success(Unit)
                    }
                }
            }
        }
    }
}
