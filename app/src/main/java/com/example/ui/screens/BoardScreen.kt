package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AshlarAppViewModel
import com.example.ui.components.WisdomPillar
import com.example.ui.theme.Charcoal
import com.example.ui.theme.DividerWhite
import com.example.ui.theme.Gold
import com.example.ui.theme.LightText
import com.example.ui.theme.Silver
import com.example.ui.theme.Slate
import com.example.ui.theme.Surface

@Composable
fun BoardScreen(viewModel: AshlarAppViewModel) {
    val weeklyVolume by viewModel.weeklyVolume.collectAsState()

    androidx.compose.foundation.lazy.LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // The Path — the home's center of gravity. One hero: the rough→perfect ashlar (driven by
        // real journey progress, not a manual slider) fused with the current degree and what's next.
        item {
            val streak by viewModel.briefingStreak.collectAsState()
            val entries by viewModel.aarEntries.collectAsState()
            val plumb by viewModel.plumbSessions.collectAsState()
            val gauge by viewModel.gaugeDaysComplete.collectAsState()
            val recall by viewModel.recallSessions.collectAsState()
            val score = com.example.tools.Degrees.score(
                com.example.tools.WorkStats(streak, entries.size, plumb, gauge, recall)
            )
            val degree = com.example.tools.Degrees.current(score)
            val next = com.example.tools.Degrees.next(degree)
            TracingBoardVisual(
                progress = com.example.tools.Degrees.journeyProgress(score),
                degreeName = degree.display,
                nextName = next?.display,
                progressToNext = com.example.tools.Degrees.progressToNext(score)
            )
        }

        // Cognitive Briefing
        item {
            val dailyBriefing by viewModel.dailyBriefing.collectAsState()
            val isFetchingBriefing by viewModel.isFetchingBriefing.collectAsState()
            val briefingStreak by viewModel.briefingStreak.collectAsState()
            CognitiveBriefingCard(
                briefing = dailyBriefing,
                isFetching = isFetchingBriefing,
                streak = briefingStreak,
                onRefresh = { viewModel.fetchDailyBriefing() }
            )
        }

        // Resilience Chart
        item {
            val briefingStreak by viewModel.briefingStreak.collectAsState()
            com.example.ui.components.ResilienceChartCard(currentStreak = briefingStreak)
        }

        // After Action Report
        item {
            val aarEntries by viewModel.aarEntries.collectAsState()
            TagHeatmapCard(entries = aarEntries)
        }
        
        item {
            val aarEntries by viewModel.aarEntries.collectAsState()
            val aarDraft by viewModel.aarDraft.collectAsState()
            AarNotesCard(
                entries = aarEntries,
                draft = aarDraft,
                onDraftChange = { viewModel.setAarDraft(it) },
                onAddEntry = { viewModel.addAarEntry(it) },
                onRemoveEntry = { viewModel.removeAarEntry(it) }
            )
        }

        // Wisdom Pillar
        item {
            WisdomPillar()
        }

        // Pillars row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PillarCard(
                    title = "Strength",
                    value = "${weeklyVolume}/25 KM",
                    modifier = Modifier.weight(1f)
                )
                PillarCard(
                    title = "Beauty",
                    value = "Guitar Practice",
                    modifier = Modifier.weight(1f)
                )
            }
        }

    }
}

@Composable
fun TracingBoardVisual(
    progress: Float,
    degreeName: String,
    nextName: String?,
    progressToNext: Float
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "progress"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Surface)
            .border(1.dp, DividerWhite.copy(alpha = 0.05f), RoundedCornerShape(32.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "THE PATH",
            style = MaterialTheme.typography.labelSmall,
            color = Gold.copy(alpha = 0.3f),
            letterSpacing = 2.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(120.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val roughness = (1f - animatedProgress) * 40f
                val strokeWidth = if (animatedProgress > 0.9f) 3f else 6f

                val pathEffect = if (animatedProgress > 0.9f) {
                    null
                } else {
                    PathEffect.dashPathEffect(floatArrayOf(roughness, roughness / 2f), 0f)
                }

                // Inner Geometry
                drawLine(
                    color = Gold.copy(alpha = 0.3f),
                    start = Offset(size.width * 0.5f, size.height * 0.1f),
                    end = Offset(size.width * 0.5f, size.height * 0.9f),
                    strokeWidth = 1f
                )
                drawLine(
                    color = Gold.copy(alpha = 0.3f),
                    start = Offset(size.width * 0.1f, size.height * 0.5f),
                    end = Offset(size.width * 0.9f, size.height * 0.5f),
                    strokeWidth = 1f
                )

                // The 'Rough' edges
                drawRect(
                    color = Gold,
                    topLeft = Offset(size.width * 0.1f, size.height * 0.1f),
                    size = Size(size.width * 0.8f, size.height * 0.8f),
                    style = Stroke(width = strokeWidth, pathEffect = pathEffect)
                )

                // The 'Refining' core
                if (animatedProgress > 0.5f) {
                    drawRect(
                        color = Gold.copy(alpha = 0.05f),
                        topLeft = Offset(size.width * 0.2f, size.height * 0.2f),
                        size = Size(size.width * 0.6f, size.height * 0.6f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (animatedProgress >= 0.99f) "PERFECT ASHLAR ACHIEVED" else "THE ROUGH ASHLAR, IN THE WORKING",
            style = MaterialTheme.typography.labelSmall,
            color = Gold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = degreeName.uppercase(),
            style = MaterialTheme.typography.titleLarge,
            color = LightText,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Progress toward the next degree
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Slate.copy(alpha = 0.4f))
        ) {
            if (progressToNext > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressToNext)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Gold)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (nextName != null) "${(progressToNext * 100).toInt()}% TOWARD ${nextName.uppercase()}" else "MASTER OF THE CRAFT",
            style = MaterialTheme.typography.labelSmall,
            color = Silver.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PillarCard(title: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Surface)
            .border(1.dp, DividerWhite.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = Gold,
            fontSize = 9.dp.value.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            color = Silver
        )
    }
}

@Composable
fun CognitiveBriefingCard(
    briefing: String?,
    isFetching: Boolean,
    streak: Int,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Surface)
            .border(1.dp, DividerWhite.copy(alpha = 0.05f), RoundedCornerShape(32.dp))
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "A WORD FOR TODAY",
                style = MaterialTheme.typography.labelSmall,
                color = Gold.copy(alpha = 0.3f)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (streak > 0) {
                    Text(
                        text = "STREAK: $streak",
                        style = MaterialTheme.typography.labelSmall,
                        color = Gold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
                TextButton(
                    onClick = onRefresh,
                    enabled = !isFetching,
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.height(24.dp)
                ) {
                    Text(
                        text = "SYNC",
                        style = MaterialTheme.typography.labelSmall,
                        color = Gold,
                        fontSize = 10.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (isFetching) {
            CircularProgressIndicator(
                color = Gold,
                modifier = Modifier.size(24.dp).align(Alignment.CenterHorizontally),
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = briefing ?: "No word yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = Silver,
                textAlign = TextAlign.Start,
                lineHeight = 24.sp
            )
        }
    }
}

@Composable
fun TagHeatmapCard(entries: List<com.example.data.AarEntry>) {
    val allTags = listOf("#focus", "#stress", "#recovery", "#flow", "#fatigue")
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Surface)
            .border(1.dp, DividerWhite.copy(alpha = 0.05f), RoundedCornerShape(32.dp))
            .padding(24.dp)
    ) {
        Text(
            text = "YOUR PATTERNS (30 DAYS)",
            style = MaterialTheme.typography.labelSmall,
            color = Gold.copy(alpha = 0.3f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        val dayInMillis = 24 * 60 * 60 * 1000L
        val now = System.currentTimeMillis()
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = now
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        val todayStart = calendar.timeInMillis
        
        Row(modifier = Modifier.fillMaxWidth()) {
            // Y-axis: Tags
            Column(
                modifier = Modifier.padding(end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp)) // padding for x-axis labels
                allTags.forEach { tag ->
                    Text(
                        text = tag.removePrefix("#").uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Silver.copy(alpha = 0.7f),
                        fontSize = 9.sp,
                        modifier = Modifier.height(24.dp).wrapContentHeight(Alignment.CenterVertically)
                    )
                }
            }
            
            // X-axis: 30 days
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f),
                reverseLayout = true // to show most recent days on the right
            ) {
                // days from 0 (today) to 29 (30 days ago)
                items(30) { dayOffset ->
                    val dayStart = todayStart - dayOffset * dayInMillis
                    val dayEnd = dayStart + dayInMillis
                    
                    val dayEntries = entries.filter { it.timestamp in dayStart until dayEnd }
                    
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val cal = java.util.Calendar.getInstance().apply { timeInMillis = dayStart }
                        Text(
                            text = if (dayOffset % 7 == 0) "${cal.get(java.util.Calendar.DAY_OF_MONTH)}" else "",
                            style = MaterialTheme.typography.labelSmall,
                            color = Silver.copy(alpha = 0.3f),
                            modifier = Modifier.height(16.dp),
                            fontSize = 8.sp
                        )
                        
                        allTags.forEach { tag ->
                            val count = dayEntries.count { it.tags.contains(tag) }
                            val intensity = minOf(count / 3f, 1f) // max intensity at 3 mentions
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (count > 0) Gold.copy(alpha = 0.2f + 0.8f * intensity)
                                        else Charcoal.copy(alpha = 0.3f)
                                    )
                                    .border(
                                        0.5.dp, 
                                        if (count > 0) Gold.copy(alpha = 0.5f) else DividerWhite.copy(alpha = 0.05f),
                                        RoundedCornerShape(4.dp)
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AarNotesCard(
    entries: List<com.example.data.AarEntry>,
    draft: String,
    onDraftChange: (String) -> Unit,
    onAddEntry: (com.example.data.AarEntry) -> Unit,
    onRemoveEntry: (String) -> Unit
) {
    var selectedFilterTag by remember { mutableStateOf<String?>(null) }
    val crisisController = com.example.ui.components.LocalCrisisController.current

    val allTags = listOf("#focus", "#stress", "#recovery", "#flow", "#fatigue")

    // Auto-detect tags in draft text
    val currentTags = allTags.filter { draft.contains(it) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Surface)
            .border(1.dp, DividerWhite.copy(alpha = 0.05f), RoundedCornerShape(32.dp))
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "THE DAY'S RECORD",
                style = MaterialTheme.typography.labelSmall,
                color = Gold.copy(alpha = 0.3f)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Input Area
        TextField(
            value = draft,
            onValueChange = { onDraftChange(it); crisisController.scan(it) },
            placeholder = { 
                Text(
                    text = "Record daily observations, friction points, and victories...",
                    color = Silver.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.bodyMedium
                ) 
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Slate.copy(alpha = 0.15f),
                unfocusedContainerColor = Slate.copy(alpha = 0.15f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Silver,
                unfocusedTextColor = Silver,
                cursorColor = Gold
            ),
            textStyle = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, DividerWhite.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(allTags) { tag ->
                    val isSelected = draft.contains(tag)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Gold.copy(alpha = 0.2f) else Charcoal)
                            .border(1.dp, if (isSelected) Gold.copy(alpha = 0.5f) else DividerWhite.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .clickable {
                                if (isSelected) {
                                    onDraftChange(draft.replace(tag, "").trim())
                                } else {
                                    onDraftChange(if (draft.isEmpty()) tag else "$draft $tag")
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) Gold else Silver
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Button(
                onClick = {
                    if (draft.isNotBlank()) {
                        // Final safety check on the committed entry.
                        crisisController.scan(draft)
                        val entry = com.example.data.AarEntry(
                            id = java.util.UUID.randomUUID().toString(),
                            text = draft,
                            timestamp = System.currentTimeMillis(),
                            tags = currentTags
                        )
                        onAddEntry(entry)
                        onDraftChange("")
                        // Draft cleared; arm the detector for the next entry.
                        crisisController.resetAuto()
                    }
                },
                enabled = draft.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    contentColor = Surface,
                    disabledContainerColor = Charcoal,
                    disabledContentColor = Silver.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("SAVE", style = MaterialTheme.typography.labelSmall)
            }
        }
        
        if (entries.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            DividerWhite.copy(alpha = 0.1f).let {
                HorizontalDivider(color = it, thickness = 1.dp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            // Filter
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FILTER:",
                    style = MaterialTheme.typography.labelSmall,
                    color = Silver,
                    modifier = Modifier.padding(end = 8.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item {
                        val isSelected = selectedFilterTag == null
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Gold.copy(alpha = 0.2f) else Color.Transparent)
                                .border(1.dp, if (isSelected) Gold.copy(alpha = 0.5f) else DividerWhite.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                .clickable { selectedFilterTag = null }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "All",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) Gold else Silver
                            )
                        }
                    }
                    items(allTags) { tag ->
                        val isSelected = selectedFilterTag == tag
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Gold.copy(alpha = 0.2f) else Color.Transparent)
                                .border(1.dp, if (isSelected) Gold.copy(alpha = 0.5f) else DividerWhite.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                .clickable { selectedFilterTag = tag }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) Gold else Silver
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Entries
            val filteredEntries = if (selectedFilterTag == null) entries else entries.filter { it.text.contains(selectedFilterTag!!) }
            
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                filteredEntries.take(5).forEach { entry ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Slate.copy(alpha = 0.2f))
                            .border(1.dp, DividerWhite.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            val dateFormat = java.text.SimpleDateFormat("MMM dd, HH:mm", java.util.Locale.getDefault())
                            Text(
                                text = dateFormat.format(java.util.Date(entry.timestamp)),
                                style = MaterialTheme.typography.labelSmall,
                                color = Silver.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = entry.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Silver
                            )
                        }
                    }
                }
            }
        }
    }
}

