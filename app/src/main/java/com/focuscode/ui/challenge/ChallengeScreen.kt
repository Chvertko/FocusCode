package com.focuscode.ui.challenge

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ChallengeScreen(question: String, answers: List<String>, onAnswer: (Int) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
        Text(text = question)
        Spacer(modifier = Modifier.height(16.dp))
        val selected = remember { mutableStateOf(-1) }
        for ((i, ans) in answers.withIndex()) {
            Button(onClick = { selected.value = i; onAnswer(i) }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Text(text = ans)
            }
        }
    }
}
