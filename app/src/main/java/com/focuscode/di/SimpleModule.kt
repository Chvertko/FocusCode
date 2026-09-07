package com.focuscode.di

import android.content.Context
import com.focuscode.data.local.ChallengesLocalSource
import com.focuscode.data.local.PreferencesDataStore
import com.focuscode.data.repository.ChallengeRepository
import com.focuscode.domain.unblock.UnlockManager

object SimpleModule {
    lateinit var context: Context
    val unlockManager by lazy { UnlockManager() }
    val challengesSource by lazy { ChallengesLocalSource(context) }
    val challengeRepository by lazy { ChallengeRepository(challengesSource) }
    val prefs by lazy { PreferencesDataStore(context) }

    fun init(ctx: Context) {
        context = ctx.applicationContext
    }
}
