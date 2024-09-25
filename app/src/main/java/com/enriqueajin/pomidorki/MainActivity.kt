package com.enriqueajin.pomidorki

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.enriqueajin.pomidorki.presentation.navigation.MainGraph
import com.enriqueajin.pomidorki.presentation.ui.theme.PomidorkiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PomidorkiTheme {
                MainGraph()
            }
        }
    }
}