package com.focuscode.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.focuscode.di.SimpleModule
import kotlinx.coroutines.*

class AppBlockingAccessibilityService : AccessibilityService() {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var overlayManager: OverlayManager? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.i("FocusCodeService", "Accessibility service connected")
        overlayManager = OverlayManager(this.applicationContext)
        // load persisted unlocks into memory
        scope.launch {
            try {
                SimpleModule.prefs.loadUnlocksInto(SimpleModule.unlockManager)
            } catch (e: Exception) {
                Log.e("FocusCodeService", "Failed to load unlocks: ${e.message}")
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            val packageName = event.packageName?.toString() ?: return
            Log.d("FocusCodeService", "Window changed: $packageName")

            // don't react to our own package
            if (packageName == applicationContext.packageName) return

            scope.launch {
                try {
                    val blocked = SimpleModule.prefs.getBlockedAppsOnce()
                    if (!blocked.contains(packageName)) {
                        // not blocked
                        overlayManager?.hide()
                        return@launch
                    }

                    val unlocked = SimpleModule.unlockManager.isUnlocked(packageName)
                    if (unlocked) {
                        overlayManager?.hide()
                        return@launch
                    }

                    // Need to present a challenge
                    val challenge = SimpleModule.challengeRepository.getAll().firstOrNull()
                    if (challenge == null) {
                        Log.w("FocusCodeService", "No challenges available")
                        return@launch
                    }

                    // show overlay with challenge
                    overlayManager?.showChallenge(challenge) { correct ->
                        scope.launch {
                            if (correct) {
                                val until = System.currentTimeMillis() + 15 * 60 * 1000
                                SimpleModule.unlockManager.unlockForPackage(packageName, until)
                                // persist
                                SimpleModule.prefs.setUnlockForPackage(packageName, until)
                                // hide overlay
                                overlayManager?.hide()
                            } else {
                                // hide and maybe show explanation - for MVP simply hide
                                overlayManager?.hide()
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("FocusCodeService", "Error handling event: ${e.message}")
                }
            }
        }
    }

    override fun onInterrupt() {
        // no-op
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
