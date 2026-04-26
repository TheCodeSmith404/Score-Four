package com.tcs.games.score4.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NeoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFFFFE600), // NeoYellow
    shadowOffset: Dp = 4.dp,
    borderWidth: Dp = 2.dp
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
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black
        )
    }
}
