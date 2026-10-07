package dev.cirimo.trosko

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.cirimo.trosko.designsystem.TroskoTheme
import dev.cirimo.trosko.navigation.TroskoNavDisplay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TroskoTheme {
                TroskoNavDisplay()
            }
        }
    }
}
