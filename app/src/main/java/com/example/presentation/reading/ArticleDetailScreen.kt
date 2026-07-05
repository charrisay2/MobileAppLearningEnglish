package com.example.presentation.reading

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.vocabulary.VocabularyFolder
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    articleId: String,
    onBackClick: () -> Unit,
    viewModel: ArticleDetailViewModel = viewModel(factory = ArticleDetailViewModel.factory(articleId))
) {
    val article by viewModel.article.collectAsState()
    val explanation by viewModel.selectedWordExplanation.collectAsState()
    val selectedEntry by viewModel.selectedWordEntry.collectAsState()
    val isLoadingExplanation by viewModel.isLoadingExplanation.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val folders by viewModel.folders.collectAsState()

    var showFolderDialog by remember { mutableStateOf(false) }

    val bgColor = Color(0xFFF6F8F8)
    val textMain = Color(0xFF191C1C)
    val textMuted = Color(0xFF3F4948)
    val primaryColor = Color(0xFF006A6A)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Article") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = bgColor
                )
            )
        },
        containerColor = bgColor
    ) { padding ->
        if (article == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primaryColor)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Text(
                        text = article!!.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = textMain,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(article!!.source, color = textMuted, fontSize = 14.sp)
                        Text(article!!.publishDate, color = textMuted, fontSize = 14.sp)
                    }

                    Text("Tap any word for dictionary definition", color = textMuted, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
                    
                    val words = article!!.content.split(" ")
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                    ) {
                        words.forEach { word ->
                            val cleanWord = word.trim { it.isWhitespace() || !it.isLetterOrDigit() }
                            Text(
                                text = "$word ",
                                fontSize = 16.sp,
                                color = textMain,
                                modifier = Modifier.clickable {
                                    if (cleanWord.isNotBlank()) {
                                        viewModel.explainWord(cleanWord, "")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        if (isLoadingExplanation || explanation != null) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.clearExplanation() },
                containerColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    if (isLoadingExplanation) {
                        CircularProgressIndicator(color = primaryColor, modifier = Modifier.align(Alignment.CenterHorizontally))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Fetching from Dictionary API...", color = textMuted, modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Dictionary", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textMain)
                            if (selectedEntry != null) {
                                if (isSaving) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp,
                                        color = primaryColor
                                    )
                                } else {
                                    IconButton(onClick = { showFolderDialog = true }) {
                                        Icon(Icons.Default.Add, contentDescription = "Add to Vocab", tint = primaryColor)
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(explanation ?: "", color = textMain, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }
        }
    }

    if (showFolderDialog) {
        AlertDialog(
            onDismissRequest = { showFolderDialog = false },
            title = { Text("Add to Folder") },
            text = {
                Column {
                    Text("Choose a folder for '${selectedEntry?.word}':")
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                        item {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.saveToVocabulary(null)
                                        showFolderDialog = false
                                    }
                                    .padding(vertical = 12.dp),
                                color = Color.Transparent
                            ) {
                                Text("No Folder (All)", style = MaterialTheme.typography.bodyLarge)
                            }
                            HorizontalDivider()
                        }
                        items(folders) { folder ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.saveToVocabulary(folder.id)
                                        showFolderDialog = false
                                    }
                                    .padding(vertical = 12.dp),
                                color = Color.Transparent
                            ) {
                                Text(folder.name, style = MaterialTheme.typography.bodyLarge)
                            }
                            HorizontalDivider()
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showFolderDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
