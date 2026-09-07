package com.focuscode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.focuscode.ui.blockedapps.BlockedAppsScreen
import com.focuscode.di.SimpleModule

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SimpleModule.init(this.applicationContext)
        setContent {
            App()
        }
    }
}

@Composable
fun App() {
    MaterialTheme {
        Surface {
            BlockedAppsScreen()
        }
    }
}
