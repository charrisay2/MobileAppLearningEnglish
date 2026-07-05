package com.example.presentation.vocabulary

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.domain.vocabulary.VocabularyWord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    onBackClick: () -> Unit,
    viewModel: VocabularyViewModel = viewModel(factory = VocabularyViewModel.Factory)
) {
    val words by viewModel.uiState.collectAsStateWithLifecycle()
    
    var currentIndex by remember { mutableStateOf(0) }
    var userInput by remember { mutableStateOf("") }
    var showResult by remember { mutableStateOf<Boolean?>(null) } // true = correct, false = wrong
    var score by remember { mutableStateOf(0) }
    var quizFinished by remember { mutableStateOf(false) }

    val currentWord = if (words.isNotEmpty() && currentIndex < words.size) words[currentIndex] else null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vocabulary Quiz") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (words.isEmpty()) {
                Text("Add some words to your folder first!")
                return@Scaffold
            }

            if (quizFinished) {
                Text("Quiz Finished!", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Your Score: $score / ${words.size}", fontSize = 20.sp)
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = {
                    currentIndex = 0
                    score = 0
                    quizFinished = false
                    userInput = ""
                }) {
                    Text("Restart Quiz")
                }
                return@Scaffold
            }

            currentWord?.let { word ->
                Text("Translate to English:", fontSize = 16.sp, color = MaterialTheme.colorScheme.outline)
                Text(
                    text = word.meaning,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 24.dp)
                )

                OutlinedTextField(
                    value = userInput,
                    onValueChange = { 
                        userInput = it 
                        showResult = null
                    },
                    label = { Text("Enter English Word") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = showResult == false
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (showResult != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (showResult == true) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            tint = if (showResult == true) Color(0xFF2E7D32) else Color.Red
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (showResult == true) "Correct!" else "Wrong! It's \"${word.word}\"",
                            color = if (showResult == true) Color(0xFF2E7D32) else Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                Button(
                    onClick = {
                        if (showResult == null) {
                            val isCorrect = userInput.trim().equals(word.word.trim(), ignoreCase = true)
                            showResult = isCorrect
                            if (isCorrect) score++
                        } else {
                            if (currentIndex < words.size - 1) {
                                currentIndex++
                                userInput = ""
                                showResult = null
                            } else {
                                quizFinished = true
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text(if (showResult == null) "Check" else "Next")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text("Progress: ${currentIndex + 1} / ${words.size}")
            }
        }
    }
}
