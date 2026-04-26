package com.tcs.games.score4.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun NeoCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    shadowOffset: Dp = 6.dp,
    borderWidth: Dp = 2.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .drawBehind {
                drawRect(
                    color = Color.Black,
                    topLeft = androidx.compose.ui.geometry.Offset(shadowOffset.toPx(), shadowOffset.toPx()),
                    size = size
                )
            }
            .border(BorderStroke(borderWidth, Color.Black))
            .background(backgroundColor)
            .padding(12.dp)
    ) {
        Column {
            content()
        }
    }
}
