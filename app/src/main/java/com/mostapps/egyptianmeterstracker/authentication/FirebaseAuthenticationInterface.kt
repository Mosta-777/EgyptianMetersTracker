package com.mostapps.egyptianmeterstracker.authentication

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseUser
import com.mostapps.egyptianmeterstracker.utils.Result

interface FirebaseAuthenticationInterface {


    fun getUserAuthenticationState(): LiveData<Result<FirebaseUser>>


}