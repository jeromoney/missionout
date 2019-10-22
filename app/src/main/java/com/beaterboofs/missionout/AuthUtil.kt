package com.beaterboofs.missionout

import com.google.firebase.auth.FirebaseAuth

object AuthUtil {

    fun getDomain(): String{
        val auth = FirebaseAuth.getInstance()
        val email = auth.currentUser?.email
        if (email == null){
            return ""
        }
        else {
            return email.substringAfter("@")
        }
    }
}