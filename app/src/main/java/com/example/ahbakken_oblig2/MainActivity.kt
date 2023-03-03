package com.example.ahbakken_oblig2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ahbakken_oblig2.ui.screens.AlpacaScreen
import com.example.ahbakken_oblig2.ui.screens.AlpacaViewModel
import com.example.ahbakken_oblig2.ui.theme.Ahbakken_oblig2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Ahbakken_oblig2Theme {
                AlpacaScreen()

            }
        }
    }
}