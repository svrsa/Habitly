package com.example.habitly.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitly.HabitlyApplication
import com.example.habitly.data.local.entity.StudyEvidenceEntity
import com.example.habitly.ui.components.HabitlyCard
import com.example.habitly.ui.components.HabitlyScreen
import com.example.habitly.ui.evidence.EvidenceViewModel
import com.example.habitly.ui.evidence.EvidenceViewModelFactory
import com.example.habitly.ui.evidence.formatEvidenceTimestamp

@Composable
fun EvidenceJournalScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val application = LocalContext.current.applicationContext as HabitlyApplication
    val viewModel: EvidenceViewModel = viewModel(
        factory = EvidenceViewModelFactory(application.studyEvidenceRepository)
    )
    val uiState by viewModel.uiState.collectAsState()

    HabitlyScreen(
        title = "Journal",
        subtitle = "${uiState.evidence.size} study snapshots saved from your sessions.",
        modifier = modifier,
        scrollable = false
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back to statistics")
        }
        if (uiState.evidence.isEmpty()) {
            HabitlyCard {
                Icon(Icons.Outlined.PhotoCamera, contentDescription = null)
                Text("No study snapshots yet", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Complete a focus session and capture your notes to build this journal.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.evidence.chunked(2)) { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { evidence ->
                            EvidenceCard(
                                evidence = evidence,
                                onDelete = { viewModel.deleteEvidence(evidence) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowItems.size == 1) Box(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun EvidenceCard(
    evidence: StudyEvidenceEntity,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bitmap = remember(evidence.imagePath) {
        BitmapFactory.decodeFile(evidence.imagePath)?.asImageBitmap()
    }
    val label = remember(evidence.createdAt) {
        formatEvidenceTimestamp(evidence.createdAt)
    }

    HabitlyCard(modifier = modifier) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = "Study snapshot from $label",
                modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                contentScale = ContentScale.Crop
            )
        } else {
            MissingEvidenceImagePlaceholder(label = label)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                Text("Session #${evidence.sessionId}", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete snapshot")
            }
        }
    }
}

@Composable
private fun MissingEvidenceImagePlaceholder(label: String) {
    Surface(
        modifier = Modifier.fillMaxWidth().aspectRatio(1f),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Outlined.PhotoCamera,
                contentDescription = null
            )
            Text(
                text = "Snapshot unavailable",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
