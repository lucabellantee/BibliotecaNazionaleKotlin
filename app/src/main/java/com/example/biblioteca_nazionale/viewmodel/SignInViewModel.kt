package com.example.biblioteca_nazionale.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biblioteca_nazionale.interface_.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignInViewModel(private val authRepository: AuthRepository) : ViewModel() {

    // Registrazione con Email e password
    fun registerUser(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = authRepository.registerUser(email, password)
            // Gestisci il risultato qui (ad esempio, emetti un LiveData, un StateFlow, ecc.)
            // TODO LUCA: Vedere cosa fare qui
        }
    }



    // Registrazione con account google
    fun registerUserWithGoogle(idToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = authRepository.registerUserWithGoogle(idToken)
            // Gestisci il risultato qui (ad esempio, emetti un LiveData, un StateFlow, ecc.)
            // TODO LUCA: Vedere cosa fare qui
        }
    }
}