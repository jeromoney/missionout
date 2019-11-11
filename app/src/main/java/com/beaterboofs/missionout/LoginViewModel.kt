package com.beaterboofs.missionout

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    enum class AuthenticationState {
        UNAUTHENTICATED,        // Initial state before user has authenticated
        AUTHENTICATED,          // User has logged in
        INVALID_AUTHENTICATION  // User failed log in process
    }

    val authenticationState = MutableLiveData<AuthenticationState>()
    val username = MutableLiveData<String>()

    init {
        // TODO - Check if user is logged in
        authenticationState.value = AuthenticationState.UNAUTHENTICATED
        username.value = ""
    }

    fun refuseAuthentication(){
        authenticationState.value = AuthenticationState.UNAUTHENTICATED
    }

    fun authenticate(username: String, password: String){
        // TODO - authenticate user
        authenticationState.value = AuthenticationState.AUTHENTICATED
    }


}