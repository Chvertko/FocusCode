package com.focuscode.service

import android.content.Context
import android.view.Gravity
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import com.focuscode.data.model.Challenge
import com.focuscode.data.repository.ChallengeRepository
import com.focuscode.di.SimpleModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OverlayManager(private val context: Context) {
    private val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var container: FrameLayout? = null

    fun showChallenge(challenge: Challenge, onAnswer: (correct: Boolean) -> Unit) {
        hide()
        val layoutParams = WindowManager.LayoutParams().apply {
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.MATCH_PARENT
            type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            gravity = Gravity.CENTER
        }

        val composeView = ComposeView(context).apply {
            setContent {
                // Use the ChallengeScreen composable from ui.challenge
                com.focuscode.ui.challenge.ChallengeScreen(challenge.question, challenge.answers) { idx ->
                    val correct = idx == challenge.correctAnswer
                    onAnswer(correct)
                }
            }
        }

        val frame = FrameLayout(context)
        frame.addView(composeView)
        try {
            wm.addView(frame, layoutParams)
            container = frame
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hide() {
        try {
            container?.let { wm.removeView(it) }
        } catch (e: Exception) {
            // ignore
        } finally {
            container = null
        }
    }
}
