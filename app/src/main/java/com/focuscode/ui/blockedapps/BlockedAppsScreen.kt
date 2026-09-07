package com.focuscode.ui.blockedapps

import android.content.pm.PackageManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.focuscode.di.SimpleModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun BlockedAppsScreen() {
    val context = LocalContext.current
    val pm = context.packageManager
    val apps = remember { loadLaunchableApps(pm) }
    val coroutineScope = rememberCoroutineScope()
    var blockedSet by remember { mutableStateOf(setOf<String>()) }

    LaunchedEffect(Unit) {
        // load blocked apps from DataStore
        try {
            blockedSet = SimpleModule.prefs.getBlockedAppsOnce()
        } catch (e: Exception) {
            blockedSet = emptySet()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = {
            // placeholder for onboarding/settings
        }) {
            Text(text = "Onboarding / Settings")
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(apps) { app ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = app.label ?: app.packageName)
                    val isChecked = blockedSet.contains(app.packageName)
                    Switch(checked = isChecked, onCheckedChange = { checked ->
                        val newSet = if (checked) blockedSet + app.packageName else blockedSet - app.packageName
                        blockedSet = newSet
                        // persist
                        coroutineScope.launch(Dispatchers.IO) {
                            try {
                                SimpleModule.prefs.setBlockedApps(newSet)
                            } catch (_: Exception) { }
                        }
                    })
                }
            }
        }
    }
}
