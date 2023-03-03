package com.example.ahbakken_oblig2.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ahbakken_oblig2.DataSource
import com.example.ahbakken_oblig2.ui.screens.AlpacaUiState.*
import com.example.ahbakken_oblig2.model.AlpacaParty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException


sealed interface AlpacaUiState {
    data class Success(val alpacaList: List<AlpacaParty>) : AlpacaUiState
    object Loading : AlpacaUiState
    object Error : AlpacaUiState
}

class AlpacaViewModel: ViewModel() {
    private var _uiState = MutableStateFlow<AlpacaUiState>(Loading)
    val uiState: StateFlow<AlpacaUiState> = _uiState

    init {
        getAlpacaParties()
    }

    private fun getAlpacaParties() {
        try {
            viewModelScope.launch {
                _uiState = MutableStateFlow(Success(DataSource.retrofit.getAlpacaParties()))
            }
        } catch (e: IOException) {
            _uiState = MutableStateFlow(Error)
        }
    }
}