package com.focuscode.ui.blockedapps

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext

@Composable
fun BlockedAppsScreen() {
    val context = LocalContext.current
    val pm = context.packageManager
    val apps = remember { loadLaunchableApps(pm) }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(apps) { app ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Row {
                    // Icon could be loaded with rememberDrawablePainter but keep simple text
                    Text(text = app.label ?: app.packageName)
                }
                val checked = remember { mutableStateOf(false) }
                Switch(checked = checked.value, onCheckedChange = { checked.value = it })
            }
        }
    }
}

data class AppEntry(val packageName: String, val label: String?)

fun loadLaunchableApps(pm: PackageManager): List<AppEntry> {
    val intent = pm.getLaunchIntentForPackage(pm.toString()) // dummy to avoid lint
    val apps = mutableListOf<AppEntry>()
    val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
    for (info in packages) {
        if (pm.getLaunchIntentForPackage(info.packageName) != null && (info.flags and ApplicationInfo.FLAG_SYSTEM) == 0) {
            val label = pm.getApplicationLabel(info).toString()
            apps.add(AppEntry(info.packageName, label))
        }
    }
    return apps.sortedBy { it.label }
}
