package com.focuscode

import com.focuscode.data.local.ChallengesLocalSource
import com.focuscode.data.repository.ChallengeRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import org.robolectric.Robolectric
import org.robolectric.RuntimeEnvironment

class ChallengeRepositoryTest {
    @Test
    fun loadChallenges() {
        val context = RuntimeEnvironment.getApplication()
        val source = ChallengesLocalSource(context)
        val repo = ChallengeRepository(source)
        val all = repo.getAll()
        // expect at least the two provided in assets
        assertEquals(true, all.size >= 2)
    }
}
