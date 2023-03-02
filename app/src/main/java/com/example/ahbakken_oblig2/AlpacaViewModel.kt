package com.example.ahbakken_oblig2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ahbakken_oblig2.AlpacaUiState.*
import com.example.ahbakken_oblig2.model.AlpacaParty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException


sealed interface AlpacaUiState {
    data class Success(val photos: List<AlpacaParty>) : AlpacaUiState
    object Loading : AlpacaUiState
    object Error : AlpacaUiState
}

class AlpacaViewModel: ViewModel() {
    private var _uiState = MutableStateFlow<AlpacaUiState>(Loading)
    val uiState: StateFlow<AlpacaUiState> = _uiState

    fun getAlpacaParties() {
        try {
            viewModelScope.launch {
                _uiState = MutableStateFlow(Success(DataSource.retrofit.getAlpacaParties()))
            }
        } catch (e: IOException) {
            _uiState = MutableStateFlow(Error)
        }

    }
}