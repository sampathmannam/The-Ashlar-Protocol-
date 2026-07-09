package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.tools.PracticeAuthoring
import com.example.ui.theme.Gold
import com.example.ui.theme.LightText
import com.example.ui.theme.Silver
import com.example.ui.theme.Slate
import com.example.ui.theme.Surface

/**
 * The practice-authoring dialog (SPEC T1.4). Two fields — an existing-routine anchor and an approach
 * action — composing "After [anchor], I will [action]." It won't save avoidance phrasing: as soon as
 * the action reads like "stop…" it shows the reframe hint and disables save. Approach, anchored, yours.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeDialog(onDismiss: () -> Unit, onSave: (anchor: String, action: String) -> Unit) {
    var anchor by remember { mutableStateOf("") }
    var action by remember { mutableStateOf("") }
    val avoidance = action.isNotBlank() && PracticeAuthoring.isAvoidanceFramed(action)
    val plan = PracticeAuthoring.composePlan(anchor, action)
    val canSave = PracticeAuthoring.canSave(anchor, action)

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 680.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Surface)
                .border(1.dp, Gold.copy(alpha = 0.25f), RoundedCornerShape(28.dp))
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text("SET A PRACTICE", style = MaterialTheme.typography.labelSmall, color = Gold, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Anchor a small, doable action to something you already do — then name what you WILL do.",
                style = MaterialTheme.typography.bodyMedium,
                color = Silver,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(20.dp))

            Text("AFTER…", style = MaterialTheme.typography.labelSmall, color = Gold.copy(alpha = 0.5f), letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            PracticeField(anchor, { anchor = it }, "e.g. I pour my morning coffee")

            Spacer(modifier = Modifier.height(16.dp))
            Text("…I WILL", style = MaterialTheme.typography.labelSmall, color = Gold.copy(alpha = 0.5f), letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            PracticeField(action, { action = it }, "e.g. write one line in my journal")

            if (avoidance) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Slate.copy(alpha = 0.3f))
                        .border(1.dp, Gold.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text(PracticeAuthoring.REFRAME_HINT, style = MaterialTheme.typography.bodyMedium, color = Gold.copy(alpha = 0.85f), lineHeight = 22.sp)
                }
            } else if (plan.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Slate.copy(alpha = 0.3f))
                        .border(1.dp, Gold.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text("YOUR PRACTICE", style = MaterialTheme.typography.labelSmall, color = Gold, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("“$plan”", style = MaterialTheme.typography.bodyLarge, color = LightText, lineHeight = 26.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "KEEP THIS PRACTICE",
                style = MaterialTheme.typography.labelSmall,
                color = if (canSave) Gold else Silver.copy(alpha = 0.35f),
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, (if (canSave) Gold else Silver).copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .then(if (canSave) Modifier.clickable { onSave(anchor, action); onDismiss() } else Modifier)
                    .padding(vertical = 14.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Cancel",
                style = MaterialTheme.typography.labelSmall,
                color = Silver.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().clickable { onDismiss() }.padding(vertical = 12.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PracticeField(value: String, onChange: (String) -> Unit, placeholder: String) {
    TextField(
        value = value,
        onValueChange = onChange,
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Gold.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Slate.copy(alpha = 0.2f),
            unfocusedContainerColor = Slate.copy(alpha = 0.2f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = LightText,
            unfocusedTextColor = LightText,
            cursorColor = Gold
        ),
        placeholder = { Text(placeholder, style = MaterialTheme.typography.bodyMedium, color = Silver.copy(alpha = 0.45f)) },
        textStyle = MaterialTheme.typography.bodyMedium
    )
}
