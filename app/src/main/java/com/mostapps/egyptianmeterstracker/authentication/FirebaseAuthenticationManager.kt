package com.mostapps.egyptianmeterstracker.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthenticationManager(private val authentication: FirebaseAuth = FirebaseAuth.getInstance()) :
    FirebaseAuthenticationInterface {

    override fun getUserAuthenticationState(): LiveData<AuthenticationState> {
        return FirebaseUserLiveData(authentication).map { user ->
            if (user != null) {
                AuthenticationState.AUTHENTICATED
            } else {
                AuthenticationState.UNAUTHENTICATED
            }
        }
    }


}