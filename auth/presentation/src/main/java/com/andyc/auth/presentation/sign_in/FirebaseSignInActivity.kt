package com.andyc.auth.presentation.sign_in

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.andyc.auth.presentation.R

class FirebaseSignInActivity : ComponentActivity() {

    private val signInLauncher = registerForActivityResult(
        contract = FirebaseAuthUIActivityResultContract()
    ) { result ->
        onSignInResult(result)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        setContent {
//            FacterTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//            }
//        }
        createSignInIntent()
    }

    private fun createSignInIntent() {
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build(),
            AuthUI.IdpConfig.TwitterBuilder().build(),
        )

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.drawable.ic_launcher)
            .setTheme(R.style.Theme_Facter)
            .build()
        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            // TODO: Do something with the user
            user?.run {
                Log.d("FirebaseSignInActivity", "$email signed in successfully")
                Toast.makeText(
                    this@FirebaseSignInActivity,
                    "Signed in successfully as $displayName",
                    Toast.LENGTH_LONG
                ).show()
//                signOut()
//                delete()
                finish()
            }
        } else {
            response?.error?.run {
                // Sign in failed
                Log.d("FirebaseSignInActivity", message ?: "Unknown Error")
                Toast.makeText(
                    this@FirebaseSignInActivity,
                    "Failed to sign in. Please try again",
                    Toast.LENGTH_LONG
                ).show()
            }
            finish()
        }
    }

    private fun signOut() {
        AuthUI.getInstance().signOut(this)
    }

    private fun delete() {
        AuthUI.getInstance().delete(this)
    }
}