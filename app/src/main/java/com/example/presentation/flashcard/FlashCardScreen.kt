package com.example.presentation.flashcard

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.domain.vocabulary.VocabularyWord
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun FlashCardScreen(
    onBackClick: () -> Unit,
    viewModel: FlashCardViewModel = viewModel(factory = FlashCardViewModel.Factory)
) {
    val words by viewModel.words.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val pronunciationScore by viewModel.pronunciationScore.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    
    val bgColor = Color(0xFFF6F8F8)
    val primaryColor = Color(0xFF006A6A)

    var isFlipped by remember { mutableStateOf(false) }
    
    // Swipe state
    var swipeOffset by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(currentIndex) {
        isFlipped = false
        swipeOffset = 0f
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Flash Cards") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.shuffle() }) {
                        Text("Shuffle", color = primaryColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )
        },
        containerColor = bgColor
    ) { padding ->
        if (words.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No words in vocabulary yet.")
            }
        } else {
            val currentWord = words[currentIndex]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${currentIndex + 1} / ${words.size}",
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragEnd = {
                                    if (swipeOffset > 100) {
                                        viewModel.previousCard()
                                    } else if (swipeOffset < -100) {
                                        viewModel.nextCard()
                                    } else {
                                        swipeOffset = 0f
                                    }
                                }
                            ) { change, dragAmount ->
                                change.consume()
                                swipeOffset += dragAmount.x
                            }
                        }
                ) {
                    FlipCard(
                        word = currentWord,
                        isFlipped = isFlipped,
                        onClick = { isFlipped = !isFlipped },
                        onFavoriteClick = { viewModel.toggleFavorite(currentWord) },
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                translationX = swipeOffset
                                alpha = 1f - (Math.abs(swipeOffset) / 500f).coerceIn(0f, 0.5f)
                            }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Navigation controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = { viewModel.previousCard() }) {
                        Text("Previous")
                    }
                    Button(
                        onClick = { isFlipped = !isFlipped },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray, contentColor = Color.Black)
                    ) {
                        Text(if (isFlipped) "Show Word" else "Show Meaning")
                    }
                    Button(onClick = { viewModel.nextCard() }) {
                        Text("Next")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pronunciation Challenge
                if (isFlipped) {
                    PronunciationChallengeCard(
                        word = currentWord.word,
                        isAnalyzing = isAnalyzing,
                        score = pronunciationScore,
                        onEvaluate = { spoken -> viewModel.evaluatePronunciation(currentWord.word, spoken) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PronunciationChallengeCard(
    word: String,
    isAnalyzing: Boolean,
    score: String?,
    onEvaluate: (String) -> Unit
) {
    val context = LocalContext.current
    val permissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    var isRecording by remember { mutableStateOf(false) }
    var recognizedText by remember { mutableStateOf("") }
    
    val speechRecognizer = remember {
        SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {
                    isRecording = false
                }
                override fun onError(error: Int) {
                    isRecording = false
                    Toast.makeText(context, "Error recording speech", Toast.LENGTH_SHORT).show()
                }
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        recognizedText = matches[0]
                        onEvaluate(recognizedText)
                    }
                    isRecording = false
                }
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
    }

    DisposableEffect(Unit) {
        onDispose { speechRecognizer.destroy() }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F3F2))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Pronunciation Challenge", fontWeight = FontWeight.Bold, color = Color(0xFF006A6A))
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = {
                        if (!permissionState.status.isGranted) {
                            permissionState.launchPermissionRequest()
                        } else {
                            if (!isRecording) {
                                isRecording = true
                                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
                                }
                                speechRecognizer.startListening(intent)
                            } else {
                                speechRecognizer.stopListening()
                                isRecording = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRecording) Color.Red else Color(0xFF006A6A)
                    )
                ) {
                    Icon(Icons.Filled.Mic, contentDescription = "Record", modifier = Modifier.padding(end = 8.dp))
                    Text(if (isRecording) "Listening..." else "Record")
                }
            }
            if (recognizedText.isNotEmpty() && !isAnalyzing && score == null) {
                 Text("You said: $recognizedText", modifier = Modifier.padding(top = 8.dp), color = Color.DarkGray)
            }
            if (isAnalyzing) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                Text("AI is analyzing...", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
            } else if (score != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = score,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun FlipCard(
    word: VocabularyWord,
    isFlipped: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "flipRotation"
    )

    Card(
        modifier = modifier
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        if (rotation <= 90f) {
            // Front
            Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (word.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (word.isFavorite) Color.Red else Color.Gray
                    )
                }
                
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = word.word, fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(16.dp))
                    IconButton(
                        onClick = { /* TTS could go here */ },
                        modifier = Modifier.background(Color(0xFFE0F3F2), RoundedCornerShape(50))
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = "Play Audio", tint = Color(0xFF006A6A))
                    }
                }
            }
        } else {
            // Back
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f } // Counter-rotate so text isn't backwards
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = word.word, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    if (word.ipa.isNotEmpty()) {
                        Text(text = "/${word.ipa}/", fontSize = 18.sp, color = Color.DarkGray)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = word.meaning, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF006A6A), textAlign = TextAlign.Center)
                    
                    if (word.englishDefinition.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Def: ${word.englishDefinition}", fontSize = 16.sp, color = Color.DarkGray, textAlign = TextAlign.Center)
                    }
                    if (word.example.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Ex: \"${word.example}\"", fontSize = 16.sp, color = Color.Gray, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}
