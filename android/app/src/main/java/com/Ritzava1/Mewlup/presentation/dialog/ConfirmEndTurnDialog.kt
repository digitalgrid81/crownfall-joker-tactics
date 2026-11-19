package com.Ritzava1.Mewlup.presentation.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.Ritzava1.Mewlup.R
import com.Ritzava1.Mewlup.core.theme.MidnightBlue
import com.Ritzava1.Mewlup.core.theme.PolishedSilver
import com.Ritzava1.Mewlup.core.theme.RoyalCrimson
import com.Ritzava1.Mewlup.core.theme.TextPrimary
import com.Ritzava1.Mewlup.core.theme.TextSecondary

@Composable
fun ConfirmEndTurnDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MidnightBlue,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        title = {
            Text(
                text = stringResource(R.string.confirm_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
        },
        text = {
            Text(
                text = stringResource(R.string.confirm_body),
                style = MaterialTheme.typography.bodyLarge
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.flip_orders),
                    color = RoyalCrimson,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.6.sp
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = PolishedSilver,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.6.sp
                )
            }
        }
    )
}
