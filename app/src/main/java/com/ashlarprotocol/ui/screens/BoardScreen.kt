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

@Composable
fun BoardScreen(viewModel: AshlarAppViewModel) {
    val intention by viewModel.intention.collectAsState()
    val plumbRecords by viewModel.plumbRecords.collectAsState()
    val todayWorking by viewModel.todayWorking.collectAsState()
    val whoFiveResults by viewModel.whoFiveResults.collectAsState()
    val practices by viewModel.practices.collectAsState()
    // The WHO-5 wellbeing check is offered at baseline, then gently ~every two weeks (T3.1).
    val whoFiveDue = remember(whoFiveResults) {
        com.ashlarprotocol.tools.WhoFive.isDue(whoFiveResults.firstOrNull()?.timestamp, System.currentTimeMillis())
    }
    // Anti-harm (T3.2): if it's late night and today's work is already done, gently answer a
    // compulsive pattern with rest — on-device care, no tracking (reuses todayWorking + the clock).
    val restNudge = remember(todayWorking) {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        com.ashlarprotocol.tools.AntiHarm.restNudge(hour, didTodaysWork = todayWorking != null)
    }
    // "What the Stone Remembers" (the mirror) — pull-only; expands to reflect your own data back.
    var showRemembers by remember { mutableStateOf(false) }
    androidx.compose.foundation.lazy.LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // The remembered greeting (T1.9): a warm line composed from what the app actually remembers —
        // the time, the days you've tended, the intention you set. On-device, nothing generated/uploaded.
        item {
            val daysTended by viewModel.briefingStreak.collectAsState()
            val hour = remember { java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY) }
            Text(
                text = com.ashlarprotocol.tools.Greeting.greeting(
                    com.ashlarprotocol.tools.Greeting.Context(hourOfDay = hour, daysTended = daysTended, intention = intention)
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = LightText.copy(alpha = 0.85f),
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        // Anti-harm (T3.2): a caring "it's late, rest" when the pattern looks compulsive rather than
        // helpful — surfaced first, above the gamified UI, so rest wins the moment.
        if (restNudge != null) {
            item { RestNudgeCard(message = restNudge) }
        }

        // A warm welcome back after a lapse — surfaced only when returning, never loss-framed
        // (see tools/KindStreak.comebackMessage). Dismissed on tap.
        item {
            val comeback by viewModel.streakComeback.collectAsState()
            comeback?.let { message ->
                ComebackCard(message = message, onDismiss = { viewModel.clearStreakComeback() })
            }
        }

        // The Path — the home's center of gravity. The rough→perfect ashlar now smooths from real
        // tending (the kind streak's cumulative daysTended), not the degree score: it advances with
        // the days you show up and never regresses on a miss (SPEC P0.1; KindStreak feeds the stone).
        item {
            // `briefingStreak` is the kind-streak's cumulative daysTended — the stone's true fuel.
            val daysTended by viewModel.briefingStreak.collectAsState()
            val entries by viewModel.aarEntries.collectAsState()
            val plumb by viewModel.plumbSessions.collectAsState()
            val gauge by viewModel.gaugeDaysComplete.collectAsState()
            val recall by viewModel.recallSessions.collectAsState()
            // A real completed action bumps this — the stone catches the light on it (T2.3).
            val actionPulse by viewModel.actionPulse.collectAsState()
            // The stone's six faces are the six VIA virtues (tools/StoneFacets); the ones you've claimed
            // as signature strengths refine a little faster. Empty signature => an even, coherent stone.
            val signature by viewModel.signatureStrengths.collectAsState()
            // `score` = total work done; it still refines the stone's six virtue facets (below).
            val score = com.ashlarprotocol.tools.Degrees.score(
                com.ashlarprotocol.tools.WorkStats(daysTended, entries.size, plumb, gauge, recall)
            )
            // The degree names the layer beneath the stone — now the Temple's milestone (Phase-3).
            val degree by viewModel.currentDegree.collectAsState()
            // The grace reserve, made visible (F3) — shown only once tending has begun.
            val grace by viewModel.graceRemaining.collectAsState()
            TracingBoardVisual(
                progress = com.ashlarprotocol.tools.KindStreak.stoneProgress(daysTended),
                degreeName = degree.display,
                daysTended = daysTended,
                pulse = actionPulse,
                facets = com.ashlarprotocol.tools.StoneFacets.orderedFacets(signature, score),
                graceLabel = if (daysTended > 0) com.ashlarprotocol.tools.KindStreak.graceLabel(grace) else null
            )
        }

        // (Phase-3 consolidation) The standalone "work beneath the stone" degree card is retired: the
        // degree is now the Temple's milestone, and the Temple card already reads "Course N of 50 ·
        // <Degree>". One progression surface, not two stacked ones.

        // What the Stone Remembers (the mirror) — pull-only: tap to expand and see your own data
        // reflected back (facts + hedged noticings). Computed on demand; a snapshot at open.
        item {
            // reflect() reads the DataStore (suspend), so compute it off the composition thread and
            // re-run whenever the card opens; collapsed → empty. Snapshot on open, pull-only.
            val reflections by produceState(
                initialValue = emptyList<com.ashlarprotocol.tools.Reflection>(), showRemembers
            ) { value = if (showRemembers) viewModel.reflect() else emptyList() }
            com.ashlarprotocol.ui.components.StoneRemembersCard(
                expanded = showRemembers,
                reflections = reflections,
                onToggle = { showRemembers = !showRemembers }
            )
        }

        // The day's work — research-designed challenges that pay wages (The Temple).
        item {
            val completions by viewModel.challengeCompletions.collectAsState()
            val now = remember { System.currentTimeMillis() }
            val today = remember { com.ashlarprotocol.tools.KindStreak.epochDay(now, java.util.TimeZone.getDefault().getOffset(now)) }
            val week = java.lang.Math.floorDiv(today, 7L)
            val daily = remember(today) { com.ashlarprotocol.tools.Challenges.dailyMenu(today) }
            val weekly = remember(week) { com.ashlarprotocol.tools.Challenges.weeklyChallenge(today) }
            val doneIds = completions
                .filter { (it.cadence == "WEEKLY" && it.periodKey == week) || (it.cadence == "DAILY" && it.periodKey == today) }
                .map { it.challengeId }.toSet()
            com.ashlarprotocol.ui.components.ChallengesCard(
                daily = daily, weekly = weekly, doneIds = doneIds,
                onComplete = { viewModel.completeChallenge(it) }
            )
        }

        // The Temple — lay wages to raise the next course, and adorn it with a finish.
        item {
            val raised by viewModel.coursesRaised.collectAsState()
            val balance by viewModel.wageBalance.collectAsState()
            val selectedFinish by viewModel.selectedFinish.collectAsState()
            val ownedFinishes by viewModel.unlockedFinishes.collectAsState()
            val next = com.ashlarprotocol.tools.Temple.nextCourse(raised)
            com.ashlarprotocol.ui.components.TempleCard(
                coursesRaised = raised, nextCourse = next, balance = balance,
                canRaise = next != null && balance >= next.cost,
                onRaise = { viewModel.raiseCourse() },
                selectedFinishId = selectedFinish,
                ownedFinishIds = ownedFinishes,
                onSelectFinish = { viewModel.selectFinish(it) },
                onBuyFinish = { viewModel.unlockFinish(it) }
            )
        }

        // Your intention — the app remembers what you said you're working toward (the re-authoring
        // engine; docs/ACTION_PLAN §1A). Shown only once it's been set at initiation.
        if (intention.isNotBlank()) {
            item { IntentionCard(intention = intention) }
        }

        // A word for today — bundled and local; it appears instantly (no network, no spinner).
        item {
            val dailyBriefing by viewModel.dailyBriefing.collectAsState()
            val briefingStreak by viewModel.briefingStreak.collectAsState()
            CognitiveBriefingCard(
                briefing = dailyBriefing,
                streak = briefingStreak,
                onRefresh = { viewModel.fetchDailyBriefing() }
            )
        }

        // The daily Working — the mood-adaptive practice. Check in with how you're arriving and the
        // day's ask scales: a floor task on a hard day is the point (Behavioral Activation), not a
        // compromise. Pays more acknowledgment on low days, never "you're behind".
        item {
            val working by viewModel.todayWorking.collectAsState()
            val dial by viewModel.dial.collectAsState()
            WorkingCard(
                working = working,
                dial = dial,
                onCheckIn = { viewModel.checkInReadiness(it) },
                onNudge = { viewModel.nudgeDial(it) }
            )
        }

        // (Phase-3 IA) A quiet divider: above is today's work (the Stone, the Day's Work, the Temple);
        // below is the record you've kept — strengths, counts, thoughts, rhythms, practices. Grouping
        // the sprawl into two legible halves without moving anything.
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Column(modifier = Modifier.fillMaxWidth().height(1.dp).background(DividerWhite.copy(alpha = 0.06f))) {}
                Spacer(Modifier.height(16.dp))
                Text(
                    "THE RECORD",
                    style = MaterialTheme.typography.labelSmall,
                    color = Gold.copy(alpha = 0.55f),
                    letterSpacing = 2.sp
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "What you've kept and noticed — yours to read back, never a scoreboard.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Silver.copy(alpha = 0.7f),
                    lineHeight = 18.sp
                )
            }
        }

        // Strengths — the intrinsic progression (identity, not points): name your VIA signature
        // strengths, then each day get one to use "in a new way" (Seligman 2005). On-device.
        item {
            val signature by viewModel.signatureStrengths.collectAsState()
            val todayPrompt by viewModel.todayStrengthPrompt.collectAsState()
            StrengthsCard(
                signature = signature,
                todayPrompt = todayPrompt,
                onToggle = { s ->
                    viewModel.setSignatureStrengths(
                        if (signature.contains(s)) signature - s else signature + s
                    )
                }
            )
        }

        // The work so far — an honest count of what you have actually done. No scores, no
        // predictions, no invented trends (the old random/hardcoded charts were removed).
        item {
            val streak by viewModel.briefingStreak.collectAsState()
            val entries by viewModel.aarEntries.collectAsState()
            val plumb by viewModel.plumbSessions.collectAsState()
            val gauge by viewModel.gaugeDaysComplete.collectAsState()
            val recall by viewModel.recallSessions.collectAsState()
            WorkSoFarCard(
                streak = streak,
                journalEntries = entries.size,
                thoughtRecords = plumb,
                gaugeDays = gauge,
                recalls = recall
            )
        }

        // Thoughts you've straightened — your own words from The Plumb, kept so you can read them
        // again and watch a leaning thought get squared (narrative agency; docs/ACTION_PLAN §1).
        if (plumbRecords.isNotEmpty()) {
            item { PlumbRecordsCard(records = plumbRecords) }
        }

        // The WHO-5 wellbeing check — the plan's primary outcome metric (T3.1). Offered at baseline
        // then gently every ~2 weeks; always skippable. Stored on-device only.
        if (whoFiveDue) {
            item { WhoFiveCard(onComplete = { viewModel.addWhoFiveResult(it) }) }
        }

        // Automaticity (F4): a gentle, ~weekly "is it becoming automatic?" — the honest progress signal
        // (a habit is context-cued automaticity, not a streak count). Noticing, not grading; skippable.
        item {
            val autoDay by viewModel.automaticityDay.collectAsState()
            val tendedForAuto by viewModel.briefingStreak.collectAsState()
            val today = com.ashlarprotocol.tools.KindStreak.epochDay(
                System.currentTimeMillis(),
                java.util.TimeZone.getDefault().getOffset(System.currentTimeMillis())
            ).toInt()
            if (tendedForAuto > 0 && com.ashlarprotocol.tools.Automaticity.isDue(autoDay.toLong(), today.toLong())) {
                AutomaticityCard(onRecord = { viewModel.recordAutomaticity(it) })
            }
        }

        // The rhythm anchor (F6): a steady rise + wind-down. Regularity (not earliness/duration) is
        // linked to steadier days. A gentle set-your-own anchor — never an alarm, never a bad-night scold.
        item {
            val rhythm by viewModel.rhythm.collectAsState()
            RhythmCard(current = rhythm, onSet = { wake, wind -> viewModel.setRhythm(wake, wind) })
        }

        // Your practices — self-authored, anchored, approach-framed (T1.4). The autonomy heart of the
        // Working: write your own "After [anchor], I will [action]" and it's yours to keep.
        item {
            PracticesCard(
                practices = practices,
                intention = intention,
                onSave = { anchor, action, reminder, cueKind -> viewModel.addPractice(anchor, action, reminder, cueKind) },
                onRemove = { viewModel.removePractice(it) }
            )
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

        // The graceful exit — once you've done today's Working, a gentle "enough for today" that
        // gives permission to stop instead of pulling you back in (SPEC P0.7 / T2.5). No FOMO.
        if (todayWorking != null) {
            item { GracefulExitCard() }
        }

    }
}

/**
 * Shown once the day's Working is done: explicit permission to stop. The opposite of a dark pattern —
 * no streak threat, no FOMO, no interstitial in the way of closing the app. Rest as part of the work.
 */
@Composable
fun GracefulExitCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Surface)
            .border(1.dp, DividerWhite.copy(alpha = 0.05f), RoundedCornerShape(32.dp))
            .padding(24.dp)
    ) {
        Text(
            text = "ENOUGH FOR TODAY",
            style = MaterialTheme.typography.labelSmall,
            color = Gold.copy(alpha = 0.4f),
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        com.ashlarprotocol.tools.GracefulExit.LINES.forEachIndexed { i, line ->
            if (i > 0) Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = line,
                style = MaterialTheme.typography.bodyMedium,
                color = if (i == 0) LightText else Silver,
                lineHeight = 22.sp
            )
        }
    }
}

/**
 * Anti-harm's gentle "rest" card (SPEC T3.2). Shown when the pattern looks compulsive rather than
 * helpful — late at night, today's work already done — and answers it with rest, never a streak
 * nudge or FOMO. Dismissible; it never blocks anything. On-device care, nothing measured or sent.
 */
@Composable
fun RestNudgeCard(message: String) {
    var shown by remember { mutableStateOf(true) }
    if (!shown) return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Surface)
            .border(1.dp, Gold.copy(alpha = 0.22f), RoundedCornerShape(32.dp))
            .padding(24.dp)
    ) {
        Text(
            text = "A GENTLE WORD",
            style = MaterialTheme.typography.labelSmall,
            color = Gold.copy(alpha = 0.5f),
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = LightText,
            lineHeight = 22.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "REST WELL",
            style = MaterialTheme.typography.labelSmall,
            color = Gold.copy(alpha = 0.6f),
            letterSpacing = 1.sp,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { shown = false }
                .padding(vertical = 6.dp)
        )
    }
}

/**
 * Your practices (SPEC T1.4) — the self-authored, anchored, approach-framed actions you're building.
 * Lists what you've set (as "After …, I will …") and opens the authoring dialog; each is removable.
 * The empty state invites the first one. Autonomy: they're yours, in your words.
 */
@Composable
fun PracticesCard(
    practices: List<com.ashlarprotocol.data.Practice>,
    intention: String,
    onSave: (String, String, Int?, String?) -> Unit,
    onRemove: (String) -> Unit
) {
    var show by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Surface)
            .border(1.dp, DividerWhite.copy(alpha = 0.05f), RoundedCornerShape(32.dp))
            .padding(24.dp)
    ) {
        Text(
            text = "YOUR PRACTICES",
            style = MaterialTheme.typography.labelSmall,
            color = Gold.copy(alpha = 0.4f),
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (practices.isEmpty()) {
            Text(
                text = "Small actions, anchored to your day. Set one — in your own words — and it's yours.",
                style = MaterialTheme.typography.bodyMedium,
                color = Silver,
                lineHeight = 22.sp
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                practices.take(5).forEach { p ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Slate.copy(alpha = 0.2f))
                            .border(1.dp, DividerWhite.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "“${com.ashlarprotocol.tools.PracticeAuthoring.composePlan(p.anchor, p.action)}”",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LightText,
                            lineHeight = 22.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "REMOVE",
                            style = MaterialTheme.typography.labelSmall,
                            color = Silver.copy(alpha = 0.5f),
                            fontSize = 9.sp,
                            modifier = Modifier.clickable { onRemove(p.id) }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "SET A PRACTICE  →",
            style = MaterialTheme.typography.labelSmall,
            color = Gold.copy(alpha = 0.7f),
            letterSpacing = 1.sp,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { show = true }
                .padding(vertical = 6.dp)
        )
    }
    if (show) {
        com.ashlarprotocol.ui.components.PracticeDialog(intention = intention, onDismiss = { show = false }, onSave = onSave)
    }
}

/**
 * The rhythm card (F6) — a steady rise and wind-down. Sleep-wake *regularity* (not duration or
 * earliness) is what the research associates with steadier days (Windred 2024; Li 2025). Framed
 * associationally ("linked to"), never causal/clinical; never an alarm; never shames a bad night.
 */
@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun RhythmCard(current: com.ashlarprotocol.data.RhythmAnchor?, onSet: (Int, Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Surface)
            .border(1.dp, DividerWhite.copy(alpha = 0.05f), RoundedCornerShape(32.dp))
            .padding(24.dp)
    ) {
        Text("YOUR RHYTHM", style = MaterialTheme.typography.labelSmall, color = Gold.copy(alpha = 0.4f), letterSpacing = 2.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "A steady rise and a wind-down. It's the regularity that's linked to steadier days — " +
                "not the hours, and no perfect night required.",
            style = MaterialTheme.typography.bodySmall, color = Silver, lineHeight = 19.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("RISE", style = MaterialTheme.typography.labelSmall, color = Gold.copy(alpha = 0.5f), letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(8.dp))
        androidx.compose.foundation.layout.FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            com.ashlarprotocol.tools.Rhythm.WAKE_SLOTS.forEach { m ->
                RhythmPill(com.ashlarprotocol.tools.Rhythm.formatTime(m), current?.wakeMinutesOfDay == m) {
                    onSet(m, current?.windDownMinutesOfDay ?: 22 * 60)
                }
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text("WIND DOWN", style = MaterialTheme.typography.labelSmall, color = Gold.copy(alpha = 0.5f), letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(8.dp))
        androidx.compose.foundation.layout.FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            com.ashlarprotocol.tools.Rhythm.WIND_DOWN_SLOTS.forEach { m ->
                RhythmPill(com.ashlarprotocol.tools.Rhythm.formatTime(m), current?.windDownMinutesOfDay == m) {
                    onSet(current?.wakeMinutesOfDay ?: 7 * 60, m)
                }
            }
        }
        if (current != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = com.ashlarprotocol.tools.Rhythm.reflection(current),
                style = MaterialTheme.typography.bodyMedium, color = Silver, lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun RhythmPill(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = if (selected) Gold else Silver,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) Gold.copy(alpha = 0.2f) else Slate.copy(alpha = 0.2f))
            .border(1.dp, if (selected) Gold.copy(alpha = 0.5f) else Slate, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 9.dp)
    )
}

/**
 * The automaticity card (F4) — a gentle, occasional "is the work becoming automatic?" A habit IS
 * context-cued automaticity, not a streak count, so this is the honest progress signal. Noticing,
 * not grading: it names what automaticity means and never scores the person. Always skippable.
 */
@Composable
fun AutomaticityCard(onRecord: (Int) -> Unit) {
    var recorded by remember { mutableStateOf<Int?>(null) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Surface)
            .border(1.dp, Gold.copy(alpha = 0.2f), RoundedCornerShape(32.dp))
            .padding(24.dp)
    ) {
        Text("A MOMENT TO NOTICE", style = MaterialTheme.typography.labelSmall, color = Gold.copy(alpha = 0.5f), letterSpacing = 2.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = com.ashlarprotocol.tools.Automaticity.PROMPT,
            style = MaterialTheme.typography.bodyLarge, color = LightText, lineHeight = 26.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        val chosen = recorded
        if (chosen == null) {
            com.ashlarprotocol.tools.Automaticity.LEVELS.forEach { lvl ->
                Text(
                    text = lvl.label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Silver,
                    modifier = Modifier
                        .fillMaxWidth().padding(top = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Slate.copy(alpha = 0.18f))
                        .border(1.dp, Slate.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .clickable { recorded = lvl.value; onRecord(lvl.value) }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                )
            }
        } else {
            Text(
                text = com.ashlarprotocol.tools.Automaticity.reflection(chosen),
                style = MaterialTheme.typography.bodyMedium, color = Silver, lineHeight = 22.sp
            )
        }
    }
}

/**
 * The WHO-5 card — a gentle invitation to the wellbeing check when it's due (SPEC T3.1). Opens the
 * five-question dialog; the score is stored on-device only. The card just invites — always skippable.
 */
@Composable
fun WhoFiveCard(onComplete: (Int) -> Unit) {
    var show by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Surface)
            .border(1.dp, DividerWhite.copy(alpha = 0.05f), RoundedCornerShape(32.dp))
            .padding(24.dp)
    ) {
        Text(
            text = "YOUR WELLBEING",
            style = MaterialTheme.typography.labelSmall,
            color = Gold.copy(alpha = 0.4f),
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "A gentle two-minute check on how the last two weeks have actually felt — five short questions, just for you.",
            style = MaterialTheme.typography.bodyMedium,
            color = Silver,
            lineHeight = 22.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "TAKE THE CHECK  →",
            style = MaterialTheme.typography.labelSmall,
            color = Gold.copy(alpha = 0.7f),
            letterSpacing = 1.sp,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { show = true }
                .padding(vertical = 6.dp)
        )
    }
    if (show) {
        com.ashlarprotocol.ui.components.WhoFiveDialog(
            onDismiss = { show = false },
            onComplete = onComplete
        )
    }
}

/**
 * The user's stated intention from initiation, surfaced so the app never forgets what they're
 * working toward. The first visible piece of the re-authoring engine (docs/ACTION_PLAN §1A).
 */
@Composable
fun IntentionCard(intention: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Surface)
            .border(1.dp, Gold.copy(alpha = 0.2f), RoundedCornerShape(32.dp))
            .padding(24.dp)
    ) {
        Text(
            text = "YOU'RE WORKING TOWARD",
            style = MaterialTheme.typography.labelSmall,
            color = Gold.copy(alpha = 0.5f),
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "“${intention}”",
            style = MaterialTheme.typography.bodyLarge,
            color = LightText,
            lineHeight = 26.sp
        )
    }
}

/**
 * An honest reflection of what the user has actually done — literal counts, nothing invented.
 * Replaces the removed fabricated "resilience"/"wisdom" charts. No score, no trend, no prediction.
 */
@Composable
fun WorkSoFarCard(
    streak: Int,
    journalEntries: Int,
    thoughtRecords: Int,
    gaugeDays: Int,
    recalls: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Surface)
            .border(1.dp, DividerWhite.copy(alpha = 0.05f), RoundedCornerShape(32.dp))
            .padding(24.dp)
    ) {
        Text(
            text = "THE WORK SO FAR",
            style = MaterialTheme.typography.labelSmall,
            color = Gold.copy(alpha = 0.55f),
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        WorkRow("Days tended", streak)
        WorkRow("Notes in the journal", journalEntries)
        WorkRow("Thoughts set to the plumb", thoughtRecords)
        WorkRow("Days fully divided by the gauge", gaugeDays)
        WorkRow("Principles held to memory", recalls)

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Only what you've actually done — no scores, no predictions.",
            style = MaterialTheme.typography.labelSmall,
            color = Silver.copy(alpha = 0.5f),
            lineHeight = 16.sp
        )
    }
}

@Composable
private fun WorkRow(label: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Silver)
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleLarge,
            color = if (count > 0) Gold else Silver.copy(alpha = 0.3f)
        )
    }
}

// Shown when the user returns after a lapse: a warm, self-forgiving welcome (KindStreak), never a
// "you lost your streak". The cumulative count never dropped, so this reassures rather than scolds.
@Composable
private fun ComebackCard(message: String, onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Surface)
            .border(1.dp, Gold.copy(alpha = 0.15f), RoundedCornerShape(32.dp))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "WELCOME BACK",
            style = MaterialTheme.typography.labelSmall,
            color = Gold.copy(alpha = 0.5f)
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = LightText
        )
        TextButton(
            onClick = onDismiss,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.height(24.dp)
        ) {
            Text(text = "CONTINUE", style = MaterialTheme.typography.labelSmall, color = Gold)
        }
    }
}

// The daily Working card: a mood check-in that scales the day's ask. Before check-in it asks how
// you're arriving; after, it acknowledges (warmer on hard days) and offers a lighter/heavier dial.
@Composable
private fun WorkingCard(
    working: DailyWorking?,
    dial: Int,
    onCheckIn: (Readiness) -> Unit,
    onNudge: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Surface)
            .border(1.dp, DividerWhite.copy(alpha = 0.05f), RoundedCornerShape(32.dp))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (working == null) {
            Text(
                text = "HOW ARE YOU ARRIVING?",
                style = MaterialTheme.typography.labelSmall,
                color = Gold.copy(alpha = 0.4f)
            )
            Text(
                text = "Today's work will match how you feel. There's no wrong answer.",
                style = MaterialTheme.typography.bodyMedium,
                color = Silver
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Readiness.values().forEach { r ->
                    Text(
                        text = r.display,
                        style = MaterialTheme.typography.labelMedium,
                        color = Charcoal,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Gold.copy(alpha = 0.85f))
                            .clickable { onCheckIn(r) }
                            .padding(vertical = 10.dp)
                    )
                }
            }
        } else {
            Text(
                text = "TODAY'S WORKING",
                style = MaterialTheme.typography.labelSmall,
                color = Gold.copy(alpha = 0.4f)
            )
            Text(
                text = working.acknowledgment,
                style = MaterialTheme.typography.bodyLarge,
                color = LightText
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${working.effort.display} · one gentle step",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Silver
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DialButton("Lighter", enabled = dial > -1) { onNudge(-1) }
                    DialButton("Heavier", enabled = dial < 1) { onNudge(1) }
                }
            }
        }
    }
}

@Composable
private fun DialButton(label: String, enabled: Boolean, onClick: () -> Unit) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = if (enabled) Gold else Slate,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Slate.copy(alpha = 0.25f))
            .then(if (enabled) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

// The intrinsic-progression card: tap to name your VIA signature strengths (identity, not points),
// then each day get one to use "in a new way" (Seligman 2005). Read-and-set, entirely on-device.
@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun StrengthsCard(
    signature: List<Strength>,
    todayPrompt: String?,
    onToggle: (Strength) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Surface)
            .border(1.dp, DividerWhite.copy(alpha = 0.05f), RoundedCornerShape(32.dp))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = if (todayPrompt != null) "STRENGTH FOR TODAY" else "NAME YOUR STRENGTHS",
            style = MaterialTheme.typography.labelSmall,
            color = Gold.copy(alpha = 0.4f)
        )
        if (todayPrompt != null) {
            Text(text = todayPrompt, style = MaterialTheme.typography.bodyLarge, color = LightText)
        } else {
            Text(
                text = "Choose the character strengths you most want to grow. Each day you'll get " +
                    "one to use in a new way.",
                style = MaterialTheme.typography.bodyMedium,
                color = Silver
            )
        }
        androidx.compose.foundation.layout.FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Strengths.all().forEach { s ->
                val selected = signature.contains(s)
                Text(
                    text = s.display,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (selected) Charcoal else Silver,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (selected) Gold else Slate.copy(alpha = 0.3f))
                        .clickable { onToggle(s) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun CognitiveBriefingCard(
    briefing: String?,
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
                color = Gold.copy(alpha = 0.55f)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (streak > 0) {
                    Text(
                        text = "TENDED: $streak",
                        style = MaterialTheme.typography.labelSmall,
                        color = Gold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
                // The word is bundled and local — it appears instantly. No network, so no "SYNC"
                // pretense and no spinner; the button simply offers ANOTHER word, honestly.
                TextButton(onClick = onRefresh, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)) {
                    Text(
                        text = "ANOTHER",
                        style = MaterialTheme.typography.labelSmall,
                        color = Gold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = briefing ?: "No word yet.",
            style = MaterialTheme.typography.bodyMedium,
            color = Silver,
            textAlign = TextAlign.Start,
            lineHeight = 24.sp
        )
    }
}

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
