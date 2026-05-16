package com.hastashilpa.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import android.app.Activity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hastashilpa.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var phoneNumber by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    val context = LocalContext.current // Get context to find Activity
    
    // Collecting state from ViewModel
    val authState by viewModel.authState.collectAsState()
    val otpSent by viewModel.otpSent.collectAsState()

    // Handle navigation when login is successful
    LaunchedEffect(authState) {
        if (authState is AuthViewModel.AuthState.Success) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Hasta-Shilpa", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Artisan Login", style = MaterialTheme.typography.titleMedium)
        
        Spacer(modifier = Modifier.height(32.dp))

        if (authState is AuthViewModel.AuthState.Loading) {
            CircularProgressIndicator()
        } else {
            if (!otpSent) {
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    isError = authState is AuthViewModel.AuthState.Error
                )
                if (authState is AuthViewModel.AuthState.Error) {
                    Text(
                        text = (authState as AuthViewModel.AuthState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { 
                        val activity = context as? Activity
                        if (activity != null) {
                            viewModel.sendOtp(phoneNumber, activity) 
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Send OTP")
                }
            } else {
                Text("OTP sent to $phoneNumber", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = otp,
                    onValueChange = { otp = it },
                    label = { Text("Enter OTP") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    isError = authState is AuthViewModel.AuthState.Error
                )
                if (authState is AuthViewModel.AuthState.Error) {
                    Text(
                        text = (authState as AuthViewModel.AuthState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.verifyOtp(otp) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Login")
                }
            }
        }
    }
}
