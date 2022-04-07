package com.mostapps.egyptianmeterstracker.authentication

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.mostapps.egyptianmeterstracker.base.BaseViewModel
import com.mostapps.egyptianmeterstracker.data.local.MetersDataSource
import com.mostapps.egyptianmeterstracker.data.remote.FirebaseDatabaseManager
import com.mostapps.egyptianmeterstracker.data.remote.models.User
import com.mostapps.egyptianmeterstracker.utils.Result
import com.mostapps.egyptianmeterstracker.utils.SharedPreferencesUtils
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.java.KoinJavaComponent.inject

class AuthenticationViewModel(
    app: Application,
    private val dataSource: MetersDataSource
) : BaseViewModel(app) {

    private val firebaseAuthenticationManager: FirebaseAuthenticationManager
            by inject(FirebaseAuthenticationManager::class.java)
    private val sharedPreferences: SharedPreferencesUtils by inject(SharedPreferencesUtils::class.java)

    val authenticationState: LiveData<Result<FirebaseUser>> =
        firebaseAuthenticationManager.getUserAuthenticationState()

    fun changeAppLanguage(toEnglish: Boolean) {
        sharedPreferences.storeAppLanguage(toEnglish)
    }

    fun storeUserData(user: FirebaseUser) {
        showLoading.value = true
        viewModelScope.launch {
            dataSource.storeUserData(user)
            showLoading.postValue(false)
        }
    }


}
