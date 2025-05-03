package com.andyc.auth.presentation.sign_in

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andyc.auth.presentation.R
import com.andyc.core.presentation.designsystem.FacterTheme
import com.andyc.core.presentation.ui.NavigationDestination
import com.andyc.core.presentation.ui.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

object SignInDestination : NavigationDestination {
    override val route: String = "sign_in"
}

@Composable
fun SignInScreenRoot(
    onSignInClick: () -> Unit,
    onSignInSuccess: () -> Unit,
    viewModel: SignInViewModel = koinViewModel()
) {
    val applicationContext = LocalContext.current

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is SignInEvent.Error -> {
                Log.d("SignInScreen", "Error: ${event.error.asString(applicationContext)}")
            }
            SignInEvent.SignInSuccess -> {
                onSignInSuccess()
            }
        }
    }

    SignInScreen(
        state = viewModel.state,
        onAction = { action ->
            when (action) {
                SignInAction.OnSignInClick -> {
                    viewModel.onAction(SignInAction.OnSignInClick)
                    onSignInClick()
                }
            }
        }
    )
}

@Composable
fun SignInScreen(
    state: SignInState,
    onAction: (SignInAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            Text(
                text = stringResource(R.string.welcome_to_facter),
                modifier = Modifier.padding(12.dp),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(150.dp))
            OutlinedButton(
                onClick = {
                    onAction(SignInAction.OnSignInClick)
                },
                enabled = !state.isSigningIn
            ) {
                Text(
                    text = stringResource(R.string.sign_in)
                )
            }

            if (state.isSigningIn) {
                CircularProgressIndicator(modifier = Modifier.padding(8.dp))
            }
        }

    }

}

@Preview(showBackground = true)
@Composable
private fun SignInScreenPrev() {
    FacterTheme {
        var state by mutableStateOf(SignInState())
        SignInScreen(
            state = state,
            onAction = { state = state.copy(isSigningIn = !state.isSigningIn) }
        )
   }
}