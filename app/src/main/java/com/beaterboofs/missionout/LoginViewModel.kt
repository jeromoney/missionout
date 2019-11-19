package com.beaterboofs.missionout

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginViewModel : ViewModel() {
    enum class AuthenticationState {
        UNAUTHENTICATED,        // Initial state before user has authenticated
        AUTHENTICATED,          // User has logged in
        INVALID_AUTHENTICATION  // User failed log in process
    }

    val authenticationState = MutableLiveData<AuthenticationState>()
    val user = MutableLiveData<FirebaseUser>()
    val teamDocID = MutableLiveData<String?>()
    val editor = MutableLiveData<Boolean>()

    init {
        val auth = FirebaseAuth.getInstance()
        authenticationState.apply {
            if (auth.currentUser == null){
                // User is not logged in
                value = AuthenticationState.UNAUTHENTICATED
                user.value = null
                teamDocID.value = null
                editor.value = false
            }
            else {
                value = AuthenticationState.AUTHENTICATED
                user.value = auth.currentUser
                teamDocID.value = "raux5KIhuIL84bBmPSPs"
                updateClaims()
            }
        }
    }

    fun refuseAuthentication(){
        authenticationState.value = AuthenticationState.UNAUTHENTICATED
    }

    fun authenticate(username: String, password: String){
        // TODO - authenticate user
        authenticationState.value = AuthenticationState.AUTHENTICATED
    }


    fun updateClaims() {
        FirebaseAuth.getInstance().currentUser!!.getIdToken(true).addOnSuccessListener { result -> // TODO - convert to coroutine
            val claims = result?.claims ?: return@addOnSuccessListener
            editor.value = claims.getOrDefault("editor",false) as Boolean
            teamDocID.value = claims.getOrDefault("teamDocID", null) as String?
        }
    }
}