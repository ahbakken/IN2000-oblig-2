package com.example.ahbakken_oblig2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.ahbakken_oblig2.ui.screens.AlpacaScreen
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