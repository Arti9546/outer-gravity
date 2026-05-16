package com.hastashilpa.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    private val _otpSent = MutableStateFlow(false)
    val otpSent = _otpSent.asStateFlow()

    private var verificationId: String? = null

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        object Success : AuthState()
        data class Error(val message: String) : AuthState()
    }

    // Learning Tip: Firebase Phone Auth requires an Activity context to handle
    // reCAPTCHA and other security checks. We pass it only for the function call.
    fun sendOtp(phoneNumber: String, activity: Activity) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        // This happens if Google can verify automatically (rare but possible)
                        signInWithCredential(credential)
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        _authState.value = AuthState.Error(e.localizedMessage ?: "Verification Failed")
                    }

                    override fun onCodeSent(
                        verificationId: String,
                        token: PhoneAuthProvider.ForceResendingToken
                    ) {
                        this@AuthViewModel.verificationId = verificationId
                        _otpSent.value = true
                        _authState.value = AuthState.Idle
                    }
                })
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }

    fun verifyOtp(otp: String) {
        val id = verificationId ?: run {
            _authState.value = AuthState.Error("Session Expired. Please resend OTP.")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val credential = PhoneAuthProvider.getCredential(id, otp)
            signInWithCredential(credential)
        }
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error(task.exception?.localizedMessage ?: "Login Failed")
                }
            }
    }

    fun resetError() {
        _authState.value = AuthState.Idle
    }
}
