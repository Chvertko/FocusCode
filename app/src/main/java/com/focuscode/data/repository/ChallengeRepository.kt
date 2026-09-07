package com.focuscode.data.repository

import com.focuscode.data.local.ChallengesLocalSource
import com.focuscode.data.model.Challenge

class ChallengeRepository(private val source: ChallengesLocalSource) {
    private val challenges by lazy { source.loadAll() }

    fun getAll(): List<Challenge> = challenges

    fun getByDifficulty(difficulty: String): List<Challenge> = challenges.filter { it.difficulty == difficulty }

    fun findById(id: String): Challenge? = challenges.find { it.id == id }
}
