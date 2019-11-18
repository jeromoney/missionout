package com.beaterboofs.missionout

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

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
        val auth = FirebaseAuth.getInstance()
        authenticationState.apply {
            if (auth.currentUser == null){
                value = AuthenticationState.UNAUTHENTICATED
            }
            else {
                value = AuthenticationState.AUTHENTICATED
            }
        }
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