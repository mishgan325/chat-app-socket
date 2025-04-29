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
import ru.mishgan325.chatappsocket.data.api.model.RegisterResponse
import ru.mishgan325.chatappsocket.domain.usecases.RegisterUseCase
import ru.mishgan325.chatappsocket.domain.usecases.WhoamiUseCase
import ru.mishgan325.chatappsocket.utils.NetworkResult
import ru.mishgan325.chatappsocket.utils.SessionManager
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val whoamiUseCase: WhoamiUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _authResponse = MutableLiveData<NetworkResult<RegisterResponse>>()
    val authResponse: LiveData<NetworkResult<RegisterResponse>> get() = _authResponse

    private val _authState = MutableStateFlow<NetworkResult<Unit>>(NetworkResult.Loading())
    val authState: StateFlow<NetworkResult<Unit>> = _authState

    private val TAG = "RegisterViewModel"

    fun register(email: String, username: String, password: String) {
        viewModelScope.launch {
            _authState.value = NetworkResult.Loading()

            registerUseCase.invoke(email, username, password).let { result ->
                _authResponse.value = result

                when (result) {
                    is NetworkResult.Error -> {
                        Log.d(TAG, "Error: ${result.message}")
                        _authState.value = NetworkResult.Error(null, result.message)
                    }

                    is NetworkResult.Loading -> {
                        Log.d(TAG, "Register is loading")
                        _authState.value = NetworkResult.Loading()
                    }

                    is NetworkResult.Success -> {
                        Log.d(TAG, "SUCCESS: ${result.data?.token}")
                        _authState.value = NetworkResult.Success(Unit)
                        result.data?.let { data ->
                            sessionManager.saveAuthToken(data.token)
                            Log.d(TAG, "SUCCESS: ${result.data?.token}")
                        }
                        saveUserData()
                    }
                }
            }
        }
    }

    fun saveUserData() {
        viewModelScope.launch {
            val result = whoamiUseCase.invoke()

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
                    Log.d(TAG, "SUCCESS: ${result.data?.id} ${result.data?.username}")
                    _authState.value = NetworkResult.Success(Unit)

                    result.data?.let { data ->
                        sessionManager.saveUserId(result.data.id)
                        sessionManager.saveUsername(result.data.username.toString())
                    }
                }
            }
        }
    }

    fun resetAuthResponse() {
        _authResponse.value = NetworkResult.Loading()
    }
}