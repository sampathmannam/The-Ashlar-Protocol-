package com.ashlarprotocol.ui.screens

import androidx.compose.animation.core.*
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ashlarprotocol.tools.Strength
import com.ashlarprotocol.tools.Strengths
import com.ashlarprotocol.tools.Readiness
import com.ashlarprotocol.ui.AshlarAppViewModel
import com.ashlarprotocol.ui.DailyWorking
import com.ashlarprotocol.ui.theme.Charcoal
import com.ashlarprotocol.ui.theme.DividerWhite
import com.ashlarprotocol.ui.theme.Gold
import com.ashlarprotocol.ui.theme.LightText
import com.ashlarprotocol.ui.theme.Silver
import com.ashlarprotocol.ui.theme.Slate
import com.ashlarprotocol.ui.theme.Surface

// "THE RECORD" — the look-back cards on the Board's lower section: the tag heatmap over your
// after-action notes, the notes themselves, and your kept Plumb records. Extracted from BoardScreen
// (same package, so call sites are unchanged) to keep that screen focused on its scaffold + upper cards.

@Composable
fun TagHeatmapCard(entries: List<com.ashlarprotocol.data.AarEntry>) {
    val allTags = listOf("#focus", "#stress", "#recovery", "#flow", "#fatigue")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Surface)
            .border(1.dp, DividerWhite.copy(alpha = 0.05f), RoundedCornerShape(32.dp))
            .padding(24.dp)
            // The 150-cell grid is decorative to a screen reader; give it one clear summary instead.
            .semantics(mergeDescendants = true) {
                contentDescription =
                    "Your patterns over the last 30 days, drawn from your journal tags."
            }
    ) {
        Text(
            text = "YOUR PATTERNS (30 DAYS)",
            style = MaterialTheme.typography.labelSmall,
            color = Gold.copy(alpha = 0.55f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (entries.isEmpty()) {
            // A blank dark grid told a new user nothing; name what will fill it.
            Text(
                text = "As you keep a few notes, a month of your patterns will chart itself here.",
                style = MaterialTheme.typography.bodySmall,
                color = Silver.copy(alpha = 0.7f),
                lineHeight = 18.sp
            )
            return@Column
        }

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
    entries: List<com.ashlarprotocol.data.AarEntry>,
    draft: String,
    onDraftChange: (String) -> Unit,
    onAddEntry: (com.ashlarprotocol.data.AarEntry) -> Unit,
    onRemoveEntry: (String) -> Unit
) {
    var selectedFilterTag by remember { mutableStateOf<String?>(null) }
    val crisisController = com.ashlarprotocol.ui.components.LocalCrisisController.current

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
                color = Gold.copy(alpha = 0.55f)
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
                        val entry = com.ashlarprotocol.data.AarEntry(
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


/**
 * The thoughts you've straightened — your own words from The Plumb, kept so you can read them
 * again. Seeing a leaning thought squared, and squared again on another day, is the point: it's
 * how you notice your own story can change (narrative agency, the strongest evidence lever —
 * Adler 2012; docs/ACTION_PLAN §1). Tap a record to re-read how you squared it. Nothing invented.
 */
@Composable
fun PlumbRecordsCard(records: List<com.ashlarprotocol.data.PlumbRecord>) {
    val now = remember { System.currentTimeMillis() }
    var expandedId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Surface)
            .border(1.dp, DividerWhite.copy(alpha = 0.05f), RoundedCornerShape(32.dp))
            .padding(24.dp)
    ) {
        Text(
            text = "THOUGHTS YOU'VE STRAIGHTENED",
            style = MaterialTheme.typography.labelSmall,
            color = Gold.copy(alpha = 0.55f),
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            records.take(6).forEach { rec ->
                val open = expandedId == rec.id
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Slate.copy(alpha = 0.2f))
                        .border(1.dp, DividerWhite.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                        .clickable { expandedId = if (open) null else rec.id }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = com.ashlarprotocol.tools.relativeDay(rec.timestamp, now).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = Silver.copy(alpha = 0.5f),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = if (open) "READING" else "RE-READ",
                            style = MaterialTheme.typography.labelSmall,
                            color = Gold.copy(alpha = 0.5f),
                            fontSize = 9.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "“${rec.thought}”",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LightText,
                        lineHeight = 22.sp
                    )
                    if (open && rec.reflection.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = DividerWhite.copy(alpha = 0.08f))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = rec.reflection,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Silver,
                            lineHeight = 24.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Your own words, kept. A thought can lean — and be straightened, and straightened again.",
            style = MaterialTheme.typography.labelSmall,
            color = Silver.copy(alpha = 0.5f),
            lineHeight = 16.sp
        )
    }
}
