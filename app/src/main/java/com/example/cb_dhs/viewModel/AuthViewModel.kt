package com.example.cb_dhs.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cb_dhs.api.SimplifiedMessageApi
import com.example.cb_dhs.model.AuthResponse
import com.example.cb_dhs.model.LoginRequest
import com.example.cb_dhs.model.RegisterRequest
import com.example.cb_dhs.model.UserInfoResponse
import com.example.cb_dhs.repository.AuthRepository
import com.example.cb_dhs.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
) : ViewModel() {

    private val _registerResponse: MutableLiveData<Resource<AuthResponse>> = MutableLiveData()
    val registerResponse: LiveData<Resource<AuthResponse>>
        get() = _registerResponse

    private val _loginResponse: MutableLiveData<Resource<AuthResponse>> = MutableLiveData()
    val loginResponse: LiveData<Resource<AuthResponse>>
        get() = _loginResponse

    private val _userInfoResponse: MutableLiveData<Resource<UserInfoResponse>> = MutableLiveData()
    val userInfoResponse: LiveData<Resource<UserInfoResponse>>
        get() = _userInfoResponse

//    private val _logoutResponse: MutableLiveData<Resource<StatusResponse>> = MutableLiveData()
//    val logoutResponse: LiveData<Resource<StatusResponse>>
//        get() = _logoutResponse


    fun register(
        registerRequest: RegisterRequest
    ) = viewModelScope.launch {
        _registerResponse.value = Resource.Loading()

        repository.register(registerRequest)
            .enqueue(object : Callback<AuthResponse> {
                override fun onResponse(
                    call: Call<AuthResponse>,
                    response: Response<AuthResponse>
                ) {
                    if (response.isSuccessful) {
                        val registerResponse = response.body()
                        _registerResponse.value = Resource.Success(registerResponse)
                    } else {
                        // this other response from api ex) email exist , email required , ....
                        val messageResponse = SimplifiedMessageApi.get(
                            response.errorBody()!!.byteStream().reader().readText()
                        )["message"]
                        _registerResponse.value = Resource.Error(messageResponse.toString())
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    _registerResponse.value = Resource.Error(t.message.toString())
                }
            })

    }

    fun login(
        loginRequest: LoginRequest
    ) = viewModelScope.launch {
        _loginResponse.value = Resource.Loading()

        repository.login(loginRequest).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    _loginResponse.value = Resource.Success(loginResponse)
                } else {
                    // this other response from api ex) email exist , email required , ....
                    val messageResponse = SimplifiedMessageApi.get(
                        response.errorBody()!!.byteStream().reader().readText()
                    )["message"]
                    _loginResponse.value = Resource.Error(messageResponse.toString())
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                _loginResponse.value = Resource.Error(t.message.toString())
            }

        })
    }


    fun getUserInfo(token: String) = viewModelScope.launch {
        _userInfoResponse.value = Resource.Loading()

        repository.getUserInfo(token).enqueue(object : Callback<UserInfoResponse> {
            override fun onResponse(
                call: Call<UserInfoResponse>,
                response: Response<UserInfoResponse>
            ) {
                if (response.isSuccessful) {
                    _userInfoResponse.value = Resource.Success(response.body())
                } else {
                    val messageResponse = SimplifiedMessageApi.get(
                        response.errorBody()!!.byteStream().reader().readText()
                    )["message"]
                    _userInfoResponse.value = Resource.Error(messageResponse.toString())
                }
            }

            override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                _userInfoResponse.value = Resource.Error(t.message.toString())
            }

        })
    }

//    fun logout(
//    ) = viewModelScope.launch {
//        _logoutResponse.value = Resource.Loading()
//
//        repository.logout().enqueue(object : Callback<StatusResponse> {
//            override fun onResponse(
//                call: Call<StatusResponse>,
//                response: Response<StatusResponse>
//            ) {
//                if (response.isSuccessful) {
//                    val statusResponse = response.body()
//                    _logoutResponse.value = Resource.Success(data = statusResponse)
//                } else {
//                    _logoutResponse.value = Resource.Error(response.errorBody().toString())
//                }
//            }
//
//            override fun onFailure(call: Call<StatusResponse>, t: Throwable) {
//                _logoutResponse.value = Resource.Error(t.message.toString())
//            }
//
//        })
//    }
}

