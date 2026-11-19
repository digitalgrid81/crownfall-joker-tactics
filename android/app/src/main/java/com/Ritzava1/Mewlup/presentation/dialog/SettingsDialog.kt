package com.Ritzava1.Mewlup.presentation.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Ritzava1.Mewlup.R
import com.Ritzava1.Mewlup.core.theme.MidnightBlue
import com.Ritzava1.Mewlup.core.theme.Parchment
import com.Ritzava1.Mewlup.core.theme.RoyalCrimson
import com.Ritzava1.Mewlup.core.theme.TextMuted
import com.Ritzava1.Mewlup.core.theme.TextPrimary
import com.Ritzava1.Mewlup.core.theme.TextSecondary
import com.Ritzava1.Mewlup.core.ui.SecondaryButton
import com.Ritzava1.Mewlup.core.ui.difficultyLabel
import com.Ritzava1.Mewlup.domain.model.AppSettings
import com.Ritzava1.Mewlup.domain.model.Difficulty

@Composable
fun SettingsDialog(
    settings: AppSettings,
    onSelectDifficulty: (Difficulty) -> Unit,
    onToggleThreatOverlay: () -> Unit,
    onToggleConfirmEndTurn: () -> Unit,
    onToggleReduceMotion: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MidnightBlue,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        title = {
            Text(
                text = stringResource(R.string.settings),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.settings_rival_court),
                    style = MaterialTheme.typography.labelSmall,
                    color = Parchment
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Difficulty.entries.forEach { level ->
                        val label = difficultyLabel(level)
                        SecondaryButton(
                            text = label,
                            onClick = { onSelectDifficulty(level) },
                            enabled = settings.difficulty != level,
                            modifier = Modifier
                                .weight(1f)
                                .semantics { stateDescription = label }
                        )
                    }
                }
                SettingsToggle(
                    title = stringResource(R.string.settings_threat_overlay),
                    hint = stringResource(R.string.settings_threat_overlay_hint),
                    checked = settings.threatOverlay,
                    onToggle = onToggleThreatOverlay
                )
                SettingsToggle(
                    title = stringResource(R.string.settings_confirm_end_turn),
                    hint = stringResource(R.string.settings_confirm_end_turn_hint),
                    checked = settings.confirmEndTurn,
                    onToggle = onToggleConfirmEndTurn
                )
                SettingsToggle(
                    title = stringResource(R.string.settings_reduce_motion),
                    hint = stringResource(R.string.settings_reduce_motion_hint),
                    checked = settings.reduceMotion,
                    onToggle = onToggleReduceMotion
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.close),
                    color = RoyalCrimson,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.6.sp
                )
            }
        }
    )
}

@Composable
private fun SettingsToggle(
    title: String,
    hint: String,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = Parchment
            )
            Text(
                text = hint,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = TextMuted,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = TextPrimary,
                checkedTrackColor = RoyalCrimson
            ),
            modifier = Modifier.semantics { stateDescription = title }
        )
    }
}
