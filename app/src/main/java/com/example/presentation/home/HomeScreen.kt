package com.example.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.presentation.vocabulary.VocabularyViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    onFlashCardClick: () -> Unit = {},
    vocabularyViewModel: VocabularyViewModel = viewModel(factory = VocabularyViewModel.Factory)
) {
    val words by vocabularyViewModel.uiState.collectAsState()
    val currentUser = remember { FirebaseAuth.getInstance().currentUser }
    val userName = currentUser?.displayName ?: "Learner"
    
    // Daily Goal State
    var showGoalDialog by remember { mutableStateOf(false) }
    var targetMinutes by remember { mutableStateOf(20) }
    var currentSeconds by remember { mutableStateOf(0) }
    var isTimerRunning by remember { mutableStateOf(false) }

    val bgColor = Color(0xFFF6F8F8)
    val textMain = Color(0xFF191C1C)
    val textMuted = Color(0xFF3F4948)

    val wordCount = words.size
    val reviewCount = words.filter { it.progress < 100 }.size

    // Timer Logic
    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning) {
            delay(1000)
            currentSeconds++
            if (currentSeconds >= targetMinutes * 60) {
                isTimerRunning = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Header Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp, top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "WELCOME BACK",
                    color = textMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = userName,
                    color = Color(0xFF002020),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                modifier = Modifier
                    .background(Color.White, CircleShape)
                    .border(1.dp, Color(0xFFDCE5E4), CircleShape)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🔥", fontSize = 18.sp)
                Text("12", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textMain)
            }
        }

        // Hero Recommendation Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(24.dp))
        ) {
            AsyncImage(
                model = "https://images.unsplash.com/photo-1543165796-5426273eaab3?q=80&w=2070&auto=format&fit=crop",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text("Today's Spotlight", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                Text("Idioms for Daily Life", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* TODO */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006A6A)),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Learn Now", fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bento Grid
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Row for Vocab and Goal Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Vocab Card
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color.White, RoundedCornerShape(24.dp))
                        .border(1.dp, Color(0xFFDCE5E4), RoundedCornerShape(24.dp))
                        .clickable { onFlashCardClick() }
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFFFDAD6), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🔤", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Vocabulary", fontWeight = FontWeight.Bold, color = textMain, fontSize = 16.sp)
                        Text("$reviewCount to review", fontSize = 12.sp, color = textMuted)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF006A6A), RoundedCornerShape(12.dp))
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Start Drill", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Daily Goal Card
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFF1E0FF), RoundedCornerShape(24.dp))
                        .border(1.dp, Color(0xFFD7BFFF), RoundedCornerShape(24.dp))
                        .clickable { showGoalDialog = true }
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    val progress = (currentSeconds.toFloat() / (targetMinutes * 60)).coerceIn(0f, 1f)
                    Box(
                        modifier = Modifier.size(48.dp).align(Alignment.CenterHorizontally),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = progress,
                            modifier = Modifier.fillMaxSize(),
                            color = Color(0xFF6750A4),
                            trackColor = Color.White.copy(alpha = 0.5f),
                            strokeWidth = 6.dp
                        )
                        Text("${(progress * 100).toInt()}%", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("DAILY GOAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6750A4), modifier = Modifier.align(Alignment.CenterHorizontally))
                    Text("${currentSeconds / 60} / $targetMinutes m", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textMain, modifier = Modifier.align(Alignment.CenterHorizontally))
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        if (isTimerRunning) "Stop" else "Start",
                        color = Color(0xFF6750A4),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable { isTimerRunning = !isTimerRunning }
                    )
                }
            }

            // Quick Actions / Categories
            Text("Discover Topics", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(3) { index ->
                    val (title, icon, color) = when(index) {
                        0 -> Triple("Grammar", Icons.Default.Translate, Color(0xFFE8F5E9))
                        1 -> Triple("Reading", Icons.Default.AutoStories, Color(0xFFE3F2FD))
                        else -> Triple("Rankings", Icons.Default.EmojiEvents, Color(0xFFFFF3E0))
                    }
                    Card(
                        modifier = Modifier.width(140.dp),
                        colors = CardDefaults.cardColors(containerColor = color),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(title, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // News Card (Restyled)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFDE293), RoundedCornerShape(24.dp))
                    .border(1.dp, Color(0xFFF0D170), RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Text("NEWS FEED", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF241E00))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Practice reading with world news in real-time.",
                        fontSize = 13.sp,
                        color = Color(0xFF241E00).copy(alpha = 0.8f)
                    )
                }
            }
        }
    }

    if (showGoalDialog) {
        AlertDialog(
            onDismissRequest = { showGoalDialog = false },
            title = { Text("Set Learning Goal") },
            text = {
                Column {
                    Text("Select target minutes per day:")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Slider(
                            value = targetMinutes.toFloat(),
                            onValueChange = { targetMinutes = it.toInt() },
                            valueRange = 5f..120f,
                            steps = 23,
                            modifier = Modifier.weight(1f)
                        )
                        Text("$targetMinutes m", modifier = Modifier.padding(start = 8.dp))
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showGoalDialog = false }) {
                    Text("Save")
                }
            }
        )
    }
}
