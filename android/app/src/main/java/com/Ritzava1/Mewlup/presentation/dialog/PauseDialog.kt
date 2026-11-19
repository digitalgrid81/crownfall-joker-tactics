package com.Ritzava1.Mewlup.presentation.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Ritzava1.Mewlup.R
import com.Ritzava1.Mewlup.core.theme.MidnightBlue
import com.Ritzava1.Mewlup.core.theme.RoyalCrimson
import com.Ritzava1.Mewlup.core.theme.TextPrimary
import com.Ritzava1.Mewlup.core.theme.TextSecondary
import com.Ritzava1.Mewlup.core.ui.PrimaryButton
import com.Ritzava1.Mewlup.core.ui.SecondaryButton

@Composable
fun PauseDialog(
    onResume: () -> Unit,
    onRules: () -> Unit,
    onMenu: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MidnightBlue,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        title = {
            Text(
                text = stringResource(R.string.pause_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.pause_hint),
                    style = MaterialTheme.typography.bodyLarge
                )
                PrimaryButton(
                    text = stringResource(R.string.resume),
                    onClick = onResume,
                    height = 52,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
                SecondaryButton(
                    text = stringResource(R.string.how_to_play),
                    onClick = onRules,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onMenu) {
                Text(
                    text = stringResource(R.string.menu),
                    color = RoyalCrimson,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.6.sp
                )
            }
        }
    )
}
