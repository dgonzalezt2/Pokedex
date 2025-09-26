package com.example.pokedex

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import com.example.pokedex.ui.main.MainScreen
import dagger.hilt.android.AndroidEntryPoint
import com.example.pokedex.ui.theme.PokedexTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokedexTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    MainScreen()
                }
            }
        }
    }
}
