package com.example.ristosmart.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ristosmart.model.User


@Composable
fun LogInScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(),
    onNavigateToForgot: () -> Unit,
    onLoginSuccess: (User) -> Unit
) {
    // Used to interact with viewmodel by collecting states
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Observe the state for navigation side-effects
    LaunchedEffect(uiState.forgotPressed) {
        if (uiState.forgotPressed) {
            onNavigateToForgot()
            viewModel.onForgotPressedConsumed() // Reset the state immediately
        }
    }

    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess && uiState.user != null) {
            onLoginSuccess(uiState.user!!)
            viewModel.onLoginSuccessConsumed()
        }
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Welcome back to RistoSmart!", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        if (uiState.error != null) {
            Text(
                text = uiState.error!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        OutlinedTextField(
            //these are all parameters of this outlinedtextfield
            //value is specified by the view model
            value = uiState.email,
            //onvaluechange specifies what to do when text is changed (event handler)
            onValueChange = { viewModel.onEmailChange(it) }, // Send event to ViewModel
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
            isError = uiState.error != null
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
            isError = uiState.error != null
        )

        Spacer(modifier = Modifier.height(16.dp))


        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = uiState.rememberMe, // Replace with state variable like `rememberMe`
                /* !API CALL MUST BE HANDLED WITH VIEWMODEL! */
                onCheckedChange = { viewModel.onRememberMeChange(it) },
                enabled = !uiState.isLoading
            )

            Text(
                text = "Remember me",
                style = MaterialTheme.typography.bodySmall
            )

        }

        TextButton(
            onClick = { viewModel.onForgotPressed(true) },
            enabled = !uiState.isLoading
        ) {
            Text(
                text = "Forgot how to login?",
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { viewModel.login() },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.email.isNotBlank() && uiState.password.isNotBlank()
            ) {
                Text(text = "Log In")
            }
        }
    }
}
