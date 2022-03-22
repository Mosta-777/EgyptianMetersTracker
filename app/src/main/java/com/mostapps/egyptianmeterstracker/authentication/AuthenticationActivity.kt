package com.mostapps.egyptianmeterstracker.authentication
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.mostapps.egyptianmeterstracker.screens.home.HomeActivity
import com.mostapps.egyptianmeterstracker.R
import com.mostapps.egyptianmeterstracker.base.BaseActivity

class AuthenticationActivity : BaseActivity() {

    private val viewModel by viewModels<AuthenticationViewModel>()
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
    }

    private fun observeAuthenticationState() {

        viewModel.authenticationState.observe(this) { authenticationState ->
            when (authenticationState) {
                AuthenticationViewModel.AuthenticationState.AUTHENTICATED -> {
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
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setTheme(R.style.SignInScreenTheme)
            .build()
        signInLauncher.launch(signInIntent)
    }
}
