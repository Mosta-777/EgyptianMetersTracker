package com.mostapps.egyptianmeterstracker.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseUser
import com.mostapps.egyptianmeterstracker.R
import com.mostapps.egyptianmeterstracker.base.BaseActivity
import com.mostapps.egyptianmeterstracker.screens.home.HomeActivity
import com.mostapps.egyptianmeterstracker.screens.home.meterslist.MetersListViewModel
import com.mostapps.egyptianmeterstracker.utils.Result
import com.mostapps.egyptianmeterstracker.utils.SharedPreferencesUtils
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthenticationActivity : BaseActivity() {

    val viewModel: AuthenticationViewModel by viewModel()
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        observeAuthenticationState()
        findViewById<Button>(R.id.loginButton).setOnClickListener {
            startSigningInOrRegistering()
        }
        findViewById<Button>(R.id.buttonArabic).setOnClickListener {
            changeLanguage(isToEnglish = false)
        }


        findViewById<Button>(R.id.englishButton).setOnClickListener {
            changeLanguage(isToEnglish = true)
        }


    }

    private fun changeLanguage(isToEnglish: Boolean) {
        viewModel.changeAppLanguage(isToEnglish)
        val i = Intent(this, AuthenticationActivity::class.java)
        finish()
        overridePendingTransition(0, 0)
        startActivity(i)
        overridePendingTransition(0, 0)
    }

    private fun observeAuthenticationState() {

        viewModel.authenticationState.observe(this) { authenticationState: Result<FirebaseUser> ->
            when (authenticationState) {
                is Result.Success<FirebaseUser> -> {
                    viewModel.storeUserData(authenticationState.data)
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                else -> {
                    findViewById<Button>(R.id.loginButton).setOnClickListener {
                        startSigningInOrRegistering()
                    }
                }
            }
        }
    }

    private fun startSigningInOrRegistering() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setTheme(R.style.SignInScreenTheme)
            .build()
        signInLauncher.launch(signInIntent)
    }
}
