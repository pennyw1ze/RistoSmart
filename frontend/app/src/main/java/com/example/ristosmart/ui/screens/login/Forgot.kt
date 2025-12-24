package com.example.ristosmart.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun Forgot(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel()
) {
    // Used to interact with viewmodel by collecting states
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.", style = MaterialTheme.typography.headlineMedium)

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


        Button(
            onClick = { viewModel.login() },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.email.isNotBlank()
        ) {
            Text(text = "Send")
        }
    }
}
