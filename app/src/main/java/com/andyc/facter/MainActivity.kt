package com.andyc.facter

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.andyc.auth.presentation.sign_in.FirebaseSignInActivity
import com.andyc.core.presentation.designsystem.FacterTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FacterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController()
                    NavigationRoot(
                        navController = navController,
                        isSignedIn = viewModel.state.isSignedIn, // TODO
                        onSignInClick = { launchFirebaseSignInActivity() }
                    )
                }
            }
        }
    }

    private fun launchFirebaseSignInActivity() {
        val intent = Intent(this, FirebaseSignInActivity::class.java)
        startActivity(intent)
    }
}