package com.focuscode.domain.challenge

import com.focuscode.data.model.Challenge
import com.focuscode.data.repository.ChallengeRepository
import kotlin.random.Random

class ChallengeInteractor(private val repo: ChallengeRepository) {
    private val history = mutableListOf<String>()
    private val maxHistory = 30

    fun nextChallenge(difficulty: String = "Beginner"): Challenge? {
        val pool = repo.getByDifficulty(difficulty)
        if (pool.isEmpty()) return null
        val candidates = pool.filter { !history.contains(it.id) }
        val chosen = if (candidates.isNotEmpty()) candidates.random() else pool.random()
        history.add(chosen.id)
        if (history.size > maxHistory) history.removeAt(0)
        return chosen
    }

    fun recordSolved(challenge: Challenge) {
        // already added to history on selection
    }
}
