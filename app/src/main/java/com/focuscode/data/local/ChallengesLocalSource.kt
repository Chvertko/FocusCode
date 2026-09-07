package com.focuscode.data.local

import android.content.Context
import com.focuscode.data.model.Challenge
import org.json.JSONArray

class ChallengesLocalSource(private val context: Context) {
    fun loadAll(): List<Challenge> {
        val json = context.assets.open("challenges.json").bufferedReader().use { it.readText() }
        val arr = JSONArray(json)
        val out = mutableListOf<Challenge>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            val ch = Challenge(
                id = obj.getString("id"),
                category = obj.getString("category"),
                difficulty = obj.getString("difficulty"),
                type = obj.getString("type"),
                question = obj.getString("question"),
                code = if (obj.has("code")) obj.getString("code") else null,
                answers = if (obj.has("answers")) {
                    val a = obj.getJSONArray("answers"); List(a.length()) { idx -> a.getString(idx) }
                } else emptyList(),
                correctAnswer = obj.getInt("correctAnswer"),
                explanation = if (obj.has("explanation")) obj.getString("explanation") else null
            )
            out.add(ch)
        }
        return out
    }
}
