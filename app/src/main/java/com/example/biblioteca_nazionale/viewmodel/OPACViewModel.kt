package com.example.biblioteca_nazionale.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biblioteca_nazionale.model.OPACResponse
import com.example.biblioteca_nazionale.utils.OPACApiClient
import kotlinx.coroutines.async

class OPACViewModel: ViewModel()  {

    private val opacApiClient: OPACApiClient = OPACApiClient()

    private val _opacLiveData = MutableLiveData<OPACResponse>()

    fun getOpacLiveData(): LiveData<OPACResponse> {
        return _opacLiveData
    }

    fun searchIdentificativoLibro(query: String): LiveData<OPACResponse> {
        viewModelScope.async {
            try {
                val response = opacApiClient.getApiService().searchIdentificativoLibro(query)

                _opacLiveData.value = response
            } catch (e: Exception) {
                Log.d("OPACViewModel", "Error exception: "+e.message)
            }
        }
        return _opacLiveData
    }
}