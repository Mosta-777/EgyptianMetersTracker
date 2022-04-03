package com.mostapps.egyptianmeterstracker.authentication

import androidx.lifecycle.LiveData

interface FirebaseAuthenticationInterface {


    fun getUserAuthenticationState(): LiveData<AuthenticationState>


}