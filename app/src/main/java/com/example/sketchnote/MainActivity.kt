package com.example.sketchnote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.sketchnote.ui.theme.SketchNoteTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() { // Chuyển về ComponentActivity nếu không dùng Fragment đặc biệt
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Bật hiển thị tràn viền (Edge-to-Edge)
        enableEdgeToEdge()

        setContent {
            SketchNoteTheme {
                // Scaffold là khung chuẩn của Material3 để quản lý TopBar, BottomBar và Padding
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "SketchNote User",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name! Sẵn sàng để bắt đầu code rồi đấy.",
        modifier = modifier
    )
}