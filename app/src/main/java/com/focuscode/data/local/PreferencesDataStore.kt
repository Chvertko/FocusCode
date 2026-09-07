package com.focuscode.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("focuscode_prefs")

class PreferencesDataStore(private val context: Context) {
    private val ds = context.dataStore

    companion object {
        val KEY_BLOCKED = preferencesKey<String>("blocked_apps_json")
        val KEY_UNLOCKS = preferencesKey<String>("unlocks_json")
    }

    suspend fun setBlockedApps(json: String) {
        ds.edit { prefs -> prefs[KEY_BLOCKED] = json }
    }

    fun blockedApps(): Flow<String?> = ds.data.map { it[KEY_BLOCKED] }

    suspend fun setUnlocks(json: String) { ds.edit { it[KEY_UNLOCKS] = json } }
    fun unlocks(): Flow<String?> = ds.data.map { it[KEY_UNLOCKS] }
}
