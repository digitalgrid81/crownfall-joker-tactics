package com.Ritzava1.Mewlup.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.Ritzava1.Mewlup.core.theme.Parchment
import com.Ritzava1.Mewlup.core.theme.PolishedSilver

@Composable
fun ParchmentPanel(
    modifier: Modifier = Modifier,
    cornerRadius: Int = 18,
    borderAlpha: Float = 0.26f,
    fillAlpha: Float = 0.08f,
    elevation: Int = 16,
    contentPadding: Int = 14,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(cornerRadius.dp),
        colors = CardDefaults.cardColors(containerColor = Parchment.copy(alpha = fillAlpha)),
        border = BorderStroke(1.dp, PolishedSilver.copy(alpha = borderAlpha)),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding.dp),
            content = content
        )
    }
}
