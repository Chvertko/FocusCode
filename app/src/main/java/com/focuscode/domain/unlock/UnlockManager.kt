package com.focuscode.domain.unblock

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.ConcurrentHashMap

class UnlockManager {
    // in-memory for MVP; persisted via DataStore in fuller implementation
    private val unlocks = ConcurrentHashMap<String, Long>()

    fun unlockForPackage(pkg: String, untilMillis: Long) {
        unlocks[pkg] = untilMillis
    }

    fun isUnlocked(pkg: String, now: Long = System.currentTimeMillis()): Boolean {
        val until = unlocks[pkg] ?: return false
        return now <= until
    }

    fun remainingMillis(pkg: String, now: Long = System.currentTimeMillis()): Long {
        val until = unlocks[pkg] ?: return 0L
        return (until - now).coerceAtLeast(0L)
    }
}
