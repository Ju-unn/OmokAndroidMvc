package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.myapplication.omok.controller.GameController
import com.example.myapplication.omok.view.OmokScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

// 앱 진입점: Controller를 생성하고 View(OmokScreen)와 연결 (콘솔 Main.kt의 역할)
class MainActivity : ComponentActivity() {

    private val controller = GameController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    OmokScreen(
                        controller = controller,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
