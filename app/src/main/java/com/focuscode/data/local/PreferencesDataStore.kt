package com.focuscode.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject
import com.focuscode.domain.unlock.UnlockManager

private val Context.dataStore by preferencesDataStore("focuscode_prefs")

class PreferencesDataStore(private val context: Context) {
    private val ds = context.dataStore

    companion object {
        private val KEY_BLOCKED = preferencesKey<String>("blocked_apps_json")
        private val KEY_UNLOCKS = preferencesKey<String>("unlocks_json")
        private val KEY_SETTINGS = preferencesKey<String>("settings_json")
    }

    // Blocked apps as JSON array string
    suspend fun setBlockedApps(packages: Set<String>) {
        val arr = JSONArray()
        for (p in packages) arr.put(p)
        val json = arr.toString()
        ds.edit { prefs -> prefs[KEY_BLOCKED] = json }
    }

    suspend fun getBlockedAppsOnce(): Set<String> {
        val prefs = ds.data.first()
        val json = prefs[KEY_BLOCKED] ?: return emptySet()
        return try {
            val arr = JSONArray(json)
            val out = mutableSetOf<String>()
            for (i in 0 until arr.length()) out.add(arr.getString(i))
            out
        } catch (e: Exception) {
            emptySet()
        }
    }

    fun blockedAppsFlow(): Flow<String?> = ds.data.map { it[KEY_BLOCKED] }

    // Unlocks stored as JSON object: { "com.package": 1623, ... }
    suspend fun setUnlocks(map: Map<String, Long>) {
        val obj = JSONObject()
        for ((k, v) in map) obj.put(k, v)
        ds.edit { prefs -> prefs[KEY_UNLOCKS] = obj.toString() }
    }

    suspend fun setUnlockForPackage(pkg: String, untilMillis: Long) {
        val current = getUnlocksOnce().toMutableMap()
        current[pkg] = untilMillis
        setUnlocks(current)
    }

    suspend fun getUnlocksOnce(): Map<String, Long> {
        val prefs = ds.data.first()
        val json = prefs[KEY_UNLOCKS] ?: return emptyMap()
        return try {
            val obj = JSONObject(json)
            val out = mutableMapOf<String, Long>()
            val keys = obj.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                out[k] = obj.optLong(k, 0L)
            }
            out
        } catch (e: Exception) {
            emptyMap()
        }
    }

    suspend fun loadUnlocksInto(manager: UnlockManager) {
        val map = getUnlocksOnce()
        for ((k, v) in map) manager.unlockForPackage(k, v)
    }
}
