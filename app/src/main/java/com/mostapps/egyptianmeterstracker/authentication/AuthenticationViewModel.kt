package com.mostapps.egyptianmeterstracker.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.mostapps.egyptianmeterstracker.data.remote.FirebaseDatabaseManager
import com.mostapps.egyptianmeterstracker.data.remote.models.User
import com.mostapps.egyptianmeterstracker.utils.Result
import com.mostapps.egyptianmeterstracker.utils.SharedPreferencesUtils
import org.koin.core.component.KoinComponent
import org.koin.java.KoinJavaComponent.inject

class AuthenticationViewModel : ViewModel(), KoinComponent {

    private val firebaseAuthenticationManager: FirebaseAuthenticationManager
            by inject(FirebaseAuthenticationManager::class.java)
    private val sharedPreferences: SharedPreferencesUtils by inject(SharedPreferencesUtils::class.java)
    private val firebaseDatabaseManager: FirebaseDatabaseManager
            by inject(FirebaseDatabaseManager::class.java)

    val authenticationState: LiveData<Result<FirebaseUser>> =
        firebaseAuthenticationManager.getUserAuthenticationState()

    fun changeAppLanguage(toEnglish: Boolean) {
        sharedPreferences.storeAppLanguage(toEnglish)
    }

    fun storeUserData(user: FirebaseUser) {
        firebaseDatabaseManager.saveUser(
            user.run { User(username = displayName, email = email) },
            user.uid
        )
    }
}
