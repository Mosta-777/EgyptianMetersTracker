package com.mostapps.egyptianmeterstracker.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mostapps.egyptianmeterstracker.utils.Result

class FirebaseAuthenticationManager(private val authentication: FirebaseAuth = FirebaseAuth.getInstance()) :
    FirebaseAuthenticationInterface {
    override fun getCurrentUser(): FirebaseUser? = authentication.currentUser

    override fun getUserAuthenticationState(): LiveData<Result<FirebaseUser>> {
        return FirebaseUserLiveData(authentication).map { user ->
            if (user != null) {
                Result.Success(user)
            } else {
                Result.Error("User not authenticated")
            }
        }
    }


}