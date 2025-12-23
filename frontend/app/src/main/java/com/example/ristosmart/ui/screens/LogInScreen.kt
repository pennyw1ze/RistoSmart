package com.example.ristosmart.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun LogInScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel() ,
    onNavigateToForgot: () -> Unit
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


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Welcome back to RistoSmart!", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            //these are all parameters of this outlinedtextfield
            //value is specified by the view model
            value = uiState.email,
            //onvaluechange specifies what to do when text is changed (event handler)
            onValueChange = { viewModel.onEmailChange(it) }, // Send event to ViewModel
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = uiState.rememberMe, // Replace with state variable like `rememberMe`
                /* !API CALL MUST BE HANDLED WITH VIEWMODEL! */
                onCheckedChange = { viewModel.onRememberMeChange(it) }
            )

            Text(
                text = "Remember me",
                style = MaterialTheme.typography.bodySmall
            )

        }

        TextButton(onClick = { viewModel.onForgotPressed(true) }) {
            Text(
                text = "Forgot how to login?",
                style = MaterialTheme.typography.bodySmall
            )
        }

        Button(
            onClick = { viewModel.login() },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.email.isNotBlank()
        ) {
            Text(text = "Log In")
        }
    }
}

